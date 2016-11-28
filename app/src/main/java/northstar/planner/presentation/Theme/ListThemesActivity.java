package northstar.planner.presentation.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseActivity;

public class ListThemesActivity extends BaseActivity
        implements ListThemesFragment.ListThemesFragmentListener {

    @BindView(R.id.activity_theme_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_view);
        ButterKnife.bind(this);
        finishDrawerInit(this, mDrawerLayout, "Theme");

        ListThemesFragment fragment = ListThemesFragment.newInstance();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void deleteAction() {

    }

    @Override
    protected void editAction() {

    }
}
