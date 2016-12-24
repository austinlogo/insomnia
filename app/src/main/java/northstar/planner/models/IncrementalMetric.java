package northstar.planner.models;

import android.database.Cursor;


public class IncrementalMetric extends Metric {

    public IncrementalMetric(Cursor c) {
        super(c);
        setType();
    }

    public IncrementalMetric(String newSuccessCriteriaTitle, int committed) {
        super(newSuccessCriteriaTitle, committed);
        setType();
    }

    @Override
    public boolean needsCommitmentInput() {
        return true;
    }

    @Override
    public void updateProgress(double taskCommitment) {
        progress = Math.min(taskCommitment + progress, committed);
    }

    @Override
    protected void setType() {
        metricType = MetricType.INCREMENTAL;
    }

    @Override
    public String getProgressString() {
        return (int) progress + " / " + committed;
    }

    @Override
    public void adjustProgress() {
        //NOOP
    }


}
