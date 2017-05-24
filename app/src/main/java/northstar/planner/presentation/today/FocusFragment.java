package northstar.planner.presentation.today;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.task.TaskBasedFragment;

public class FocusFragment
        extends TaskBasedFragment {

    @BindView(R.id.fragment_today_tasks)
    RecyclerView taskList;

    @BindView(R.id.fragment_today_add_fab)
    FloatingActionButton addFab;

    @BindView(R.id.fragment_focus_lonely_container)
    RelativeLayout lonelyImageContainer;

//    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;
    GoalFragment.TaskActionListener activityListener;
    FocusActivityListener todayActivityListener;

    public static FocusFragment newInstance() {
        FocusFragment fragment = new FocusFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_focus, container, false);
        ButterKnife.bind(this, v);

        taskListAdapter = new TaskRecyclerViewAdapter(new ArrayList<Task>(), activityListener);
        initRecyclerView(taskList, taskListAdapter);

        return v;
    }

    public void initViews(List<Task> scratchTasks) {
        taskListAdapter.updateList(scratchTasks);
    }

    @OnClick(R.id.fragment_today_add_fab)
    public void onClick() {
        todayActivityListener.openAddTaskWorkflow();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (GoalFragment.TaskActionListener) activity;
        todayActivityListener = (FocusActivityListener) activity;
    }

    @Override protected void updateMetricOnUI(Metric sc) { /* NOOP */ }

    @Override
    public void setActionButtonVisibility(boolean isVisible) {
        addFab.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void showLonelyImage(boolean show) {
        lonelyImageContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public interface FocusActivityListener {
        void openAddTaskWorkflow();
    }
}
