package northstar.planner.models.checkboxgroup;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.tables.ActiveHoursTable;
import northstar.planner.utils.DateUtils;

/**
 * Created by Austin on 12/23/2016.
 */

public class CheckboxGroup
        extends BaseModel
        implements TimePickerDialog.OnTimeSetListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Activity ctx;
    private CheckBox checkBox;

    private long startTime, endTime;
    private TextView start, end, activePicker;

    public CheckboxGroup(LinearLayout container, String title, Activity act) {
        ctx = act;
        checkBox = (CheckBox) container.findViewById(R.id.day_checkbox);
        start = (TextView) container.findViewById(R.id.day_start);
        end = (TextView) container.findViewById(R.id.day_end);
        checkBox.setText(title);

        checkBox.setOnCheckedChangeListener(this);
        toggleViews();
    }

    private void toggleViews() {
        boolean isEnabled = checkBox.isChecked();

        start.setOnClickListener(isEnabled
                ? this
                : null);
        end.setOnClickListener(isEnabled
                ? this
                : null);

        if (!isEnabled) {
            startTime = 0;
            endTime = 0;
        }

        updateDisplay();
    }


    public void setTime(Cursor c) {
        startTime = getColumnLong(c, ActiveHoursTable.START_COLUMN);
        endTime = getColumnLong(c, ActiveHoursTable.END_COLUMN);
        updateDisplay();
        setCheckBoxStates();
    }

    public void updateDisplay() {
        start.setText(DateUtils.getStringTime(startTime));
        end.setText(DateUtils.getStringTime(endTime));
    }

    public void setCheckBoxStates() {
        checkBox.setChecked(startTime != endTime);
    }

    @Override
    public void onClick(View v) {
        activePicker = (TextView) v;
        new TimePickerDialog(ctx, this, 0, 0, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (activePicker == null) {
            return; // API 19 Bug
        }

        if (activePicker.getId() == start.getId()) {
            startTime = DateUtils.getTimeOfDay(hourOfDay, minute);
            activePicker.setText(DateUtils.getStringTime(startTime));
        } else {
            endTime = DateUtils.getTimeOfDay(hourOfDay, minute);
            activePicker.setText(DateUtils.getStringTime(endTime));
        }

        activePicker = null;
    }


    @Override
    public String getTitle() { return "";} // TODO: this is terrible and needs a different layer of abstractions in the Model in order to be good.

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        toggleViews();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public enum CheckboxGroupIndex{
        MONDAY(Calendar.MONDAY),
        TUESDAY(Calendar.TUESDAY),
        WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY),
        FRIDAY(Calendar.FRIDAY),
        SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY),
        WEEKDAYS(24601),
        WEEKENDS(24602);

        private long value;

        CheckboxGroupIndex(long val) {
            value = val;
        }

        public long getValue() {
            return value;
        }

        public static CheckboxGroupIndex valueOf(long aLong) {
            switch ( (int) aLong) {
                case Calendar.MONDAY:
                    return MONDAY;
                case Calendar.TUESDAY:
                    return TUESDAY;
                case Calendar.WEDNESDAY:
                    return WEDNESDAY;
                case Calendar.THURSDAY:
                    return THURSDAY;
                case Calendar.FRIDAY:
                    return FRIDAY;
                case Calendar.SATURDAY:
                    return SATURDAY;
                case Calendar.SUNDAY:
                    return SUNDAY;
                case 24601:
                    return WEEKDAYS;
                case 24602:
                    return WEEKENDS;
                default:
                    return null;
            }
        }
    }
}
