 package northstar.planner.presentation.goal;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.presentation.success.AddMetricFragment;
import northstar.planner.presentation.task.TaskBasedActivity;
import northstar.planner.presentation.today.AddOverlayFragment;

public class GoalActivity
        extends     TaskBasedActivity
        implements  GoalFragment.GoalFragmentListener,
                    AddTaskFragment.AddTaskFragmentListener,
//                    GoalFragment.TaskActionListener,
                    AddMetricFragment.AddMetricFragmentListener,
                    AddOverlayFragment.AddOverlayListener {

    @BindView(R.id.activity_goal_add_overlay)
    FrameLayout addOverlay;

    private GoalFragment goalFragment;
    private AddMetricFragment addMetricFragment;
    private Goal currentGoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        goalFragment = (GoalFragment) super.onCreate(savedInstanceState, GoalTable.TABLE_NAME);
        addMetricFragment = AddMetricFragment.newInstance();
        currentGoal = (Goal) getMainModel();
        addOverlayFragment = AddOverlayFragment.newInstance();
        ButterKnife.bind(this);

        finishDrawerInit(this, (DrawerLayout) getRootView(), currentGoal.getTitle());
    }

    @Override
    public void updateActivity() {
        Goal updatedGoal = getDao().getGoal(currentGoal.getId());

        if (updatedGoal != null) {
            currentGoal = updatedGoal;
            goalFragment.initViews(currentGoal);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentGoal.updateGoal(goalFragment.getNewGoalValues());
        getDao().updateGoal(currentGoal);
    }

    @Override
    public void startAddWorkflow() {
        attachAddOverlayToActivity();
        addOverlay.setVisibility(View.VISIBLE);
        addOverlay.requestLayout();
        addOverlay.bringToFront();
        addOverlay.requestFocus();
    }

    @Override
    public void removeMetricWorkflow(final Metric currentMetric) {
        DeleteMetricDialogFragment dialogFragment = DeleteMetricDialogFragment.newInstance(currentMetric.getId(), new DialogFragmentOnDismissCallback() {
            @Override
            public void onDismiss() {
                currentGoal.getMetrics().remove(currentMetric);
                refreshGoal();
            }
        });
        dialogFragment.show(getFragmentManager(), "");
    }

    private void refreshGoal() {
        currentGoal = getDao().getGoal(currentGoal.getId());
        goalFragment.initViews(currentGoal);
    }

    @Override
    protected void deleteAction() {

        getDao().removeGoal(currentGoal.getId());
        finish();
    }

    @Override
    public void editAction() {
        getSupportActionBar().setTitle(goalFragment.getNewGoalValues().getTitle());
        goalFragment.toggleEditing();
    }

    @Override
    protected void storeNewTask(Task newTask) {
        if (newTask.getMetric() != null) {
            newTask.getMetric().adjustProgress();
            goalFragment.updateMetricOnUI(newTask.getMetric());
        }

        newTask.setGoal(currentGoal);
        newTask = getDao().addTask(newTask);
        currentGoal.getTasks().add(newTask);
        goalFragment.updateViews(this, currentGoal);
    }

    private void setAddFragment(Fragment fragmentToBeVisible) {
        addFragmentIfNotAlreadyAdded(fragmentToBeVisible);
        setFragmentVisible(addTaskLayout);
    }

    private void addFragmentIfNotAlreadyAdded(Fragment fragmentToBeVisible) {
        if (!fragmentToBeVisible.isAdded()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_goal_add_task, fragmentToBeVisible)
                    .commit();
        }
    }

    @Override
    public Metric addandStoreMetric(Metric sc) {
        setFragmentVisible(mainFragmentLayout);

        sc.setGoal(currentGoal.getId());
        sc = getDao().addMetric(sc);
        currentGoal.addMetric(sc);
        goalFragment.initViews(currentGoal);
        return sc;
    }

    @Override
    public void showAddTaskWorkflow() {
        removeAddOverlayFromActivity();
        setAddFragment(addTaskFragment);
        addTaskFragment.updateFragmentValues(goalFragment.getMetricsSpinnerAdapter());
    }

    @Override
    public void showAddMetricWorkflow() {
        removeAddOverlayFromActivity();
        setAddFragment(addMetricFragment);
    }

    @Override
    public void dismissOverlay() {
        removeAddOverlayFromActivity();
    }

    @Override
    public void completeTask(Task t) {
        super.completeTask(t);
        currentGoal.getTasks().remove(t);
    }

    @Override
    public void removeTask(int position, Task t) {
        super.removeTask(position, t);
        currentGoal.getTasks().remove(t);
    }
}
