package northstar.planner.presentation.goal;

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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.utils.DateUtils;

public class AddTaskFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.fragment_add_task_title)
    EditText addTaskTitle;

    @BindView(R.id.fragment_add_task_datepicker_choice)
    TextView datePickerChoice;

    @BindView(R.id.fragment_add_task_successcriteria_value)
    Spinner scValues;


    private SuccessCriteria selectedSc;
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

    @OnClick(R.id.fragment_add_task_title_icon)
    public void onClickTitle() {
        setVisible(addTaskTitle);
    }

    @OnClick(R.id.fragment_add_task_datepicker)
    public void openDatePicker(View v) {
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

    @OnClick(R.id.fragment_add_task_successcriteria)
    public void onClickSuccessCriteria() {
        setVisible(scValues);

    }

    private void setVisible(View visibleView) {
        addTaskTitle.setVisibility(addTaskTitle.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        datePickerChoice.setVisibility(datePickerChoice.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
        scValues.setVisibility(scValues.getId() == visibleView.getId() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedDate.set(year, monthOfYear, dayOfMonth);
//        picker.setText(DateUtils.getDateString(getResources().getString(R.string.drawer_item_today).toLowerCase(), selectedDate));
    }

    public void updateFragmentValues(String newTaskTitle, SuccessCriteriaListAdapter successCriteriasAdapter) {
        addTaskTitle.setText(newTaskTitle);
        scValues.setAdapter(successCriteriasAdapter);

        addTaskTitle.requestFocus();
    }

    public interface AddTaskFragmentListener {

    }
}
