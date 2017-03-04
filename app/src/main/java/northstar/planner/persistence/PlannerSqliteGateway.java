package northstar.planner.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.TaskStatus;
import northstar.planner.models.Theme;
import northstar.planner.models.checkboxgroup.CheckboxGroup;
import northstar.planner.models.tables.ActiveHoursTable;
import northstar.planner.models.tables.BaseTable;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.MetricTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.utils.DateUtils;

public class PlannerSqliteGateway {//implements PlannerGateway {

    private static final String EQUALSQ = " = ?";

    SQLiteDatabase db;

    public PlannerSqliteGateway(Context ctx) {
        new PlannerDBHelper(ctx);
        db = PlannerDBHelper.getInstance();
    }

    public PlannerSqliteGateway() {
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
        goal.setId(result);
        return result;
    }

    public Metric addMetric(Metric sc) {
        ContentValues newValues = new ContentValues();

        newValues.put(MetricTable.TITLE_COLUMN, sc.getTitle());
        newValues.put(MetricTable.GOAL_COLUMN, sc.getGoal());
        newValues.put(MetricTable.COMMITTED_COLUMN, sc.getCommitted());
        newValues.put(MetricTable.PROGRESS_COLUMN, sc.getProgress());

        long result = db.insert(MetricTable.TABLE_NAME, null, newValues);
        sc.setId(result);
        return sc;
    }

    public Task addTask(Task task) {
        ContentValues newValues = new ContentValues();

        updateMetric(task.getMetric());

        if (task.getDue() != null) {
            newValues.put(TaskTable.DUE_COLUMN, task.getDue().getTime());
        } else {
            newValues.put(TaskTable.DUE_COLUMN, 0);
        }

        if (task.getSnooze() != null) {
            newValues.put(TaskTable.SNOOZE_COLUMN, task.getSnooze().getTime());
        } else {
            newValues.put(TaskTable.SNOOZE_COLUMN, 0);
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

    public Goal getGoal(long goalId) {
        String selection = GoalTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(goalId) };

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Goal result = new Goal(c);
        result.setChildren(getMetrics(goalId), getTasksByGoalId(goalId));
        c.close();
        return result;
    }

    public Task getTask(long taskId) {
        String selection = TaskTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(taskId) };

//        Cursor c = db.query(TaskTable.TABLE_NAME, TaskTable.projection, selection, selectionArgs, null, null, null);
//        c.moveToFirst();
        String query = constructTaskQuery(TaskTable._ID + " = " + taskId);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Task result = new Task(c);
        result.setMetric(getMetric(result.getCompletes()));
        c.close();
        return result;
    }

    public List<Metric> getMetrics(long goalId) {
        String selection = MetricTable.GOAL_COLUMN + EQUALSQ;
        String[] selectionArgs = { Long.toString(goalId) };

        Cursor c = db.query(MetricTable.TABLE_NAME, MetricTable.projection , selection, selectionArgs, null, null, null);
        c.moveToFirst();

        List<Metric> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(Metric.newInstance(c));
            c.moveToNext();
        }
        c.close();
        return result;
    }

    public Metric getMetric(long metricId) {
        String selection = MetricTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(metricId) };

        Cursor c = db.query(MetricTable.TABLE_NAME, MetricTable.projection , selection, selectionArgs, null, null, null);
        c.moveToFirst();

        if (c.isAfterLast()) {
            return null;
        }

        Metric result = Metric.newInstance(c);
        c.close();
        return result;
    }

    public List<Task> getTasksByGoalId(long goalId) {
        String query = constructTaskQuery(TaskTable.GOAL_COLUMN + " = " + goalId);

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new Task(c));
            c.moveToNext();
        }
        c.close();
        return result;
    }

    private List<Task> filterTaskBySnoozeTime(List<Task> tasks, long currentTime) {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task currentTask : tasks) {
            if (currentTask.hasSnoozed() && currentTask.getSnooze().getTime() >= currentTime) { // still snoozing
                continue;
            }

            filteredTasks.add(currentTask);
        }
        return filteredTasks;
    }

    public List<Task> getTasksBeforeDueDate(Calendar cal) {
        Calendar endOfDay = DateUtils.getEndOfDay(cal);
        String query = constructTaskQuery(TaskTable.DUE_COLUMN + " < " + endOfDay.getTime().getTime());

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(new Task(c));
            c.moveToNext();
        }
        return result;
    }

    private String constructTaskQuery(String criteria) {
        // TODO: Consolidate this select
        return "Select t.*, g.Title as " + GoalTable.uniqueTitle() + " from " + TaskTable.TABLE_NAME + " t LEFT JOIN " + GoalTable.TABLE_NAME + " g"
                + " ON t." + TaskTable.GOAL_COLUMN + " = g." + GoalTable._ID
                + " WHERE t." + TaskTable.STATUS_COLUMN + " != '" + TaskStatus.DONE.toString() + "'"
                + " AND t." + criteria
                + " ORDER BY " + TaskTable.ORDER_COLUMN + " ASC";
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

    public boolean removeSuccessCriteria(Metric metric) {
        String whereClause = MetricTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(metric.getId()) };

        int i = db.delete(MetricTable.TABLE_NAME, whereClause, whereArgs);
        removeTasksBySuccessCriteriaId(metric.getId());

        return i == 1;
    }

    private void removeTasksBySuccessCriteriaId(long successCriteriaId) {
        String whereClause = TaskTable.COMPLETES_COLUMN + EQUALSQ;
        String[] whereArgs = { Long.toString(successCriteriaId)};

        db.delete(TaskTable.TABLE_NAME, whereClause, whereArgs);
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

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, GoalTable.getorderAsc());
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

        updateOrder(TaskTable.TABLE_NAME, currentGoal.getTasks());

        return db.update(GoalTable.TABLE_NAME, cv, whereClause, null);
    }

    public void updateTask(Task task) {
        ContentValues newValues = new ContentValues();

        String whereClause = TaskTable._ID + EQUALSQ;
        String[] whereArgs = {Long.toString(task.getId())};

        if (task.getDue() != null) {
            newValues.put(TaskTable.DUE_COLUMN, task.getDue().getTime());
        }

        newValues.put(TaskTable.TITLE_COLUMN, task.getTitle());
        newValues.put(TaskTable.GOAL_COLUMN, task.getGoal());
        newValues.put(TaskTable.COMPLETES_COLUMN, task.getCompletes());
        newValues.put(TaskTable.TASK_COMMITMENT_COLUMN, task.getTaskCommitment());
        newValues.put(TaskTable.STATUS_COLUMN, task.getTaskStatus().toString());

        long result = db.update(TaskTable.TABLE_NAME, newValues, whereClause, whereArgs);
        task.setId(result);
    }

    public void updateOrder(String tableName, List<? extends BaseModel> list) {
        for (int i = 0; i < list.size(); i++) {
            BaseModel currentTask = list.get(i);
            String whereClause = TaskTable._ID + " = " + currentTask.getId();

            ContentValues cv = new ContentValues();
            cv.put(BaseTable.ORDER_COLUMN, i+1);

            db.update(tableName, cv, whereClause, null);
        }
    }

    public List<Task> getTodaysTasks() {
        List<Task> tasks = getTasksByGoalId(Task.SCRATCH_ID);
        tasks = filterTaskBySnoozeTime(tasks, (new Date()).getTime());

        List<Task> todaysTasks = getTasksByPriority(); //getTasksBeforeDueDate(DateUtils.today());
        for (int i = todaysTasks.size() - 1; i >= 0; i--) {
            Task t = todaysTasks.get(i);
            if (t.getGoal() == BaseModel.SCRATCH_ID) {
                todaysTasks.remove(i);
            }
        }

        tasks.addAll(todaysTasks);
        return tasks;
    }

    public List<Task> getTasksByPriority() {
        Date now = new Date();
        long timeOfDay = DateUtils.getLongTime(now.getHours(), now.getMinutes());

        String query = "Select ta.*, g." + GoalTable.TITLE_COLUMN + " as " + GoalTable.uniqueTitle() + " from " + TaskTable.TABLE_NAME + " ta"
                + " LEFT JOIN " + GoalTable.TABLE_NAME + " g on"
                    + " ta." + TaskTable.GOAL_COLUMN + " = g." + GoalTable._ID
                + " LEFT JOIN " + ThemeTable.TABLE_NAME + " th on"
                    + " g." + GoalTable.THEME_COLUMN + " = th." + ThemeTable._ID
                + " LEFT JOIN " + ActiveHoursTable.TABLE_NAME + " ah on"
                    + " th." + ThemeTable._ID + " = ah." + ActiveHoursTable.THEME_COLUMN
                    + " AND ah." + ActiveHoursTable.DAY_COLUMN + " = " + DateUtils.today().get(Calendar.DAY_OF_WEEK)
                + " WHERE ta." + TaskTable.GOAL_COLUMN + " >= 0"
                    + " AND ta." + TaskTable.STATUS_COLUMN + " != '" + TaskStatus.DONE + "'"
                    + " AND ta." + TaskTable.SNOOZE_COLUMN + " < " + now.getTime()
                    + " AND ah." + ActiveHoursTable.START_COLUMN + " < " + timeOfDay
                    + " AND ah." + ActiveHoursTable.END_COLUMN + " > " + timeOfDay
                + " ORDER BY th." + ThemeTable.ORDER_COLUMN
                + " , g." + GoalTable.ORDER_COLUMN
                + " , ta." + TaskTable.ORDER_COLUMN
                + " ASC";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        List<Task> tasks = new ArrayList<>();
        while (!c.isAfterLast()) {
            tasks.add(new Task(c));
            c.moveToNext();
        }

        return tasks;
    }

    public Metric completeTask(Task completedTask) {
        setTask(completedTask, TaskStatus.DONE);
        return updateMetric(completedTask);
    }

    private void setTask(Task currentTask, TaskStatus taskStatus) {
        ContentValues cv = new ContentValues();
        cv.put(TaskTable.STATUS_COLUMN, taskStatus.toString());

        String whereClause = TaskTable._ID + " = " + currentTask.getId();

        db.update(TaskTable.TABLE_NAME, cv, whereClause, null);
    }

    private Metric updateMetric(Task t) {
        Metric currentMetric = t.getMetric();

        if (currentMetric == null) {
            return null;
        }

        currentMetric.updateProgress(t.getTaskCommitment());

        return updateMetric(currentMetric);
    }

    private Metric updateMetric(Metric currentMetric) {
        if (currentMetric == null) {
            return currentMetric;
        }

        ContentValues cv = new ContentValues();
        cv.put(MetricTable.PROGRESS_COLUMN, currentMetric.getProgress());

        String whereClause = MetricTable._ID + " = " + currentMetric.getId();

        db.update(MetricTable.TABLE_NAME, cv, whereClause, null);
        return currentMetric;
    }

    public void updateActiveHours(long themeId, Map<CheckboxGroup.CheckboxGroupIndex, CheckboxGroup> checkBoxGroups) {

        CheckboxGroup weekends = checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.WEEKENDS);
        CheckboxGroup weekdays = checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.WEEKDAYS);

        if (weekdays.isChecked()) {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKDAYS, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.MONDAY, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.TUESDAY, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEDNESDAY, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.THURSDAY, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.FRIDAY, weekdays);
        } else {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKDAYS, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.MONDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.MONDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.TUESDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.TUESDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEDNESDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.WEDNESDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.THURSDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.THURSDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.FRIDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.FRIDAY));
        }

        if (weekends.isChecked()) {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKENDS, weekends);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SATURDAY, weekends);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SUNDAY, weekends);
        } else {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKENDS, weekends);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SATURDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.SATURDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SUNDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.SUNDAY));
        }
    }

    private void updateActiveHour(long themeId, CheckboxGroup.CheckboxGroupIndex index, CheckboxGroup currentGroup) {
        ContentValues cv = new ContentValues();
        cv.put(ActiveHoursTable.THEME_COLUMN, themeId);
        cv.put(ActiveHoursTable.DAY_COLUMN, index.getValue());
        cv.put(ActiveHoursTable.START_COLUMN, currentGroup.getStartTime());
        cv.put(ActiveHoursTable.END_COLUMN, currentGroup.getEndTime());

        db.insertWithOnConflict(ActiveHoursTable.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Map<CheckboxGroup.CheckboxGroupIndex, CheckboxGroup> getActiveHours(long themeId, Map<CheckboxGroup.CheckboxGroupIndex, CheckboxGroup> checkBoxGroups) {
        String selection = ActiveHoursTable.THEME_COLUMN + EQUALSQ;
        String[] selectionArgs = { Long.toString(themeId) };

        Cursor c = db.query(ActiveHoursTable.TABLE_NAME, ActiveHoursTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            CheckboxGroup.CheckboxGroupIndex day = CheckboxGroup.CheckboxGroupIndex.valueOf(c.getLong(c.getColumnIndexOrThrow(ActiveHoursTable.DAY_COLUMN)));
            CheckboxGroup group = checkBoxGroups.get(day);
            group.setTime(c);
            checkBoxGroups.put(day, group);
            c.moveToNext();
        }

        return checkBoxGroups;
    }

    public void snooze(long taskId, Date snoozeTime) {
        ContentValues cv = new ContentValues();
        cv.put(TaskTable.SNOOZE_COLUMN, snoozeTime.getTime());

        String whereClause = TaskTable._ID + " = " + taskId;

        db.update(TaskTable.TABLE_NAME, cv, whereClause, null);
        return;
    }
}
