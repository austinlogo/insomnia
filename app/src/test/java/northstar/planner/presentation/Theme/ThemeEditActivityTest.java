package northstar.planner.presentation.Theme;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import northstar.planner.BuildConfig;
import northstar.planner.models.Theme;


import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ThemeEditActivityTest {

    ThemeEditActivity themeEditActivity;

    @Before
    public void setUp() {
        themeEditActivity = Robolectric.setupActivity(ThemeEditActivity.class);
    }

    @Test
    public void testInit() {
        assertNotNull(themeEditActivity);
    }

    @Test
    public void testStartThemeEdit() {
        themeEditActivity.openGoal(new Theme(1, "TITLE_TEXT", "DESCRIPTION_TEXT"));
    }
}