package northstar.planner.models;

import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;

import northstar.planner.models.tables.BaseTable;

public abstract class BaseModel implements Serializable {
    protected long _id;
    public static final long NEW_ID = -1;
    public static final long SCRATCH_ID = -2;

    public BaseModel() {
    }

    public BaseModel(Cursor c) {
        _id = getColumnLong(c, BaseTable._ID);
    }

    public long getId() {
        return _id;
    }

    protected long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    protected String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    protected int getColumnInt(Cursor cursor, String columnName) {
        try {
            return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
        } catch (Exception e) {
            return -1;
        }
    }

    protected double getColumnDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    protected Date getColumnDate(Cursor cursor, String columnName) {
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
        return date == 0
                ? null
                : new Date(date);
    }



    public boolean isNew() {
        return _id == NEW_ID;
    }

    public boolean equals(BaseModel model) {
        return model._id == this._id;
    }

    public void setId(long id) {
        _id = id;
    }

}
