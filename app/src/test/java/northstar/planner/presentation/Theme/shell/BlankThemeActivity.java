package northstar.planner.presentation.Theme.shell;

import android.app.Activity;

import northstar.planner.models.Theme;
import northstar.planner.presentation.Theme.ThemeEditFragment;
import northstar.planner.presentation.Theme.ThemeFragment;

/**
 * Created by Austin on 11/20/2016.
 */
public class BlankThemeActivity extends Activity
        implements ThemeFragment.ThemeFragmentListener, ThemeEditFragment.ThemeEditFragmentListener {

    @Override
    public void startThemeEdit(Theme theme) {

    }

    @Override
    public void newGoal(Theme theme) {

    }
}
