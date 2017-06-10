package northstar.planner.presentation.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.goal.AddTaskFragment;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.today.AddOverlayFragment;
import northstar.planner.presentation.today.FocusFragment;

public abstract class TaskBasedActivity
        extends BaseActivity
        implements AddTaskFragment.AddTaskFragmentListener, GoalFragment.TaskActionListener {

    protected abstract void storeNewTask(Task newTask);

    @BindView(R.id.activity_goal_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_goal_fragment)
    protected FrameLayout mainFragmentLayout;

    @BindView(R.id.activity_goal_add_task)
    protected FrameLayout addTaskLayout;

    @BindView(R.id.activity_goal_add_overlay)
    protected FrameLayout addOverlay;

    private TaskBasedFragment mainFragment;
    protected BaseModel mainModel;

    protected AddTaskFragment addTaskFragment;
    protected AddOverlayFragment addOverlayFragment;

    protected BaseFragment onCreate(@Nullable Bundle savedInstanceState, String tableName) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);

        mainFragment = setMainFragment(tableName);
        addTaskFragment = AddTaskFragment.newInstance("");

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_goal_add_task, addTaskFragment)
                .add(R.id.activity_goal_fragment, mainFragment)
                .commit();

        return mainFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public TaskBasedFragment setMainFragment(String tableName) {
        switch (tableName) {
            case GoalTable.TABLE_NAME:
                Goal currentGoal = getDao().getGoal(getIntent().getExtras().getLong(GoalTable.TABLE_NAME));
                mainModel = currentGoal;
                return GoalFragment.newInstance(currentGoal);
            case TaskTable.TABLE_NAME:
                return FocusFragment.newInstance();
        }
        return null;
    }

    public BaseModel getMainModel() {
        return mainModel;
    }

    @Override
    public View getRootView() {
        return mDrawerLayout;
    }

    @Override
    public void addNewTask(Task newTask) {
        hideKeyboard();
//        setFragmentVisible(mainFragmentLayout);
        storeNewTask(newTask);
    }

    public void setFragmentVisible(FrameLayout fragmentVisible) {
        mainFragment.setActionButtonVisibility(mainFragmentLayout.getId() == fragmentVisible.getId());
        addTaskLayout.setVisibility(addTaskLayout.getId() == fragmentVisible.getId() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openTask(Task t) {
        Intent i = new Intent(this, TaskActivity.class);
        i.putExtra(TaskTable.TABLE_NAME, t);
        startActivity(i);
    }

    @Override
    public void removeTask(int position, Task t) {
        mainFragment.removeItemWorkflow(t, position);
    }

    @Override
    public void completeTask(Task t) {
        Metric updatedMetric = getDao().completeTask(t);
        mainFragment.updateMetricOnUI(updatedMetric);
        getDao().updateDependencyOnComplete(t.getId());
        cancelAllNotificationsForTask(t);
        updateActivity();
    }

    @Override
    public void updateTask(Task updatedTask) {
        getDao().updateTask(updatedTask);
        sche
        updateActivity();
    }

    public void attachAddOverlayToActivity() {
        getFragmentManager().beginTransaction()
                .add(R.id.activity_goal_add_overlay, addOverlayFragment)
                .commit();
    }

    public void removeAddOverlayFromActivity() {
        addOverlay.setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .remove(addOverlayFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (addTaskLayout.getVisibility() == View.VISIBLE) {
            setFragmentVisible(mainFragmentLayout);
        } else if (addOverlay.getVisibility() == View.VISIBLE) {
            removeAddOverlayFromActivity();
        } else {
            super.onBackPressed();
        }
    }
}
