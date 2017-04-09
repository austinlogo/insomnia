package northstar.planner.presentation.dependency;

import northstar.planner.models.Task;

public interface DependencyChooserCallback {
    void onChoose(Task chosenTask);
}
