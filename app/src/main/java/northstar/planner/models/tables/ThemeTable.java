package northstar.planner.models.tables;

public class ThemeTable extends BaseTable {
    public static final String TITLE_COLUMN = "Title";
    public static final String TABLE_NAME = "Themes";
    public static final String DESCRIPTION_COLUMN = "Description";

    public static final String[] projection = {
            _ID,
            TITLE_COLUMN,
            ORDER_COLUMN,
            DESCRIPTION_COLUMN
    };

    public static final String[] shallowProjection = {
            _ID,
            TITLE_COLUMN
    };

    public static final String SQL_CREATE_QUERY =
             CREATE_STATEMENT +
             TABLE_NAME  + " (" +
             PRIMARY_KEY + ", " +
             TITLE_COLUMN + " TEXT, " +
             ORDER_COLUMN + " INTEGER, " +
             DESCRIPTION_COLUMN + " TEXT)";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
