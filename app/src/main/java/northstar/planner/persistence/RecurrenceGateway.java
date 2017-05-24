package northstar.planner.persistence;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import northstar.planner.models.Task;
import northstar.planner.models.tables.RecurrenceTable;

public class RecurrenceGateway extends BaseGateway {

    private final SQLiteDatabase db;

    public RecurrenceGateway(Context ctx) {
        new PlannerDBHelper(ctx);
        db = PlannerDBHelper.getDbInstance();
    }

    public boolean addRecurrenceRecord(Task task, int period, PeriodUnit unit) {
        ContentValues newValues = new ContentValues();

        newValues.put(RecurrenceTable.TASK_ID, task.getId() );
        newValues.put(RecurrenceTable.START_TIME, task.getSnooze().getTime());
        newValues.put(RecurrenceTable.UNIT, unit.toString());
        newValues.put(RecurrenceTable.PERIOD, period);

        long newId = db.insert(RecurrenceTable.TABLE_NAME, null, newValues);

        return newId > 0;
    }

    public boolean deleteRecurrence(long taskId) {
        String whereClause = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] whereArgs = { Long.toString(taskId) };

        int i = db.delete(RecurrenceTable.TABLE_NAME, whereClause, whereArgs);

        return i > 0;
    }
}
