package northstar.planner.presentation.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import northstar.planner.R;
import northstar.planner.models.Metric;
import northstar.planner.models.Task;
import northstar.planner.models.tables.MetricTable;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.presentation.adapter.MetricsListAdapter;
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
    private MetricsListAdapter metricsListAdapter;
    private Metric selectedSc;
    private DateTime selectedDate;
    private boolean firstSelect = true;

    public static NewTaskDialog newinstance(EditText editTitle, MetricsListAdapter successCriteriasAdapter) {
        NewTaskDialog newTaskdialog = new NewTaskDialog();
        Bundle b = new Bundle();
        b.putString(TaskTable.TITLE_COLUMN, editTitle.getText().toString());
        b.putSerializable(MetricTable.TABLE_NAME, successCriteriasAdapter);
        newTaskdialog.setArguments(b);
        return newTaskdialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        metricsListAdapter = (MetricsListAdapter) getArguments().get(MetricTable.TABLE_NAME);
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
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.dialog_new_task, container, false);
        ButterKnife.bind(this, v);

        title.setText(getArguments().getString(TaskTable.TITLE_COLUMN));
        scSpinner.setAdapter(new SuccessCriteriaSpinnerAdapter(getActivity(), metricsListAdapter.getList()));
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
        selectedDate = selectedDate == null
                ? DateUtils.today()
                : selectedDate;
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                this,
                selectedDate.getYear(),
                selectedDate.getMonthOfYear(),
                selectedDate.getDayOfMonth());
        dialog.getDatePicker().setMinDate(selectedDate.getMillis());
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new DateTime(year, monthOfYear, dayOfMonth, selectedDate.getHourOfDay(), selectedDate.getMinuteOfHour());
        picker.setText(DateUtils.getDateString(selectedDate));
    }

    @OnClick(R.id.dialog_new_task_done)
    public void onDoneClick(View v) {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.no_title), Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(title, selectedDate, selectedSc, Integer.parseInt(commitment.getText().toString()));
        activityListener.setResult(newTask);
        dismiss();
    }

    @OnItemSelected(R.id.dialog_new_task_successcriteria)
    public void onSuccessCriteriaSelected(Spinner spinner, int position) {
        if (firstSelect) {
            firstSelect = false;
            commitment.setText("0");
            commitment.setVisibility(View.GONE);
            return;
        } else {
            commitment.setVisibility(View.VISIBLE);
        }

        selectedSc = metricsListAdapter.getItem(position);
        String maxHint = String.format(getString(R.string.max), selectedSc.getCommitted());
        commitment.setHint(maxHint);
    }

    public interface NewTaskDialogListener {
        Task setResult(Task task);
    }
}
