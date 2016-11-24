package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.DrawerItem;


public class DrawerAdapter extends ArrayAdapter<String> {
    List<DrawerItem> drawerItems;

    public DrawerAdapter(Context context, int resource, List<DrawerItem> objects) {
        super(context, resource);

        drawerItems = objects;
    }

    @Override
    public int getCount() {
        return drawerItems.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return drawerItems.get(position).getText();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_drawer, parent, false)
                : convertView;

        ImageView imageView = (ImageView) convertView.findViewById(R.id.item_drawer_icon);
        TextView text = (TextView) convertView.findViewById(R.id.item_drawer_text);

        imageView.setImageResource(drawerItems.get(position).getDrawableResource());
        text.setText(getItem(position));

        return convertView;
    }
}
