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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionReceivedListener;
import edu.uco.schambers.classmate.ListenerInterfaces.OnQuestionSendErrorListener;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;
import edu.uco.schambers.classmate.SocketActions.SocketAction;
import edu.uco.schambers.classmate.SocketActions.StudentReceiveQuestionsAction;
import edu.uco.schambers.classmate.SocketActions.StudentSendQuestionAction;

public class StudentQuestionService extends Service implements OnQuestionReceivedListener, OnQuestionSendErrorListener
{
    public static final String ACTION_NOTIFY_QUESTION_RECEIVED = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_NOTIFY_QUESTION_RECEIVED";
    public static final String ACTION_REQUEST_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_REQUEST_QUESTION_RESPONSE";
    public static final String ACTION_SEND_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_SEND_QUESTION_RESPONSE";
    public static final String ACTION_START_SERVICE_STICKY= "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_START_SERVICE_STICKY";

    public static final int MSG_QUESTION_RECEIEVED = 1;
    public static final int MSG_QUESTION_SEND_ERROR = 2;
    public static final int MSG_QUESTION_SEND_SUCCESS = 3;

    private final IBinder serviceBinder = new LocalBinder();
    private SocketAction listenForQuestions;

    private boolean serviceIsBound = false;

    private IQuestion question;

    Handler handler;

    Messenger fragmentMessenger;

    public StudentQuestionService()
    {
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
                notifyQuestionReceived();
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

    private void notifyQuestionReceived()
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
        serviceIsBound = true;
        return serviceBinder;
    }

    @Override
    public void onRebind(Intent intent)
    {
        super.onRebind(intent);
        serviceIsBound = true;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        serviceIsBound = false;
        fragmentMessenger = null;
        return true;
    }

    public void setFragmentMessenger(Messenger messenger)
    {
        fragmentMessenger = messenger;
    }

    @Override
    public void onQuestionReceived(IQuestion question)
    {
        this.question = question;
        if(serviceIsBound)
        {
            Message questionArrivedMessage = Message.obtain(null, MSG_QUESTION_RECEIEVED, 0, 0);
            try
            {
                fragmentMessenger.send(questionArrivedMessage);
            }catch (RemoteException e)
            {
                Log.d("StudentQuestionService", String.format("Message failed. Exception: %s", e.toString()));
            }
        }
        notifyQuestionReceived();
    }

    @Override
    public void onQuestionSentSuccessfully(String domain, int port)
    {
        this.question = null;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.integer.question_received_notification);
        final String domainFinal = domain;
        final int portFinal = port;
        //Yes, I know this is totally awful. Its not going to stay this way, I swear. Just want these toasts to fire for debug purposes.
        handler.post(new Runnable()
        {
            @Override

            public void run()
            {
                Toast.makeText(getBaseContext(), String.format("The question was sent successfully to domain: %s port %d ", domainFinal, portFinal), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onQuestionSendError(String message)
    {
        if(serviceIsBound)
        {
            Message questionSendErrorMessage = Message.obtain(null, MSG_QUESTION_SEND_ERROR, 0, 0);
            questionSendErrorMessage.obj = message;
            try
            {
                fragmentMessenger.send(questionSendErrorMessage);
            }
            catch (RemoteException e)
            {
                Log.d("StudentQuestionService", String.format("Message failed. Exception: %s", e.toString()));
            }
        }

    }

    public class LocalBinder extends Binder
    {
        public StudentQuestionService getService()
        {
            return StudentQuestionService.this;
        }
    }
}
