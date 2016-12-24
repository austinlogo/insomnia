package northstar.planner.presentation.today;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TodayFragment
        extends TaskBasedFragment {

    @BindView(R.id.fragment_today_tasks)
    RecyclerView taskList;

//    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;
    GoalFragment.TaskActionListener activityListener;
    TodayActivityListener todayActivityListener;

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, v);

        taskListAdapter = new TaskRecyclerViewAdapter(new ArrayList<Task>(), activityListener);
        initRecyclerView(taskList, taskListAdapter);

//        addTask.setOnEditorActionListener(this);
        return v;
    }

    public void initViews(List<Task> scratchTasks) {
        taskListAdapter.updateList(scratchTasks);
    }

//    @Override
//    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
//        if (actionId == EditorInfo.IME_ACTION_DONE) {
//            todayActivityListener.openAddTaskWorkflow(v.getText().toString());
//            v.setText("");
//            getBaseActivity().hideKeyboard();
//        }
//        return false;
//    }

    @OnClick(R.id.fragment_today_add_fab)
    public void onClick() {
        todayActivityListener.openAddTaskWorkflow("");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (GoalFragment.TaskActionListener) activity;
        todayActivityListener = (TodayActivityListener) activity;
    }

    @Override protected void updateMetric(Metric sc) { /* NOOP */ }

    public interface TodayActivityListener {
        void openAddTaskWorkflow(String newTaskTitle);
    }
}
