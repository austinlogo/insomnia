package northstar.planner.models;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import northstar.planner.models.tables.GoalTable;

public class Goal extends BaseModel {
    private long theme;
    private String title;
    private String description;
    private List<Metric> metrics;
    private Map<Long, Metric> successCriteriaMap;
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

    public Goal(long themeId, String title, String description) {
        theme = themeId;
        this.title = title;
        this.description = description;
        initLists();
    }

    public void initLists() {
        metrics = new ArrayList<>();
        successCriteriaMap = new HashMap<>();
        tasks = new ArrayList<>();
    }

    public void updateGoal(Goal newGoalValues) {
        this.title = newGoalValues.getTitle();
        this.description = newGoalValues.getDescription();
        this.tasks = newGoalValues.getTasks();
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

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addMetric(Metric sc) {
        metrics.add(0, sc);
    }

    public void setChildren(List<Metric> metrics, List<Task> tasksByGoalId) {
        this.metrics = metrics;

        for (Metric sc : metrics) {
            successCriteriaMap.put(sc.getId(), sc);
        }

        for (Task task : tasksByGoalId) {
            Metric completes = successCriteriaMap.get(task.getCompletes());
            task.setMetric(completes);
            this.tasks.add(task);
        }
    }
}
