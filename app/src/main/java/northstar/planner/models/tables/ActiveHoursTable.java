package northstar.planner.models.tables;

public class ActiveHoursTable extends BaseTable {
    public static final String TABLE_NAME = "ACTIVE_HOURS";
    public static final String THEME_COLUMN = "Theme";
    public static final String DAY_COLUMN = "Day_OF_WEEK";
    public static final String START_COLUMN = "Start";
    public static final String END_COLUMN = "End";

    public static final String[] projection = {
            THEME_COLUMN,
            DAY_COLUMN,
            START_COLUMN,
            END_COLUMN
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME  + " (" +
            THEME_COLUMN + " INTEGER, " +
            DAY_COLUMN + " INTEGER, " +
            START_COLUMN + " INTEGER, " +
            END_COLUMN + " INTEGER, " +
            "PRIMARY KEY (" + THEME_COLUMN + "," + DAY_COLUMN + "))";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
