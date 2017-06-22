package northstar.planner.presentation.task;

import northstar.planner.models.Task;
import northstar.planner.presentation.BaseActivity;

public abstract class TaskManagementActivity extends BaseActivity {

    public void updateTask(Task updatedTask) {
        getDao().updateTask(updatedTask);
        getPlannerNotificationManager().scheduleNextRecurrenceIteration(updatedTask);
        updateActivity();
    }

    public boolean completeTask(Task completedTask) {
        boolean taskContainsAnotherIteration = completedTask.containsAnotherIteration();

        if (taskContainsAnotherIteration) {
            updateTask(completedTask);
        } else {
            removeTask(completedTask);
        }

        return !taskContainsAnotherIteration;
    }

    private void removeTask(Task removedTask) {
        getDao().updateDependencyOnComplete(removedTask.getId());
        getPlannerNotificationManager().cancelAllNotificationsForTask(removedTask);
    }
}
