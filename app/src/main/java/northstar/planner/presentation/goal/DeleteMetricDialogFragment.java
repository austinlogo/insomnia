package northstar.planner.presentation.goal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.tables.MetricTable;
import northstar.planner.presentation.hours.BaseDialogFragment;

public class DeleteMetricDialogFragment extends BaseDialogFragment {


    private int currentMetricId;
    private DialogFragmentOnDismissCallback dismissCallback;

    public static DeleteMetricDialogFragment newInstance(long metricId, DialogFragmentOnDismissCallback dc) {


        Bundle b = new Bundle();
        b.putInt(MetricTable._ID, (int) metricId);

        DeleteMetricDialogFragment dialog = new DeleteMetricDialogFragment();

        dialog.setDismissCallback(dc);
        dialog.setArguments(b);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentMetricId = (int) getArguments().get(MetricTable._ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.dialog_delete_metric, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.dialog_delete_metric_layout)
    public void onClickDeleteMetric() {
        getBaseActivity().getDao().removeMetric(currentMetricId);
        dismissCallback.onDismiss();
        dismiss();
    }

    public void setDismissCallback(DialogFragmentOnDismissCallback dismissCallback) {
        this.dismissCallback = dismissCallback;
    }
}
