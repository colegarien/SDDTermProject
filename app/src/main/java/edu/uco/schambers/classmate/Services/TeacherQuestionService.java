package edu.uco.schambers.classmate.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Fragments.TeacherQuestion;
import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.DefaultUnanswerdQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.ObservableManagers.IPAddressManager;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentReceiveQuestionsAction;
import edu.uco.schambers.classmate.SocketActions.StudentSendQuestionAction;
import edu.uco.schambers.classmate.SocketActions.TeacherReceiveQuestionsAction;
import edu.uco.schambers.classmate.SocketActions.TeacherSendQuestionAction;

public class TeacherQuestionService extends Service implements OnQuestionReceivedListener {
    public static final String ACTION_NOTIFY_QUESTION_RECEIVED = "edu.uco.schambers.classmate.Services.TeacherQuestionService.ACTION_NOTIFY_QUESTION_RECEIVED";
    public static final String ACTION_REQUEST_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.TeacherQuestionService.ACTION_REQUEST_QUESTION_RESPONSE";
    public static final String ACTION_SEND_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.TeacherQuestionService.ACTION_SEND_QUESTION_RESPONSE";

    public static final String ACTION_CALL_TIME = "edu.uco.schambers.classmate.Services.TeacherQuestionService.ACTION_CALL_TIME";

    private SocketAction listenForQuestions;
    private ArrayList<IQuestion> answerList;
    private boolean isAccepting;
    Handler handler;

    public TeacherQuestionService() {
    }

    public static Intent getNewSendQuestionIntent(Context context, IQuestion question) {
        Intent questionReceivedIntent = getBaseIntent(context,question);
        questionReceivedIntent.setAction(ACTION_NOTIFY_QUESTION_RECEIVED);
        return questionReceivedIntent;
    }

    public static Intent getNewSendResponseIntent(Context context, IQuestion question) {
        Intent questionResponseIntent = getBaseIntent(context,question);
        questionResponseIntent.setAction(ACTION_SEND_QUESTION_RESPONSE);
        return questionResponseIntent;
    }

    public static Intent getNewCallTimeIntent(Context context, IQuestion question) {
        Intent callTimeIntent = getBaseIntent(context, question);
        callTimeIntent.setAction(ACTION_CALL_TIME);
        return callTimeIntent;
    }

    private static Intent getBaseIntent(Context context, IQuestion question) {
        Intent baseQuestionIntent= new Intent(context, TeacherQuestionService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeacherQuestion.ARG_QUESTION, question);
        baseQuestionIntent.putExtras(bundle);
        return baseQuestionIntent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        answerList = new ArrayList<>();
        isAccepting = false;
        //Todo CARCHER TQS.onCreate() -> SRQA Listener
        listenForQuestions = new TeacherReceiveQuestionsAction(this);
        listenForQuestions.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        IQuestion question;
        switch (action)
        {
            case ACTION_NOTIFY_QUESTION_RECEIVED://Question recieved from student, called from listener
                question = (IQuestion) intent.getExtras().getSerializable(TeacherQuestion.ARG_QUESTION);
                aggregateQuestion(question);//Add the current answer to the running answer list
                notifyQuestionReceived(question);
                break;
            case ACTION_SEND_QUESTION_RESPONSE://Button pressed to send questions to all students
                question = (IQuestion) intent.getExtras().getSerializable(TeacherQuestion.ARG_QUESTION);
                isAccepting = true; //Begin accepting Answers
                balanceAnswerList();
                sendQuestionResponse(question);
                break;
            case ACTION_CALL_TIME://Button to collect answers pressed
                isAccepting = false;
                balanceAnswerList();
                //Update UI/Send to Thomas
                break;
        }
        return START_STICKY;
    }

    private void balanceAnswerList() {
        if (isAccepting) {
            answerList.clear();
        } else {
            int totalReceived, totalExpected;
            totalReceived = answerList.size();
            totalExpected = IPAddressManager.getInstance().getStudentAddresses().size();//Store size in IPAdd?
            for (int i = 0; i < (totalExpected - totalReceived); i++) {
                answerList.add(new DefaultUnanswerdQuestion());
            }
        }
    }

    private void sendQuestionResponse(IQuestion question) {
        //Todo CARCHER TQS.sendQuestionResponse() -> SocketAction StudentSQA
        SocketAction sendQuestion = new TeacherSendQuestionAction(question, this);
        sendQuestion.execute();
    }

    private void aggregateQuestion(IQuestion question) {
        answerList.add(question);
    }

    private void notifyQuestionReceived(IQuestion question) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_question_broadcast_recieved)
                .setContentTitle("Question Received")
                .setContentText("Questions Received! Press here to see class aggregate answers!");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setAction(ACTION_REQUEST_QUESTION_RESPONSE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeacherQuestion.ARG_QUESTION, question);
        notifyIntent.putExtras(bundle);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        notificationManager.notify(R.integer.question_received_notification, notificationBuilder.build());
    }

    /**
     * Intended to be called from TeacherQuestionResults, will eventually need to check if
     * the questions have been answered; if sending from TeacherQuestion Fragment may not
     * need if bundled in intent.
     *
     * @return answerList ArrayList<IQuestion> containing all answered/unanswered questions
     */
    public ArrayList<IQuestion> getAnswerList() {
        return answerList;
    }

    /**
     * @return isAccepting boolean, true if outstanding question & accepting questions from students
     */
    public boolean getIsAccepting() {
        return isAccepting;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onQuestionReceived(IQuestion question) {
        Intent intent = getNewSendQuestionIntent(this, question);
        startService(intent);
    }

    @Override
    public void onQuestionSentSuccessfully(String domain, int port) {
        final String domainFinal = domain;
        final int portFinal = port;
        //Yes, I know this is totally awful. Its not going to stay this way, I swear. Just want these toasts to fire for debug purposes.
        handler.post(new Runnable()
        {
            @Override

            public void run()
            {
                Toast.makeText(getBaseContext(),String.format("The question was sent successfully to %s port %d ", domainFinal, portFinal), Toast.LENGTH_LONG).show();
            }
        });
    }
}
