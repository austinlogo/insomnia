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
import northstar.planner.models.SuccessCriteria;

public class SuccessCriteriaListAdapter extends ArrayAdapter<SuccessCriteria> implements Serializable {
    private List<SuccessCriteria> successCriterias;

    public SuccessCriteriaListAdapter(Context context, List<SuccessCriteria> successCriterias) {
        super(context, R.layout.item_theme);
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
                ? LayoutInflater.from(getContext()).inflate(R.layout.item_success_criteria, parent, false)
                : convertView;

        TextView text = (TextView) convertView.findViewById(R.id.item_success_criteria_title);
        TextView committed = (TextView) convertView.findViewById(R.id.item_success_criteria_progress);

        text.setText(successCriterias.get(position).getTitle());
        committed.setText(successCriterias.get(position).getProgressString());


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return successCriterias.get(position).getId();
    }

    @Override
    public SuccessCriteria getItem(int position) {
        return position == 0
                ? null
                : successCriterias.get(position - 1);
    }

    @Override
    public void add(SuccessCriteria object) {
        successCriterias.add(0, object);
        notifyDataSetChanged();
    }

    public List<SuccessCriteria> getList() {
        return successCriterias;
    }

    public void updateSuccessCriteria(SuccessCriteria updatedSuccessCriteria) {
        for (int i = 0; i < successCriterias.size(); i++) {
            SuccessCriteria sc = successCriterias.get(i);
            if (sc.getId() == updatedSuccessCriteria.getId()) {
                successCriterias.set(i, updatedSuccessCriteria);
                break;
            }
        }

        notifyDataSetChanged();
    }
}
