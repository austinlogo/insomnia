package northstar.planner.presentation.goal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import northstar.planner.R;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.utils.DateUtils;

public class AddTaskFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.fragment_add_task_title_value)
    EditText addTaskTitle;

    @BindView(R.id.fragment_add_task_datepicker_value)
    TextView datePickerValue;

    @BindView(R.id.fragment_add_task_successcriteria_value)
    Spinner scValues;

    @BindView(R.id.fragment_add_task_committed_value)
    EditText committedValue;

    private boolean firstSelect;
    private AddTaskFragmentListener activityListener;
    private SuccessCriteria selectedSc;
    private SuccessCriteriaSpinnerAdapter successCriteriaSpinnerAdapter;
    private Calendar selectedDate;

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

        selectedDate = selectedDate == null
                ? DateUtils.today()
                : selectedDate;

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(selectedDate.getTime().getTime());
        dialog.show();
    }

    @OnClick(R.id.fragment_add_task_successcriteria_icon)
    public void onClickSuccessCriteria() {
        setVisible(scValues);
    }

    @OnItemSelected(R.id.fragment_add_task_successcriteria_value)
    public void onSuccessCriteriaSelected(Spinner spinner, int position) {
        if (firstSelect) {
            firstSelect = false;
            committedValue.setText("0");
//            committedValue.setVisibility(View.GONE);
            return;
        } else {
            committedValue.setText("");
//            commitment.setVisibility(View.VISIBLE);
        }

        selectedSc = successCriteriaSpinnerAdapter.getItem(position);
        String maxHint = String.format(getString(R.string.max), selectedSc.getCommitted());
        committedValue.setHint(maxHint);
    }

    @OnClick(R.id.fragment_add_task_done_icon)
    public void onDoneClick(View v) {
        if (addTaskTitle.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.no_title), Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(addTaskTitle, selectedDate, selectedSc, Integer.parseInt(committedValue.getText().toString()));
        activityListener.addNewTask(newTask);
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedDate.set(year, monthOfYear, dayOfMonth);
        datePickerValue.setText(DateUtils.getDateString(getString(R.string.today), selectedDate));
    }

    public void updateFragmentValues(String newTaskTitle, SuccessCriteriaSpinnerAdapter successCriteriasAdapter) {
        addTaskTitle.setText(newTaskTitle);
        scValues.setAdapter(successCriteriasAdapter);
        this.successCriteriaSpinnerAdapter = successCriteriasAdapter;

        addTaskTitle.requestFocus();
    }

    public interface AddTaskFragmentListener {
        void addNewTask(Task t);
    }
}
