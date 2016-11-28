package northstar.planner.presentation.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseActivity;

public class TaskActivity extends BaseActivity {

    @BindView(R.id.activity_task_drawer_layout)
    DrawerLayout mDrawerLayout;

    PlannerSqliteDAO dao;
    Task currentTask;
    TaskFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        dao = new PlannerSqliteDAO();
        currentTask = (Task) getIntent().getExtras().getSerializable(TaskTable.TABLE_NAME);
        mFragment = TaskFragment.newInstance(getIntent().getExtras());

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_task_fragment, mFragment)
                .commit();

        finishDrawerInit(this, mDrawerLayout, currentTask.getTitle());
    }


    @Override
    protected void deleteAction() {
        dao.removeTask(currentTask.getId());
        finish();
    }

    @Override
    protected void editAction() {

    }
}
