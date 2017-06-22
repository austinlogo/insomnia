package northstar.planner.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.joda.time.DateTime;




public class DateTimeSetter implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private DateTimeSetterCallback callback;
    private DateTime selectedDate;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private boolean use24Hour;

    public DateTimeSetter(Activity context, DateTimeSetterCallback cb) {
        callback = cb;

//        ((PlannerApplication) context.getApplication()).getComponent().inject(this);

        use24Hour = DateFormat.is24HourFormat(context);

        DateTime today = new DateTime();
        selectedDate = new DateTime();

        datePickerDialog = new DatePickerDialog(
                context,
                this,
                today.getYear(),
                today.getMonthOfYear() - 1,
                today.getDayOfMonth());

        datePickerDialog.getDatePicker().setMinDate(today.getMillis());
        timePickerDialog = new TimePickerDialog(context, this, today.getHourOfDay(), today.getMinuteOfHour(), use24Hour);
    }

    public void selectTime() {
        datePickerDialog.show();
    }

    public DateTime getSelectedTime() {
        return selectedDate;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new DateTime(
                year,
                monthOfYear + 1, // offset for joda
                dayOfMonth,
                new DateTime().getHourOfDay(),
                new DateTime().getMinuteOfHour());
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDate = new DateTime(
                selectedDate.getYear(),
                selectedDate.getMonthOfYear(),
                selectedDate.getDayOfMonth(),
                hourOfDay,
                minute,
                0);
        callback.onValuesSet(selectedDate);
    }
}
