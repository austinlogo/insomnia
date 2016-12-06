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
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.task.NewTaskDialog;
import northstar.planner.presentation.task.TaskActivity;

public class GoalActivity
        extends BaseActivity
        implements GoalFragment.GoalFragmentListener, NewTaskDialog.NewTaskDialogListener {

    @BindView(R.id.activity_goal_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_goal_fragment)
    FrameLayout goalFragmentLayout;

    @BindView(R.id.activity_goal_add_task)
    FrameLayout goalAddTaskLayout;

    private GoalFragment goalFragment;
    private AddTaskFragment addTaskFragment;
    private PlannerSqliteDAO dao;
    private Goal currentGoal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        dao = new PlannerSqliteDAO();
        currentGoal = dao.getGoal(getIntent().getExtras().getLong(GoalTable._ID));
        goalFragment = GoalFragment.newInstance(currentGoal);
        addTaskFragment = AddTaskFragment.newInstance("");


        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_goal_fragment, goalFragment)
                .add(R.id.activity_goal_add_task, addTaskFragment)
                .commit();

        finishDrawerInit(this, mDrawerLayout, currentGoal.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentGoal = dao.getGoal(currentGoal.getId());
        goalFragment.initViews(currentGoal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentGoal.updateGoal(goalFragment.getNewGoalValues());
        dao.updateGoal(currentGoal);
    }

    @Override
    public SuccessCriteria addSuccessCriteria(SuccessCriteria sc) {
        sc.setGoal(currentGoal);
        sc = dao.addSuccessCriteria(sc);
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
    public void createTask(String newTaskTitle, SuccessCriteriaListAdapter successCriteriasAdapter) {
        goalFragmentLayout.setVisibility(View.GONE);
        goalAddTaskLayout.setVisibility(View.VISIBLE);
        addTaskFragment.updateFragmentValues(newTaskTitle, successCriteriasAdapter);
    }

    @Override
    public View getRootView() {
        return mDrawerLayout;
    }

    @Override
    protected void deleteAction() {
        dao.removeGoal(currentGoal.getId());
        finish();
    }

    @Override
    protected void editAction() {
        getSupportActionBar().setTitle(goalFragment.getNewGoalValues().getTitle());
        goalFragment.toggleEditing();
    }

    @Override
    public Task setResult(Task task) {
        task.setGoal(currentGoal);
        task = dao.addTask(task);
//        currentGoal.getTasks().add(0, task);
        ((TaskRecyclerViewAdapter) goalFragment.tasksRecyclerView.getAdapter()).addItem(task);
        return task;
    }
}
