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
import northstar.planner.presentation.adapter.TaskRecyclerViewAdapter;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.task.TaskBasedActivity;

public class TodayActivity
        extends TaskBasedActivity
        implements GoalFragment.TaskActionListener, TodayFragment.TodayActivityListener {

    TodayFragment mFragment;
//    AddTaskFragment addTaskFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragment = (TodayFragment) super.onCreate(savedInstanceState, TaskTable.TABLE_NAME);
        ButterKnife.bind(this);

        finishDrawerInit(this, (DrawerLayout) getRootView(), getString(R.string.today));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Task> scratchTasks = getDao().getTodaysTasks();
        getDao().getTasksByPriority();
//        getDao().getTasksByGoalId(BaseModel.SCRATCH_ID);
        mFragment.initViews(scratchTasks);
    }

    @Override
    protected void storeNewTask(Task newTask) {
        newTask.setGoal(BaseModel.SCRATCH_ID);
        getDao().addTask(newTask);
        ((TaskRecyclerViewAdapter) mFragment.taskList.getAdapter()).addItem(newTask);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override protected void deleteAction() {}
    @Override public void editAction() {}

    @Override
    public void openAddTaskWorkflow(String newTaskTitle) {
        setFragmentVisible(addTaskLayout);
        addTaskFragment.updateFragmentValuesForTodayTask(newTaskTitle);
    }

    @Override
    public void completeTask(Task t) {
        getDao().completeTask(t);
    }
}
