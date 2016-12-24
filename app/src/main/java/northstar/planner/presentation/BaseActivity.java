package northstar.planner.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnLongClick;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.Theme.ListThemesActivity;
import northstar.planner.presentation.Theme.ThemeActivity;
import northstar.planner.presentation.adapter.ThemeListAdapter;
import northstar.planner.presentation.today.TodayActivity;

public abstract class BaseActivity
        extends AppCompatActivity
        implements DrawerLayout.DrawerListener {

    public static final int EDIT_MENUITEM_INDEX = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_drawer_list_theme_head)
    TextView themeHead;

    @BindView(R.id.activity_drawer_list_today)
    TextView today;

    @BindView(R.id.activity_drawer_list_themes)
    protected ListView themeList;

    private ThemeListAdapter themeListAdapter;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private List<Theme> themes;
    protected Menu optionsMenu;

    PlannerSqliteGateway dao;

    public abstract View getRootView();

    protected abstract void deleteAction();
    public abstract void editAction();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new PlannerSqliteGateway();
//        dao.getThemeStructure();
    }

    protected void finishDrawerInit(Activity act, DrawerLayout mDrawerLayout, String actionBarTitle) {

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                act,
                mDrawerLayout,
                toolbar,
                android.R.string.ok,
                android.R.string.cancel);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(actionBarTitle);
        mActionBarDrawerToggle.syncState();
        ((DrawerLayout) getRootView()).setDrawerListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        themes = dao.getAllThemes();
        themeListAdapter = new ThemeListAdapter(
                this,
                R.layout.include_drawer_list,
                themes);

        themeList.setAdapter(themeListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
        closeDrawers();
    }

    @OnClick(R.id.activity_drawer_list_today)
    public void OnTodayClicked() {
        startActivity(new Intent(this, TodayActivity.class));
    }

    @OnClick(R.id.activity_drawer_list_theme_head)
    public void onClickTheme(TextView view) {
        if (themeList.getVisibility() == View.VISIBLE) {
            themeList.setVisibility(View.GONE);
        } else {
            themeList.setVisibility(View.VISIBLE);
        }
    }

    @OnLongClick(R.id.activity_drawer_list_theme_head)
    public boolean OnThemeLongClick() {
//        closeDrawers();
        startActivity(new Intent(this, ListThemesActivity.class));
        return true;
    }

    @OnItemClick(R.id.activity_drawer_list_themes)
    public void onThemeListItemSelected(int position) {
//        closeDrawers();
        Theme selectedTheme = themeListAdapter.getItem(position);

        Intent i = new Intent(this, ThemeActivity.class);
        i.putExtra(ThemeTable.TABLE_NAME, selectedTheme);
        startActivity(i);

    }

    private void closeDrawers() {
        ((DrawerLayout) getRootView()).closeDrawers();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_delete:
                deleteAction();
                return true;
            case R.id.main_menu_edit:
                toggleEditIcon(item);
                return true;
        }

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void toggleEditIcon(MenuItem menuItem) {
        if(menuItem.getTitle().equals(getString(R.string.action_edit))) {
            menuItem.setTitle(getString(R.string.action_save));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_done_white_36dp));

        } else {
            menuItem.setTitle(getString(R.string.action_edit));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_36dp));
        }

        editAction();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        optionsMenu = menu;
        return true;
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        hideKeyboard();
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public PlannerSqliteGateway getDao() {
        return dao;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void updateThemes(List<Theme> list) {
        dao.updateOrder(ThemeTable.TABLE_NAME, list);
    }

    public void removeFromDb(Theme theme) {
        getDao().removeTheme(theme.getId());
    }

    public void removeFromDb(Goal goal) {
        getDao().removeGoal(goal.getId());
    }

    public void removeFromDb(Task task) {
        getDao().removeTask(task.getId());
    }

    public void removeFromDb(Metric sc) {
        getDao().removeTheme(sc.getId());
    }
}
