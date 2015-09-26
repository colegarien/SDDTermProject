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

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.Models.Questions.IQuestion;
import edu.uco.schambers.classmate.R;

public class StudentQuestionService extends Service
{
    public static final String ACTION_NOTIFY_QUESTION_RECEIVED = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_NOTIFY_QUESTION_RECEIVED";
    public static final String ACTION_REQUEST_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_REQUEST_QUESTION_RESPONSE";
    public static final String ACTION_SEND_QUESTION_RESPONSE = "edu.uco.schambers.classmate.Services.StudentQuestionService.ACTION_SEND_QUESTION_RESPONSE";
    public StudentQuestionService()
    {
    }

    public static Intent getNewSendQuestionIntent(Context context, IQuestion question)
    {
        Intent questionResponseBroadcastIntent = new Intent(context, StudentQuestionService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudentResponseFragment.ARG_QUESTION, question);
        questionResponseBroadcastIntent.putExtras(bundle);
        questionResponseBroadcastIntent.setAction(ACTION_NOTIFY_QUESTION_RECEIVED);
        return questionResponseBroadcastIntent;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        //will probably set up wifi p2p connection instance here when we get that far.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String action = intent.getAction();
        switch (action)
        {
            case ACTION_NOTIFY_QUESTION_RECEIVED:
                IQuestion question = (IQuestion) intent.getExtras().getSerializable(StudentResponseFragment.ARG_QUESTION);
                notifyQuestionReceived(question);
                break;
            case ACTION_SEND_QUESTION_RESPONSE:
                //TODO
                break;
        }
        return START_NOT_STICKY;
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
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this,0,notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        notificationManager.notify(R.integer.question_received_notification, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
