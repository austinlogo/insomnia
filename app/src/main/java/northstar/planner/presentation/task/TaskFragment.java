package northstar.planner.presentation.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import northstar.planner.R;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseFragment;

public class TaskFragment extends BaseFragment {

    Task currentTask;
    PlannerSqliteDAO dao;

    public static TaskFragment newInstance(Bundle b) {
        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new PlannerSqliteDAO();
        currentTask = (Task) getArguments().getSerializable(TaskTable.TABLE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_task, container, false);
        getActivity().setTitle("me");

        return v;
    }

    public interface TaskFragmentListener {

    }
}
