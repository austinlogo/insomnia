package northstar.planner.models.tables;

import android.provider.BaseColumns;

public abstract class BaseTable implements BaseColumns {
    public static final String TITLE_COLUMN = "Title";
    protected static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS ";
    static final String PRIMARY_KEY = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT ";
}
