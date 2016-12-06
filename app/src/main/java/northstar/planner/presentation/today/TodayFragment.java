package northstar.planner.presentation.today;

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
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.goal.GoalFragment;

public class TodayFragment
        extends BaseFragment
        implements TextView.OnEditorActionListener, GoalFragment.GoalFragmentListener {

    @BindView(R.id.fragment_today_add)
    EditText addTask;

    @BindView(R.id.fragment_today_tasks)
    RecyclerView taskList;

    private TaskRecyclerViewAdapter taskRecyclerViewAdapter;

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this, v);

        taskRecyclerViewAdapter = new TaskRecyclerViewAdapter(new ArrayList<Task>(), this);
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
            taskRecyclerViewAdapter.addItem(newTask);
            v.setText("");
        }
        return false;
    }

    @Override
    public SuccessCriteria addSuccessCriteria(SuccessCriteria sc) {
        return null;
    }

    @Override
    public void openTask(Task t) {

    }

    @Override
    public void removeTask(int position, Task t) {

    }

    @Override
    public void completeTask(Task t) {

    }

    @Override
    public void createTask(String newTask, SuccessCriteriaListAdapter adapter) {

    }
}
