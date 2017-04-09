package northstar.planner.presentation.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Task;
import northstar.planner.models.drawer.ShallowModel;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.dependency.DependencyChooserCallback;
import northstar.planner.presentation.dependency.DependencyDialogFragment;
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
    public void updateActivity() {
        Task updatedTask = getDao().getTask(currentTask.getId());

        if (updatedTask == null) {
            finish();
            return;
        }
        currentTask = updatedTask;
        mFragment.initUI(currentTask);
    }

    @Override
    protected void deleteAction() {
        dao.deleteTask(currentTask);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public void editAction() {
        getSupportActionBar().setTitle(mFragment.updateValues().getTitle());
        mFragment.toggleEditing();
    }

    @Override
    public void navigateToGoal() {
        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra(GoalTable.TABLE_NAME, currentTask.getGoal());
        startActivity(i);
    }

    @Override
    public void addTaskDependency() {
        Goal taskGoal = dao.getGoal(currentTask.getGoal());
        List<Task> tasks = taskGoal.getTasks();

        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task iterTask = iterator.next();
            if (iterTask.getId() == currentTask.getId()) {
                iterator.remove();
            }
        }

        DependencyDialogFragment.newInstance(tasks, new DependencyChooserCallback() {
            @Override
            public void onChoose(Task chosenTask) {
                dao.updateDependencyTable(currentTask, chosenTask);
                currentTask.setDependentTask(new ShallowModel(chosenTask));
                mFragment.initUI(currentTask);
            }
        }).show(getFragmentManager(), "");
    }

    @Override
    public void removeDependency() {
        dao.removeDependency(currentTask.getId());
        currentTask.setDependentTask(null);
        mFragment.initUI(currentTask);
    }

//    @Override
//    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        selectedDate = Calendar.getDbInstance();
//        selectedDate.set(year, monthOfYear, dayOfMonth);
//        timePickerDialog.show();
//    }
//
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        selectedDate.set(Calendar.MINUTE, minute);
//
//        dao.snooze(currentTask.getId(), selectedDate.getTime());
//        currentTask.setSnooze(selectedDate.getTime());
//        mFragment.initUI(currentTask);
//    }
}
