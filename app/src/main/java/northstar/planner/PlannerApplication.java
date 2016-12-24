package northstar.planner;

import android.app.Application;

import northstar.planner.persistence.PlannerSqliteGateway;

public class PlannerApplication extends Application{

    private static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        new PlannerSqliteGateway(getApplicationContext());
    }

    public static Application getInstance() {
        return context;
    }
}
