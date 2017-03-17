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

public class FocusActivity
        extends TaskBasedActivity
        implements GoalFragment.TaskActionListener, FocusFragment.FocusActivityListener {

    FocusFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragment = (FocusFragment) super.onCreate(savedInstanceState, TaskTable.TABLE_NAME);
        ButterKnife.bind(this);

        finishDrawerInit(this, (DrawerLayout) getRootView(), getString(R.string.drawer_item_focus));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Task> focusTasks = getDao().getTodaysTasks();
        getDao().getTasksByPriority();
        determineIfWeShowHelperImage(focusTasks);
    }

    private void determineIfWeShowHelperImage(List<Task> focusTasks) {
        boolean showLonelyImage = themes.isEmpty() && focusTasks.isEmpty();

        mFragment.showLonelyImage(showLonelyImage);
        mFragment.initViews(focusTasks);
    }

    @Override
    protected void storeNewTask(Task newTask) {
        mFragment.showLonelyImage(false);
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

    protected boolean isEmptyApp(List<Task> focusTasks) {
        return themes.isEmpty() && focusTasks.isEmpty();
    }
}
