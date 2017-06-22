package northstar.planner.models;


import android.database.Cursor;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.persistence.PeriodTarget;
import northstar.planner.persistence.PeriodUnit;
import northstar.planner.persistence.TargetUnit;

public class SimpleRecurrence extends Recurrence {

    public SimpleRecurrence(Cursor c) {
        taskId = getColumnLong(c, RecurrenceTable.TASK_ID);
        periodUnitMultiplier = getColumnInt(c, RecurrenceTable.PERIOD);
        startTime = getColumnDate(c, RecurrenceTable.START_TIME);
        endTime = getColumnDate(c, RecurrenceTable.END_TIME);
        periodUnit = PeriodUnit.valueOf(getColumnString(c, RecurrenceTable.PERIOD_UNIT));
    }

    public SimpleRecurrence(long id, int period, PeriodUnit selectedItem, DateTime due, DateTime toDate) {
        super(id, period, selectedItem, due, toDate);
    }

    @Override
    public List<PeriodTarget> getTargets() {
        return new ArrayList<>();
    }

    @Override
    public TargetUnit getTargetUnit() {
        return null;
    }

    @Override
    public DateTime calculateNextIterationFromGivenDate(DateTime dateTime) {
        return dateTime.plus(getPeriod());
    }
}
