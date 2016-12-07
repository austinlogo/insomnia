package northstar.planner.presentation.goal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import butterknife.ButterKnife;
import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.task.TaskBasedActivity;

public class GoalActivity
        extends TaskBasedActivity
        implements GoalFragment.GoalFragmentListener, AddTaskFragment.AddTaskFragmentListener, GoalFragment.TaskActionListener {

    private GoalFragment goalFragment;
    private Goal currentGoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        goalFragment = (GoalFragment) super.onCreate(savedInstanceState, GoalTable.TABLE_NAME);
        currentGoal = (Goal) getMainModel();
        ButterKnife.bind(this);

        finishDrawerInit(this, (DrawerLayout) getRootView(), currentGoal.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        goalFragment.initViews(currentGoal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentGoal.updateGoal(goalFragment.getNewGoalValues());
        getDao().updateGoal(currentGoal);
    }

    @Override
    public SuccessCriteria addSuccessCriteria(SuccessCriteria sc) {
        sc.setGoal(currentGoal);
        sc = getDao().addSuccessCriteria(sc);
        currentGoal.addSuccessCriteria(sc);
        return sc;
    }

    @Override
    public boolean removeSuccessCriteria(SuccessCriteria sc) {
        return getDao().removeSuccessCriteria(sc);
    }

    @Override
    public void createTask(String newTaskTitle, SuccessCriteriaSpinnerAdapter successCriteriasAdapter) {
        setFragmentVisible(goalAddTaskLayout);
        addTaskFragment.updateFragmentValues(newTaskTitle, successCriteriasAdapter);
    }

    @Override
    protected void deleteAction() {
        getDao().removeGoal(currentGoal.getId());
        finish();
    }

    @Override
    protected void editAction() {
        getSupportActionBar().setTitle(goalFragment.getNewGoalValues().getTitle());
        goalFragment.toggleEditing();
    }

    @Override
    protected void storeNewTask(Task newTask) {
        newTask.setGoal(currentGoal);
        newTask = getDao().addTask(newTask);
        currentGoal.getTasks().add(0, newTask);
        ((TaskRecyclerViewAdapter) goalFragment.tasksRecyclerView.getAdapter()).addItem(newTask);
    }
}
