package northstar.planner.presentation.success;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Metric;
import northstar.planner.models.MetricType;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.utils.NumberUtils;

public class AddMetricFragment
        extends BaseFragment {

    @BindView(R.id.fragment_add_metric_title_value)
    EditText addMetricTitle;

    @BindView(R.id.fragment_add_metric_committed_value)
    EditText addMetricCommittedValue;

    @BindView(R.id.fragment_add_metric_type_icon)
    ImageButton metricTypeIcon;

    @BindView(R.id.fragment_add_metric_committed_icon)
    ImageButton metricCommittedIcon;

    private AddMetricFragmentListener attachedActivity;
    private MetricType metricType = MetricType.INCREMENTAL;

    public static AddMetricFragment newInstance() {
        return new AddMetricFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_metric, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachedActivity = (AddMetricFragmentListener) activity;
    }

    @OnClick(R.id.fragment_add_metric_title_icon)
    public void onClickTitleIcon() {
        setVisible(addMetricTitle);
    }

    @OnClick(R.id.fragment_add_metric_committed_icon)
    public void onClickCommitted() {
        setVisible(addMetricCommittedValue);
    }

    @OnClick(R.id.fragment_add_metric_type_icon)
    public void onClickType() {
        if (metricType.equals(MetricType.INCREMENTAL)) {
            metricType = MetricType.DECREMENTAL;
            metricTypeIcon.setImageResource(R.drawable.ic_arrow_downward_white_24dp);
            metricCommittedIcon.setVisibility(View.GONE);
        } else {
            metricType = MetricType.INCREMENTAL;
            metricCommittedIcon.setVisibility(View.VISIBLE);
            metricTypeIcon.setImageResource(R.drawable.ic_arrow_upward_white_24dp);
        }
    }

    @OnClick(R.id.fragment_add_metric_done_icon)
    public void onDoneIconClicked() {
        String metricTitle = addMetricTitle.getText().toString();
        int committedValue = NumberUtils.parseInt(addMetricCommittedValue.getText().toString());
        Metric newMetric = Metric.newInstance(metricTitle, committedValue);

        attachedActivity.addandStoreMetric(newMetric);
    }

    private void setVisible(View visibleView) {
        addMetricTitle.setVisibility(addMetricTitle.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        addMetricCommittedValue.setVisibility(addMetricCommittedValue.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);

        visibleView.requestFocus();
    }

    public interface AddMetricFragmentListener {
        Metric addandStoreMetric(Metric sc);
    }
}
