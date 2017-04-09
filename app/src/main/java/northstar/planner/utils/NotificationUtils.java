package northstar.planner.utils;

/**
 * Created by Austin on 4/3/2017.
 */

public class NotificationUtils {

    public static int constructNotificationId(long id, NotificationType notificationType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(notificationType.getValue());
        stringBuilder.append(id);
        return Integer.parseInt(stringBuilder.toString());
    }
}
