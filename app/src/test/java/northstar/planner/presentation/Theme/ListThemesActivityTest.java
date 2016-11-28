package northstar.planner.presentation.Theme;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import northstar.planner.BuildConfig;
import northstar.planner.models.Theme;


import static org.junit.Assert.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ListThemesActivityTest {

    ListThemesActivity listThemesActivity;

    @Before
    public void setUp() {
        listThemesActivity = Robolectric.setupActivity(ListThemesActivity.class);
    }

    @Test
    public void testInit() {
        assertNotNull(listThemesActivity);
    }

    @Test
    public void testStartThemeEdit() {
        listThemesActivity.startThemeEdit(new Theme(0, "test_1", "test_2"));
    }
}