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

public class ThemeActivity extends BaseActivity
        implements ThemeFragment.ThemeFragmentListener {

    @BindView(R.id.activity_theme_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        ButterKnife.bind(this);

        finishDrawerInit(this, mDrawerLayout, "Theme");

        ThemeFragment fragment = ThemeFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void startThemeEdit(Theme theme) {

        Intent i = new Intent(this, ThemeEditActivity.class);
        if (theme != null) {
            i.putExtra(ThemeTable._ID, theme.getId());
            i.putExtra(ThemeTable.TITLE_COLUMN, theme.getTitle());
            i.putExtra(ThemeTable.DESCRIPTION_COLUMN, theme.getDescription());
        }

        startActivity(i);
    }
}
