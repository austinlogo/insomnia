package northstar.planner.models;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import northstar.planner.models.tables.TaskTable;

public class Task extends BaseModel {
    private long goal;
    private String title;
    private double taskCommitment;
    private long completes;
    private SuccessCriteria successCriteria;
    private Date due;
    private Status status;

    public Task() {
        _id = NEW_ID;
        goal = NEW_ID;
        due = new Date();
        status = Status.NOT_STARTED;
    }

    public Task(Cursor c) {
        super(c);
        title = getColumnString(c, TaskTable.TITLE_COLUMN);
        goal = getColumnLong(c, TaskTable.GOAL_COLUMN);
        taskCommitment = getColumnDouble(c, TaskTable.TASK_COMMITMENT_COLUMN);
        completes = getColumnLong(c, TaskTable.COMPLETES_COLUMN);
        due = getColumnDate(c, TaskTable.DUE_COLUMN);
        status = Status.valueOf(getColumnString(c, TaskTable.STATUS_COLUMN));
    }

    public Task (long goalId, String newTitle) {
        title = newTitle;
        goal = goalId;
    }

    public Task(EditText newTaskTitle, Calendar chosenDate, SuccessCriteria item, int commitment) {
        goal = NEW_ID;
        _id = NEW_ID;
        title = newTaskTitle.getText().toString();
        due = chosenDate.getTime();
        completes = item.getId();
        successCriteria = item;
        taskCommitment = commitment;
        status = Status.NOT_STARTED;
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

    public Date getDue() {
        return due;
    }

    public Status getStatus() {
        return status;
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

    public void setSuccessCriteria(SuccessCriteria successCriteria) {
        this.completes = successCriteria.getId();
        this.successCriteria = successCriteria;
    }

    public void setGoal(Goal goal) {
        this.goal = goal.getId();
    }
}
