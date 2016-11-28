package northstar.planner.presentation.goal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;

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
import northstar.planner.presentation.task.NewTaskDialog;
import northstar.planner.presentation.task.TaskActivity;

public class GoalActivity
        extends BaseActivity
        implements GoalFragment.GoalFragmentListener, NewTaskDialog.NewTaskDialogListener {

    @BindView(R.id.activity_theme_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    private GoalFragment mFragment;
    private PlannerSqliteDAO dao;
    private Goal currentGoal;
    private long parentTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);
        dao = new PlannerSqliteDAO();
        parentTheme = getIntent().getExtras().getLong(GoalTable.THEME_COLUMN);
        currentGoal = dao.getGoal(getIntent().getExtras().getLong(GoalTable._ID));
        mFragment = GoalFragment.newInstance(currentGoal);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_theme_edit_fragment, mFragment)
                .commit();

        finishDrawerInit(this, mDrawerLayout, currentGoal.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentGoal = dao.getGoal(currentGoal.getId());
        mFragment.initViews(currentGoal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentGoal.updateGoal(mFragment.getNewGoalValues());
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
    protected void deleteAction() {
        dao.removeGoal(currentGoal.getId());
        finish();
    }

    @Override
    protected void editAction() {
        getSupportActionBar().setTitle(mFragment.getNewGoalValues().getTitle());
        mFragment.toggleEditing();
    }

    @Override
    public Task setResult(Task task) {
        task.setGoal(currentGoal);
        task = dao.addTask(task);
        currentGoal.getTasks().add(0, task);
        return task;
    }
}
