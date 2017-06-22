package northstar.planner.models;


import android.database.Cursor;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.models.tables.RecurrenceTargetTable;
import northstar.planner.persistence.PeriodTarget;
import northstar.planner.persistence.PeriodUnit;
import northstar.planner.persistence.TargetUnit;

public class ComplexRecurrence extends Recurrence {

    private List<PeriodTarget> periodTargets;
    private TargetUnit targetUnit;

    public ComplexRecurrence(
            long taskId,
            int periodUnitMultiplier,
            PeriodUnit periodUnit, DateTime
                    startTime, DateTime endTime,
            List<PeriodTarget> periodTargets,
            TargetUnit targetUnit) {
        super(taskId, periodUnitMultiplier, periodUnit, startTime, endTime);

        this.periodTargets = periodTargets;
        this.targetUnit = targetUnit;
    }

    public ComplexRecurrence(Cursor recurrenceTableCursor, Cursor periodTargetsCursor) {
        taskId = getColumnLong(recurrenceTableCursor, RecurrenceTable.TASK_ID);
        periodUnitMultiplier = getColumnInt(recurrenceTableCursor, RecurrenceTable.PERIOD);
        startTime = getColumnDate(recurrenceTableCursor, RecurrenceTable.START_TIME);
        endTime = getColumnDate(recurrenceTableCursor, RecurrenceTable.END_TIME);
        periodUnit = PeriodUnit.valueOf(getColumnString(recurrenceTableCursor, RecurrenceTable.PERIOD_UNIT));
        targetUnit = TargetUnit.valueOf(getColumnString(periodTargetsCursor, RecurrenceTargetTable.TARGET_UNIT));

        periodTargets = new ArrayList<>();
        while (!periodTargetsCursor.isAfterLast()) {
            periodTargets.add(PeriodTarget.getEnum(getColumnInt(periodTargetsCursor, RecurrenceTargetTable.PERIOD_TARGET)));
            periodTargetsCursor.moveToNext();
        }
    }

    @Override
    public List<PeriodTarget> getTargets() {
        return periodTargets;
    }

    @Override
    public TargetUnit getTargetUnit() {
        return targetUnit;
    }

    @Override
    public DateTime calculateNextIterationFromGivenDate(DateTime currentIterationDueDate) {
        PeriodTarget firstTarget = periodTargets.get(0);
        DateTime result = calculateNextIterationFromGivenDate_Weekly(currentIterationDueDate, firstTarget);

        for (int i = 1; i < periodTargets.size(); i++) {
            PeriodTarget target = periodTargets.get(i);
            if (targetUnit.equals(TargetUnit.WEEKDAY)) {
                DateTime contender = calculateNextIterationFromGivenDate_Weekly(currentIterationDueDate, target);
                result = result.isAfter(contender)
                        ? contender
                        : result;
            }
        }
        return result;
    }

    private DateTime calculateNextIterationFromGivenDate_Weekly(DateTime givenDate, PeriodTarget target) {
        final int daysInaWeek = 7;
        int currentDayOfWeek = givenDate.getDayOfWeek();
        int nextIterationDayDifference = (target.getTargetValue() - currentDayOfWeek) % daysInaWeek;
        DateTime nextOccurrenceDayOfWeek = givenDate.plusDays(nextIterationDayDifference);

        if (target.getTargetValue() <= currentDayOfWeek) {
            return nextOccurrenceDayOfWeek.plus(getPeriod());
        } else {
            return nextOccurrenceDayOfWeek;
        }
    }
}
