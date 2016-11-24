package northstar.planner.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.SuccessCriteriaTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;

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

    public long addTheme(Theme newTheme) {
        ContentValues newValues = new ContentValues();

        newValues.put(ThemeTable.TITLE_COLUMN, newTheme.getTitle());
        newValues.put(ThemeTable.DESCRIPTION_COLUMN, newTheme.getDescription());

        return db.insert(ThemeTable.TABLE_NAME, null, newValues);
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

        newValues.put(TaskTable.TITLE_COLUMN, task.getTitle());
        newValues.put(TaskTable.GOAL_COLUMN, task.getGoal());

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
        result.setGoals(getGoalsByThemeId(themeId));
        return result;
    }

    public Goal getGoal(long GoalId) {
        String selection = GoalTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(GoalId) };

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Goal result = new Goal(c);
        result.setSuccessCriterias(getSuccessCriterias(result.getId()));
        result.setTasks(getTasks(result.getId()));
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
        return result;
    }

    public List<Task> getTasks(long goalId) {
        String selection = TaskTable.GOAL_COLUMN + EQUALSQ;
        String[] selectionArgs = { Long.toString(goalId) };

        Cursor c = db.query(TaskTable.TABLE_NAME, TaskTable.projection , selection, selectionArgs, null, null, null);
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new Task(c));
        }
        return result;
    }

    public boolean removeTheme(long themeId) {
        String whereClause = ThemeTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(themeId) };

        int i = db.delete(ThemeTable.TABLE_NAME, whereClause, whereArgs);
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

        int i = db.delete(GoalTable.TABLE_NAME, whereClause, whereArgs);
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
        Cursor c = db.query(ThemeTable.TABLE_NAME, ThemeTable.projection, null, null, null, null, null);
        c.moveToFirst();

        List<Theme> resultList = new ArrayList<>();
        while(!c.isAfterLast()) {
            resultList.add(new Theme(c));
            c.moveToNext();
        }

        return resultList;
    }

    public long updateTheme(Theme currentTheme) {
        if (currentTheme.isNew()) {
            return addTheme(currentTheme);
        }
        String whereClause = ThemeTable._ID + " = " + currentTheme.getId();

        ContentValues cv = new ContentValues();
        cv.put(ThemeTable.TITLE_COLUMN, currentTheme.getTitle());
        cv.put(ThemeTable.DESCRIPTION_COLUMN, currentTheme.getDescription());

        return db.update(ThemeTable.TABLE_NAME, cv, whereClause, null);
    }

    public long updateGoal(Goal currentGoal) {
        if (currentGoal.isNew()) {
            return addGoal(currentGoal);
        }

        String whereClause = GoalTable._ID + " = " + currentGoal.getId();

        ContentValues cv = new ContentValues();
        cv.put(GoalTable.TITLE_COLUMN, currentGoal.getTitle());
        cv.put(GoalTable.DESCRIPTION_COLUMN, currentGoal.getDescription());

        return db.update(GoalTable.TABLE_NAME, cv, whereClause, null);

    }

    public void clearGoals() {
        String whereClause = GoalTable._ID + " > ?";
        String[] whereArgs = { "1" };

        db.delete(GoalTable.TABLE_NAME, whereClause, whereArgs);
    }
}
