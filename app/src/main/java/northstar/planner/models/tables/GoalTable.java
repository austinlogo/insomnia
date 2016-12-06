package northstar.planner.models.tables;

public class GoalTable extends BaseTable {
    public static final String TABLE_NAME = "Goals";
    public static final String THEME_COLUMN = "Theme";
    public static final String DESCRIPTION_COLUMN = "Description";

    public static final String[] projection = {
        _ID,
        THEME_COLUMN,
        TITLE_COLUMN,
        DESCRIPTION_COLUMN,
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT + TABLE_NAME + " (" +
            PRIMARY_KEY + ", " +
            THEME_COLUMN + " INTEGER, " +
            TITLE_COLUMN + " TEXT, " +
            ORDER_COLUMN + " INTEGER, " +
            DESCRIPTION_COLUMN + " TEXT)";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
