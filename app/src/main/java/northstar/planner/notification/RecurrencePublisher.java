package northstar.planner.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



import org.joda.time.DateTime;

import northstar.planner.models.Recurrence;

public class RecurrencePublisher extends BroadcastReceiver {

    public static String RECURRENCE_ID = "recurrence-id";
    public static String RECURRENCE = "recurrence";

    @Override
    public void onReceive(Context context, Intent intent) {

        Recurrence rec = (Recurrence) intent.getSerializableExtra(RECURRENCE);
//        int id = (int) intent.getLongExtra(RECURRENCE_ID, 0L);

        Log.d("TEST", "Testing: " + new DateTime().getMillis() / 1000);
    }
}
