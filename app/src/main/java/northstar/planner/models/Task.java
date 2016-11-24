package northstar.planner.models;

import android.database.Cursor;

import java.util.Date;

import northstar.planner.models.tables.TaskTable;

public class Task extends BaseModel{
    private long goal;
    private String title;
    private double taskCommitment;
    private long completes;
    private Date due;
    private Status status;

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
}
