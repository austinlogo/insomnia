package northstar.planner.presentation.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import northstar.planner.R;

public class PlannerSpinnerAdapter<E> extends ArrayAdapter<E> {

    public PlannerSpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public PlannerSpinnerAdapter(Context baseActivity, int simple_spinner_item, E[] values) {
        super(baseActivity, simple_spinner_item, values);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_default, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_default_text);
        text.setText(getItem(position).toString());
        return convertView;
    }
}
