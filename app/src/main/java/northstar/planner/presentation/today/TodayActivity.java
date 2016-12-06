package northstar.planner.presentation.today;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.goal.AddTaskFragment;
import northstar.planner.presentation.task.TaskFragment;

public class TodayActivity
        extends BaseActivity
        implements AddTaskFragment.AddTaskFragmentListener{

    @BindView(R.id.activity_goal_drawer_layout)
    DrawerLayout mDrawerLayout;

    PlannerSqliteDAO dao;
    TodayFragment mFragment;
    AddTaskFragment addTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        dao = new PlannerSqliteDAO();
        mFragment = TodayFragment.newInstance();
        addTaskFragment = AddTaskFragment.newInstance("");

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_goal_fragment, mFragment)
                .add(R.id.activity_goal_add_task, addTaskFragment)
                .commit();

        finishDrawerInit(this, mDrawerLayout, getString(R.string.drawer_item_today));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        List<Task> scratchTasks = getDao().getTasksByGoalId(BaseModel.SCRATCH_ID);
        List<Task> scratchTasks = getDao().getTodaysTasks();
        getDao().getTasksByGoalId(BaseModel.SCRATCH_ID);
        mFragment.initViews(scratchTasks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public View getRootView() {
        return null;
    }

    @Override
    protected void deleteAction() {
        finish();
    }

    @Override
    protected void editAction() {

    }

    @Override
    public void addNewTask(Task t) {

    }
}
