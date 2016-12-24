package northstar.planner.models;

import android.database.Cursor;

import northstar.planner.models.tables.MetricTable;
import northstar.planner.models.tables.TaskTable;

public abstract class Metric extends BaseModel{
    private long goal;
    private String title;
    protected double progress;
    protected int committed;
    protected MetricType metricType;

    public abstract String getProgressString();
    public abstract void adjustProgress();
    public abstract boolean needsCommitmentInput();
    public abstract void updateProgress(double taskCommitment);
    protected abstract void setType();

    public static Metric newInstance(Cursor c) {
        int gettingCommitted = c.getInt(c.getColumnIndexOrThrow(MetricTable.COMMITTED_COLUMN));

        if (gettingCommitted == 0) {
            return new DecrementalMetric(c);
        } else return new IncrementalMetric(c);
    }

    public static Metric newInstance(String title, int committed) {
        if (committed == 0) {
            return new DecrementalMetric(title);
        } else {
            return new IncrementalMetric(title, committed);
        }
    }

    public Metric(Cursor c) {
        super(c);
        title = getColumnString(c, TaskTable.TITLE_COLUMN);
        goal = getColumnLong(c, TaskTable.GOAL_COLUMN);
        progress = getColumnDouble(c, MetricTable.PROGRESS_COLUMN);
        committed = getColumnInt(c, MetricTable.COMMITTED_COLUMN);
    }

    public Metric(String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        _id = NEW_ID;
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
}
