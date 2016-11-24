package northstar.planner.presentation.goal;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.GoalTable;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;

public class GoalFragment
        extends Fragment implements View.OnKeyListener, TextView.OnEditorActionListener {

    @BindView(R.id.fragment_goal_title)
    EditText editTitle;

    @BindView(R.id.fragment_goal_description)
    EditText editDescription;

    @BindView(R.id.fragment_goal_edit)
    ImageButton editButton;

    @BindView(R.id.fragment_goal_success_criteria)
    ListView successCriteria;

    @BindView(R.id.fragment_goal_tasks)
    ListView successTasks;

    @BindView(R.id.fragment_goal_new_success_criteria_title)
    EditText successCriteriaTitle;

    @BindView(R.id.fragment_goal_new_success_criteria_committed)
    EditText successCriteriaCommittedValue;

    @BindView(R.id.fragment_goal_success_criteria_spinner)
    Spinner scSpinner;

    private long parentTheme;
    private Goal currentGoal;
    private PlannerSqliteDAO dao;
    private SuccessCriteriaListAdapter successCriteriasAdapter;

    public static GoalFragment newInstance(Bundle b) {
        GoalFragment newFragment = new GoalFragment();
        newFragment.setArguments(b);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new PlannerSqliteDAO();

        parentTheme = getArguments().getLong(GoalTable.THEME_COLUMN);
        currentGoal = dao.getGoal(getArguments().getLong(GoalTable._ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_goal, container, false);
        ButterKnife.bind(this, v);

        initListeners();

        successCriteriasAdapter = new SuccessCriteriaListAdapter(getActivity().getApplicationContext(), currentGoal.getSuccessCriterias());
        successCriteria.setAdapter(successCriteriasAdapter);

        scSpinner.setAdapter(new SuccessCriteriaSpinnerAdapter(getActivity().getApplicationContext(), currentGoal.getSuccessCriterias()));

        return v;
    }

    private void initListeners() {
        successCriteriaTitle.setOnKeyListener(this);
        successCriteriaCommittedValue.setOnEditorActionListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentGoal = dao.getGoal(currentGoal.getId());
        initViews();
    }

    public void initViews() {
        editTitle.setText(currentGoal.getTitle());
        editDescription.setText(currentGoal.getDescription());
    }

    @Override
    public void onPause() {
        super.onPause();

        currentGoal.updateGoal(editTitle.getText().toString(), editDescription.getText().toString());
        dao.updateGoal(currentGoal);
    }

    @OnClick(R.id.fragment_goal_edit)
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_goal_edit:
                setEditable(!isEditing());
                break;
            case R.id.fragment_goal_delete:

        }
    }

    @OnClick(R.id.fragment_goal_new_task)
    public void newGoal(View v) {

    }

    private void setEditable(boolean isEditable) {

        if (isEditable) {
            editButton.setImageResource(R.drawable.ic_done_black_36dp);

            editTitle.setInputType(InputType.TYPE_CLASS_TEXT);
            editDescription.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editButton.setImageResource(R.drawable.ic_mode_edit_black_36dp);

            editTitle.setKeyListener(null);
            editDescription.setKeyListener(null);

        }
    }

    private boolean isEditing() {
        return editTitle.getKeyListener() != null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            return addSuccessCriteria();
        }

        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {

            return addSuccessCriteria();
        }

        return false;
    }

    public boolean addSuccessCriteria() {
        String newSuccessCriteriaTitle = successCriteriaTitle.getText().toString();
        int newSuccessCriteriaCommitted = convertSuccessCriteriaCommittedValueToInt();

        if (newSuccessCriteriaCommitted == 0 || newSuccessCriteriaTitle.isEmpty()) {
            return false;
        }

        storeAndPropagateNewSuccessCriteria(newSuccessCriteriaTitle, newSuccessCriteriaCommitted);
        return true;
    }

    private int convertSuccessCriteriaCommittedValueToInt() {
        try {
            return Integer.parseInt(successCriteriaCommittedValue.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void storeAndPropagateNewSuccessCriteria(String newSuccessCriteriaTitle, int newSuccessCriteriaCommitted) {
        SuccessCriteria sc = new SuccessCriteria(currentGoal, newSuccessCriteriaTitle, newSuccessCriteriaCommitted);
        sc = dao.addSuccessCriteria(sc);
        successCriteriasAdapter.add(sc);
        currentGoal.addSuccessCriteria(sc);

    }

    public interface GoalFragmentListener {
        String setTitle();
    }
}
