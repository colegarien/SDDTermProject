package edu.uco.schambers.classmate.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentReceiveQuestionsAction;
import edu.uco.schambers.classmate.SocketActions.StudentSendQuestionAction;

public class StudentQuestionService extends Service implements OnQuestionReceivedListener
{
    public static final String ACTION_NOTIFY_QUESTION_RECEIVED = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_NOTIFY_QUESTION_RECEIVED";
    public static final String ACTION_REQUEST_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_REQUEST_QUESTION_RESPONSE";
    public static final String ACTION_SEND_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_SEND_QUESTION_RESPONSE";

    private SocketAction listenForQuestions;

    public StudentQuestionService()
    {
    }

    public static Intent getNewSendQuestionIntent(Context context, IQuestion question)
    {
        Intent questionReceivedIntent = getBaseIntent(context,question);
        questionReceivedIntent.setAction(ACTION_NOTIFY_QUESTION_RECEIVED);
        return questionReceivedIntent;
    }

    public static Intent getNewSendResponseIntent(Context context, IQuestion question)
    {
        Intent questionResponseIntent = getBaseIntent(context,question);
        questionResponseIntent.setAction(ACTION_SEND_QUESTION_RESPONSE);
        return questionResponseIntent;
    }

    private static Intent getBaseIntent(Context context, IQuestion question)
    {
        Intent baseQuestionIntent= new Intent(context, StudentQuestionService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudentResponseFragment.ARG_QUESTION, question);
        baseQuestionIntent.putExtras(bundle);
        return baseQuestionIntent;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        listenForQuestions = new StudentReceiveQuestionsAction(this);
        listenForQuestions.execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String action = intent.getAction();
        IQuestion question;
        switch (action)
        {
            case ACTION_NOTIFY_QUESTION_RECEIVED:
                question = (IQuestion) intent.getExtras().getSerializable(StudentResponseFragment.ARG_QUESTION);
                notifyQuestionReceived(question);
                break;
            case ACTION_SEND_QUESTION_RESPONSE:
                question = (IQuestion) intent.getExtras().getSerializable(StudentResponseFragment.ARG_QUESTION);
                sendQuestionResponse(question);
                break;
        }
        return START_STICKY;
    }

    private void sendQuestionResponse(IQuestion question)
    {
        //todo actual implementation
        SocketAction sendQuestion = new StudentSendQuestionAction(question);
        sendQuestion.execute();
        if(((StudentSendQuestionAction)sendQuestion).isQuestionSentSuccessfully())
        {
            Toast.makeText(this, String.format(getResources().getString(R.string.response_sent), question.getAnswer()), Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyQuestionReceived(IQuestion question)
    {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_question_broadcast_recieved)
                .setContentTitle("Question Received")
                .setContentText("Click here to respond");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setAction(ACTION_REQUEST_QUESTION_RESPONSE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudentResponseFragment.ARG_QUESTION, question);
        notifyIntent.putExtras(bundle);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        notificationManager.notify(R.integer.question_received_notification, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onQuestionReceived(IQuestion question)
    {
        Intent intent = getNewSendQuestionIntent(this, question);
        startService(intent);
    }
}
