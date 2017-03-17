package northstar.planner.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.presentation.Theme.ThemeFragment;
import northstar.planner.presentation.swipe.ItemTouchHelperAdapter;

public class GoalRecyclerViewAdapter
        extends RecyclerView.Adapter<GoalRecyclerViewAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Goal> goals;
    private ThemeFragment.ThemeFragmentListener activityListener;


    public GoalRecyclerViewAdapter(List<Goal> goals, ThemeFragment.ThemeFragmentListener lis) {
        this.goals = goals;
        activityListener = lis;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_theme, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Goal currentGoal = goals.get(position);
        holder.view.setText(currentGoal.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityListener.openGoal(currentGoal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
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
                Collections.swap(goals, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(goals, i, i - 1);
            }
        }
    }

    public void addItem(Goal newGoal) {
        goals.add(newGoal);
        notifyItemInserted(goals.size() );
    }

    @Override
    public void onItemComplete(int position) {
        goals.remove(position); // temp delete, needs a little more ... body.
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDeleted(int position) {
        notifyItemRemoved(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView view;

        public MyViewHolder(View v) {
            super(v);
            view = (TextView) v.findViewById(R.id.item_default_text);
        }
    }


}
