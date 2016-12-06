package northstar.planner.models;

import android.database.Cursor;

import northstar.planner.models.tables.SuccessCriteriaTable;
import northstar.planner.models.tables.TaskTable;

public class SuccessCriteria extends BaseModel{
    private long goal;
    private String title;
    private double progress;
    private int committed;

    public SuccessCriteria(Cursor c) {
        super(c);
        title = getColumnString(c, TaskTable.TITLE_COLUMN);
        goal = getColumnLong(c, TaskTable.GOAL_COLUMN);
        progress = getColumnDouble(c, SuccessCriteriaTable.PROGRESS_COLUMN);
        committed = getColumnInt(c, SuccessCriteriaTable.COMMITTED_COLUMN);
    }

    public SuccessCriteria(String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        goal = NEW_ID;
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

    public int getCommitted() {
        return committed;
    }

    public void setGoal(Goal goal) {
        this.goal = goal.getId();
    }

    public String getProgressString() {
        return (int) progress + " / " + committed;
    }

    public void updateProgress(double taskCommitment) {
        progress = Math.min(taskCommitment + progress, committed);
    }
}
