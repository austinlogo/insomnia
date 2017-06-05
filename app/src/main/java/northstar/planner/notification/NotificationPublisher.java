package northstar.planner.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import northstar.planner.presentation.BaseActivity;
import northstar.planner.utils.NotificationType;

public class NotificationPublisher extends BroadcastReceiver {

    public static final String TASK_ID = "task-id";
    public static final String NOTIFICATION_TYPE = "notification-type";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_STOP = "notification-stop";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        int id = (int) intent.getLongExtra(NOTIFICATION_ID, 0L);
        long taskId = intent.getLongExtra(TASK_ID, 0L);
        String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
        long stopTime = intent.getLongExtra(NOTIFICATION_STOP, -1);

        long now = new Date().getTime();

//        if (true) {
        if (stopTime > 0 && now > stopTime) {
            notificationManager.cancel(id);
//            PendingIntent pIntent = BaseActivity.constructNotificationPendingIntent(context, )
            PendingIntent pIntent = BaseActivity.constructBasePendingIntent(context, taskId, NotificationType.valueOf(notificationType), intent);
            alarmManager.cancel(pIntent);
        } else {
            notificationManager.cancel(id);
            notificationManager.notify(id, notification);
        }

    }
}
