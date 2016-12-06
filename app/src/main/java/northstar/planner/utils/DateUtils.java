package northstar.planner.utils;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import northstar.planner.PlannerApplication;
import northstar.planner.R;

public class DateUtils {

    private static final int ONE_WEEK = 7;

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public static Calendar today() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal;
    }

    private static Calendar nextWeek() {
        Calendar nextWeek = today();
        nextWeek.add(Calendar.DAY_OF_YEAR, ONE_WEEK);
        return nextWeek;
    }

    public static String getDateString(String todayString, Calendar cal) {

        if (cal.get(Calendar.DAY_OF_YEAR) == today().get(Calendar.DAY_OF_YEAR)) {
            return todayString;
        } else if (cal.get(Calendar.DAY_OF_YEAR) - today().get(Calendar.DAY_OF_YEAR) == 1) {
            return PlannerApplication.getInstance().getString(R.string.tomorrow);
        } else if (cal.get(Calendar.DAY_OF_YEAR) > today().get(Calendar.DAY_OF_YEAR)
                && cal.getTime().getTime() <= nextWeek().getTime().getTime()) {
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        } else {
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US) + " " +
                    cal.get(Calendar.DAY_OF_MONTH) + " " +
                    cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + ", " +
                    cal.get(Calendar.YEAR);
        }
    }

    public static Calendar getStartOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

    public static Calendar getEndOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.MILLISECOND, 59);

        return c;
    }
}
