package northstar.planner.utils;

import org.joda.time.DateTime;

import northstar.planner.PlannerApplication;
import northstar.planner.R;


public class DateUtils {

    private static final int MINUTES_IN_HOUR = 60;

    public static DateTime today() {
        return new DateTime();
    }

    private static DateTime nextWeek() {
        DateTime nextWeek = today().plusWeeks(1);
        return nextWeek;
    }

    public static String getDateString(DateTime dateTime) {
        final int today = today().getDayOfYear();
        final int yesterday = today - 1;
        int tomorrow = today + 1;


        if (dateTime.getDayOfYear() == yesterday) {
            return PlannerApplication.getInstance().getString(R.string.yesterday);
        } else if (dateTime.getDayOfYear() == today) {
            return PlannerApplication.getInstance().getString(R.string.today);
        } else if (dateTime.getDayOfYear() == tomorrow) {
            return PlannerApplication.getInstance().getString(R.string.tomorrow);
        } else if (dateTime.getDayOfYear() > today().getDayOfYear()
                && dateTime.getMillis() <= nextWeek().getMillis()) {
            return dateTime.toString("dddd");
        } else {

            String dayOfWeek = dateTime.dayOfWeek().getAsShortText();
            String dayOfMonth = dateTime.toString("dd");
            String monthOfYear = dateTime.toString("MMM");
            String year = dateTime.toString("yyyy");

            return String.format("%s %s %s, %s", dayOfWeek, dayOfMonth, monthOfYear, year);
        }
    }

    public static long getLongTimeOfDay(int hourOfDay, int minute) {
        return (hourOfDay * MINUTES_IN_HOUR) + minute;
    }

    public static String getStringTimeOfDay(long time, boolean use24HourFormat) {
        int hours = (int) time / MINUTES_IN_HOUR;
        int minutes = (int) time % MINUTES_IN_HOUR;


        if (use24HourFormat) {
            return new DateTime().withHourOfDay(hours).withMinuteOfHour(minutes).toString("hh:mm");
        } else {
            return new DateTime().withHourOfDay(hours).withMinuteOfHour(minutes).toString("hh:mm.a");
        }
    }
}
