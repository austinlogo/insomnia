package northstar.planner.persistence;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import northstar.planner.models.Recurrence;
import northstar.planner.models.Task;
import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.models.tables.RecurrenceTargetTable;

public class RecurrenceGateway extends BaseGateway {

    private final SQLiteDatabase db;

    public RecurrenceGateway(Context ctx) {
        db = PlannerDBHelper.getDbInstance();
    }

    public void addRecurrenceRecord(Recurrence recurrenceToBeSaved) {
        addOrUpdateSimpleRecurrence(recurrenceToBeSaved);
        addOrUpdateComplexRecurrence(recurrenceToBeSaved);
    }

    private void addOrUpdateSimpleRecurrence(Recurrence rec) {
        ContentValues newValues = new ContentValues();

        if (rec.getEndTime() != null) {
            newValues.put(RecurrenceTable.END_TIME, rec.getEndTime().getMillis());
        }

        newValues.put(RecurrenceTable.TASK_ID, rec.getTaskId() );
        newValues.put(RecurrenceTable.PERIOD_UNIT, rec.getPeriodUnit().toString());
        newValues.put(RecurrenceTable.PERIOD, rec.getPeriodUnitMultiplier());

        db.insertWithOnConflict(RecurrenceTable.TABLE_NAME, null, newValues, SQLiteDatabase.CONFLICT_REPLACE);
    }


    private void addOrUpdateComplexRecurrence(Recurrence updatedRecurrence) {
        removeRecurrenceTargetRecords(updatedRecurrence.getTaskId());

        if (updatedRecurrence.getTargets().isEmpty()) {
            return;
        }


        for (PeriodTarget target : updatedRecurrence.getTargets()) {
            ContentValues cv = new ContentValues();
            cv.put(RecurrenceTargetTable.TASK_ID, updatedRecurrence.getTaskId());
            cv.put(RecurrenceTargetTable.PERIOD_TARGET, target.getTargetValue());
            cv.put(RecurrenceTargetTable.TARGET_UNIT, updatedRecurrence.getTargetUnit().getValue());
            db.insertWithOnConflict(RecurrenceTargetTable.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public Recurrence getRecurrence(Task task) {
        String selection = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(task.getId()) };
//
//        String formattedRecurrenceQuery = "SELECT * FROM %1$s rec LEFT JOIN %2$s targets ON rec.%3$s = targets.%4$s WHERE %3$s = %5$d";
//        String query = String.format(formattedRecurrenceQuery,
//                RecurrenceTable.TABLE_NAME,
//                RecurrenceTargetTable.TABLE_NAME,
//                RecurrenceTable.TASK_ID,
//                RecurrenceTargetTable.TASK_ID,
//                task.getId());

        Cursor c = db.query(RecurrenceTable.TABLE_NAME, RecurrenceTable.projection, selection, selectionArgs, null, null, null);

//        Cursor c = db.rawQuery(formattedRecurrenceQuery, null);

        return constructRecurrenceFromCursor(c, task.getId());
    }

    public Cursor getPeriodTargetsCursorFromDb(long taskId) {
        String selection = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(taskId) };

        Cursor c = db.query(RecurrenceTargetTable.TABLE_NAME, RecurrenceTargetTable.projection, selection, selectionArgs, null, null, null);

        List<PeriodTarget> periodTargets;
        c.moveToFirst();
        if (c.isAfterLast()) {
            return null;
        }

        return c;
    }

    public void removeRecurrenceTargetRecords(long id) {
        String whereClause = RecurrenceTargetTable.TASK_ID + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(id)};
        db.delete(RecurrenceTargetTable.TABLE_NAME, whereClause, whereArgs);
    }

    private Recurrence constructRecurrenceFromCursor (Cursor recurrenceTableCursor, long taskId) {
        recurrenceTableCursor.moveToFirst();
        if (recurrenceTableCursor.isAfterLast()) {
            return null;
        }

        Cursor recurrenceTableTargetsCursor = getPeriodTargetsCursorFromDb(taskId);

        return Recurrence.newInstance(recurrenceTableCursor, recurrenceTableTargetsCursor);
    }

    public void removeRecurrenceRecord(Task item) {
        removeReccurenceRecord(item.getId());
    }

    public void removeReccurenceRecord(long taskId) {
        String whereClause = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(taskId)};
        db.delete(RecurrenceTable.TABLE_NAME, whereClause, whereArgs);
    }
}
