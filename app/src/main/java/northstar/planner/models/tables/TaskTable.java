package northstar.planner.models.tables;

public class TaskTable extends BaseTable{
    public static final String TABLE_NAME = "Tasks";
    public static final String GOAL_COLUMN = "Goal";
    public static final String TASK_COMMITMENT_COLUMN = "Task_Commitment";
    public static final String COMPLETES_COLUMN = "Completes";
    public static final String DUE_COLUMN = "Due";
    public static final String REMINDER_COLUMN = "Reminder_Time";
    public static final String SNOOZE_COLUMN = "Snooze_Time";
    public static final String STATUS_COLUMN = "TaskStatus";
    public static final String SNOOZE_REMINDER_COLUMN = "SNOOZE_REMINDER_COLUMN";
    public static final String DUE_REMINDER_COLUMN = "DUE_REMINDER_COLUMN";

    public static final String[] projection = {
            _ID,
            GOAL_COLUMN,
            TITLE_COLUMN,
            TASK_COMMITMENT_COLUMN,
            COMPLETES_COLUMN,
            DUE_COLUMN,
            ORDER_COLUMN,
            STATUS_COLUMN
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME + " (" +
            PRIMARY_KEY + ", " +
            GOAL_COLUMN + " INTEGER, " +
            TITLE_COLUMN + " TEXT, " +
            TASK_COMMITMENT_COLUMN + " REAL, " +
            COMPLETES_COLUMN + " INTEGER, " +
            DUE_COLUMN + " NUMERIC, " +
            SNOOZE_COLUMN + " NUMERIC, " +
            REMINDER_COLUMN + " NUMERIC, " +
            ORDER_COLUMN + " INTEGER, " +
            SNOOZE_REMINDER_COLUMN + " INTEGER" +
            DUE_REMINDER_COLUMN + " INTEGER" +
            STATUS_COLUMN +" STATUS_COLUMN)";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
