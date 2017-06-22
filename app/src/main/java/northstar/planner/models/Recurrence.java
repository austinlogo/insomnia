package northstar.planner.models;

import android.database.Cursor;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.persistence.PeriodTarget;
import northstar.planner.persistence.PeriodUnit;
import northstar.planner.persistence.TargetUnit;

public abstract class Recurrence extends BaseModel {

    protected long taskId;
    protected int periodUnitMultiplier;
    protected DateTime startTime;
    protected DateTime endTime;
    protected PeriodUnit periodUnit;

    public abstract List<PeriodTarget> getTargets();
    public abstract TargetUnit getTargetUnit();
    public abstract DateTime calculateNextIterationFromGivenDate(DateTime dateTime);

    public static Recurrence newInstance(Cursor recurrenceTableCursor, Cursor periodTargetsCursor) {
        if (periodTargetsCursor == null) {
            return new SimpleRecurrence(recurrenceTableCursor);
        }

        return new ComplexRecurrence(recurrenceTableCursor, periodTargetsCursor);
    }

    public Recurrence() {}

    public Recurrence(long taskId, int periodUnitMultiplier, PeriodUnit periodUnit, DateTime startTime, DateTime endTime) {
        this.taskId = taskId;
        this.periodUnitMultiplier = periodUnitMultiplier;
        this.periodUnit = periodUnit;
        this.endTime = endTime;
        this.startTime = calculateStartTime(startTime);
    }

    @Override
    public String getTitle() {
        return "";
    }

    public long getTaskId() {
        return taskId;
    }

    public Period getPeriod() {
        switch(periodUnit) {
//            case Minute:
//                return new Period().withMinutes(periodUnitMultiplier);
            case Week:
                return new Period().withWeeks(periodUnitMultiplier);
            case Day:
                return new Period().withDays(periodUnitMultiplier);
            case Month:
                return new Period().withMonths(periodUnitMultiplier);
        }
        return Period.ZERO;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    protected DateTime calculateStartTime(DateTime givenStartTime) {
        DateTime calculatedStartTime = givenStartTime == null ?
                new DateTime() :
                givenStartTime;

        calculatedStartTime = calculatedStartTime.plus(getPeriod());
        return new DateTime(calculatedStartTime);
    }

    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    public int getPeriodUnitMultiplier() {
        return periodUnitMultiplier;
    }

    public String toString() {
        String formattedSingleString = PlannerApplication.getInstance().getString(R.string.recurrence_single_display_string);
        String formattedPluralString = PlannerApplication.getInstance().getString(R.string.recurrence_multi_display_string);

        return periodUnitMultiplier > 1
                ? String.format(formattedPluralString, periodUnitMultiplier, periodUnit.toString())
                : String.format(formattedSingleString, periodUnit.toString());
    }

    public boolean containsAnotherIteration(DateTime due) {
        return endTime == null ||
                calculateNextIterationFromGivenDate(due).isBefore(endTime);
    }
}
