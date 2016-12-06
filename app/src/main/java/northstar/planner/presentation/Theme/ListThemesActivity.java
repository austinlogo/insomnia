package northstar.planner.presentation.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseActivity;

public class ListThemesActivity extends BaseActivity
        implements ListThemesFragment.ListThemesFragmentListener {

    @BindView(R.id.activity_theme_drawer_layout)
    DrawerLayout mDrawerLayout;

    ListThemesFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_view);
        ButterKnife.bind(this);
        finishDrawerInit(this, mDrawerLayout, "Theme");

        fragment = ListThemesFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, fragment)
                .commit();
    }

    @Override
    public void startThemeEdit(Theme theme) {
        theme = theme != null
                ? theme
                : new Theme();

        Intent i = new Intent(this, ThemeActivity.class);
        i.putExtra(ThemeTable.TABLE_NAME, theme);
        startActivity(i);
    }

    @Override
    public void removeItem(Theme item, int position) {
        fragment.removeItemWorkflow(item, position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public View getRootView() {
        return mDrawerLayout;
    }

    @Override
    protected void deleteAction() {
        //NOOP
    }

    @Override
    protected void editAction() {
        //NOOP
    }
}
