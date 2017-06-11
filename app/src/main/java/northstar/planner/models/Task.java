package northstar.planner.models;

import android.database.Cursor;
import android.widget.EditText;

import org.joda.time.DateTime;

import northstar.planner.models.drawer.ShallowModel;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.utils.DateUtils;



public class Task extends BaseModel {
    private long goal;
    private String goalTitle;
    private String title;
    private double taskCommitment;
    private long completes;
    private Metric metric;
    private DateTime due;
    private DateTime snooze;
    private DateTime reminderTime;
    private TaskStatus taskStatus;
    private ShallowModel dependentTask;
    private Recurrence recurrenceSchedule;

    public Task() {
        _id = NEW_ID;
        goal = NEW_ID;
        due = new DateTime();
        taskStatus = TaskStatus.NOT_STARTED;
    }

    public Task(Cursor c) {
        super(c);
        title = getColumnString(c, TaskTable.TITLE_COLUMN);
        goal = getColumnLong(c, TaskTable.GOAL_COLUMN);
        goalTitle = getColumnString(c, GoalTable.uniqueTitle());
        taskCommitment = getColumnDouble(c, TaskTable.TASK_COMMITMENT_COLUMN);
        completes = getColumnLong(c, TaskTable.COMPLETES_COLUMN);
        due = getColumnDate(c, TaskTable.DUE_COLUMN);
        snooze = getColumnDate(c, TaskTable.SNOOZE_COLUMN);
        reminderTime = getColumnDate(c, TaskTable.REMINDER_COLUMN);
        taskStatus = TaskStatus.valueOf(getColumnString(c, TaskTable.STATUS_COLUMN));
    }

    public Task (long goalId, String newTitle) {
        title = newTitle;
        goal = goalId;
    }

    public Task(long goalId, String title, DateTime due) {
        goal = goalId;
        _id = NEW_ID;
        this.title = title;
        this.due = due;
        metric = null;
        taskCommitment = 0;
        taskStatus = TaskStatus.IN_PROGRESS;
    }

    public Task(EditText newTaskTitle, DateTime chosenDate, Metric item, int commitment) {
        boolean hasSuccessCriteria = item != null && item.getId() != NEW_ID;

        goal = NEW_ID;
        _id = NEW_ID;
        title = newTaskTitle.getText().toString();
        due = chosenDate == null ? null : chosenDate;
        taskStatus = TaskStatus.NOT_STARTED;

        if (hasSuccessCriteria) {
            completes = item.getId();
            metric = item;
            taskCommitment = commitment;
        } else {
            completes = NEW_ID;
            metric = null;
            taskCommitment = 0;
        }
    }

    public long getGoal() {
        return goal;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public double getTaskCommitment() {
        return taskCommitment;
    }

    public long getCompletes() {
        return completes;
    }

    public Metric getMetric() {
        return metric;
    }

    public DateTime getDue() {
        return due;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setId(long id) {
        _id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDueDate(DateTime dueDate) {
        this.due = dueDate;
    }

    public void setMetric(Metric metric) {
        this.completes = metric == null
                ? NEW_ID
                : metric.getId();

        this.metric = metric;
    }

    public void setGoal(Goal goal) {
        this.goal = goal.getId();
    }

    public void setGoal(long id) {
        this.goal = id;
    }

    public String getDueString() {
        return getDateString(due);
    }

    public static String getDateString(DateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

//        return dateTime.toString("%s, %s", D)

        return String.format("%s, %s", DateUtils.getDateString(dateTime), dateTime.toString("HH:mm"));
//        return DateUtils.getDateString(cal) + ", " + time;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public String getSnoozeString() {
        return getDateString(snooze);
    }

    public void setSnooze(DateTime snooze) {
        this.snooze = snooze;
    }

    public boolean hasSnoozed() {
        return snooze != null;
    }

    public DateTime getSnooze() {
        return snooze;
    }

    public DateTime getReminder() {
        return reminderTime;
    }

    public String getReminderString() {
        return getDateString(reminderTime);
    }

    public void setReminder(DateTime reminder) {
        this.reminderTime = reminder;
    }

    public void setDependentTask(ShallowModel dependentTask) {
        this.dependentTask = dependentTask;
    }

    public ShallowModel getDependentTask() {
        return dependentTask;
    }

    public Recurrence getRecurrenceSchedule() {
        return recurrenceSchedule;
    }

    public void setRecurrenceSchedule(Recurrence recurrenceSchedule) {
        this.recurrenceSchedule = recurrenceSchedule;

        if (due == null && recurrenceSchedule != null) {
            due = new DateTime();
        }
    }

    public void udpateToNextIteration() {
        DateTime nextDueIteration = new DateTime(due.getMillis() + recurrenceSchedule.getPeriod());
        DateTime nextSnoozeIteration = null;

        if (snooze != null) {
            long snoozeDelta = due.getMillis() - snooze.getMillis();
             nextSnoozeIteration = new DateTime(nextDueIteration.getMillis() - snoozeDelta);
        }

        setDueDate(nextDueIteration);
        setSnooze(nextSnoozeIteration);
    }

    public static long getNextIteration(Task task) {
        return task.getDue().getMillis() + task.getRecurrenceSchedule().getPeriod();
    }
}
