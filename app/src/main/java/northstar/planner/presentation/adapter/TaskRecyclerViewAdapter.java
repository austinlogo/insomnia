package northstar.planner.presentation.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.presentation.goal.GoalActivity;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.swipe.ItemTouchHelperAdapter;
import northstar.planner.utils.DateUtils;

public class TaskRecyclerViewAdapter
        extends RecyclerView.Adapter<TaskRecyclerViewAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Task> tasks;
    private GoalFragment.TaskActionListener activityListener;

    public TaskRecyclerViewAdapter(List<Task> tasks, GoalFragment.TaskActionListener lis) {
        this.tasks = tasks;
        activityListener = lis;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_task_context, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Task currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTitle());
        holder.due.setText(currentTask.getDueString());

        if (!(activityListener instanceof GoalActivity)) {
            holder.goalTitle.setText(currentTask.getGoalTitle());
        }

        if (currentTask.getDue() != null
                && currentTask.getDue().getMillis() < DateUtils.today().getMillis()) {
            holder.due.setTextColor(Color.RED);
        } else {
            holder.due.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityListener.openTask(currentTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        swapItems(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private void swapItems(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(tasks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(tasks, i, i - 1);
            }
        }
    }

    public void addItem(Task newTask) {
        tasks.add(0, newTask);
        notifyItemInserted(0);
    }

    @Override
    public void onItemComplete(int position) {
        Task taskSwipedForCompletion = tasks.get(position);
        if (activityListener.completeTask(taskSwipedForCompletion)) {
//            tasks.remove(position);
//            notifyItemRemoved(position);
        }

//        if (removedTask.containsAnotherIteration()) {
//            tasks.add(position, removedTask);
//            activityListener.updateTask(removedTask);
//        } else {
//            notifyItemRemoved(position);
//            activityListener.completeTask(removedTask);
//        }
    }

    @Override
    public void onItemDeleted(int position) {
        Task removed = tasks.remove(position);
        activityListener.removeTask(position, removed);
        notifyItemRemoved(position);
    }

    /**
     * Just trust me. The UI code has issues with doing this quickly
     * @param newTasks
     */
    public void updateList(List<Task> newTasks) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            notifyItemRemoved(0);
        }

        for (Task t  : newTasks) {
            tasks.add(t);
            notifyItemInserted(tasks.size());
        }
    }

    public List<Task> getList() {
        return tasks;
    }

    public void undoDeletion(Task item, int position) {
        tasks.add(position, item);
        notifyItemInserted(position);
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView title, due, goalTitle;

        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.item_task_title);
            due = (TextView) v.findViewById(R.id.item_task_due);
            goalTitle = (TextView) v.findViewById(R.id.item_task_goal_title);
        }
    }
}
