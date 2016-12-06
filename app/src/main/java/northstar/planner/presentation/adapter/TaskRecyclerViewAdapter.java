package northstar.planner.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.presentation.Theme.ThemeFragment;
import northstar.planner.presentation.goal.GoalFragment;
import northstar.planner.presentation.swipe.ItemTouchHelperAdapter;

public class TaskRecyclerViewAdapter
        extends RecyclerView.Adapter<TaskRecyclerViewAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Task> tasks;
    private GoalFragment.GoalFragmentListener activityListener;

    public TaskRecyclerViewAdapter(List<Task> tasks, GoalFragment.GoalFragmentListener lis) {
        this.tasks = tasks;
        activityListener = lis;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Task currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTitle());
        holder.due.setText(currentTask.getDueString());

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
        Task removedTask = tasks.remove(position); // temp delete, needs a little more ... body.
        notifyItemRemoved(position);

        activityListener.completeTask(removedTask);
    }

    @Override
    public void onItemDeleted(int position) {
        Task removed = tasks.remove(position);
        activityListener.removeTask(position, removed);
        notifyItemRemoved(position);
    }

    public void updateList(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    public List<Task> getList() {
        return tasks;
    }

    public void undoDeletion(Task item, int position) {
        tasks.add(position, item);
        notifyItemInserted(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView title, due;

        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.item_task_title);
            due = (TextView) v.findViewById(R.id.item_task_due);
        }
    }
}
