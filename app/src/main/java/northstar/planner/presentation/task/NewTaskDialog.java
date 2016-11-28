package northstar.planner.presentation.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.SuccessCriteria;
import northstar.planner.models.Task;
import northstar.planner.models.tables.SuccessCriteriaTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.adapter.SuccessCriteriaListAdapter;
import northstar.planner.presentation.adapter.SuccessCriteriaSpinnerAdapter;
import northstar.planner.utils.DateUtils;

public class NewTaskDialog
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.dialog_new_task_title)
    EditText title;

    @BindView(R.id.dialog_new_task_successcriteria)
    Spinner scSpinner;

    @BindView(R.id.dialog_new_task_datepicker)
    TextView picker;

    @BindView(R.id.dialog_new_task_commitment)
    EditText commitment;

    private NewTaskDialogListener activityListener;
    private SuccessCriteriaListAdapter successCriteriaListAdapter;
    private SuccessCriteria selectedSc;
    private Calendar selectedDate;

    public static NewTaskDialog newinstance(EditText editTitle, SuccessCriteriaListAdapter successCriteriasAdapter) {
        NewTaskDialog newTaskdialog = new NewTaskDialog();
        Bundle b = new Bundle();
        b.putString(TaskTable.TITLE_COLUMN, editTitle.getText().toString());
        b.putSerializable(SuccessCriteriaTable.TABLE_NAME, successCriteriasAdapter);
        newTaskdialog.setArguments(b);
        return newTaskdialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        successCriteriaListAdapter = (SuccessCriteriaListAdapter) getArguments().get(SuccessCriteriaTable.TABLE_NAME);
        selectedDate = DateUtils.today();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (NewTaskDialogListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_new_task, container, false);
        ButterKnife.bind(this, v);

        title.setText(getArguments().getString(TaskTable.TITLE_COLUMN));
        scSpinner.setAdapter(new SuccessCriteriaSpinnerAdapter(getActivity(), successCriteriaListAdapter.getList()));
        title.requestFocus();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.dialog_new_task_datepicker)
    public void onDatePickerClick(View v) {
        DatePickerDialog dialog = initDatePickerDialog();
        dialog.show();
    }

    private DatePickerDialog initDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(selectedDate.getTime().getTime());
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedDate.set(year, monthOfYear, dayOfMonth);
        picker.setText(DateUtils.getDateString(getResources().getString(R.string.drawer_item_today).toLowerCase(), selectedDate));
    }

    @OnClick(R.id.dialog_new_task_done)
    public void onDoneClick(View v) {
        if (title.getText().toString().isEmpty()
                || picker.getText().toString().isEmpty()
                || commitment.getText().toString().isEmpty()) {

            Toast.makeText(getActivity(), getString(R.string.not_full), Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(title, selectedDate, selectedSc, Integer.parseInt(commitment.getText().toString()));
        activityListener.setResult(newTask);
        dismiss();
    }

    @OnItemSelected(R.id.dialog_new_task_successcriteria)
    public void onSuccessCriteriaSelected(Spinner spinner, int position) {
        selectedSc = successCriteriaListAdapter.getItem(position);
        String maxHint = String.format(getString(R.string.max), selectedSc.getCommitted());
        commitment.setHint(maxHint);
    }

    public interface NewTaskDialogListener {
        Task setResult(Task task);
    }
}
