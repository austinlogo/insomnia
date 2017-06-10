package northstar.planner.presentation.recurrence;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Recurrence;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PeriodUnit;
import northstar.planner.persistence.RecurrenceGateway;
import northstar.planner.presentation.hours.BaseDialogFragment;
import northstar.planner.utils.DateUtils;


public class RecurrenceDialogFragment extends BaseDialogFragment {

    @BindView(R.id.dialog_recurrence_period_value)
    EditText periodValue;

    @BindView(R.id.dialog_recurrence_period_unit)
    Spinner periodUnit;

    @BindView(R.id.dialog_recurrence_end_time)
    TextView endTime;

    @Inject
    RecurrenceGateway recurrenceGateway;

    private Task currentTask;
    private int period;
    private Calendar to;
    private DatePickerDialog datePickerDialog;
    private ArrayAdapter<PeriodUnit> spinnerAdapter;

    public static RecurrenceDialogFragment newInstance(Task currentTask) {
        RecurrenceDialogFragment dialog =  new RecurrenceDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskTable.TABLE_NAME, currentTask);

        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTask = (Task) getArguments().getSerializable(TaskTable.TABLE_NAME);


        datePickerDialog = new DatePickerDialog(getBaseActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, DateUtils.today().get(Calendar.YEAR), DateUtils.today().get(Calendar.MONTH), DateUtils.today().get(Calendar.DAY_OF_MONTH));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.dialog_recurrence, container, false);
        ButterKnife.bind(this, v);

        spinnerAdapter = new ArrayAdapter<>(getBaseActivity(), android.R.layout.simple_spinner_item, PeriodUnit.values());
        periodUnit.setAdapter(spinnerAdapter);

        initUIComponents(currentTask.getRecurrenceSchedule());

        return v;
    }


    private void initUIComponents(Recurrence recurrence) {
        if (recurrence == null) {
            return;
        }

        Calendar endTimeCalendar = Calendar.getInstance();

        if (recurrence.getEndTime() != null) {
            endTimeCalendar.setTime(recurrence.getEndTime());
            endTime.setText(DateUtils.getDateString(getString(R.string.today), endTimeCalendar));
        }

        periodValue.setText(String.valueOf(recurrence.getPeriodUnitMultiplier()));
        periodUnit.setSelection(spinnerAdapter.getPosition(recurrence.getPeriodUnit()));
    }

    @OnClick(R.id.dialog_recurrence_end_time)
    public void onClickEndTime() {
        datePickerDialog = new DatePickerDialog(getBaseActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                to = Calendar.getInstance();
                to.set(year, monthOfYear, dayOfMonth);

                endTime.setText(DateUtils.getDateString(getString(R.string.today), to));
            }
        }, DateUtils.today().get(Calendar.YEAR), DateUtils.today().get(Calendar.MONTH), DateUtils.today().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @OnClick(R.id.dialog_recurrence_done)
    public void onClickDone() {

        try {
            period = Integer.parseInt(periodValue.getText().toString());
        } catch (NumberFormatException e) {
            period = 0;
        }

        if (period == 0) {
            Toast.makeText(getBaseActivity(), getString(R.string.no_period_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Date toDate = to == null ? null : to.getTime();

        Recurrence rec = new Recurrence(currentTask.getId(), period, (PeriodUnit) periodUnit.getSelectedItem(), currentTask.getDue(), toDate);
        currentTask.setRecurrenceSchedule(rec);

        getBaseActivity().getRecurrenceDao().addRecurrenceRecord(rec);
        getBaseActivity().scheduleRecurringNotification(rec, currentTask);

        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        getBaseActivity().updateActivity();
    }
}

