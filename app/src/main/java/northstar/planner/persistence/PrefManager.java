package northstar.planner.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    private static final String LAST_TIME_RATED = "last_time_rated";
    private static final String HAS_RATED = "HAS_RATED";
    private static final String SNOOZE_REMINDER  = "SNOOZE_REMINDER";
    private static final String DUE_REMINDER = "DUE_REMINDER";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    private final long timeBetweenRating = 1000 * 60 * 60 * 24 * 5; // 5 days

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setLastRatedAsked() {
        editor.putLong(LAST_TIME_RATED, new Date().getTime()).commit();
    }

    public boolean isTimeToRate() {
        long lastRateTime = getLastRatedAsked();
        long now = new Date().getTime();
        long timeElapsed = now - lastRateTime;
        return timeElapsed > timeBetweenRating;
    }

    private long getLastRatedAsked() {
        return pref.getLong(LAST_TIME_RATED, 0L);
    }

    public void hasRated(boolean bool) {
        editor.putBoolean(HAS_RATED, bool).commit();
    }

    public boolean hasRated() {
        return pref.getBoolean(HAS_RATED, false);
    }

    public void remindAfterSnooze(boolean remindAfterSnooze) {
        editor.putBoolean(SNOOZE_REMINDER, remindAfterSnooze).commit();
    }

    public boolean remindAfterSnooze() {
        return pref.getBoolean(SNOOZE_REMINDER, false);
    }

    public void remindWhenDue(boolean remindWhenDue) {
        editor.putBoolean(DUE_REMINDER, remindWhenDue).commit();
    }

    public boolean remindWhenDue() {
        return pref.getBoolean(DUE_REMINDER, false);
    }
}
