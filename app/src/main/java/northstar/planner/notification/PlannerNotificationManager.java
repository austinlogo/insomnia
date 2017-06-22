package northstar.planner.notification;


import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.joda.time.DateTime;

import javax.inject.Inject;

import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.models.Recurrence;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.persistence.RecurrenceGateway;
import northstar.planner.presentation.task.TaskActivity;
import northstar.planner.utils.NotificationType;
import northstar.planner.utils.NotificationUtils;

public class PlannerNotificationManager {

    @Inject
    RecurrenceGateway recurrenceGateway;

    @Inject
    PlannerSqliteGateway plannerGateway;

    private Context ctx;
    private AlarmManager alarmManager;

    public PlannerNotificationManager(Application context, AlarmManager alarmManager) {
        ctx = context;
        this.alarmManager = alarmManager;

        ((PlannerApplication) context).getComponent().inject(this);
    }


    public void scheduleNotification(Task task, NotificationType notificationType) {
        PendingIntent pendingIntent = constructNotificationPendingIntent(null, task, notificationType);
        long notificationTime = getNotificationTime(task, notificationType);
        scheduleNotification(notificationTime, pendingIntent);
    }

    public void  scheduleNextRecurrenceIteration(Task task) {
        task.updateToNextIteration();
        plannerGateway.updateTask(task);

        if (task.getDue().isAfterNow()) {
            scheduleNexRecurrenceNotification(task, task.getDue());
        }
    }

    private void scheduleNexRecurrenceNotification(Task task, DateTime nextOccurrence) {
        Recurrence taskRecurrenceSchedule = task.getRecurrenceSchedule();
        PendingIntent pendingIntent = constructNotificationPendingIntent(taskRecurrenceSchedule, task, NotificationType.RECURRING_NOTIFICATION);
        scheduleNotification(nextOccurrence.getMillis(), pendingIntent);
    }

    private void scheduleNotification(long notificationTime, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
        }
    }

    private static long getNotificationTime(Task task, NotificationType notificationType) {
        switch (notificationType) {
            case DUE_NOTIFICATION:
                return task.getDue().getMillis();
            case REMINDER_NOTIFICATION:
                return task.getReminder().getMillis();
            case SNOOZE_NOTIFICATION:
                return task.getSnooze().getMillis();
            case RECURRING_NOTIFICATION:
                return task.getDue().getMillis();
        }
        return 0;
    }

    private PendingIntent constructNotificationPendingIntent(Recurrence rec, Task task, NotificationType notificationType) {
        Intent notificationIntent = constructNotificationIntent(task, getNotificationTime(task, notificationType));

        if (rec != null && rec.getEndTime() != null) {
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_STOP, rec.getEndTime().getMillis());
        }

        notificationIntent.putExtra(NotificationPublisher.TASK_ID, task.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, notificationType.toString());

        return constructBasePendingIntent(ctx, task.getId(), notificationType, notificationIntent);
    }

//    private PendingIntent constructNotificationPendingIntent(Context ctx, Task task, NotificationType notificationType) {
//        Intent notificationIntent = constructNotificationIntent(task, getNotificationTime(task, notificationType));
//
//
//        return constructBasePendingIntent(ctx, task.getId(), notificationType, notificationIntent);
//    }

//    public static PendingIntent constructBasePendingIntent(Context ctx, long taskId, String notificationType, Intent intent) {
//        return constructBasePendingIntent(ctx, taskId, NotificationType.valueOf(notificationType), intent);
//    }

    public static PendingIntent constructBasePendingIntent(Context ctx, long taskId, NotificationType notificationType, Intent intent) {
        return PendingIntent.getBroadcast(
                ctx,
                NotificationUtils.constructNotificationId(taskId, notificationType),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }



    private Intent constructNotificationIntent(Task task, long showTimestamp) {
        Notification notification = getNotification(task, showTimestamp);
        Intent notificationIntent = new Intent(ctx, NotificationPublisher.class);

        notificationIntent.putExtra(
                NotificationPublisher.NOTIFICATION_ID, task.getId());
        notificationIntent.putExtra(
                NotificationPublisher.NOTIFICATION, notification);

        return notificationIntent;
    }

    private Notification getNotification(Task task, long showtimestamp) {
        Intent result = new Intent(ctx, TaskActivity.class);
        result.putExtra(TaskTable.TABLE_NAME, task);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, (int) task.getId() , result, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(ctx);
        builder.setContentTitle("Task Reminder");
        builder.setContentText(task.getTitle());
        builder.setWhen(showtimestamp);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVibrate(new long[]{0,100,100,100});
        builder.setSmallIcon(R.drawable.logo_nobackground);

        if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(ctx.getResources().getColor(R.color.colorPrimary));
        }

        result.putExtra(TaskTable.TABLE_NAME, task);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    public Task cancelAllNotificationsForTask(Task item) {
        if (item.getDue() != null) {
            cancelAllNotificationsForTask(item, NotificationType.DUE_NOTIFICATION);

            cancelAllNotificationsForTask(item, NotificationType.RECURRING_NOTIFICATION);
            recurrenceGateway.removeRecurrenceRecord(item);
            item.setRecurrenceSchedule(null);
        }

        if (item.getReminder() != null) {
            cancelAllNotificationsForTask(item, NotificationType.REMINDER_NOTIFICATION);
        }

        if (item.getSnooze() != null) {
            cancelAllNotificationsForTask(item, NotificationType.SNOOZE_NOTIFICATION);
        }

        return item;
    }

    private void cancelAllNotificationsForTask(Task item, NotificationType notificationType) {
        alarmManager.cancel(constructNotificationPendingIntent(null, item, notificationType));
    }
}
