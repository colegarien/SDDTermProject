package edu.uco.schambers.classmate.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import edu.uco.schambers.classmate.R;

public class CallForStudentQuestionResponseReceiver extends BroadcastReceiver
{
    public CallForStudentQuestionResponseReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_question_broadcast_recieved)
            .setContentTitle("Question Received")
            .setContentText("Click here to respond");
        notificationManager.notify(R.integer.question_received_notification, notificationBuilder.build());
    }
}
