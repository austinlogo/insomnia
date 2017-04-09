package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Task;

public class DependencyListAdapter extends ArrayAdapter<Task> implements Serializable {
    private List<Task> tasks;

    public DependencyListAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.item_theme);
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_simple, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_simple_text);

        text.setText(tasks.get(position).getTitle());
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getId();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    public List<Task> getList() {
        return tasks;
    }
}
