package northstar.planner.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Theme;
import northstar.planner.presentation.Theme.ListThemesFragment;
import northstar.planner.presentation.Theme.ThemeFragment;
import northstar.planner.presentation.swipe.ItemTouchHelperAdapter;

public class ThemeRecyclerViewAdapter
        extends RecyclerView.Adapter<ThemeRecyclerViewAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<Theme> themes;
    ListThemesFragment.ListThemesFragmentListener activityListener;

    public ThemeRecyclerViewAdapter(List<Theme> themes, ListThemesFragment.ListThemesFragmentListener lis) {
        this.themes = themes;
        activityListener = lis;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_theme, parent, false);

        MyViewHolder holder =  new MyViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Theme theme = themes.get(position);
        holder.view.setText(theme.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityListener.startThemeEdit(theme);
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
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
                Collections.swap(themes, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(themes, i, i - 1);
            }
        }
    }

    @Override
    public void onItemComplete(int position) {
//        themes.remove(position); // temp delete, needs a little more ... body.
//        activityListener.removeItem(position);
//        notifyItemRemoved(position);
    }

    @Override
    public void onItemDeleted(int position) {
        Theme removed = themes.remove(position);
        activityListener.removeItem(removed, position);
        notifyItemRemoved(position);
    }

    public List<Theme> getList() {
        return themes;
    }

    public void updateList(List<Theme> allThemes) {
        themes.clear();
        themes.addAll(allThemes);
        notifyDataSetChanged();
    }

    public void undoDeletion(Theme item, int position) {
        themes.add(position, item);
        notifyItemInserted(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView view;
        private ThemeFragment.ThemeFragmentListener listener;

        public MyViewHolder(View v) {
            super(v);
            view = (TextView) v.findViewById(R.id.item_default_text);
        }
    }


}
