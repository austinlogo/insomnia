package northstar.planner.presentation.Theme.shell;

import android.app.Activity;

import northstar.planner.models.Theme;
import northstar.planner.presentation.Theme.ListThemesFragmentTest;
import northstar.planner.presentation.Theme.ThemeFragment;

/**
 * Created by Austin on 11/20/2016.
 */
public class BlankThemeActivityListener extends Activity
        implements ListThemesFragmentTest.ThemeFragmentListener, ThemeFragment.ThemeFragmentListener {

    @Override
    public void startThemeEdit(Theme theme) {

    }

    @Override
    public void newGoal(Theme theme) {

    }
}
