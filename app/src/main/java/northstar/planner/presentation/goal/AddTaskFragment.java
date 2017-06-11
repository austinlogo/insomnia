package northstar.planner.presentation.goal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemSelected;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.utils.DateTimeSetter;
import northstar.planner.utils.DateTimeSetterCallback;
import northstar.planner.utils.DateUtils;
import northstar.planner.utils.NumberUtils;

public class AddTaskFragment
        extends BaseFragment
        implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.fragment_add_task_title_value)
    EditText addTaskTitle;

    @BindView(R.id.fragment_add_task_datepicker_value)
    TextView datePickerValue;

    @BindView(R.id.fragment_add_task_successcriteria_value)
    Spinner scValues;

    @BindView(R.id.fragment_add_task_committed_value)
    EditText committedValue;

    @BindView(R.id.fragment_add_task_successcriteria_icon)
    ImageButton metricIcon;

    @BindView(R.id.fragment_add_task_committed_icon)
    ImageButton committedIcon;

    private AddTaskFragmentListener activityListener;
    private Metric selectedMetric;
    private SuccessCriteriaSpinnerAdapter successCriteriaSpinnerAdapter;
    private DateTime selectedDate;
    private boolean isScratch;

    public static AddTaskFragment newInstance(String title) {
        Bundle b = new Bundle();
        b.putString(TaskTable.TITLE_COLUMN, title);

        AddTaskFragment newFragment = new AddTaskFragment();
        newFragment.setArguments(b);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, v);
        updateFragmentValues(successCriteriaSpinnerAdapter);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (AddTaskFragmentListener) activity;
    }

    @OnClick(R.id.fragment_add_task_title_icon)
    public void onClickTitle() {
        setVisible(addTaskTitle);
    }

    @OnClick(R.id.fragment_add_task_datepicker_icon)
    public void openDatePicker(View v) {
        setVisible(datePickerValue);
        new DateTimeSetter(getActivity(), new DateTimeSetterCallback() {
            @Override
            public void onValuesSet(DateTime time) {
                selectedDate = time;
                datePickerValue.setText( Task.getDateString(time));
            }
        }).selectTime();
    }

    @OnClick(R.id.fragment_add_task_successcriteria_icon)
    public void onClickSuccessCriteria() {
        setVisible(scValues);
    }

    @OnItemSelected(R.id.fragment_add_task_successcriteria_value)
    public void onMetricSelected(Spinner spinner, int position) {
        selectedMetric = successCriteriaSpinnerAdapter.getItem(position);
        spinner.setSelection(position);

        if (position == 0 || (selectedMetric != null && !selectedMetric.needsCommitmentInput())) {
            committedIcon.setVisibility(View.GONE);
        } else {
            committedIcon.setVisibility(View.VISIBLE);
        }

        String maxHint = String.format(getString(R.string.max), selectedMetric.getCommitted());
        committedValue.setHint(maxHint);
    }

    @OnClick(R.id.fragment_add_task_done_icon)
    public void onDoneClick() {
        if (addTaskTitle.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.no_title), Toast.LENGTH_SHORT).show();
            return;
        }

        int committedValueInt = 0;
        if (selectedMetric == null) {
            committedValueInt = 0;
        } else if (selectedMetric.needsCommitmentInput()) {
            committedValueInt = NumberUtils.parseInt(committedValue.getText().toString());
        } else {
            committedValueInt = 1;
        }

        Task newTask = new Task(addTaskTitle, selectedDate, selectedMetric, committedValueInt);

        if (isScratch) {
            newTask.setGoal(BaseModel.SCRATCH_ID);
        }

        activityListener.addNewTask(newTask);
        addTaskTitle.setText("");
    }

    @OnClick(R.id.fragment_add_task_committed_icon)
    public void onCommittedClick() {
        setVisible(committedValue);
    }

    private void setVisible(View visibleView) {
        addTaskTitle.setVisibility(addTaskTitle.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        datePickerValue.setVisibility(datePickerValue.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        scValues.setVisibility(scValues.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        committedValue.setVisibility(committedValue.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);

        visibleView.requestFocus();
    }

    @OnEditorAction(R.id.fragment_add_task_title_value)
    public boolean onEditTitleEdiorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDoneClick();
            return true;
        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = selectedDate
                .withYear(year)
                .withMonthOfYear(monthOfYear)
                .withDayOfMonth(dayOfMonth);
//        selectedDate.set(year, monthOfYear, dayOfMonth);
        datePickerValue.setText(DateUtils.getDateString(selectedDate));
    }

    public void updateFragmentValues(SuccessCriteriaSpinnerAdapter successCriteriasAdapter) {
        isScratch = false;
        setVisible(addTaskTitle);
        datePickerValue.setText("");
        selectedDate = null;
        addTaskTitle.setText("");
        selectedMetric = null;
        scValues.setAdapter(successCriteriasAdapter);
        this.successCriteriaSpinnerAdapter = successCriteriasAdapter;
        metricIcon.setVisibility(View.VISIBLE);
        committedIcon.setVisibility(View.GONE);

        addTaskTitle.requestFocus();
    }

    public void updateFragmentValuesForTodayTask() {
        updateFragmentValues(null);
        metricIcon.setVisibility(View.GONE);
        isScratch = true;
        selectedDate = null;
        committedValue.setText("");
    }

    public interface AddTaskFragmentListener {
        void addNewTask(Task t);
    }
}
