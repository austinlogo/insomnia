package northstar.planner.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import northstar.planner.models.tables.ActiveHoursTable;
import northstar.planner.models.tables.DependencyTable;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.MetricTable;
import northstar.planner.models.tables.RecurrenceTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;

public class PlannerDBHelper extends SQLiteOpenHelper {

    public static final String DB_FILENAME = "PlannerApplication.db";
    private static final int DB_VERSION = 1;
    public static int newInstall = 0;
    private static PlannerDBHelper instance;

    public PlannerDBHelper(Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
        instance = this;
        onCreate(getWritableDatabase());
    }

    public static SQLiteDatabase getDbInstance() {
        return instance == null
                ? null
                : instance.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        newInstall += doesTableExists(ThemeTable.TABLE_NAME, db) ? 0 : 1;

//        db.execSQL(RecurrenceTable.getDropTableQuery());

        db.execSQL(ThemeTable.SQL_CREATE_QUERY);
        db.execSQL(GoalTable.SQL_CREATE_QUERY);
        db.execSQL(TaskTable.SQL_CREATE_QUERY);
        db.execSQL(RecurrenceTable.SQL_CREATE_QUERY);
        db.execSQL(MetricTable.SQL_CREATE_QUERY);
        db.execSQL(ActiveHoursTable.SQL_CREATE_QUERY);
        db.execSQL(DependencyTable.SQL_CREATE_QUERY);
    }

    public static void clearAll(SQLiteDatabase db) {

        db.execSQL(ThemeTable.getDropTableQuery());
        db.execSQL(GoalTable.getDropTableQuery());
        db.execSQL(TaskTable.getDropTableQuery());
        db.execSQL(RecurrenceTable.getDropTableQuery());
        db.execSQL(MetricTable.getDropTableQuery());
        db.execSQL(MetricTable.getDropTableQuery());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearAll(db);
        onCreate(db);
    }

    private boolean doesTableExists(String tableName, SQLiteDatabase mDatabase) {


        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
