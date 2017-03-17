package northstar.planner.presentation.today;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.presentation.BaseFragment;

/**
 * Created by Austin on 12/13/2016.
 */

public class AddOverlayFragment extends BaseFragment {

    private AddOverlayListener attachedActivity;

    public static AddOverlayFragment newInstance() {
        return new AddOverlayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal_add_submenue, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachedActivity = (AddOverlayListener) activity;
    }

    @OnClick(R.id.fragment_today_add_submenu_add_task)
    public void onClickAddTask() {
        attachedActivity.showAddTaskWorkflow();
    }

    @OnClick(R.id.fragment_today_add_submenu_add_metric)
    public void onClickAddMetric() {
        attachedActivity.showAddMetricWorkflow();
    }

    @OnClick(R.id.fragment_today_add_submenu_layout)
    public void onClickLayout() {
        attachedActivity.dismissOverlay();
    }

    public interface AddOverlayListener {
        void showAddTaskWorkflow();
        void showAddMetricWorkflow();
        void dismissOverlay();
    }
}
