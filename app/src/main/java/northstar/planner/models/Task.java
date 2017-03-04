package northstar.planner.models;

import android.database.Cursor;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import northstar.planner.PlannerApplication;
import northstar.planner.R;
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
    private Date due;
    private Date snooze;
    private TaskStatus taskStatus;

    public Task() {
        _id = NEW_ID;
        goal = NEW_ID;
        due = new Date();
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
        taskStatus = TaskStatus.valueOf(getColumnString(c, TaskTable.STATUS_COLUMN));
    }

    public Task (long goalId, String newTitle) {
        title = newTitle;
        goal = goalId;
    }

    public Task(long goalId, String title, Date due) {
        goal = goalId;
        _id = NEW_ID;
        this.title = title;
        this.due = due;
        metric = null;
        taskCommitment = 0;
        taskStatus = TaskStatus.IN_PROGRESS;
    }

    public Task(EditText newTaskTitle, Calendar chosenDate, Metric item, int commitment) {
        boolean hasSuccessCriteria = item != null && item.getId() != NEW_ID;

        goal = NEW_ID;
        _id = NEW_ID;
        title = newTaskTitle.getText().toString();
        due = chosenDate == null ? null : chosenDate.getTime();
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

    public Date getDue() {
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

    public void setDueDate(Date dueDate) {
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

    private String getDateString(Date date) {
        if (date == null) {
            return "";
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String todayString = PlannerApplication.getInstance().getString(R.string.today);
        return DateUtils.getDateString(todayString, cal);
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public String getSnoozeString() {
        if (snooze == null) {
            return null;
        }

        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        String time = localDateFormat.format(snooze);

        return getDateString(snooze) + ", " + time;
    }

    public void setSnooze(Date snooze) {
        this.snooze = snooze;
    }

    public boolean hasSnoozed() {
        return snooze != null;
    }

    public Date getSnooze() {
        return snooze;
    }
}
