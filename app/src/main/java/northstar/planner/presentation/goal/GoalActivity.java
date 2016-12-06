package northstar.planner.presentation.goal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.task.NewTaskDialog;
import northstar.planner.presentation.task.TaskActivity;
import northstar.planner.presentation.task.TaskBasedActivity;

public class GoalActivity
        extends TaskBasedActivity
        implements GoalFragment.GoalFragmentListener, AddTaskFragment.AddTaskFragmentListener {

    @BindView(R.id.activity_goal_fragment)
    FrameLayout goalFragmentLayout;

    @BindView(R.id.activity_goal_add_task)
    FrameLayout goalAddTaskLayout;

    private GoalFragment goalFragment;
    private AddTaskFragment addTaskFragment;
    private Goal currentGoal;
    private FrameLayout fragmentVisible;

    public GoalActivity() {

    }

    @Override
    public BaseFragment setMainFragment() {
        return null;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        goalFragment = (GoalFragment) super.onCreate(savedInstanceState, GoalTable.TABLE_NAME);
        ButterKnife.bind(this);
        
        currentGoal = getDao().getGoal(getIntent().getExtras().getLong(GoalTable._ID));

        finishDrawerInit(this, getmDrawerLayout(), currentGoal.getTitle());
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
    public void openTask(Task t) {
        Intent i = new Intent(this, TaskActivity.class);
        i.putExtra(TaskTable.TABLE_NAME, t);
        startActivity(i);
    }

    @Override
    public void removeTask(int position, Task t) {
        goalFragment.removeItemWorkflow(t, position);
    }

    @Override
    public void completeTask(Task t) {
        SuccessCriteria updatedSuccessCriteria = getDao().completeTask(t);
        goalFragment.updateSuccessCriteria(updatedSuccessCriteria);
    }

    @Override
    public void createTask(String newTaskTitle, SuccessCriteriaSpinnerAdapter successCriteriasAdapter) {
        setFragmentVisible(goalAddTaskLayout);
        addTaskFragment.updateFragmentValues(newTaskTitle, successCriteriasAdapter);
    }

    @Override
    public View getRootView() {
        return null;//mDrawerLayout;
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

//    @Override
//    public Task setResult(Task task) {
//        task.setGoal(currentGoal);
//        task = getDao().addTask(task);
////        currentGoal.getTasks().add(0, task);
//        ((TaskRecyclerViewAdapter) goalFragment.tasksRecyclerView.getAdapter()).addItem(task);
//        return task;
//    }

    @Override
    public void addNewTask(Task newTask) {
        hideKeyboard(goalAddTaskLayout);
        setFragmentVisible(goalFragmentLayout);
        storeNewTask(newTask);

    }

    private void storeNewTask(Task newTask) {
        newTask.setGoal(currentGoal);
        newTask = getDao().addTask(newTask);
        currentGoal.getTasks().add(0, newTask);
        ((TaskRecyclerViewAdapter) goalFragment.tasksRecyclerView.getAdapter()).addItem(newTask);
    }

    public void setFragmentVisible(FrameLayout fragmentVisible) {
        goalFragmentLayout.setVisibility(goalFragmentLayout.getId() == fragmentVisible.getId() ? View.VISIBLE : View.GONE);
        goalAddTaskLayout.setVisibility(goalAddTaskLayout.getId() == fragmentVisible.getId() ? View.VISIBLE : View.GONE);
    }
}
