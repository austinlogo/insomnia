package northstar.planner.presentation.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.goal.GoalActivity;

public class ThemeEditActivity
        extends BaseActivity
        implements ThemeEditFragment.ThemeEditFragmentListener {

    @BindView(R.id.activity_theme_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    private ThemeEditFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_edit);
        ButterKnife.bind(this);
        mFragment = ThemeEditFragment.newInstance(getIntent().getExtras());
        finishDrawerInit(this, mDrawerLayout, "Edit Theme");

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_theme_edit_fragment, mFragment)
                .commit();
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
