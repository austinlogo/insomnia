package northstar.planner;

import android.app.Application;

import northstar.planner.persistence.PlannerDBHelper;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.persistence.fresh.FreshInstallData;

public class PlannerApplication extends Application{

    private static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        PlannerSqliteGateway dao = new PlannerSqliteGateway(getApplicationContext());

        if (PlannerDBHelper.newInstall == 1) {
            FreshInstallData freshInstallData = new FreshInstallData(context);
            freshInstallData.professionalThemeConstructor(dao);
            freshInstallData.personalThemeConstructor(dao);
        }
    }

    public static Application getInstance() {
        return context;
    }
}
