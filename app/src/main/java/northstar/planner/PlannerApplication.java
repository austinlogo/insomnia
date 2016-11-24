package northstar.planner;

import android.app.Application;

import northstar.planner.persistence.PlannerSqliteDAO;

public class PlannerApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        new PlannerSqliteDAO(getApplicationContext());
    }
}
