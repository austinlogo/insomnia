package northstar.planner.persistence;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import northstar.planner.models.Recurrence;
import northstar.planner.models.Task;
import northstar.planner.models.tables.RecurrenceTable;

public class RecurrenceGateway extends BaseGateway {

    private final SQLiteDatabase db;

    public RecurrenceGateway(Context ctx) {
        db = PlannerDBHelper.getDbInstance();
    }

    public Recurrence addRecurrenceRecord(Recurrence rec) {
        ContentValues newValues = new ContentValues();

        newValues.put(RecurrenceTable.TASK_ID, rec.getTaskId() );
        newValues.put(RecurrenceTable.START_TIME, rec.calculateStartTime().getTime());
        newValues.put(RecurrenceTable.PERIOD_UNIT, rec.getPeriodUnit().toString());
        newValues.put(RecurrenceTable.PERIOD, rec.getPeriodUnitMultiplier());

        long newId = db.insertWithOnConflict(RecurrenceTable.TABLE_NAME, null, newValues, SQLiteDatabase.CONFLICT_REPLACE);

        rec.set_id(newId);

        return rec;
    }

    public Recurrence getRecurrence(Task task) {
        String selection = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(task.getId()) };
        Cursor c = db.query(RecurrenceTable.TABLE_NAME, RecurrenceTable.projection, selection, selectionArgs, null, null, null);

        return constructRecurrenceFromCursor(c);
    }

    private Recurrence constructRecurrenceFromCursor (Cursor c) {
        c.moveToFirst();
        if (c.isAfterLast()) {
            return null;
        }

        return new Recurrence(c);
    }

    public void removeRecurrenceRecord(Task item) {
        removeReccurenceRecord(item.getId());
    }

    public void removeReccurenceRecord(long taskId) {
        String whereClause = RecurrenceTable.TASK_ID + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(taskId)};
        db.delete(RecurrenceTable.TABLE_NAME, whereClause, whereArgs);
    }

//    private void setRecurrenceAlarm(Recurrence recurrence) {
//
//        long unitInMillis = PeriodUnit.unitToMillis(recurrence.getPeriodUnit());
//        long interval = recurrence.getPeriodUnitMultiplier() * unitInMillis;
//
//
////        AlarmManager alarmManager = (AlarmManager) PlannerApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
////        alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime(), mPendingIntent);
//
//
//    }
//

//
//    public boolean deleteRecurrence(long taskId) {
//        String whereClause = RecurrenceTable.TASK_ID + EQUALSQ;
//        String[] whereArgs = { Long.toString(taskId) };
//
//        int i = db.delete(RecurrenceTable.TABLE_NAME, whereClause, whereArgs);
//
//        return i > 0;
//    }
}
