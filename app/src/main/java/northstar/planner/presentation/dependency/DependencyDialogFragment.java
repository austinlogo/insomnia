package northstar.planner.presentation.dependency;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.presentation.adapter.DependencyListAdapter;
import northstar.planner.presentation.hours.BaseDialogFragment;


public class DependencyDialogFragment extends BaseDialogFragment {

    @BindView(R.id.dialog_dependency_chooser_list)
    ListView dependentTaskChooser;

    private DependencyListAdapter mAdapter;
    private List<Task> tasks;
    private List<Task> selected;
    private DependencyChooserCallback callback;

    public static DependencyDialogFragment newInstance(List<Task> tasks, DependencyChooserCallback callback) {
        DependencyDialogFragment fragment = new DependencyDialogFragment();
        fragment.setTaskList(tasks);
        fragment.setCallback(callback);

        return fragment;
    }

    public void setTaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selected = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.dialog_dependency_chooser, container, false);
        ButterKnife.bind(this, v);

        mAdapter = new DependencyListAdapter(getActivity(), tasks);
        dependentTaskChooser.setAdapter(mAdapter);

        return v;
    }

    @OnItemClick(R.id.dialog_dependency_chooser_list)
    public void onClickItem(int position) {
        callback.onChoose(tasks.get(position));
        dismiss();
    }

    public void setCallback(DependencyChooserCallback callback) {
        this.callback = callback;
    }
}

