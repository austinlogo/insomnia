package northstar.planner.models.tables;

public class RecurrenceTargetTable extends BaseTable{
    public static final String TABLE_NAME = "Recurrence_Targets";
    public static final String TASK_ID = "TASK_ID";
//    public static final String START_TIME = "START_TIME";
//    public static final String END_TIME = "END_TIME";
//    public static final String PERIOD = "PERIOD";
//    public static final String PERIOD_UNIT = "PERIOD_UNIT";
    public static final String PERIOD_TARGET = "PERIOD_TARGET";
    public static final String TARGET_UNIT = "TARGET_UNIT";



    public static final String[] projection = {
//            _ID,
            TASK_ID,
//            START_TIME,
//            END_TIME,
//            PERIOD,
//            PERIOD_UNIT,
            PERIOD_TARGET,
            TARGET_UNIT
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME + " (" +
//            PRIMARY_KEY + ", " +
            TASK_ID + " INTEGER" + ", " +
//            TITLE_COLUMN + " TEXT, " +
//            START_TIME + " NUMERIC, " +
//            END_TIME + " NUMERIC, " +
//            PERIOD + " INTEGER, " +
//            PERIOD_UNIT +" INTEGER, " +
            PERIOD_TARGET + " INTEGER, " +
            TARGET_UNIT + " TEXT, " +
            "PRIMARY KEY (" + TASK_ID + ", " + PERIOD_TARGET +", " + TARGET_UNIT + ")" +
            ")";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
