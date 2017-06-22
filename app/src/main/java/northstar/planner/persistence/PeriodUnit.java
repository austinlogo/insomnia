package northstar.planner.persistence;

import northstar.planner.PlannerApplication;
import northstar.planner.R;

public enum PeriodUnit {
//    Minute(R.string.minute),
    Day(R.string.day),
    Week(R.string.week),
    Month(R.string.month);

    private int periodString;

    private PeriodUnit(int ps) {
        periodString = ps;
    }

    @Override
    public String toString() {
        return PlannerApplication.getInstance().getResources().getString(periodString);
    }
}
