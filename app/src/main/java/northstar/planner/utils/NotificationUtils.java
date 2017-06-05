package northstar.planner.utils;

/**
 * Created by Austin on 4/3/2017.
 */

public class NotificationUtils {

    public static int constructNotificationId(long id, NotificationType notificationType) {
        int type = notificationType.getValue();
        return constructNotificationId(id, type);
    }

    public static int constructNotificationId(long id, int notificationType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(notificationType);
        stringBuilder.append(id);
        return Integer.parseInt(stringBuilder.toString());
    }
}
