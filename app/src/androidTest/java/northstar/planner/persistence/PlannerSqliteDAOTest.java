package northstar.planner.persistence;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.Goal;
import northstar.planner.models.Theme;


import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PlannerSqliteDAOTest {

    private static long testThemeId = 24601;

    private PlannerSqliteDAO dao;
    private Theme testTheme;
    private Goal testGoal;

    @Before
    public void setUp() throws Exception {
        testTheme = new Theme("TEST_TITLE", "TEST_DESCRIPTION");
        testGoal = new Goal("GOAL_TITLE", "GOAL_DESCRIPTION");
        testGoal.setTheme(testThemeId);
        dao = new PlannerSqliteDAO();
    }

    @After
    public void tearDown() throws Exception {

    }

    @AfterClass
    public static void cleanup() {
        new PlannerSqliteDAO().removeTheme("TEST_TITLE");
    }

    @Test
    public void addTheme() throws Exception {
        long rowId = dao.addTheme(testTheme);
        assertTrue(rowId >= 0);

        Theme recoveredTheme = dao.getTheme(rowId);
        assertTrue(recoveredTheme.getId() == rowId);

        boolean successfulRemoval = dao.removeTheme(rowId);
        assertTrue(successfulRemoval);
    }

    @Test
    public void getThemes() {
        long rowId = dao.addTheme(testTheme);
        List<Theme> firstSnapshot = dao.getAllThemes();
        assertFalse(firstSnapshot.isEmpty());

        long rowId2 = dao.addTheme(testTheme);
        List<Theme> secondSnapshot = dao.getAllThemes();
        assertFalse(secondSnapshot.isEmpty());

        assertEquals(1, secondSnapshot.size() - firstSnapshot.size());

        dao.removeTheme(rowId2);
        dao.removeTheme(rowId);
    }

    @Test
    public void getThemes_enumeratedList() {
        long rowId = dao.addTheme(testTheme);
        List<Theme> firstSnapshot = dao.getAllThemes();
        assertFalse(firstSnapshot.isEmpty());

        long rowId2 = dao.addTheme(testTheme);
        List<Theme> secondSnapshot = dao.getAllThemes();
        assertFalse(secondSnapshot.isEmpty());

        assertEquals(1, secondSnapshot.size() - firstSnapshot.size());

        List<Long> rowIdList = new ArrayList<>();
        rowIdList.add(rowId);
        rowIdList.add(rowId2);

        List<Theme> themes = dao.getAllThemes(rowIdList);
        assertEquals(2, themes.size());

        dao.removeTheme(rowId2);
        dao.removeTheme(rowId);
    }

    @Test
    public void editTheme() {
        long rowId = dao.addTheme(testTheme);
        Theme firstSnapshot = dao.getTheme(rowId);
        assertEquals(rowId, firstSnapshot.getId());

        dao.updateTheme(new Theme(rowId, "ALTERED_TITLE", "ALTERED_DESCRIPTION"));
        Theme secondSnapshot = dao.getTheme(rowId);
        assertEquals("ALTERED_TITLE", secondSnapshot.getTitle());

        dao.removeTheme(rowId);
    }

    @Test
    public void addGoal() throws Exception {
        long rowId = dao.addGoal(testGoal);
        assertTrue(rowId >= 0);

        Goal recoveredTheme = dao.getGoal(rowId);
        assertTrue(recoveredTheme.getId() == rowId);

        boolean successfulRemoval = dao.removeGoal(rowId);
        assertTrue(successfulRemoval);
    }
}