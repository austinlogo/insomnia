package northstar.planner.utils;

/**
 * Created by Austin on 4/3/2017.
 */

public enum NotificationType {
    DUE_NOTIFICATION(0),
    SNOOZE_NOTIFICATION(1),
    REMINDER_NOTIFICATION(2);


    private final int value;

    private NotificationType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
}
