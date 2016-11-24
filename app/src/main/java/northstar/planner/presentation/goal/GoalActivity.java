package northstar.planner.presentation.goal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Theme;
import northstar.planner.presentation.BaseActivity;

public class GoalActivity
        extends BaseActivity
        implements GoalFragment.GoalFragmentListener {

    @BindView(R.id.activity_theme_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    private GoalFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_edit);
        ButterKnife.bind(this);
        mFragment = GoalFragment.newInstance(getIntent().getExtras());
        finishDrawerInit(this, mDrawerLayout, "Goal");

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_theme_edit_fragment, mFragment)
                .commit();
    }

    @Override
    public String setTitle() {
        return null;
    }
}
