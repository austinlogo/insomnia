package northstar.planner.presentation.Theme;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;


import northstar.planner.BuildConfig;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.Theme.shell.BlankThemeActivity;


import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ThemeEditFragmentTest {

//    @Mock
//    EditText editTitle;
//
//    @Mock
//    EditText editDescription;

//    @Mock
    ImageButton editButton;

    @Mock
    TextView newGoalText;

    @Mock
    Theme currentTheme;

    @Mock
    private ThemeEditFragment.ThemeEditFragmentListener activityListener;

    @InjectMocks
    ThemeEditFragment fragment;

    @Before
    public void setUp() {
        Bundle b = new Bundle();
        b.putString(ThemeTable.TITLE_COLUMN, "TEST_TITLE");
        b.putString(ThemeTable.DESCRIPTION_COLUMN, "TEST_DESCRIPTION");

        fragment = ThemeEditFragment.newInstance(b);
        FragmentTestUtil.startFragment(fragment, BlankThemeActivity.class);

        MockitoAnnotations.initMocks(this);
//        currentTheme = (Theme) Whitebox.getInternalState(fragment, "currentTheme");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetup() {
        assertNotNull(fragment);

    }

    @Test
    public void testPause() {
        fragment.onPause();
        verify(currentTheme).updateTheme("TEST_TITLE", "TEST_DESCRIPTION");
    }

    @Test
    public void testOnClick_edit() {
        editButton = (ImageButton) Whitebox.getInternalState(fragment, "editButton");
        fragment.onClick(editButton);
        fragment.onClick(editButton);
    }

    @Test
    public void testNewGoal() {
        fragment.newGoal(null);
        verify(activityListener).openGoal(currentTheme);
    }




}