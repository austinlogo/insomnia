package northstar.planner.models;

import android.database.Cursor;
import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.tables.ThemeTable;

public class Theme extends BaseModel {
    private String title;
    private String description;
    private List<Goal> goals;
    private boolean newTheme;

    public Theme(long id, String title, String description) {
        _id = id;
        this.title = title;
        this.description = description;
        this.goals = new ArrayList<>();
    }


    public Theme(Bundle bundle) {

        if (bundle != null) {
            _id = bundle.getLong(ThemeTable._ID);
            title = bundle.getString(ThemeTable.TITLE_COLUMN);
            description = bundle.getString(ThemeTable.DESCRIPTION_COLUMN);
        } else {
            _id = NEW_ID;
        }

        this.goals = new ArrayList<>();
    }

    public Theme() {
        this("", "");
    }

    public Theme(String title, String description) {
        this(NEW_ID, title, description);
    }


    public void updateTheme(Theme newThemeValues) {
        this.title = newThemeValues.getTitle();
        this.description = newThemeValues.getDescription();
        this.goals = new ArrayList<>();
    }

    public Theme(Cursor c) {
        super(c);
        title = getColumnString(c, ThemeTable.TITLE_COLUMN);
        description = getColumnString(c, ThemeTable.DESCRIPTION_COLUMN);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }
}
