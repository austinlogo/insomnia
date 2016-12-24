package northstar.planner.models.tables;

public class MetricTable extends BaseTable{
    public static final String TABLE_NAME = "Success_Criteria";
    public static final String GOAL_COLUMN = "Goal";
    public static final String PROGRESS_COLUMN = "Progress";
    public static final String COMMITTED_COLUMN = "Commited";

    public static final String[] projection = {
            _ID,
            GOAL_COLUMN,
            TITLE_COLUMN,
            PROGRESS_COLUMN,
            COMMITTED_COLUMN
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME + " ( " +
            PRIMARY_KEY + ", " +
            GOAL_COLUMN + " INTEGER, " +
            TITLE_COLUMN + " TEXT, " +
            PROGRESS_COLUMN + " REAL, " +
            ORDER_COLUMN + " INTEGER, " +
            COMMITTED_COLUMN + " REAL)";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
