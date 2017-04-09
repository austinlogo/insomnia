package northstar.planner.presentation.goal;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemLongClick;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.presentation.adapter.MetricsListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.task.TaskBasedFragment;

public class GoalFragment
        extends TaskBasedFragment {

    @BindView(R.id.fragment_goal_title_container)
    LinearLayout titleContainer;

    @BindView(R.id.fragment_goal_title)
    EditText editTitle;

    @BindView(R.id.fragment_goal_description)
    EditText editDescription;

    @BindView(R.id.fragment_goal_add_fab)
    FloatingActionButton addFab;

    @BindView(R.id.fragment_goal_success_criteria)
    ListView metrics;

    @BindView(R.id.fragment_goal_tasks)
    RecyclerView tasksRecyclerView;

    @BindView(R.id.fragment_goal_metric_header_container)
    RelativeLayout metricHeaderContainer;

    @BindView(R.id.fragment_goal_task_header_container)
    RelativeLayout taskHeaderContainer;

    private MetricsListAdapter metricsListAdapter;
    private SuccessCriteriaSpinnerAdapter metricsSpinnerAdapter;
    GoalFragmentListener attachedActivity;
    private TaskActionListener taskActionListener;

    public static GoalFragment newInstance(Goal currentGoal) {
        GoalFragment newFragment = new GoalFragment();
        Bundle b = new Bundle();
        b.putSerializable(GoalTable.TABLE_NAME, currentGoal);
        newFragment.setArguments(b);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal, container, false);
        ButterKnife.bind(this, v);

        taskListAdapter = new TaskRecyclerViewAdapter(new ArrayList<Task>(), taskActionListener);
        initRecyclerView(tasksRecyclerView, taskListAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initViews(Goal currentGoal) {
        updateViews(getActivity(), currentGoal);
    }

    public void updateViews(Activity activity, Goal currentGoal) {
        editTitle.setText(currentGoal.getTitle());
        editDescription.setText(currentGoal.getDescription());

        metricsListAdapter = new MetricsListAdapter(activity, currentGoal.getMetrics());
        metricsListAdapter.registerDataSetObserver(new ListObserver());
        metrics.setAdapter(metricsListAdapter);

        metricsSpinnerAdapter = new SuccessCriteriaSpinnerAdapter(activity, currentGoal.getMetrics());
        taskListAdapter.updateList(currentGoal.getTasks());

        initViews();
    }

    public void initViews() {
        int headerVisible = metricsListAdapter.isEmpty()
                ? View.GONE
                : View.VISIBLE;
        metricHeaderContainer.setVisibility(headerVisible);

        int taskHeaderVisible = taskListAdapter.isEmpty()
                ? View.GONE
                : View.VISIBLE;
        taskHeaderContainer.setVisibility(taskHeaderVisible);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachedActivity = (GoalFragmentListener) activity;
        taskActionListener = (TaskActionListener) activity;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnItemLongClick(R.id.fragment_goal_success_criteria)
    public boolean longClickSuccessCriteria(int position) {
        Metric metric = metricsListAdapter.getItem(position);

        if (metric != null) {
            attachedActivity.removeMetricWorkflow(metric);
        }
        return true;
    }

    @OnEditorAction(R.id.fragment_goal_title)
    public boolean editActionDone(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            getBaseActivity().editAction();
            getBaseActivity().hideKeyboard();
            return true;
        }
        return false;
    }

    public SuccessCriteriaSpinnerAdapter getMetricsSpinnerAdapter() {
        return metricsSpinnerAdapter;
    }

    @Override
    public void updateMetricOnUI(Metric sc) {
        if (sc != null) {
            metricsListAdapter.updateSuccessCriteria(sc);
        }
        initViews();
    }

    @Override
    public void setActionButtonVisibility(boolean isVisible) {
        addFab.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public Goal getNewGoalValues() {
        Goal updatedGoalValues = new Goal(BaseModel.NEW_ID, editTitle.getText().toString(), editDescription.getText().toString());
        updatedGoalValues.setTasks(taskListAdapter.getList());
        return updatedGoalValues;
    }

    public void toggleEditing() {
        boolean isEditable = titleContainer.getVisibility() != View.VISIBLE;

        int inputType = isEditable
                ? InputType.TYPE_CLASS_TEXT
                : InputType.TYPE_NULL;

        int visibility = isEditable
                ? View.VISIBLE
                : View.GONE;

        editTitle.setInputType(inputType);
        editTitle.setFocusable(isEditable);
        editTitle.setFocusableInTouchMode(isEditable);
        titleContainer.setVisibility(visibility);

        editDescription.setInputType(inputType);
        editDescription.setFocusable(isEditable);
        editDescription.setFocusableInTouchMode(isEditable);

        if (isEditable) {
            editTitle.requestFocus();
        }
    }

    @OnClick(R.id.fragment_goal_add_fab)
    public void onClickFab() {
        attachedActivity.startAddWorkflow();
    }

    public interface GoalFragmentListener {
        void startAddWorkflow();
        void removeMetricWorkflow(Metric sc);
    }

    public interface TaskActionListener {
        void openTask(Task t);
        void removeTask(int position, Task t);
        void completeTask(Task t);
    }

    private class ListObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            if (metricsListAdapter.getCount() == 0) {
                metricHeaderContainer.setVisibility(View.GONE);
            } else {
                metricHeaderContainer.setVisibility(View.VISIBLE);
            }
        }
    }
}
