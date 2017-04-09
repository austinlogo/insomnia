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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnLongClick;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.utils.DateTimeSetter;
import northstar.planner.utils.DateTimeSetterCallback;
import northstar.planner.utils.DateUtils;
import northstar.planner.utils.NotificationType;
import northstar.planner.utils.StringUtils;

public class TaskFragment
        extends BaseFragment
        implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.fragment_task_title_container)
    LinearLayout titleContainer;

    @BindView(R.id.fragment_task_title)
    EditText editTitle;

    @BindView(R.id.fragment_task_due)
    TextView due;

    @BindView(R.id.fragment_task_snooze)
    TextView snooze;

    @BindView(R.id.fragment_reminder_snooze)
    TextView reminder;

    @BindView(R.id.fragment_task_snooze_container)
    RelativeLayout snoozeContainer;

    @BindView(R.id.fragment_task_goal_container)
    LinearLayout taskGoalContainer;

    @BindView(R.id.item_success_criteria_title)
    TextView metricTitle;

    @BindView(R.id.item_success_criteria_progress)
    TextView metricProgress;

    @BindView(R.id.fragment_task_goal)
    TextView goalTitle;

    @BindView(R.id.fragment_task_metric_container)
    RelativeLayout metricContainer;

    @BindView(R.id.fragment_task_blocked_on_task)
    TextView blockingTask;

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
        super.onCreateView(inflater, container, savedInstanceState);
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
        setGoalTitleRow();
        setSnoozeRow();
        setMetricProgressRow();
        setReminderRow();
        setDependentTask();
    }

    private void setDependentTask() {
        if (currentTask.getDependentTask() != null) {
            blockingTask.setText(currentTask.getDependentTask().getTitle());
        } else {
            blockingTask.setText("");
        }
    }

    private void setGoalTitleRow() {
        if (StringUtils.isEmpty(currentTask.getGoalTitle())) {
            taskGoalContainer.setVisibility(View.GONE);
        } else {
            taskGoalContainer.setVisibility(View.VISIBLE);
            goalTitle.setText(currentTask.getGoalTitle());
        }
    }

    private void setSnoozeRow() {
        if (currentTask.hasSnoozed()) {
            snooze.setText(currentTask.getSnoozeString());
        }
    }

    private void setMetricProgressRow() {
        if (currentTask.getCompletes() > 0) {
            metricContainer.setVisibility(View.VISIBLE);
            metricTitle.setText(currentTask.getMetric().getTitle());
            metricProgress.setText(currentTask.getMetric().getProgressString());
        } else {
            metricContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void setReminderRow() {
        if (currentTask.getReminder() != null) {
            reminder.setText(currentTask.getReminderString());
        }
    }

    @OnClick(R.id.fragment_task_due_container)
    public void onClickDueContainer() {
        DateTimeSetter setter = new DateTimeSetter(getActivity(), new DateTimeSetterCallback() {

            @Override
            public void onValuesSet(Date selectedDate) {
                currentTask.setDueDate(selectedDate);
                saveAndUpdateTask(currentTask);
                if (prefs.remindWhenDue()) {
                    getBaseActivity().scheduleNotification(currentTask, NotificationType.DUE_NOTIFICATION);
                }
            }
        });
        setter.selectTime();
    }

    @OnClick(R.id.fragment_task_goal_container)
    public void onGoalTitleClicked() {
        attachedActivity.navigateToGoal();
    }

    @OnClick(R.id.fragment_task_reminder_container)
    public void onReminderClicked() {
        DateTimeSetter setter = new DateTimeSetter(getActivity(), new DateTimeSetterCallback() {

            @Override
            public void onValuesSet(Date selectedDate) {
                currentTask.setReminder(selectedDate);
                getBaseActivity().scheduleNotification(currentTask, NotificationType.REMINDER_NOTIFICATION);
                saveAndUpdateTask(currentTask);
            }
        });
        setter.selectTime();
    }

    @OnClick(R.id.fragment_task_done)
    public void onDoneClicked() {
        getActivity().finish();
    }

    @OnClick(R.id.fragment_task_snooze)
    public void onSnoozeClicked() {
        DateTimeSetter setter = new DateTimeSetter(getActivity(), new DateTimeSetterCallback() {

            @Override
            public void onValuesSet(Date selectedDate) {
                currentTask.setSnooze(selectedDate);
                saveAndUpdateTask(currentTask);

                if (prefs.remindWhenDue()) {
                    getBaseActivity().scheduleNotification(currentTask, NotificationType.SNOOZE_NOTIFICATION);
                }
            }
        });
        setter.selectTime();
    }

    @OnClick(R.id.fragment_task_blocked_on_container)
    public void onClickBlocked() {
        attachedActivity.addTaskDependency();
    }

    @OnLongClick(R.id.fragment_task_blocked_on_container)
    public boolean onLongClick() {
        attachedActivity.removeDependency();
        return true;
    }

//    @OnClick(R.id.fragment_task_add_alert_due)
//    public void onClickAddAlertOnDue() {
//        Date due = currentTask.getDue();
//
//        if (due != null) {
//            getBaseActivity().scheduleNotification(currentTask, NotificationType.DUE_NOTIFICATION);
//        }
//    }

//    @OnClick(R.id.fragment_task_add_alert_snooze)
//    public void onClickAddSnoozeTask() {
//        Date snoozeDate = currentTask.getSnooze();
//
//        if (snoozeDate != null) {
//            getBaseActivity().scheduleNotification(currentTask, NotificationType.SNOOZE_NOTIFICATION);
//        }
//    }

    private void saveAndUpdateTask(Task task) {
        initUI(task);
        dao.updateTask(task);
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
        void addTaskDependency();
        void removeDependency();
    }
}
