package northstar.planner;

import android.app.Application;

import northstar.planner.persistence.PlannerSqliteDAO;

public class PlannerApplication extends Application{

    private static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        new PlannerSqliteDAO(getApplicationContext());
    }

    public static Application getInstance() {
        return context;
    }
}
