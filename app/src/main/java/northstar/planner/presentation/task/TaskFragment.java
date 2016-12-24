package northstar.planner.presentation.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.utils.DateUtils;

public class TaskFragment
        extends BaseFragment
        implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.fragment_task_title_container)
    LinearLayout titleContainer;

    @BindView(R.id.fragment_task_title)
    EditText editTitle;

    @BindView(R.id.fragment_task_due)
    TextView due;

    @BindView(R.id.item_success_criteria_title)
    TextView successCriteriaTitle;

    @BindView(R.id.item_success_criteria_progress)
    TextView successCriteriaProgress;

    @BindView(R.id.fragment_task_goal)
    TextView goalTitle;

    Task currentTask;
    PlannerSqliteGateway dao;

    TaskFragmentListener attachedActivity;

    public static TaskFragment newInstance(Bundle b) {
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new PlannerSqliteGateway();
        currentTask = (Task) getArguments().getSerializable(TaskTable.TABLE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachedActivity = (TaskFragmentListener) activity;
    }

    public Task updateValues() {
        currentTask.setTitle(editTitle.getText().toString());
        return currentTask;
    }

    public void initUI(Task currentTask) {
        this.currentTask = currentTask;
        editTitle.setText(currentTask.getTitle());
        due.setText(currentTask.getDueString());
        goalTitle.setText(currentTask.getGoalTitle());

        if (currentTask.getCompletes() > 0) {
            successCriteriaTitle.setText(currentTask.getMetric().getTitle());
            successCriteriaProgress.setText(currentTask.getMetric().getProgressString());
        }
    }

    @OnClick(R.id.fragment_task_due_container)
    public void onClickDueContainer() {
        Calendar cal = Calendar.getInstance();
        Date initDate = currentTask.getDue() != null
                ? currentTask.getDue()
                : DateUtils.today().getTime();

        cal.setTime(initDate);
         new DatePickerDialog(getActivity(),
                this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.fragment_task_goal_container)
    public void onGoalTitleClicked() {
        attachedActivity.navigateToGoal();
    }

    @OnEditorAction(R.id.fragment_task_title)
    public boolean editActionDone(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            getBaseActivity().editAction();
            getBaseActivity().hideKeyboard();
            return true;
        }
        return false;
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

        if (isEditable) {
            editTitle.requestFocus();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        currentTask.setDueDate(c.getTime());
        due.setText(DateUtils.getDateString(getString(R.string.today), c));
    }

    public interface TaskFragmentListener {
        void navigateToGoal();
    }
}
