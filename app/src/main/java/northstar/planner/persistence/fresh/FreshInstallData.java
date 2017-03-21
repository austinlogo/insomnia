package northstar.planner.persistence.fresh;

import android.app.Application;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.MetricType;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.models.checkboxgroup.CheckboxGroup;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.utils.DateUtils;

public class FreshInstallData {

    private Application ctx;
    private List<Theme> freshInstallThemes;

    public FreshInstallData(Application ctx) {
        this.ctx = ctx;
    }

    public void professionalThemeConstructor(PlannerSqliteGateway dao) {
        Theme theme = constructProfessionalTheme();
        theme = dao.addTheme(theme, 0);

        Goal goal = constructGoalFinishArticle(theme.getId());
        long id = dao.addGoal(goal);
        goal.setId(id);

        Metric approvalsNeeded = constructMetricApprovals(goal.getId());
        approvalsNeeded.adjustProgress();
        approvalsNeeded = dao.addMetric(approvalsNeeded);

        Metric quotesMetric = constructMetricQuotes(goal.getId());
        quotesMetric = dao.addMetric(quotesMetric);

        Task andyTask = constructGetAndyQuote(goal.getId(), quotesMetric);
        andyTask = dao.addTask(andyTask);

        Task chrisTask = constructGetBossApprovalTask(goal.getId(), approvalsNeeded);
        dao.addTask(chrisTask);

        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.WEEKDAYS.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.WEEKENDS.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.MONDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.TUESDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.WEDNESDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.THURSDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.FRIDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.SATURDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
        dao.updateActiveHour(theme.getId(), CheckboxGroup.CheckboxGroupIndex.SUNDAY.getValue(),DateUtils.getTimeOfDay(0, 0), DateUtils.getTimeOfDay(23, 59));
    }

    private Theme constructProfessionalTheme() {
        String professionalThemeTitle = ctx.getResources().getString(R.string.professional_title);
        String professionalThemeDescription = ctx.getResources().getString(R.string.professional_description);
        return new Theme(professionalThemeTitle, professionalThemeDescription);
    }

    private Goal constructGoalFinishArticle(long themeId) {
        String title = ctx.getResources().getString(R.string.professional_goal_article_title);
        String description = ctx.getResources().getString(R.string.professional_goal_article_description);
        return new Goal(themeId, title, description);
    }

    private Metric constructMetricApprovals(long goalId) {
        String title = ctx.getResources().getString(R.string.professional_metric_article_title);

        Metric decMetric = Metric.newInstance(title, 0, MetricType.DECREMENTAL);
        decMetric.setGoal(goalId);

        return decMetric;
    }

    private Metric constructMetricQuotes(long goalId) {
        String title = ctx.getResources().getString(R.string.professional_metric_article_title_2);

        Metric incMetric = Metric.newInstance(title, 3, MetricType.INCREMENTAL);
        incMetric.setGoal(goalId);
        incMetric.updateProgress(1);

        return incMetric;
    }

    private Task constructGetAndyQuote(long goalId, Metric metric) {
        String title = ctx.getResources().getString(R.string.professional_task_quote_andy);

        Task task = new Task();
        task.setGoal(goalId);
        task.setTitle(title);
        task.setMetric(metric);

        return task;
    }

    private Task constructGetBossApprovalTask(long goalId, Metric metric) {
        String title = ctx.getResources().getString(R.string.professional_task_approval);

        Task task = new Task();
        task.setGoal(goalId);
        task.setTitle(title);
        task.setMetric(metric);

        return task;
    }

    public void personalThemeConstructor(PlannerSqliteGateway dao) {
        Theme theme = constructPersonalTheme();
        theme = dao.addTheme(theme, 0);

        Goal goalGuitar = constructGoalLearnGuitar(theme.getId());
        long id = dao.addGoal(goalGuitar);
        goalGuitar.setId(id);

        Goal goalReadMore = constructGoalReadMore(theme.getId());
        long id2 = dao.addGoal(goalReadMore);
        goalReadMore.setId(id2);

        Task taskNewString = constructTaskNewStrings(goalGuitar.getId());
        dao.addTask(taskNewString);

        Task taskPractice = constructTaskAfternoonPractice(goalGuitar.getId());
        dao.addTask(taskPractice);

        Task taskReading = constructTaskFinishMistborn(goalReadMore.getId());
        dao.addTask(taskReading);
    }

    private Theme constructPersonalTheme() {
        String professionalThemeTitle = ctx.getResources().getString(R.string.personal_title);
        String professionalThemeDescription = ctx.getResources().getString(R.string.personal_description);
        return new Theme(professionalThemeTitle, professionalThemeDescription);
    }

    private Goal constructGoalLearnGuitar(long themeId) {
        String title = ctx.getResources().getString(R.string.personal_goal_guitar_title);
        return new Goal(themeId, title, "");
    }

    private Goal constructGoalReadMore(long themeId) {
        String title = ctx.getResources().getString(R.string.personal_goal_reading_title);
        return new Goal(themeId, title, "");
    }

    private Task constructTaskNewStrings(long goalId) {
        String title = ctx.getResources().getString(R.string.personal_task_strings_title);
        Task task = new Task();
        task.setGoal(goalId);
        task.setTitle(title);
        return task;
    }

    private Task constructTaskAfternoonPractice(long goalId) {
        String title = ctx.getResources().getString(R.string.personal_task_practice_title);
        Task task = new Task();
        task.setGoal(goalId);
        task.setTitle(title);
        return task;
    }


    private Task constructTaskFinishMistborn(long goalId) {
        String title = ctx.getResources().getString(R.string.personal_task_reading_title);
        Task task = new Task();
        task.setGoal(goalId);
        task.setTitle(title);
        return task;
    }
}
