package northstar.planner.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import northstar.planner.models.BaseModel;
import northstar.planner.models.DependencyRecord;
import northstar.planner.models.DependencyStatus;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.TaskStatus;
import northstar.planner.models.Theme;
import northstar.planner.models.checkboxgroup.CheckboxGroup;
import northstar.planner.models.drawer.ShallowModel;
import northstar.planner.models.tables.ActiveHoursTable;
import northstar.planner.models.tables.BaseTable;
import northstar.planner.models.tables.DependencyTable;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.MetricTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.utils.DateUtils;



public class PlannerSqliteGateway extends BaseGateway {

    private RecurrenceGateway recurrenceGateway;
    private SQLiteDatabase db;

    public PlannerSqliteGateway(Context ctx) {
        recurrenceGateway = new RecurrenceGateway(ctx);
        db = PlannerDBHelper.getDbInstance();
    }

    public PlannerSqliteGateway() {
        db = PlannerDBHelper.getDbInstance();
    }

    public Theme addTheme(Theme newTheme, int position) {
        ContentValues newValues = new ContentValues();

        newValues.put(ThemeTable.TITLE_COLUMN, newTheme.getTitle());
        newValues.put(ThemeTable.DESCRIPTION_COLUMN, newTheme.getDescription());
        newValues.put(ThemeTable.ORDER_COLUMN, position);

        long newId = db.insert(ThemeTable.TABLE_NAME, null, newValues);
        newTheme.setId(newId);

        // set Active hours to always.
        insertContextualActiveHours(newTheme.getId(), CheckboxGroup.CheckboxGroupIndex.WEEKDAYS.getValue(), DateUtils.getLongTimeOfDay(0, 0), DateUtils.getLongTimeOfDay(23, 0));
        insertContextualActiveHours(newTheme.getId(), CheckboxGroup.CheckboxGroupIndex.WEEKENDS.getValue(), DateUtils.getLongTimeOfDay(0, 0), DateUtils.getLongTimeOfDay(23, 0));


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

        updateMetricInDb(task.getMetric());

        if (task.getDue() != null) {
            newValues.put(TaskTable.DUE_COLUMN, task.getDue().getMillis());
        } else {
            newValues.put(TaskTable.DUE_COLUMN, 0);
        }

        if (task.getSnooze() != null) {
            newValues.put(TaskTable.SNOOZE_COLUMN, task.getSnooze().getMillis());
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
        Theme result = constructThemeFromCursor(c);
        c.close();
//        result.setGoals(getGoalsByThemeId(themeId));
        return result;
    }

    private Theme constructThemeFromCursor(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }
        Theme result = new Theme(c);
        result.setGoals(getGoalsByThemeId(result.getId()));
        return result;
    }

    public Goal getGoal(long goalId) {
        String selection = GoalTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(goalId) };

        Cursor c = db.query(GoalTable.TABLE_NAME, GoalTable.projection, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        Goal result = constructGoalFromCursor(c);
        c.close();
        return result;
    }

    private Goal constructGoalFromCursor(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        Goal result = new Goal(c);
        result.setChildren(getMetrics(result.getId()), getTasksByGoalId(result.getId()));
        return result;
    }

    public Task getShallowTask(long taskId) {
        String query = constructAllTaskQuery(TaskTable._ID + " = " + taskId);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Task result = new Task(c);
        c.close();
        return result;
    }

    public Task getTask(long taskId) {
        String query = constructOpenTaskQuery(TaskTable._ID + " = " + taskId);
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Task result = constructTaskFromCursor(c);
        c.close();
        return result;
    }

    private Task constructTaskFromCursor(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        Task result = new Task(c);
        result.setMetric(getMetric(result.getCompletes()));
        result.setDependentTask(getDependentTask(result.getId()));
        result.setRecurrenceSchedule(recurrenceGateway.getRecurrence(result));
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
        String query = constructOpenTaskQuery(TaskTable.GOAL_COLUMN + " = " + goalId);

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        List<Task> result = new ArrayList<>();
        while(!c.isAfterLast()) {
            result.add(constructTaskFromCursor(c));
            c.moveToNext();
        }
        c.close();
        return result;
    }

    private List<Task> filterTaskBySnoozeTime(List<Task> tasks, long currentTime) {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task currentTask : tasks) {
            if (currentTask.hasSnoozed() && currentTask.getSnooze().getMillis() >= currentTime) { // still snoozing
                continue;
            }

            filteredTasks.add(currentTask);
        }
        return filteredTasks;
    }

    private String constructOpenTaskQuery(String criteria) {
        // TODO: Consolidate this select
        return "Select t.*, g.Title as " + GoalTable.uniqueTitle() + " from " + TaskTable.TABLE_NAME + " t LEFT JOIN " + GoalTable.TABLE_NAME + " g"
                + " ON t." + TaskTable.GOAL_COLUMN + " = g." + GoalTable._ID
                + " WHERE t." + TaskTable.STATUS_COLUMN + " != '" + TaskStatus.DONE.toString() + "'"
                + " AND t." + criteria
                + " ORDER BY " + TaskTable.ORDER_COLUMN + " ASC";
    }

    private String constructAllTaskQuery(String criteria) {
        // TODO: Consolidate this select
        return "Select t.*, g.Title as " + GoalTable.uniqueTitle() + " from " + TaskTable.TABLE_NAME + " t LEFT JOIN " + GoalTable.TABLE_NAME + " g"
                + " ON t." + TaskTable.GOAL_COLUMN + " = g." + GoalTable._ID
                + " WHERE t." + criteria
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

    public boolean removeGoal(long goalId) {
        String whereClause = GoalTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(goalId) };
        List<Task> goalTask = getTasksByGoalId(goalId);

        int i = db.delete(GoalTable.TABLE_NAME, whereClause, whereArgs);
        for (Task task : goalTask) {
            deleteTask(task);
        }
        return i == 1;
    }

    public boolean removeMetric(int metricId) {
        String whereClause = MetricTable._ID + EQUALSQ;
        String[] whereArgs = { Long.toString(metricId) };

        int i = db.delete(MetricTable.TABLE_NAME, whereClause, whereArgs);
        removeTasksBySuccessCriteriaId(metricId);

        return i == 1;
    }

    private void removeTasksBySuccessCriteriaId(long successCriteriaId) {
        String whereClause = TaskTable.COMPLETES_COLUMN + EQUALSQ;
        String[] whereArgs = { Long.toString(successCriteriaId)};

        db.delete(TaskTable.TABLE_NAME, whereClause, whereArgs);
    }
    

    public boolean deleteTask(Task task) {
        boolean result = removeTaskFromDb(task.getId());
        updateMetricInDb(task);
        return result;
    }

    private boolean removeTaskFromDb(long taskId) {
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
            results.add(constructGoalFromCursor(c));
            c.moveToNext();
        }

        return results;
    }

    public List<Theme> getAllThemes() {
        Cursor c = db.query(ThemeTable.TABLE_NAME, ThemeTable.projection, null, null, null, null, ThemeTable.getorderAsc());
        c.moveToFirst();

        List<Theme> resultList = new ArrayList<>();
        while(!c.isAfterLast()) {
            resultList.add(constructThemeFromCursor(c));
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
            newValues.put(TaskTable.DUE_COLUMN, task.getDue().getMillis());
        }

        if (task.getSnooze() != null) {
            newValues.put(TaskTable.SNOOZE_COLUMN, task.getSnooze().getMillis());
        }


        if (task.getReminder() != null) {
            newValues.put(TaskTable.REMINDER_COLUMN, task.getReminder().getMillis());
        }

        newValues.put(TaskTable.TITLE_COLUMN, task.getTitle());
        newValues.put(TaskTable.GOAL_COLUMN, task.getGoal());
        newValues.put(TaskTable.COMPLETES_COLUMN, task.getCompletes());
        newValues.put(TaskTable.TASK_COMMITMENT_COLUMN, task.getTaskCommitment());
        newValues.put(TaskTable.STATUS_COLUMN, task.getTaskStatus().toString());

        db.update(TaskTable.TABLE_NAME, newValues, whereClause, whereArgs);
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
        tasks = filterTaskBySnoozeTime(tasks, (new DateTime()).getMillis());

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
        DateTime now = new DateTime();
        long timeOfDay = DateUtils.getLongTimeOfDay(now.getHourOfDay(), now.getMinuteOfHour());

        String query = "Select ta.*, g." + GoalTable.TITLE_COLUMN + " as " + GoalTable.uniqueTitle()
                + " from " + TaskTable.TABLE_NAME + " ta"
                + " LEFT JOIN " + GoalTable.TABLE_NAME + " g on"
                    + " ta." + TaskTable.GOAL_COLUMN + " = g." + GoalTable._ID
                + " LEFT JOIN " + ThemeTable.TABLE_NAME + " th on"
                    + " g." + GoalTable.THEME_COLUMN + " = th." + ThemeTable._ID
                + " LEFT JOIN " + ActiveHoursTable.TABLE_NAME + " ah on"
                    + " th." + ThemeTable._ID + " = ah." + ActiveHoursTable.THEME_COLUMN
                    + " AND ah." + ActiveHoursTable.DAY_COLUMN + " = " + DateUtils.today().getDayOfWeek()
                + " LEFT JOIN " + DependencyTable.TABLE_NAME + " dep on"
                    + " ta." + TaskTable._ID + " = dep." + DependencyTable._ID
                + " WHERE ta." + TaskTable.GOAL_COLUMN + " >= 0"
                    + " AND ta." + TaskTable.STATUS_COLUMN + " != '" + TaskStatus.DONE + "'"
                    + " AND ta." + TaskTable.SNOOZE_COLUMN + " < " + now.getMillis()
                    + " AND ah." + ActiveHoursTable.START_COLUMN + " < " + timeOfDay
                    + " AND ah." + ActiveHoursTable.END_COLUMN + " > " + timeOfDay
                    + " AND ("
                        + " ifnull(dep." + DependencyTable.DEPENDENCY_STATUS + ", '') = ''"
                        + " OR dep." + DependencyTable.DEPENDENCY_STATUS + " != '" + DependencyStatus.BLOCKED + "')"
                + " ORDER BY th." + ThemeTable.ORDER_COLUMN
                + " , g." + GoalTable.ORDER_COLUMN
                + " , ta." + TaskTable.ORDER_COLUMN
                + " ASC";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        List<Task> tasks = new ArrayList<>();
        while (!c.isAfterLast()) {
            tasks.add(constructTaskFromCursor(c));
            c.moveToNext();
        }

        return tasks;
    }

    public Metric completeTask(Task completedTask) {
        setTask(completedTask, TaskStatus.DONE);
        return updateMetricInDb(completedTask);
    }

    private void setTask(Task currentTask, TaskStatus taskStatus) {
        ContentValues cv = new ContentValues();
        cv.put(TaskTable.STATUS_COLUMN, taskStatus.toString());

        String whereClause = TaskTable._ID + " = " + currentTask.getId();

        db.update(TaskTable.TABLE_NAME, cv, whereClause, null);
    }

    public Metric updateMetricInDb(Task t) {
        Metric currentMetric = t.getMetric();

        if (currentMetric == null) {
            return null;
        }

        currentMetric.updateProgress(t.getTaskCommitment());
        return updateMetricInDb(currentMetric);
    }

    private Metric updateMetricInDb(Metric currentMetric) {
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
            insertContextualActiveHours(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKDAYS.getValue(), weekdays.getStartTime(), weekdays.getEndTime());
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKDAYS, weekdays);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.MONDAY, weekdays);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.TUESDAY, weekdays);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEDNESDAY, weekdays);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.THURSDAY, weekdays);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.FRIDAY, weekdays);
        } else {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKDAYS, weekdays);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.MONDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.MONDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.TUESDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.TUESDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEDNESDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.WEDNESDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.THURSDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.THURSDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.FRIDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.FRIDAY));
        }

        if (weekends.isChecked()) {
            insertContextualActiveHours(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKENDS.getValue(), weekends.getStartTime(), weekends.getEndTime());
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKENDS, weekends);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SATURDAY, weekends);
//            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SUNDAY, weekends);
        } else {
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEEKENDS, weekends);
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SATURDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.SATURDAY));
            updateActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SUNDAY, checkBoxGroups.get(CheckboxGroup.CheckboxGroupIndex.SUNDAY));
        }
    }

    public void insertContextualActiveHours(long themeId, long day, long start, long end) {
        insertSingleRowActiveHour(themeId, day, start, end);

        if (day == CheckboxGroup.CheckboxGroupIndex.WEEKDAYS.getValue()) {
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.MONDAY.getValue(), start, end);
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.TUESDAY.getValue(), start, end);
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.WEDNESDAY.getValue(), start, end);
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.THURSDAY.getValue(), start, end);
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.FRIDAY.getValue(), start, end);
        } else if (day == CheckboxGroup.CheckboxGroupIndex.WEEKENDS.getValue()) {
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SATURDAY.getValue(), start, end);
            insertSingleRowActiveHour(themeId, CheckboxGroup.CheckboxGroupIndex.SUNDAY.getValue(), start, end);
        }
    }

    public void updateActiveHour(long themeId, long day, long start, long end) {
        insertSingleRowActiveHour(themeId, day, start, end);
    }

    private void updateActiveHour(long themeId, CheckboxGroup.CheckboxGroupIndex index, CheckboxGroup currentGroup) {
        insertSingleRowActiveHour(themeId, index.getValue(), currentGroup.getStartTime(), currentGroup.getEndTime());
    }

    private void insertSingleRowActiveHour(long themeId, long day, long start, long end) {
        ContentValues cv = new ContentValues();
        cv.put(ActiveHoursTable.THEME_COLUMN, themeId);
        cv.put(ActiveHoursTable.DAY_COLUMN, day);
        cv.put(ActiveHoursTable.START_COLUMN, start);
        cv.put(ActiveHoursTable.END_COLUMN, end);

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

    public void updateDependencyTable(Task currentTask, Task dependentTask) {
        ContentValues cv = new ContentValues();
        cv.put(DependencyTable._ID, currentTask.getId());
        cv.put(DependencyTable.DEPENDS_ON, dependentTask.getId());
        cv.put(DependencyTable.DEPENDENCY_STATUS, DependencyStatus.BLOCKED.value());

        db.insertWithOnConflict(DependencyTable.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ShallowModel getDependentTask(long taskId) {
        ContentValues cv = new ContentValues();
        cv.put(TaskTable._ID, taskId);

        String selection = TaskTable._ID + EQUALSQ;
        String[] selectionArgs = { Long.toString(taskId) };

        Cursor c = db.query(DependencyTable.TABLE_NAME, DependencyTable.projection, selection, selectionArgs, null, null, null);

        c.moveToFirst();

        while (!c.isAfterLast()) {
            DependencyRecord dependencyRecord = new DependencyRecord(c);
            Task t = getShallowTask(dependencyRecord.getDependsOn());
            return new ShallowModel(t);
        }
        return null;
    }

    public void removeDependency(long task) {
        String whereClause = DependencyTable._ID + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(task)};

        db.delete(DependencyTable.TABLE_NAME, whereClause, whereArgs);
    }

    public void removeBlockers(long task) {
        String whereClause = DependencyTable.DEPENDS_ON + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(task)};

        db.delete(DependencyTable.TABLE_NAME, whereClause, whereArgs);
    }

    public int updateDependencyOnComplete(long dependency) {
        ContentValues cv = new ContentValues();
        cv.put(DependencyTable.DEPENDENCY_STATUS, DependencyStatus.UNBLOCKED.value());

        String whereClause = DependencyTable.DEPENDS_ON + EQUALSQ;
        String[] whereArgs = new String[]{ Long.toString(dependency)};

        int i = db.updateWithOnConflict(DependencyTable.TABLE_NAME, cv, whereClause, whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
        return i;
    }

    public Map<Long, List<DependencyRecord>> buildDependencyGraph() {
        Cursor  cursor = db.rawQuery("select * from " + DependencyTable.TABLE_NAME,null);
        cursor.moveToFirst();
        List<DependencyRecord> records = new ArrayList<>();
        Map<Long, List<DependencyRecord>> dependencies = new HashMap<>();

        while(!cursor.isAfterLast()) {
            DependencyRecord dependencyRecord = new DependencyRecord(cursor);

            if (dependencies.containsKey(dependencyRecord.getId())) {
                dependencies.get(dependencyRecord.getId()).add(dependencyRecord);
            } else {
                dependencies.put(dependencyRecord.getId(), Arrays.asList(dependencyRecord));
            }

            cursor.moveToNext();
        }

        return dependencies;
    }
}
