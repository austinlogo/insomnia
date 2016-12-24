package northstar.planner.presentation.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.goal.GoalActivity;

public class TaskActivity
        extends BaseActivity
        implements TaskFragment.TaskFragmentListener {

    @BindView(R.id.activity_task_drawer_layout)
    DrawerLayout mDrawerLayout;

    PlannerSqliteGateway dao;
    Task currentTask;
    TaskFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        dao = new PlannerSqliteGateway();
        currentTask = (Task) getIntent().getExtras().getSerializable(TaskTable.TABLE_NAME);
        currentTask = getDao().getTask(currentTask.getId());
        mFragment = TaskFragment.newInstance(getIntent().getExtras());

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_task_fragment, mFragment)
                .commit();

        finishDrawerInit(this, mDrawerLayout, currentTask.getTitle());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mFragment.initUI(currentTask);
    }

    @Override
    public View getRootView() {
        return mDrawerLayout;
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentTask = mFragment.updateValues();
        dao.updateTask(currentTask);
    }

    @Override
    protected void deleteAction() {
        dao.removeTask(currentTask.getId());
        finish();
    }

    @Override
    public void editAction() {
        getSupportActionBar().setTitle(mFragment.updateValues().getTitle());
        mFragment.toggleEditing();
    }

    @Override
    public void navigateToGoal() {
        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra(GoalTable._ID, currentTask.getGoal());
//        i.putExtra(GoalTable.THEME_COLUMN, goal.getTheme());
//        i.putExtra(GoalTable.TITLE_COLUMN, goal.getTitle());
        startActivity(i);
    }
}
