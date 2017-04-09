package northstar.planner.models.tables;

public class DependencyTable extends BaseTable{
    public static final String TABLE_NAME = "Dependency_Table";
    public static final String DEPENDS_ON = "DEPENDS_ON";
    public static final String DEPENDENCY_STATUS = "DEPENDENCY_STATUS";

    public static final String[] projection = {
            _ID,
            DEPENDS_ON,
            DEPENDENCY_STATUS
    };

    public static final String SQL_CREATE_QUERY =
            CREATE_STATEMENT +
            TABLE_NAME  + " (" +
            PRIMARY_KEY + ", " +
            DEPENDS_ON + " INTEGER, " +
            DEPENDENCY_STATUS + " TEXT)";

    public static String getDropTableQuery() {
        return DROP_TABLE + TABLE_NAME;
    }
}
