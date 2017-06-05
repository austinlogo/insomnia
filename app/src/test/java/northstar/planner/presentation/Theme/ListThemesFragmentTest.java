package northstar.planner.presentation.Theme;

import android.widget.ListView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import northstar.planner.BuildConfig;
import northstar.planner.models.Theme;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.presentation.adapter.ThemeListAdapter;
import northstar.planner.presentation.Theme.shell.BlankThemeActivityListener;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ListThemesFragmentTest {

    @Mock
    PlannerSqliteGateway dao;

    @Mock
    ListThemesFragment.ListThemesFragmentListener activityListener;

    @Mock
    ThemeListAdapter themeListAdapter;

    @Mock
    ListView list;

    @InjectMocks
    ListThemesFragment fragment;

    @Before
    public void setUp() {
        fragment = ListThemesFragment.newInstance();
        FragmentTestUtil.startFragment(fragment, BlankThemeActivityListener.class);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStart() {
        Assert.assertNotNull(fragment);
    }

    @Test public void testPauseWithoutCrash() {
        fragment.onPause();
    }

    @Test
    public void testOnItemClick() {
        when(themeListAdapter.getItem(0)).thenReturn(new Theme(0, "title", "desc"));
//        fragment.onItemClick(null, null, 0, 0X01);

        verify(activityListener).startThemeEdit(any(Theme.class));
    }

    @Test
    public void testCreateNewTheme() {
        fragment.createNewTheme();
        verify(activityListener).startThemeEdit(null);
    }
}