package edu.uco.schambers.classmate.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
    public static final String ACTION_START_SERVICE_STICKY= "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_START_SERVICE_STICKY";

    private final IBinder serviceBinder = new LocalBinder();
    private SocketAction listenForQuestions;

    private IQuestion question;

    Handler handler;

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
        handler = new Handler(Looper.getMainLooper());
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
        SocketAction sendQuestion = new StudentSendQuestionAction(question, this);
        sendQuestion.execute();
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

    public IQuestion getQuestion()
    {
        return question;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        return serviceBinder;
    }

    @Override
    public void onQuestionReceived(IQuestion question)
    {
        this.question = question;
        notifyQuestionReceived(question);
    }

    @Override
    public void onQuestionSentSuccessfully(String domain, int port)
    {
        this.question = null;
        final String domainFinal = domain;
        final int portFinal = port;
        //Yes, I know this is totally awful. Its not going to stay this way, I swear. Just want these toasts to fire for debug purposes.
        handler.post(new Runnable()
        {
            @Override

            public void run()
            {
                Toast.makeText(getBaseContext(),String.format("The question was sent successfully to domain: %s port %d ", domainFinal, portFinal), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class LocalBinder extends Binder
    {
        public StudentQuestionService getService()
        {
            return StudentQuestionService.this;
        }
    }
}
