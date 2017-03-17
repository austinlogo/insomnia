package northstar.planner.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;


public class DateTimeSetter implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    DateTimeSetterCallback callback;

    Calendar selectedDate;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    public DateTimeSetter(Context ctx, DateTimeSetterCallback cb) {
        callback = cb;

        Calendar today = Calendar.getInstance();
        selectedDate = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(
                ctx,
                this,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        timePickerDialog = new TimePickerDialog(ctx, this, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), true);
    }

    public void selectTime() {
        datePickerDialog.show();
    }

    public Date getSelectedTime() {
        return selectedDate.getTime();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = Calendar.getInstance();
        selectedDate.set(year, monthOfYear, dayOfMonth);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDate.set(Calendar.MINUTE, minute);
        selectedDate.set(Calendar.SECOND, 0);
        callback.onValuesSet(selectedDate.getTime());
//
//        switch (modifierValue) {
//            case REMINDER:
//                selectedTask.setReminder(selectedDate.getTime());
//                callback.getBaseActivity().scheduleNotification(selectedTask);
//                break;
//            case SNOOZE:
//                selectedTask.setSnooze(selectedDate.getTime());
//                break;
//            case DUE:
//                selectedTask.setDueDate(selectedDate.getTime());
//        }

//        callback.onValuesSet(selectedDate.getTime());
    }
}
