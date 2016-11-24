package northstar.planner.models;

import android.database.Cursor;

import northstar.planner.models.tables.SuccessCriteriaTable;
import northstar.planner.models.tables.TaskTable;

public class SuccessCriteria extends BaseModel{
    private long goal;
    private String title;
    private double progress;
    private double committed;

    public SuccessCriteria(Cursor c) {
        super(c);
        title = getColumnString(c, TaskTable.TITLE_COLUMN);
        goal = getColumnLong(c, TaskTable.GOAL_COLUMN);
        progress = getColumnDouble(c, SuccessCriteriaTable.PROGRESS_COLUMN);
        committed = getColumnDouble(c, SuccessCriteriaTable.COMMITTED_COLUMN);
    }

    public SuccessCriteria(Goal currentGoal, String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        goal = currentGoal.getId();
        title = newSuccessCriteriaTitle;
        progress = 0;
        committed = newSuccessCriteriaCommitted;
    }

    public long getGoal() {
        return goal;
    }

    public String getTitle() {
        return title;
    }

    public double getProgress() {
        return progress;
    }

    public double getCommitted() {
        return committed;
    }

    public void setId(long id) {
        _id = id;
    }
}
