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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import northstar.planner.R;
import northstar.planner.models.ComplexRecurrence;
import northstar.planner.models.Recurrence;
import northstar.planner.models.SimpleRecurrence;
import northstar.planner.models.Task;
import northstar.planner.models.tables.TaskTable;
import northstar.planner.persistence.PeriodTarget;
import northstar.planner.persistence.PeriodUnit;
import northstar.planner.persistence.RecurrenceGateway;
import northstar.planner.persistence.TargetUnit;
import northstar.planner.presentation.adapter.PlannerSpinnerAdapter;
import northstar.planner.presentation.hours.BaseDialogFragment;
import northstar.planner.utils.DateUtils;
import northstar.planner.utils.NumberUtils;



public class RecurrenceDialogFragment extends BaseDialogFragment {

    @BindView(R.id.dialog_recurrence_period_value)
    EditText periodValue;

    @BindView(R.id.dialog_recurrence_period_unit)
    Spinner periodUnit;

    @BindView(R.id.dialog_recurrence_end_time)
    TextView endTime;

    @BindView(R.id.dialog_recurrence_weekly_target_container)
    LinearLayout weeklyTargetContainer;

    @Inject
    RecurrenceGateway recurrenceGateway;

    private Task currentTask;
    private int period;
    private DateTime to;
    private DatePickerDialog datePickerDialog;
    private PlannerSpinnerAdapter<PeriodUnit> spinnerAdapter;
    private List<PeriodTarget> periodTargets;
    private DualHashBidiMap<Integer, PeriodTarget> toggleIdToTargetUnitMap;
    private View currentView;

    public static RecurrenceDialogFragment newInstance(Task currentTask) {
        RecurrenceDialogFragment dialog =  new RecurrenceDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskTable.TABLE_NAME, currentTask);

        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentTask = (Task) getArguments().getSerializable(TaskTable.TABLE_NAME);
        periodTargets = new ArrayList<>();
        datePickerDialog = new DatePickerDialog(getBaseActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, DateUtils.today().getYear(), DateUtils.today().getMonthOfYear(), DateUtils.today().getDayOfMonth());

        toggleIdToTargetUnitMap = new DualHashBidiMap<>();

        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_sunday, PeriodTarget.SUNDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_monday, PeriodTarget.MONDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_tuesday, PeriodTarget.TUESDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_wednesday, PeriodTarget.WEDNESDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_thursday, PeriodTarget.THURSDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_friday, PeriodTarget.FRIDAY);
        toggleIdToTargetUnitMap.put(R.id.dialog_recurrence_weekly_target_saturday, PeriodTarget.SATURDAY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        currentView = inflater.inflate(R.layout.dialog_recurrence, container, false);
        ButterKnife.bind(this, currentView);

        spinnerAdapter = new PlannerSpinnerAdapter<>(getBaseActivity(), android.R.layout.simple_spinner_item, PeriodUnit.values());
        periodUnit.setAdapter(spinnerAdapter);

        initUIComponents(currentTask.getRecurrenceSchedule());

        return currentView;
    }

    private void initUIComponents(Recurrence recurrence) {
        if (recurrence == null) {
            return;
        }

        if (recurrence.getEndTime() != null) {
            endTime.setText(DateUtils.getDateString(recurrence.getEndTime()));
        }

        periodValue.setText(String.valueOf(recurrence.getPeriodUnitMultiplier()));
        periodUnit.setSelection(spinnerAdapter.getPosition(recurrence.getPeriodUnit()));
        setWeeklyTargetContainerVisibility();
    }

    private void setWeeklyTargetContainerVisibility() {
        int weeklyTargetVisibility = (periodUnit.getSelectedItem().equals(PeriodUnit.Week))
                ? View.VISIBLE
                : View.GONE ;

        weeklyTargetContainer.setVisibility(weeklyTargetVisibility);

        //TODO: work to be done here preserving state
        if (currentTask.getRecurrenceSchedule() != null &&
                currentTask.getRecurrenceSchedule().getTargetUnit() != null &&
                currentTask.getRecurrenceSchedule().getTargetUnit().equals(TargetUnit.WEEKDAY)) {

            for (Integer id : toggleIdToTargetUnitMap.keySet()) {
                ToggleButton toggle = (ToggleButton) currentView.findViewById(id);
                PeriodTarget target = toggleIdToTargetUnitMap.get(id);
                toggle.setChecked(currentTask.getRecurrenceSchedule().getTargets().contains(target));
            }
        }
    }

    @OnClick(R.id.dialog_recurrence_end_time)
    public void onClickEndTime() {
        datePickerDialog = new DatePickerDialog(getBaseActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                to = new DateTime(year, monthOfYear, dayOfMonth, 23 , 59, 59); // End of Picked Date

                endTime.setText(DateUtils.getDateString(to));
            }
        }, DateUtils.today().getYear(), DateUtils.today().getMonthOfYear(), DateUtils.today().getDayOfMonth());

        datePickerDialog.show();
    }

    @OnClick(R.id.dialog_recurrence_done)
    public void onClickDone() {
        period = NumberUtils.parseInt(periodValue.getText().toString());

        if (period == 0) {
            Toast.makeText(getBaseActivity(), getString(R.string.no_period_error), Toast.LENGTH_SHORT).show();
            return;
        }

        saveRecurrenceToDb();
        dismiss();
    }

    @OnCheckedChanged({
            R.id.dialog_recurrence_weekly_target_sunday,R.id.dialog_recurrence_weekly_target_monday,
            R.id.dialog_recurrence_weekly_target_tuesday, R.id.dialog_recurrence_weekly_target_wednesday,
            R.id.dialog_recurrence_weekly_target_thursday, R.id.dialog_recurrence_weekly_target_friday,
            R.id.dialog_recurrence_weekly_target_saturday})
    public void toggledWeeklyCheckbox(CompoundButton button, boolean ischecked) {
        PeriodTarget selectedTargetUnit = toggleIdToTargetUnitMap.get(button.getId());
        if (ischecked) {
            periodTargets.add(selectedTargetUnit);
        } else {
            periodTargets.remove(selectedTargetUnit);
        }
    }

    @OnItemSelected(R.id.dialog_recurrence_period_unit)
    public void spinnerItemSelected(Spinner spinner, int position) {
        setWeeklyTargetContainerVisibility();
        if (spinnerAdapter.getPosition(PeriodUnit.Week) != position) {
            periodTargets.clear();
        }
    }

    private void saveRecurrenceToDb() {
        DateTime toDate = ( to == null )
                ? null
                : to;

        Recurrence updatedRecurrence;
        if (periodTargets.isEmpty()) {
            updatedRecurrence = new SimpleRecurrence(currentTask.getId(), period, (PeriodUnit) periodUnit.getSelectedItem(), currentTask.getDue(), toDate);
        } else {
            updatedRecurrence = new ComplexRecurrence(currentTask.getId(), period, (PeriodUnit) periodUnit.getSelectedItem(), currentTask.getDue(), toDate, periodTargets, TargetUnit.WEEKDAY);
        }

        currentTask.setRecurrenceSchedule(updatedRecurrence);

        getBaseActivity().getRecurrenceDao().addRecurrenceRecord(updatedRecurrence);
//        getBaseActivity().getPlannerNotificationManager().scheduleNextRecurrenceIteration(currentTask); //TODO: Is this necessary
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        getBaseActivity().updateActivity();
    }
}