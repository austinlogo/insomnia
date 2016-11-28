package northstar.planner.presentation;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import northstar.planner.R;
import northstar.planner.models.DrawerItem;
import northstar.planner.presentation.adapter.DrawerAdapter;

public abstract class BaseActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_drawer_list)
    protected ListView mDrawerList;

    protected static List<DrawerItem> drawerItems;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    protected abstract void deleteAction();
    protected abstract void editAction();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerItems = new ArrayList<>();
        drawerItems.add( new DrawerItem(
                R.drawable.ic_menu_gallery,
                getResources().getString(R.string.drawer_item_themes)));
        drawerItems.add( new DrawerItem(
                R.drawable.ic_menu_gallery,
                getResources().getString(R.string.drawer_item_goals)));
        drawerItems.add( new DrawerItem(
                R.drawable.ic_menu_gallery,
                getResources().getString(R.string.drawer_item_tasks)));
        drawerItems.add( new DrawerItem(
                R.drawable.ic_menu_gallery,
                getResources().getString(R.string.drawer_item_today)));


    }

    protected void finishDrawerInit(Activity act, DrawerLayout mDrawerLayout, String actionBarTitle) {
        mDrawerList.setAdapter(new DrawerAdapter(
                this,
                R.layout.include_drawer_list,
                drawerItems));

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

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_delete:
                deleteAction();
                return true;
            case R.id.main_menu_edit:
                toggleEditIcon(item);
                editAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditIcon(MenuItem menuItem) {
        if(menuItem.getTitle().equals(getString(R.string.action_edit))) {
            menuItem.setTitle(getString(R.string.action_save));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_done_white_36dp));

        } else {
            menuItem.setTitle(getString(R.string.action_edit));
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_mode_edit_white_36dp));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
