package northstar.planner.presentation.goal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.presentation.adapter.TaskListAdapter;
import northstar.planner.presentation.task.NewTaskDialog;
import northstar.planner.utils.DateUtils;
import northstar.planner.utils.StringUtils;
import northstar.planner.utils.ViewAnimationUtils;

public class GoalFragment
        extends BaseFragment
        implements TextView.OnEditorActionListener {

    @BindView(R.id.fragment_goal_title)
    EditText editTitle;

    @BindView(R.id.fragment_goal_description)
    EditText editDescription;

    @BindView(R.id.fragment_goal_success_criteria)
    ListView successCriteria;

    @BindView(R.id.fragment_goal_tasks)
    ListView successTasks;

    @BindView(R.id.fragment_goal_new_success_criteria_title)
    EditText successCriteriaTitle;

    @BindView(R.id.fragment_goal_new_success_criteria_committed)
    EditText successCriteriaCommittedValue;

    @BindView(R.id.item_new_task_title)
    EditText newTaskTitle;

    private SuccessCriteriaListAdapter successCriteriasAdapter;
    private TaskListAdapter taskListAdapter;
    private SuccessCriteriaSpinnerAdapter scAdpater;
    GoalFragmentListener activityListener;

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
        Goal currentGoal = (Goal) getArguments().get(GoalTable.TABLE_NAME);
        View v = inflater.inflate(R.layout.fragment_goal, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
    }

    public void initViews(Goal currentGoal) {
        editTitle.setText(currentGoal.getTitle());
        editDescription.setText(currentGoal.getDescription());

        successCriteriasAdapter = new SuccessCriteriaListAdapter(getActivity(), currentGoal.getSuccessCriterias());
        successCriteria.setAdapter(successCriteriasAdapter);

        taskListAdapter = new TaskListAdapter(getActivity(), currentGoal.getTasks());
        successTasks.setAdapter(taskListAdapter);

        scAdpater = new SuccessCriteriaSpinnerAdapter(getActivity(), currentGoal.getSuccessCriterias());
    }

    private void initListeners() {
        successCriteriaCommittedValue.setOnEditorActionListener(this);
        newTaskTitle.setOnEditorActionListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (GoalFragmentListener) activity;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            switch (v.getId()) {
                case R.id.fragment_goal_new_success_criteria_committed:
                    return addSuccessCriteria();
                case R.id.item_new_task_title:
                    return createNewTask();

            }
        }
        return false;
    }

    private boolean createNewTask() {
        if (!newTaskTitle.getText().toString().isEmpty()) {
            NewTaskDialog dialog = NewTaskDialog.newinstance(newTaskTitle, successCriteriasAdapter);
            dialog.show(getFragmentManager(), "new task");
            newTaskTitle.setText("");
            return true;
        } else {
            Toast.makeText(getActivity(), getString(R.string.task_title_empty), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean addSuccessCriteria() {
        String newSuccessCriteriaTitle = successCriteriaTitle.getText().toString();
        int newSuccessCriteriaCommitted = StringUtils.getIntFromEditText(successCriteriaCommittedValue);

        if (newSuccessCriteriaCommitted == 0 || newSuccessCriteriaTitle.isEmpty()) {
            return false;
        }

        storeAndPropagateNewSuccessCriteria(newSuccessCriteriaTitle, newSuccessCriteriaCommitted);
        return true;
    }

    @OnItemClick(R.id.fragment_goal_tasks)
    public void onTaskItemSelected(int position) {
        activityListener.openTask( taskListAdapter.getItem(position));
    }

    private void storeAndPropagateNewSuccessCriteria(String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        SuccessCriteria sc = new SuccessCriteria(newSuccessCriteriaTitle, newSuccessCriteriaCommitted);
        sc = activityListener.addSuccessCriteria(sc);
        successCriteriasAdapter.add(sc);
    }

    public Goal getNewGoalValues() {
        return new Goal(BaseModel.NEW_ID, editTitle.getText().toString(), editDescription.getText().toString());
    }

    public void toggleEditing() {
        boolean isEditable = editTitle.getVisibility() != View.VISIBLE;

        int inputType = isEditable
                ? InputType.TYPE_CLASS_TEXT
                : InputType.TYPE_NULL;

        int visibility = isEditable
                ? View.VISIBLE
                : View.GONE;

        editTitle.setInputType(inputType);
        editTitle.setFocusable(isEditable);
        editTitle.setFocusableInTouchMode(isEditable);
        editTitle.setVisibility(visibility);

        editDescription.setInputType(inputType);
        editDescription.setFocusable(isEditable);
        editDescription.setFocusableInTouchMode(isEditable);

        if (isEditable) {
            editTitle.requestFocus();
        }
    }

    public interface GoalFragmentListener {
        SuccessCriteria addSuccessCriteria(SuccessCriteria sc);
        void openTask(Task t);
    }
}
