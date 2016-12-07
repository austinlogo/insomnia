package northstar.planner.presentation.today;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import java.util.List;

import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.goal.AddTaskFragment;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.task.TaskBasedActivity;

public class TodayActivity
        extends TaskBasedActivity
        implements GoalFragment.TaskActionListener, TodayFragment.TodayActivityListener {

    TodayFragment mFragment;
    AddTaskFragment addTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragment = (TodayFragment) super.onCreate(savedInstanceState, TaskTable.TABLE_NAME);
        ButterKnife.bind(this);

        finishDrawerInit(this, (DrawerLayout) getRootView(), getString(R.string.today));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        List<Task> scratchTasks = getDao().getTodaysTasks();
        getDao().getTasksByGoalId(BaseModel.SCRATCH_ID);
        mFragment.initViews(scratchTasks);
    }

    @Override
    protected void storeNewTask(Task newTask) {
        getDao().addTask(newTask);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override protected void deleteAction() {}
    @Override protected void editAction() {}

    @Override
    public void addTask(Task newTask) {
        newTask = getDao().addTask(newTask);
    }

    @Override
    public void completeTask(Task t) {
        getDao().completeTask(t);
    }
}
