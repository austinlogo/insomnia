package northstar.planner.presentation;

import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import northstar.planner.models.Goal;
import northstar.planner.presentation.Theme.ThemeFragment;
import northstar.planner.presentation.adapter.GoalRecyclerViewAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.adapter.ThemeRecyclerViewAdapter;
import northstar.planner.presentation.swipe.SortableListTouchHelperCallback;
import northstar.planner.presentation.swipe.TaskListTouchHelperCallback;

public abstract class BaseFragment extends Fragment {

    public BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }

    public void initRecyclerView(RecyclerView recView, ThemeRecyclerViewAdapter adapter) {
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SortableListTouchHelperCallback(adapter, getBaseActivity());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recView);
    }

    public void initRecyclerView(RecyclerView recView, List<Goal> goalList, ThemeFragment.ThemeFragmentListener activityListener) {
        recView.setHasFixedSize(false);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));

        GoalRecyclerViewAdapter adapter = new GoalRecyclerViewAdapter(goalList, activityListener);
        recView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SortableListTouchHelperCallback(adapter, getBaseActivity());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recView);
    }

    public void initRecyclerView(RecyclerView recView, TaskRecyclerViewAdapter adapter) {
        recView.setHasFixedSize(false);
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new TaskListTouchHelperCallback(adapter, getBaseActivity());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recView);
    }

    protected boolean undoPressed(int event) {
        return event == Snackbar.Callback.DISMISS_EVENT_ACTION;
    }
}
