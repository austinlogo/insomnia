package northstar.planner.persistence;

import northstar.planner.PlannerApplication;
import northstar.planner.R;

public enum PeriodUnit {
//    Minute(R.string.minute),
    Day(R.string.day),
    Week(R.string.week);

    private int periodString;

    private PeriodUnit(int ps) {
        periodString = ps;
    }

    @Override
    public String toString() {
        return PlannerApplication.getInstance().getResources().getString(periodString);
    }



    public static long unitToMillis(PeriodUnit unit) {
        switch (unit) {
//            case Minute:
//                return 1000 * 60;
            case Day:
                return 1000* 60 * 60 * 24;
            case Week:
                return unitToMillis(Day) * 7;
            default:
                return 0;
        }
    }
}
