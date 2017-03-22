package northstar.planner.presentation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import northstar.planner.models.tables.GoalTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.notification.NotificationPublisher;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.Theme.ListThemesActivity;
import northstar.planner.presentation.Theme.ThemeActivity;
import northstar.planner.presentation.adapter.DrawerAdapter;
import northstar.planner.presentation.adapter.DrawerListeners;
import northstar.planner.presentation.goal.GoalActivity;
import northstar.planner.presentation.task.TaskActivity;
import northstar.planner.presentation.today.FocusActivity;

public abstract class BaseActivity
        extends AppCompatActivity
        implements DrawerLayout.DrawerListener {

    public static final int EDIT_MENUITEM_INDEX = 1;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.activity_drawer_list_theme_head)
    TextView themeHead;

    @BindView(R.id.activity_drawer_list_today)
    TextView today;

    @BindView(R.id.activity_drawer_list_themes)
    protected ListView themeList;

    protected DrawerAdapter drawerAdapter;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    protected List<Theme> themes;
    protected Menu optionsMenu;

    PlannerSqliteGateway dao;

    public abstract View getRootView();

    protected abstract void deleteAction();
    public abstract void editAction();
    protected abstract void updateActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new PlannerSqliteGateway();
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
        drawerAdapter = new DrawerAdapter(
                this,
                R.layout.include_drawer_list,
                themes,
                new DrawerListeners() {
                    @Override
                    public void onThemeClicked(long themeId) {
                        startThemeActivity(themeId);
                    }

                    @Override
                    public void onGoalClicked(long goalId) {
                        startGoalActivity(goalId);
                    }
                });

        themeList.setAdapter(drawerAdapter);
        updateActivity();
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
        closeDrawers();
    }

    @OnClick(R.id.activity_drawer_list_today)
    public void OnFocusClicked() {
        startActivity(new Intent(this, FocusActivity.class));
    }

    @OnLongClick(R.id.activity_drawer_list_theme_head)
    public boolean onThemeLongClick(TextView view) {
        if (themeList.getVisibility() == View.VISIBLE) {
            themeList.setVisibility(View.GONE);
        } else {
            themeList.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @OnClick(R.id.activity_drawer_list_theme_head)
    public void OnThemeClick() {
        startActivity(new Intent(this, ListThemesActivity.class));
    }

    @OnItemClick(R.id.activity_drawer_list_themes)
    public void onThemeListItemSelected(int position) {
        long selectedThemeId = drawerAdapter.getItemModel(position).getTheme().getId();
        startThemeActivity(selectedThemeId);
    }

    private void startThemeActivity(long themeId) {
        Intent i = new Intent(this, ThemeActivity.class);
        i.putExtra(ThemeTable.TABLE_NAME, themeId);
        startActivity(i);
    }

    private void startGoalActivity(long goalId) {
        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra(GoalTable.TABLE_NAME, goalId);
        startActivity(i);
    }

    private void closeDrawers() {
        ((DrawerLayout) getRootView()).closeDrawers();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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
        boolean isEditable = menuItem.getTitle().equals(getString(R.string.action_edit));
        setEditIcon(isEditable);
        editAction();
    }

    protected void setEditIcon(boolean editable) {
        MenuItem menuItem = toolbar.getMenu().getItem(EDIT_MENUITEM_INDEX);
        if (editable) {
            menuItem.setTitle(getString(R.string.action_save));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_done_white_36dp));
        } else {
            menuItem.setTitle(getString(R.string.action_edit));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_36dp));
        }
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

    @Override public void onDrawerOpened(View drawerView) {}

    @Override public void onDrawerClosed(View drawerView) {}

    @Override public void onDrawerStateChanged(int newState) {}

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

    public void scheduleNotification(Task task) {
        PendingIntent pendingIntent = constructNotificationPendingIntent(task);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminder().getTime(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getReminder().getTime(), pendingIntent);
        }
    }

    private Notification getNotification(Task task) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Task Reminder");
        builder.setContentText(task.getTitle());
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVibrate(new long[]{0,100,100,100});
        builder.setSmallIcon(R.drawable.logo_nobackground);

        if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(getResources().getColor(R.color.colorPrimary));
        }


        Intent result = new Intent(this, TaskActivity.class);
        result.putExtra(TaskTable.TABLE_NAME, task);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) task.getId() , result, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    private PendingIntent constructNotificationPendingIntent(Task task) {
        Notification notification = getNotification(task);
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, task.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        return PendingIntent.getBroadcast(this, (int) task.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void cancelNotification(Task item) {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(constructNotificationPendingIntent(item));
    }
}
