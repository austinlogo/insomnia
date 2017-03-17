package northstar.planner.models.drawer;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.models.Goal;
import northstar.planner.models.Theme;

public class ThemeItem {
    private ShallowModel theme;
    private List<ShallowModel> goals;

    public ThemeItem(Theme deepTheme) {
        theme = new ShallowModel(deepTheme);

        goals = new ArrayList<>();
        for (Goal goal : deepTheme.getGoals()) {
            goals.add(new ShallowModel(goal));
        }
    }

    public ShallowModel getTheme() {
        return theme;
    }

    public void setTheme(ShallowModel theme) {
        this.theme = theme;
    }

    public List<ShallowModel> getGoals() {
        return goals;
    }

    public void addGoal(Goal goal) {
        goals.add(new ShallowModel(goal));
    }

    public void setGoals(List<ShallowModel> goals) {
        this.goals = goals;
    }
}
