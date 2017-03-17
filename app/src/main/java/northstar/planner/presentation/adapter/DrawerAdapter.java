package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.drawer.ThemeItem;
import northstar.planner.utils.ViewAnimationUtils;


public class DrawerAdapter extends ArrayAdapter<String> {
    private List<ThemeItem> drawerItems;
    private Context context;
    private DrawerListeners listener;

    public DrawerAdapter(Context context, int resource, List<Theme> objects, DrawerListeners listener) {
        super(context, resource);
        drawerItems = new ArrayList<>();
        this.context = context;
        this.listener = listener;

        for (Theme theme : objects) {
            drawerItems.add(new ThemeItem(theme));
        }
    }

    @Override
    public int getCount() {
        return drawerItems.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return drawerItems.get(position).getTheme().getTitle();
    }

    public ThemeItem getItemModel(int position) {
        return drawerItems.get(position);
    }

    public void updateGoal(Goal goal) {
        for (ThemeItem item : drawerItems) {
            if (item.getTheme().getId() == goal.getTheme()) {
                item.addGoal(goal);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, parent, false)
                : convertView;

        final TextView text = (TextView) convertView.findViewById(R.id.item_drawer_text);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onThemeClicked(getItemModel(position).getTheme().getId());
            }
        });

        final ListView goals = (ListView) convertView.findViewById(R.id.item_drawer_goals);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.item_drawer_theme_collapse_icon);

        int buttonVisibility = drawerItems.get(position).getGoals().isEmpty() ? View.INVISIBLE : View.VISIBLE;
        button.setVisibility(buttonVisibility);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goals.getVisibility() == View.VISIBLE) {
                    goals.setVisibility(View.GONE);
                    v.setRotation(180);
                } else {
                    goals.setVisibility(View.VISIBLE);
                    v.setRotation(0);
                }
            }
        });

        goals.setAdapter(new DrawerGoalAdapter(context, android.R.layout.simple_list_item_1, drawerItems.get(position).getGoals(), listener));
        text.setText(getItem(position));

        ViewAnimationUtils.setListViewHeightBasedOnChildren(goals);

        return convertView;
    }
}
