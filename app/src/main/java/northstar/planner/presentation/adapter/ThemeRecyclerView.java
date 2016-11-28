package northstar.planner.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import northstar.planner.R;
import northstar.planner.models.Theme;

/**
 * Created by Austin on 11/27/2016.
 */

public class ThemeRecyclerView extends RecyclerView.Adapter {
    List<Theme> themes;

    public ThemeRecyclerView(List<Theme> t) {
        themes = t;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView view;

        public MyViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false));
        }
    }
}
