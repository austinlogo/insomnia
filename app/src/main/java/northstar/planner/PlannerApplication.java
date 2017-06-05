package northstar.planner;

import android.app.Application;

import northstar.planner.dagger.AnalyticsModule;
import northstar.planner.dagger.BasicComponent;
import northstar.planner.dagger.DaggerPlannerComponent;
import northstar.planner.dagger.PersistenceModule;
import northstar.planner.dagger.PlannerComponent;
import northstar.planner.persistence.PlannerDBHelper;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.persistence.RecurrenceGateway;
import northstar.planner.persistence.fresh.FreshInstallData;

//import northstar.planner.dagger.DaggerPlannerComponent;

public class PlannerApplication extends Application{

    private PlannerComponent plannerComponent;
    private static PlannerApplication context;
    private BasicComponent basicComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        new PlannerDBHelper(context);
        PlannerSqliteGateway dao = new PlannerSqliteGateway(getApplicationContext());
        new RecurrenceGateway(getApplicationContext());

        if (PlannerDBHelper.newInstall == 1) {
            FreshInstallData freshInstallData = new FreshInstallData(context);
            freshInstallData.professionalThemeConstructor(dao);
            freshInstallData.personalThemeConstructor(dao);
        }

        plannerComponent = DaggerPlannerComponent.builder()
                .analyticsModule(new AnalyticsModule(this))
                .persistenceModule(new PersistenceModule(this))
                .build();
    }

    public PlannerComponent getComponent() {
        return plannerComponent;
    }

    public static PlannerApplication getInstance() {
        return context;
    }

}
