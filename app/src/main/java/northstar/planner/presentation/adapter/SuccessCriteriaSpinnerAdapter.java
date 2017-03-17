package northstar.planner.presentation.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import northstar.planner.R;
import northstar.planner.models.IncrementalMetric;
import northstar.planner.models.Metric;

public class SuccessCriteriaSpinnerAdapter extends ArrayAdapter<Metric> implements SpinnerAdapter {
    private List<Metric> metrics;

    public SuccessCriteriaSpinnerAdapter(Context context, List<Metric> metrics) {
        super(context, R.layout.item_default);
        List<Metric> spinnerSCList = new ArrayList<>(metrics);

        spinnerSCList.add(0, new IncrementalMetric("Select an Success Criteria", 0));
        this.metrics = spinnerSCList;
    }

    @Override
    public int getCount() {
        return metrics.size();
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(metrics.get(position).getTitle());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_default, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_default_text);
        text.setText(metrics.get(position).getTitle());
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

    private String getProgressString(Metric sc) {
        return sc.getProgress() + " / " + sc.getCommitted();
    }
}
