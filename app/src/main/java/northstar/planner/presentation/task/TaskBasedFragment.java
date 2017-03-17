package northstar.planner.presentation.task;

import android.support.design.widget.Snackbar;
import android.view.View;

import northstar.planner.R;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;

public abstract class TaskBasedFragment extends BaseFragment {

    protected abstract void updateMetric(Metric sc);
    public abstract void setActionButtonVisibility(boolean isVisible);

    protected TaskRecyclerViewAdapter taskListAdapter;

    protected void removeItemWorkflow(final Task item, final int position) {

        Snackbar.make(getBaseActivity().getRootView(), R.string.deletedItem, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (getBaseActivity() == null) {
                            return;
                        } else if (undoPressed(event)) {
                            taskListAdapter.undoDeletion(item, position);
                        } else {
                            getBaseActivity().removeFromDb(item);
                            getBaseActivity().cancelNotification(item);
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }
}
