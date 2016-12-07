package northstar.planner.presentation.today;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.TaskStatus;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.task.TaskBasedFragment;

public class TodayFragment
        extends TaskBasedFragment
        implements TextView.OnEditorActionListener {

    @BindView(R.id.fragment_today_add)
    EditText addTask;

    @BindView(R.id.fragment_today_tasks)
    RecyclerView taskList;

    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;
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

        taskRecyclerViewAdapter = new TaskRecyclerViewAdapter(new ArrayList<Task>(), activityListener);
        initRecyclerView(taskList, taskRecyclerViewAdapter);

        addTask.setOnEditorActionListener(this);
        return v;
    }

    public void initViews(List<Task> scratchTasks) {
        taskRecyclerViewAdapter.updateList(scratchTasks);
    }

    @Override
    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Task newTask = new Task(BaseModel.SCRATCH_ID, v.getText().toString());
            newTask.setTaskStatus(TaskStatus.IN_PROGRESS);
            taskRecyclerViewAdapter.addItem(newTask);
            todayActivityListener.addTask(newTask);
            v.setText("");
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (GoalFragment.TaskActionListener) activity;
        todayActivityListener = (TodayActivityListener) activity;
    }

    @Override protected void updateSuccessCriteria(SuccessCriteria sc) { /* NOOP */ }

    public interface TodayActivityListener {
        void addTask(Task newTask);
    }
}
