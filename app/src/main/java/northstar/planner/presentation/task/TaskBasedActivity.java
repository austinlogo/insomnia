package northstar.planner.presentation.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.goal.AddTaskFragment;
import northstar.planner.presentation.goal.GoalFragment;

public abstract class TaskBasedActivity
        extends BaseActivity
        implements AddTaskFragment.AddTaskFragmentListener {

    @BindView(R.id.activity_goal_drawer_layout)
    DrawerLayout mDrawerLayout;

    private BaseFragment mainFragment;
    private AddTaskFragment addTaskFragment;
    private BaseModel mainModel;

    protected BaseFragment onCreate(@Nullable Bundle savedInstanceState, String tableName) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);

        mainFragment = setMainFragment(tableName);
        addTaskFragment = AddTaskFragment.newInstance("");

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_goal_fragment, mainFragment)
                .add(R.id.activity_goal_add_task, addTaskFragment)
                .commit();

        return mainFragment;
    }

    public BaseFragment setMainFragment(String tableName) {
        switch (tableName) {
            case GoalTable.TABLE_NAME:
                Goal currentGoal = getDao().getGoal(getIntent().getExtras().getLong(GoalTable._ID));
                mainModel = currentGoal;
                return GoalFragment.newInstance(currentGoal);
        }
        return null;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public BaseModel getMainModel() {
        return mainModel;
    }

    @Override
    public View getRootView() {
        return null;
    }

    @Override
    protected void deleteAction() {

    }

    @Override
    protected void editAction() {

    }

    @Override
    public void addNewTask(Task t) {

    }
}
