package northstar.planner.persistence;

import org.joda.time.DateTimeConstants;

public enum PeriodTarget {
    MONDAY(DateTimeConstants.MONDAY),
    TUESDAY(DateTimeConstants.TUESDAY),
    WEDNESDAY(DateTimeConstants.WEDNESDAY),
    THURSDAY(DateTimeConstants.THURSDAY),
    FRIDAY(DateTimeConstants.FRIDAY),
    SATURDAY(DateTimeConstants.SATURDAY),
    SUNDAY(DateTimeConstants.SUNDAY);

    private int targetValue;
    PeriodTarget(int i) {
        targetValue = i;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public static PeriodTarget getEnum(int i) {
        for (PeriodTarget target : PeriodTarget.values()) {
            if (target.getTargetValue() == i) {
                return target;
            }
        }
        return null;
    }
}
