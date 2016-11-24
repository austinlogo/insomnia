package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Theme;

public class ThemeListAdapter extends ArrayAdapter<Theme> {
    private List<Theme> themeList;

    public ThemeListAdapter(Context context, int resource, List<Theme> themeList) {
        super(context, resource);
        this.themeList = themeList;
    }

    @Override
    public int getCount() {
        return themeList.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_theme, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_default_text);
        text.setText(themeList.get(position).getTitle());

        return convertView;
    }

    @Override
    public void add(Theme object) {
        themeList.add(0, object);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return themeList.get(position).getId();
    }

    @Override
    public Theme getItem(int position) {
        return themeList.get(position);
    }
}
