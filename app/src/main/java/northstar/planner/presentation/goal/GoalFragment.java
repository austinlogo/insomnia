package northstar.planner.presentation.goal;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.task.NewTaskDialog;
import northstar.planner.presentation.task.TaskBasedFragment;
import northstar.planner.utils.StringUtils;

public class GoalFragment
        extends TaskBasedFragment
        implements TextView.OnEditorActionListener {

    @BindView(R.id.fragment_goal_title)
    EditText editTitle;

    @BindView(R.id.fragment_goal_description)
    EditText editDescription;

    @BindView(R.id.fragment_goal_success_criteria)
    ListView successCriteria;

    @BindView(R.id.fragment_goal_tasks)
    RecyclerView tasksRecyclerView;

    @BindView(R.id.fragment_goal_new_success_criteria_title)
    EditText successCriteriaTitle;

    @BindView(R.id.fragment_goal_new_success_criteria_committed)
    EditText successCriteriaCommittedValue;

    @BindView(R.id.item_new_task_title)
    EditText newTaskTitle;

    private SuccessCriteriaListAdapter successCriteriasAdapter;
    private SuccessCriteriaSpinnerAdapter scAdpater;
    GoalFragmentListener activityListener;
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
        initListeners();
    }

    public void initViews(Goal currentGoal) {
        editTitle.setText(currentGoal.getTitle());
        editDescription.setText(currentGoal.getDescription());

        successCriteriasAdapter = new SuccessCriteriaListAdapter(getActivity(), currentGoal.getSuccessCriterias());
        successCriteria.setAdapter(successCriteriasAdapter);

        scAdpater = new SuccessCriteriaSpinnerAdapter(getActivity(), currentGoal.getSuccessCriterias());

        taskListAdapter.updateList(currentGoal.getTasks());
    }

    private void initListeners() {
        successCriteriaCommittedValue.setOnEditorActionListener(this);
        newTaskTitle.setOnEditorActionListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (GoalFragmentListener) activity;
        taskActionListener = (TaskActionListener) activity;
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
                    activityListener.createTask(v.getText().toString(), scAdpater);
                    return true;
            }
        }
        return false;
    }

    @OnItemLongClick(R.id.fragment_goal_success_criteria)
    public boolean longClickSuccessCriteria(int position) {
        activityListener.removeSuccessCriteria(successCriteriasAdapter.getItem(position));
        return successCriteriasAdapter.remove(position);
    }

    private boolean createNewTask() {
        if (!newTaskTitle.getText().toString().isEmpty()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            NewTaskDialog dialog = NewTaskDialog.newinstance(newTaskTitle, successCriteriasAdapter);
            dialog.show(ft, "show");
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

    public void updateSuccessCriteria(SuccessCriteria sc) {
        if (sc != null) {
            successCriteriasAdapter.updateSuccessCriteria(sc);
        }
    }

    private void storeAndPropagateNewSuccessCriteria(String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        SuccessCriteria sc = new SuccessCriteria(newSuccessCriteriaTitle, newSuccessCriteriaCommitted);
        sc = activityListener.addSuccessCriteria(sc);
        successCriteriasAdapter.notifyDataSetChanged();
    }

    public Goal getNewGoalValues() {
        Goal updatedGoalValues = new Goal(BaseModel.NEW_ID, editTitle.getText().toString(), editDescription.getText().toString());
        updatedGoalValues.setTasks(taskListAdapter.getList());
        return updatedGoalValues;
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
        boolean removeSuccessCriteria(SuccessCriteria sc);
        void createTask(String newTask, SuccessCriteriaSpinnerAdapter adapter);
    }

    public interface TaskActionListener {
        void openTask(Task t);
        void removeTask(int position, Task t);
        void completeTask(Task t);
    }
}
