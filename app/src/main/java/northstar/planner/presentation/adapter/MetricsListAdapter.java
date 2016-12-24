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
import northstar.planner.models.Metric;

public class MetricsListAdapter extends ArrayAdapter<Metric> implements Serializable {
    private List<Metric> metrics;

    public MetricsListAdapter(Context context, List<Metric> metrics) {
        super(context, R.layout.item_theme);
        this.metrics = metrics;
    }

    @Override
    public int getCount() {
        return metrics.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_success_criteria, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_success_criteria_title);
        TextView committed = (TextView) convertView.findViewById(R.id.item_success_criteria_progress);

        text.setText(metrics.get(position).getTitle());
        committed.setText(metrics.get(position).getProgressString());


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return metrics.get(position).getId();
    }

    @Override
    public Metric getItem(int position) {
        return metrics.get(position);
    }

    @Override
    public void add(Metric object) {
        metrics.add(0, object);
        notifyDataSetChanged();
    }

    public List<Metric> getList() {
        return metrics;
    }

    public void updateSuccessCriteria(Metric updatedMetric) {
        for (int i = 0; i < metrics.size(); i++) {
            Metric sc = metrics.get(i);
            if (sc.getId() == updatedMetric.getId()) {
                metrics.set(i, updatedMetric);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public boolean remove(int position) {
        metrics.remove(position);
        notifyDataSetChanged();
        return true;
    }
}
