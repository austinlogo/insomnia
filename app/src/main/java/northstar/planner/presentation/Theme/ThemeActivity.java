package northstar.planner.presentation.Theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.goal.GoalActivity;
import northstar.planner.presentation.hours.ActiveHoursDialogFragment;
import northstar.planner.utils.StringUtils;

public class ThemeActivity
        extends BaseActivity
        implements ThemeFragment.ThemeFragmentListener {

    private static final int EDIT_ICON_INDEX = 1;
    @BindView(R.id.activity_theme_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    Theme currentTheme;
    private ThemeFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long currentThemeId = getIntent().getLongExtra(ThemeTable.TABLE_NAME, 0L);

        if (currentThemeId == Theme.NEW_ID) {
            currentTheme = new Theme();
        } else {
            currentTheme = getDao().getTheme(currentThemeId);
        }

        mFragment = northstar.planner.presentation.Theme.ThemeFragment.newInstance(currentTheme);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_theme_edit_fragment, mFragment)
                .commit();

        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);
        finishDrawerInit(this, mDrawerLayout, currentTheme.getTitle());
        hideKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void updateActivity() {

        Theme updatedTheme = getDao().getTheme(currentTheme.getId());
        if (isDeletedTheme(updatedTheme)) {
            finish();
        } else if (updatedTheme != null) { //
            currentTheme = updatedTheme;
        }

        mFragment.initGoalsList(currentTheme.getGoals());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private boolean isDeletedTheme(Theme updatedTheme) {
        return updatedTheme == null && !currentTheme.isNew();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View v = super.onCreateView(name, context, attrs);

        if (optionsMenu != null && currentTheme.isNew()) {
            editAction();
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        getDao().updateOrder(GoalTable.TABLE_NAME, currentTheme.getGoals());
        currentTheme.updateTheme(mFragment.getNewThemeValues());

        if (StringUtils.isNotEmpty(currentTheme.getTitle())) {
            getDao().updateTheme(currentTheme);
        }
    }

    @Override
    public View getRootView() {
        return mDrawerLayout;
    }

    @Override
    protected void deleteAction() {
        getDao().removeTheme(currentTheme.getId());
        finish();
    }

    @Override
    public void editAction() {
        getSupportActionBar().setTitle(mFragment.getNewThemeValues().getTitle());
        boolean isEditing = mFragment.toggleEditing();
        setEditIcon(isEditing);
    }

    @Override
    public void newGoal(String newGoalTitle) {
        storeThemeIfItDoesNotYesExist();
        Goal newGoal = new Goal(currentTheme.getId(), newGoalTitle, "");
        long id = getDao().addGoal(newGoal);
        newGoal.setId(id);
        drawerAdapter.updateGoal(newGoal);
        mFragment.addedGoal(newGoal);
    }

    private void storeThemeIfItDoesNotYesExist() {
        if (currentTheme.isNew()) {
            currentTheme.updateTheme(mFragment.getNewThemeValues());
            currentTheme = getDao().addTheme(currentTheme, 0);
        }
    }

    @Override
    public void openGoal(Goal goal) {

        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra(GoalTable.TABLE_NAME, goal.getId());
        startActivity(i);
    }

    @Override
    public void openActiveHoursDialog() {
        storeThemeIfItDoesNotYesExist();
        ActiveHoursDialogFragment.newInstance(currentTheme.getId()).show(getFragmentManager(), "TAG");
    }
}
