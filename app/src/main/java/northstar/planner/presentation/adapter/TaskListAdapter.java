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
import northstar.planner.models.Goal;
import northstar.planner.models.Task;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private List<Task> TaskList;

    public TaskListAdapter(Context context, List<Task> themeList) {
        super(context, R.layout.item_theme);
        this.TaskList = themeList;
    }

    @Override
    public int getCount() {
        return TaskList.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_theme, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_default_text);
        text.setText(TaskList.get(position).getTitle());

        return convertView;
    }

    @Override
    public void add(Task object) {
        TaskList.add(0, object);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return TaskList.get(position).getId();
    }

    @Override
    public Task getItem(int position) {
        return TaskList.get(position);
    }
}
