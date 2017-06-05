package northstar.planner.models.tables;

public class RecurrenceTable extends BaseTable{
    public static final String TABLE_NAME = "Recurrence";
    public static final String TASK_ID = "TASK_ID";
    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";
    public static final String PERIOD = "PERIOD";
    public static final String PERIOD_UNIT = "UNIT";


    public static final String[] projection = {
//            _ID,
            TASK_ID,
            START_TIME,
            END_TIME,
            PERIOD,
            PERIOD_UNIT
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME + " (" +
//            PRIMARY_KEY + ", " +
            TASK_ID + " INTEGER" + ", " +
            TITLE_COLUMN + " TEXT, " +
            START_TIME + " NUMERIC, " +
            END_TIME + " NUMERIC, " +
            PERIOD + " INTEGER, " +
            PERIOD_UNIT +" STATUS_COLUMN, " +
            "PRIMARY KEY (" + TASK_ID + ")" +
            ")";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
