package northstar.planner.presentation.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.goal.GoalActivity;
import northstar.planner.utils.StringUtils;

public class ThemeActivity
        extends BaseActivity
        implements ThemeFragment.ThemeFragmentListener {

    @BindView(R.id.activity_theme_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    Theme currentTheme;
    public PlannerSqliteDAO dao;
    private ThemeFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTheme = (Theme) getIntent().getExtras().get(ThemeTable.TABLE_NAME);
        dao = new PlannerSqliteDAO();
        mFragment = northstar.planner.presentation.Theme.ThemeFragment.newInstance(currentTheme);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_theme_edit_fragment, mFragment)
                .commit();

        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);
        finishDrawerInit(this, mDrawerLayout, currentTheme.getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Goal> goals = dao.getGoalsByThemeId(currentTheme.getId());
        mFragment.initGoalsList(goals);
    }

    @Override
    public void onPause() {
        super.onPause();

        currentTheme.updateTheme(mFragment.getNewThemeValues());
        if (StringUtils.isNotEmpty(currentTheme.getTitle())) {
            dao.updateTheme(currentTheme);
        }
    }

    @Override
    protected void deleteAction() {
        dao.removeTheme(currentTheme.getId());
        finish();
    }

    @Override
    protected void editAction() {
        getSupportActionBar().setTitle(mFragment.getNewThemeValues().getTitle());
        mFragment.toggleEditing();
    }

    @Override
    public void newGoal(String newGoalTitle) {
        Goal newGoal = new Goal(currentTheme.getId(), newGoalTitle, "");
        long id = dao.addGoal(newGoal);
        newGoal.setId(id);

        mFragment.addedGoal(newGoal);
    }

    @Override
    public void openGoal(Goal goal) {

        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra(GoalTable._ID, goal.getId());
        i.putExtra(GoalTable.THEME_COLUMN, goal.getTheme());
//        i.putExtra(GoalTable.TITLE_COLUMN, goal.getTitle());
        startActivity(i);
    }
}
