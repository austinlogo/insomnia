package northstar.planner.models;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.tables.GoalTable;

public class Goal extends BaseModel {
    private long theme;
    private String title;
    private String description;
    private List<SuccessCriteria> successCriterias;
    private List<Task> tasks;

    public Goal(Cursor c) {
        super(c);
        theme = getColumnInt(c, GoalTable.THEME_COLUMN);
        title = getColumnString(c, GoalTable.TITLE_COLUMN);
        description = getColumnString(c, GoalTable.DESCRIPTION_COLUMN);
        initLists();
    }

    public Goal(Bundle b) {
        _id = b.getLong(GoalTable._ID);
        theme = b.getLong(GoalTable.THEME_COLUMN);
        title = b.getString(GoalTable.TITLE_COLUMN);
        initLists();
    }

    public Goal(long themeId, String title) {
        theme = themeId;
        this.title = title;
        initLists();
    }

    public void initLists() {
        successCriterias = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    public void updateGoal(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public long getTheme() {
        return theme;
    }

    public void setTheme(long themeId) {
        theme = themeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        _id = id;
    }

    public List<SuccessCriteria> getSuccessCriterias() {
        return successCriterias;
    }

    public void setSuccessCriterias(List<SuccessCriteria> successCriterias) {
        this.successCriterias = successCriterias;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addSuccessCriteria(SuccessCriteria sc) {
        successCriterias.add(0, sc);
    }
}
