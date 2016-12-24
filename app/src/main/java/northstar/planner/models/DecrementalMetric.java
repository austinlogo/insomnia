package northstar.planner.models;

import android.database.Cursor;

public class DecrementalMetric extends Metric{

    public DecrementalMetric(Cursor c) {
        super(c);
        setType();
    }

    @Override
    public boolean needsCommitmentInput() {
        return false;
    }

    public DecrementalMetric(String newSuccessCriteriaTitle) {
        super(newSuccessCriteriaTitle, 0);
        setType();
    }

    @Override
    public String getProgressString() {
        return "" + (int) progress;
    }

    @Override
    public void adjustProgress() {
        progress++;
    }

    public void updateProgress(double taskCommitment) {
        progress = Math.max(progress - taskCommitment, committed);
    }

    @Override
    protected void setType() {
        metricType = MetricType.DECREMENTAL;
    }
}
