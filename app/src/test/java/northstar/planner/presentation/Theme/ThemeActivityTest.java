package northstar.planner.presentation.Theme;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import northstar.planner.BuildConfig;


import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ThemeActivityTest {

    ThemeActivity themeActivity;

    @Before
    public void setUp() {
        themeActivity = Robolectric.setupActivity(ThemeActivity.class);
    }

    @Test
    public void testInit() {
        assertNotNull(themeActivity);
    }

    @Test
    public void testStartThemeEdit() {
//        themeActivity.openGoal(new Theme(1, "TITLE_TEXT", "DESCRIPTION_TEXT"));
    }
}