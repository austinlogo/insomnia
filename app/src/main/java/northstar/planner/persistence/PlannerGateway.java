package northstar.planner.persistence;

import northstar.planner.models.Theme;

public interface PlannerGateway {

    void addTheme(Theme newTheme);

    Theme getTheme(int themeId);

    void editTheme(String title, String description);
}
