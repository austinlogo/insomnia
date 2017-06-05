package northstar.planner.models;


import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;

import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.persistence.PeriodUnit;

public class Recurrence extends BaseModel implements Serializable {

    private long _id;
    private long taskId;
    private int periodUnitMultiplier;
    private Date startTime;
    private Date endTime;
    private PeriodUnit periodUnit;

//    public Recurrence(long taskId, int periodUnitMultiplier, PeriodUnit periodUnit, Date startTime, Date endTime) {
//        this(taskId, periodUnitMultiplier, periodUnit, startTime, endTime);
//    }

    public Recurrence(long taskId, int periodUnitMultiplier, PeriodUnit periodUnit, Date startTime, Date endTime) {
        this.taskId = taskId;
        this.periodUnitMultiplier = periodUnitMultiplier;
        this.periodUnit = periodUnit;
        this.startTime = calculateStartTime(startTime);
        this.endTime = endTime;
    }

    @Override
    public String getTitle() {
        return "";
    }

    public Recurrence(Cursor c) {
//        _id = getColumnLong(c, RecurrenceTable._ID);
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

    public Date calculateStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    private Date calculateStartTime(Date startTime) {
        return startTime == null ?
                new Date() :
                startTime;
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

        return formattedPluralString = periodUnitMultiplier > 1
                ? String.format(formattedPluralString, periodUnitMultiplier, periodUnit.toString())
                : String.format(formattedSingleString, periodUnit.toString());
    }
}
