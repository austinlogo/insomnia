package northstar.planner.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.SuccessCriteria;

public class SuccessCriteriaSpinnerAdapter extends ArrayAdapter<SuccessCriteria> {
    private List<SuccessCriteria> successCriterias;

    public SuccessCriteriaSpinnerAdapter(Context context, List<SuccessCriteria> successCriterias) {
        super(context, R.layout.item_default);
        this.successCriterias = successCriterias;
    }

    @Override
    public int getCount() {
        return successCriterias.size();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        convertView = (convertView == null)
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_default, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_default_text);

        text.setText(successCriterias.get(position).getTitle());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        return successCriterias.get(position).getId();
    }

    @Override
    public SuccessCriteria getItem(int position) {
        return successCriterias.get(position);
    }

    @Override
    public void add(SuccessCriteria object) {
        successCriterias.add(0, object);
        notifyDataSetChanged();
    }

    private String getProgressString(SuccessCriteria sc) {
        return sc.getProgress() + " / " + sc.getCommitted();
    }
}
