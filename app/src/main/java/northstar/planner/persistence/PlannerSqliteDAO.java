package northstar.planner.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import northstar.planner.models.Goal;
import northstar.planner.models.TaskStatus;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.BaseTable;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.SuccessCriteriaTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.utils.DateUtils;

public class PlannerSqliteDAO {//implements PlannerDAO {

    private static final String EQUALSQ = " = ?";

    SQLiteDatabase db;

    public PlannerSqliteDAO(Context ctx) {
        new PlannerDBHelper(ctx);
        db = PlannerDBHelper.getInstance();
    }

    public PlannerSqliteDAO() {
        db = PlannerDBHelper.getInstance();
    }

    public Theme addTheme(Theme newTheme, int position) {
        ContentValues newValues = new ContentValues();

        newValues.put(ThemeTable.TITLE_COLUMN, newTheme.getTitle());
        newValues.put(ThemeTable.DESCRIPTION_COLUMN, newTheme.getDescription());
        newValues.put(ThemeTable.ORDER_COLUMN, position);

        long newId = db.insert(ThemeTable.TABLE_NAME, null, newValues);
        newTheme.setId(newId);
        return newTheme;
    }

    public long addGoal(Goal goal) {
        ContentValues newValues = new ContentValues();

        newValues.put(GoalTable.TITLE_COLUMN, goal.getTitle());
        newValues.put(GoalTable.DESCRIPTION_COLUMN, goal.getDescription());
        newValues.put(GoalTable.THEME_COLUMN, goal.getTheme());

        long result = db.insert(GoalTable.TABLE_NAME, null, newValues);
        Log.d("DAO", "inserted goal id " + result);
        return result;
    }

    public SuccessCriteria addSuccessCriteria(SuccessCriteria sc) {
        ContentValues newValues = new ContentValues();

        newValues.put(SuccessCriteriaTable.TITLE_COLUMN, sc.getTitle());
        newValues.put(SuccessCriteriaTable.GOAL_COLUMN, sc.getGoal());
        newValues.put(SuccessCriteriaTable.COMMITTED_COLUMN, sc.getCommitted());
        newValues.put(SuccessCriteriaTable.PROGRESS_COLUMN, sc.getProgress());

        long result = db.insert(SuccessCriteriaTable.TABLE_NAME, null, newValues);
        sc.setId(result);
        return sc;
    }

    public Task addTask(Task task) {
        ContentValues newValues = new ContentValues();

        if (task.getDue() != null) {
            newValues.put(TaskTable.DUE_COLUMN, task.getDue().getTime());
        }

        newValues.put(TaskTable.TITLE_COLUMN, task.getTitle());
        newValues.put(TaskTable.GOAL_COLUMN, task.getGoal());
        newValues.put(TaskTable.COMPLETES_COLUMN, task.getCompletes());
        newValues.put(TaskTable.TASK_COMMITMENT_COLUMN, task.getTaskCommitment());
        newValues.put(TaskTable.STATUS_COLUMN, task.getTaskStatus().toString());

        long result = db.insert(TaskTable.TABLE_NAME, null, newValues);
        task.setId(result);
        return task;
    }

    public Theme getTheme(long themeId) {
        String selection = ThemeTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(themeId) };

        Cursor c = db.query(ThemeTable.TABLE_NAME, ThemeTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Theme result = new Theme(c);
        c.close();
        result.setGoals(getGoalsByThemeId(themeId));
        return result;
    }

    public Goal getGoal(long GoalId) {
        String selection = GoalTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(GoalId) };

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Goal result = new Goal(c);
        result.setChildren(getSuccessCriterias(result.getId()), getTasksByGoalId(result.getId()));
//        result.setSuccessCriterias();
//        result.setTasks();
        c.close();
        return result;
    }

    public Task getTask(long taskId) {
        String selection = TaskTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(taskId) };

        Cursor c = db.query(TaskTable.TABLE_NAME, TaskTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Task result = new Task(c);
        c.close();
        return result;
    }

    private List<SuccessCriteria> getSuccessCriterias(long goalId) {
        String selection = SuccessCriteriaTable.GOAL_COLUMN + EQUALSQ;
        String[] selectionArgs = { Long.toString(goalId) };

        Cursor c = db.query(SuccessCriteriaTable.TABLE_NAME, SuccessCriteriaTable.projection , selection, selectionArgs, null, null, null);
        c.moveToFirst();

        List<SuccessCriteria> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new SuccessCriteria(c));
            c.moveToNext();
        }
        c.close();
        return result;
    }

    public List<Task> getTasksByGoalId(long goalId) {
        String selection = TaskTable.GOAL_COLUMN + EQUALSQ + " AND " + TaskTable.STATUS_COLUMN + " != ?";
        String[] selectionArgs = { Long.toString(goalId), TaskStatus.DONE.toString() };

        Cursor c = db.query(TaskTable.TABLE_NAME, TaskTable.projection , selection, selectionArgs, null, null, BaseTable.getorderAsc());
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new Task(c));
            c.moveToNext();
        }
        c.close();
        return result;
    }

    public List<Task> getTasksByDueDate(Calendar cal) {
        Calendar start = DateUtils.getStartOfDay(cal);
        Calendar end = DateUtils.getEndOfDay(cal);

        String selection = getSelectionForDatesOnThisDay(TaskTable.DUE_COLUMN);
        String[] selectionArgs = { Long.toString(start.getTime().getTime()), Long.toString(end.getTime().getTime()) };

        Cursor c = db.query(TaskTable.TABLE_NAME, TaskTable.projection , selection, selectionArgs, null, null, BaseTable.getorderAsc());
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new Task(c));
            c.moveToNext();
        }
        return result;
    }

    private String getSelectionForDatesOnThisDay(String datecolumn) {
        return datecolumn + " > ? AND " + datecolumn + " < ?";
    }



    public boolean removeTheme(long themeId) {
        String whereClause = ThemeTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(themeId) };
        List<Goal> themeGoals = getGoalsByThemeId(themeId);
        
        int i = db.delete(ThemeTable.TABLE_NAME, whereClause, whereArgs);
        for (Goal g : themeGoals) {
            removeGoal(g.getId());
        }
        return i == 1;
    }

    public boolean removeTheme(String title) {
        String whereClause = ThemeTable.TITLE_COLUMN + EQUALSQ;
        String[] whereArgs = { title };

        int i = db.delete(ThemeTable.TABLE_NAME, whereClause, whereArgs);
        return i > 0 ;
    }

    public boolean removeGoal(long goalId) {
        String whereClause = GoalTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(goalId) };
        List<Task> goalTask = getTasksByGoalId(goalId);

        int i = db.delete(GoalTable.TABLE_NAME, whereClause, whereArgs);
        for (Task task : goalTask) {
            removeTask(task.getId());
        }
        return i == 1;
    }
    

    public boolean removeTask(long taskId) {
        String whereClause = TaskTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(taskId) };

        int i = db.delete(TaskTable.TABLE_NAME, whereClause, whereArgs);
        return i == 1;
    }

    public List<Goal> getGoalsByThemeId(long themeId) {
        List<Goal> results = new ArrayList<>();
        String selection = GoalTable.THEME_COLUMN + EQUALSQ;
        String[] selectionArgs = { Long.toString(themeId) };

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            results.add(new Goal(c));
            c.moveToNext();
        }

        return results;
    }

    public List<Theme> getAllThemes(List<Long> themes) {
        List<Theme> resultList = new ArrayList<>();

        for (long themeId : themes) {
            resultList.add(getTheme(themeId));
        }

        return resultList;
    }

    public List<Theme> getAllThemes() {
        Cursor c = db.query(ThemeTable.TABLE_NAME, ThemeTable.projection, null, null, null, null, ThemeTable.getorderAsc());
        c.moveToFirst();

        List<Theme> resultList = new ArrayList<>();
        while(!c.isAfterLast()) {
            resultList.add(new Theme(c));
            c.moveToNext();
        }

        return resultList;
    }

    public Theme updateTheme(Theme currentTheme) {
        if (currentTheme.isNew()) {
            return addTheme(currentTheme, 0);
        }
        String whereClause = ThemeTable._ID + " = " + currentTheme.getId();

        ContentValues cv = new ContentValues();
        cv.put(ThemeTable.TITLE_COLUMN, currentTheme.getTitle());
        cv.put(ThemeTable.DESCRIPTION_COLUMN, currentTheme.getDescription());

        db.update(ThemeTable.TABLE_NAME, cv, whereClause, null);
        return currentTheme;
    }

    public long updateGoal(Goal currentGoal) {
        if (currentGoal.isNew()) {
            return addGoal(currentGoal);
        }

        String whereClause = GoalTable._ID + " = " + currentGoal.getId();

        ContentValues cv = new ContentValues();
        cv.put(GoalTable.TITLE_COLUMN, currentGoal.getTitle());
        cv.put(GoalTable.DESCRIPTION_COLUMN, currentGoal.getDescription());

        updateTaskOrder(currentGoal.getTasks());

        return db.update(GoalTable.TABLE_NAME, cv, whereClause, null);

    }

    public void clearGoals() {
        String whereClause = GoalTable._ID + " > ?";
        String[] whereArgs = { "1" };

        db.delete(GoalTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void updateThemeOrder(List<Theme> list) {
        for (int i = 0; i < list.size(); i++) {
            Theme currentTheme = list.get(i);
            String whereClause = ThemeTable._ID + " = " + currentTheme.getId();

            ContentValues cv = new ContentValues();
            cv.put(ThemeTable.ORDER_COLUMN, i+1);

            db.update(ThemeTable.TABLE_NAME, cv, whereClause, null);
        }
    }

    public void updateTaskOrder(List<Task> list) {
        for (int i = 0; i < list.size(); i++) {
            Task currentTask = list.get(i);
            String whereClause = TaskTable._ID + " = " + currentTask.getId();

            ContentValues cv = new ContentValues();
            cv.put(TaskTable.ORDER_COLUMN, i+1);

            db.update(TaskTable.TABLE_NAME, cv, whereClause, null);
        }
    }

    public List<Task> getTodaysTasks() {
        List<Task> tasks = getTasksByGoalId(Task.SCRATCH_ID);
        tasks.addAll(getTasksByDueDate(DateUtils.today()));
        return tasks;
    }

    public SuccessCriteria completeTask(Task completedTask) {
        setTask(completedTask, TaskStatus.DONE);
        return updateSuccessCriteria(completedTask);
    }

    private void setTask(Task currentTask, TaskStatus taskStatus) {
        ContentValues cv = new ContentValues();
        cv.put(TaskTable.STATUS_COLUMN, taskStatus.toString());

        String whereClause = TaskTable._ID + " = " + currentTask.getId();

        db.update(TaskTable.TABLE_NAME, cv, whereClause, null);
    }

    private SuccessCriteria updateSuccessCriteria(Task t) {
        SuccessCriteria currentSC = t.getSuccessCriteria();
        currentSC.updateProgress(t.getTaskCommitment());

        ContentValues cv = new ContentValues();
        cv.put(SuccessCriteriaTable.PROGRESS_COLUMN, currentSC.getProgress());

        String whereClause = SuccessCriteriaTable._ID + " = " + currentSC.getId();

        db.update(SuccessCriteriaTable.TABLE_NAME, cv, whereClause, null);
        return currentSC;
    }
}
