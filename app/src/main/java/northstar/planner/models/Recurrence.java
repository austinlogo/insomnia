package northstar.planner.models;


import android.database.Cursor;

import org.joda.time.DateTime;

import java.io.Serializable;


import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.persistence.PeriodUnit;

public class Recurrence extends BaseModel implements Serializable {

    private long _id;
    private long taskId;
    private int periodUnitMultiplier;
    private DateTime startTime;
    private DateTime endTime;
    private PeriodUnit periodUnit;

//    public Recurrence(long taskId, int periodUnitMultiplier, PeriodUnit periodUnit, Date calculateStartTime, Date endTime) {
//        this(taskId, periodUnitMultiplier, periodUnit, calculateStartTime, endTime);
//    }

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

    public Recurrence(Cursor c) {
        taskId = getColumnLong(c, RecurrenceTable.TASK_ID);
        periodUnitMultiplier = getColumnInt(c, RecurrenceTable.PERIOD);
        startTime = getColumnDate(c, RecurrenceTable.START_TIME);
        endTime = getColumnDate(c, RecurrenceTable.END_TIME);
        periodUnit = PeriodUnit.valueOf(getColumnString(c, RecurrenceTable.PERIOD_UNIT));
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getPeriod() {
        return periodUnitMultiplier * PeriodUnit.unitToMillis(periodUnit);
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    private DateTime calculateStartTime(DateTime startTime) {
        long startTimeLong = startTime == null ?
                new DateTime().getMillis() :
                startTime.getMillis();

        startTimeLong += getPeriod();
        return new DateTime(startTimeLong);
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
}
