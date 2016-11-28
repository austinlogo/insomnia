package northstar.planner.utils;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        } else if (cal.getTime().getTime() <= nextWeek().getTime().getTime()) {
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        } else {
            return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US) + " " +
                    cal.get(Calendar.DAY_OF_MONTH) + " " +
                    cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + ", " +
                    cal.get(Calendar.YEAR);
        }
    }
}
