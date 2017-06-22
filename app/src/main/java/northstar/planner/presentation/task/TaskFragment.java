package northstar.planner.presentation.task;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnLongClick;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.recurrence.RecurrenceDialogFragment;
import northstar.planner.utils.DateTimeSetter;
import northstar.planner.utils.DateTimeSetterCallback;
import northstar.planner.utils.NotificationType;
import northstar.planner.utils.StringUtils;

public class TaskFragment
        extends BaseFragment {

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

    @BindView(R.id.fragment_task_recurrence_display)
    TextView recurrenceView;

    @BindView(R.id.fragment_task_metric_container)
    RelativeLayout metricContainer;

    @BindView(R.id.fragment_task_blocked_on_task)
    TextView blockingTask;

    Task currentTask;
    TaskFragmentListener attachedActivity;

    public static TaskFragment newInstance(Bundle b) {
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setRecurrence();
    }

    private void setDependentTask() {
        if (currentTask.getDependentTask() != null) {
            blockingTask.setText(currentTask.getDependentTask().getTitle());
        } else {
            blockingTask.setText("");
        }
    }

    private void setRecurrence() {
        if (currentTask.getRecurrenceSchedule() == null) {
            recurrenceView.setText("");
        } else {
            recurrenceView.setText(currentTask.getRecurrenceSchedule().toString());
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
            metricContainer.setVisibility(View.GONE);
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
            public void onValuesSet(DateTime selectedDate) {
                currentTask.setDueDate(selectedDate);
                saveAndUpdateTask(currentTask);
                if (prefs.remindWhenDue()) {
                    getBaseActivity().getPlannerNotificationManager().scheduleNotification(currentTask, NotificationType.DUE_NOTIFICATION);
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
            public void onValuesSet(DateTime selectedDate) {
                currentTask.setReminder(selectedDate);
                getBaseActivity().getPlannerNotificationManager().scheduleNotification(currentTask, NotificationType.REMINDER_NOTIFICATION);
                saveAndUpdateTask(currentTask);
            }
        });
        setter.selectTime();
    }

    @OnClick(R.id.fragment_task_back)
    public void onBackClicked() {
        getActivity().finish();
    }

    @OnClick(R.id.fragment_task_done)
    public void onDoneClicked() {
        ((TaskActivity) getActivity()).completeTask(currentTask);
        getActivity().finish();
    }

    @OnClick(R.id.fragment_task_snooze)
    public void onSnoozeClicked() {
        DateTimeSetter setter = new DateTimeSetter(getActivity(), new DateTimeSetterCallback() {

            @Override
            public void onValuesSet(DateTime selectedDate) {
                currentTask.setSnooze(selectedDate);
                saveAndUpdateTask(currentTask);

                if (prefs.remindAfterSnooze()) {
                    getBaseActivity().getPlannerNotificationManager().scheduleNotification(currentTask, NotificationType.SNOOZE_NOTIFICATION);
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

    private void saveAndUpdateTask(Task task) {
        initUI(task);
        getBaseActivity().getDao().updateTask(task);
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

    @OnClick(R.id.fragment_task_recurrence_container)
    public void onRecurrenceClick() {
        RecurrenceDialogFragment dialogFragment = RecurrenceDialogFragment.newInstance(currentTask);
        FragmentManager fm = getFragmentManager();
        dialogFragment.show(fm, "TAG");
    }

    @OnLongClick(R.id.fragment_task_recurrence_container)
    public boolean onLongClickRecurrence() {
        currentTask = getBaseActivity().getPlannerNotificationManager().cancelAllNotificationsForTask(currentTask);
        initUI(currentTask);
        return true;
    }

    public interface TaskFragmentListener {
        void navigateToGoal();
        void addTaskDependency();
        void removeDependency();
    }
}
