package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.drawer.ShallowModel;

public class DrawerGoalAdapter extends ArrayAdapter<String> {

    private List<ShallowModel> shallowGoals;
    private DrawerListeners listener;

    public DrawerGoalAdapter(Context context, int resource, List<ShallowModel> objects, DrawerListeners listener) {
        super(context, resource);
        this.listener = listener;
        shallowGoals = objects;
    }

    @Override
    public int getCount() {
        return shallowGoals.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return shallowGoals.get(position).getTitle();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_simple, parent, false)
                : convertView;

        TextView tv = (TextView) convertView.findViewById(R.id.item_simple_text);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onGoalClicked(shallowGoals.get(position).getId());
            }
        });

        tv.setText(getItem(position));

        return convertView;
    }
}
