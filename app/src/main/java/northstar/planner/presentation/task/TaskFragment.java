package northstar.planner.presentation.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Task;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.BaseFragment;

public class TaskFragment extends BaseFragment {

    @BindView(R.id.fragment_task_due)
    TextView due;

    @BindView(R.id.item_success_criteria_title)
    TextView successCriteriaTitle;

    @BindView(R.id.item_success_criteria_progress)
    TextView successCriteriaProgress;

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
        ButterKnife.bind(this, v);

        return v;
    }

    public void initUI(Task currentTask) {
        due.setText(currentTask.getDueString());

        if (currentTask.getCompletes() != BaseModel.NEW_ID) {
            successCriteriaTitle.setText(currentTask.getSuccessCriteria().getTitle());
            successCriteriaProgress.setText(currentTask.getSuccessCriteria().getProgressString());
        }
    }

    public interface TaskFragmentListener {

    }
}
