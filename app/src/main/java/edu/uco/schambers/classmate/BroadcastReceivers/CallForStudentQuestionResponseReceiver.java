package edu.uco.schambers.classmate.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import edu.uco.schambers.classmate.Activites.MainActivity;
import edu.uco.schambers.classmate.Fragments.StudentResponseFragment;
import edu.uco.schambers.classmate.Models.Questions.DefaultMultiChoiceQuestion;
import edu.uco.schambers.classmate.R;

public class CallForStudentQuestionResponseReceiver extends BroadcastReceiver
{
    public static final String ACTION_REQUEST_QUESTION_RESPONSE = "edu.uco.schambers.classmate.BroadcastReceivers.CallForStudentQuestionResponseReceiver.ACTION_REQUEST_QUESTION_RESPONSE";

    public CallForStudentQuestionResponseReceiver()
    {
    }

    public static Intent getStartIntent(Context context)
    {
        Intent questionResponseBroadcastIntent = new Intent(context, CallForStudentQuestionResponseReceiver.class);
        questionResponseBroadcastIntent.setAction(CallForStudentQuestionResponseReceiver.ACTION_REQUEST_QUESTION_RESPONSE);
        return questionResponseBroadcastIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch(intent.getAction())
        {
            case ACTION_REQUEST_QUESTION_RESPONSE:
                createNotificationAndNotify(context);
                break;
        }

    }

    private void createNotificationAndNotify(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_question_broadcast_recieved)
                .setContentTitle("Question Received")
                .setContentText("Click here to respond");
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH).setVibrate(new long[0]);

        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setAction(ACTION_REQUEST_QUESTION_RESPONSE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudentResponseFragment.ARG_QUESTION, new DefaultMultiChoiceQuestion());
        notifyIntent.putExtras(bundle);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context,0,notifyIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        notificationManager.notify(R.integer.question_received_notification, notificationBuilder.build());
    }
}
