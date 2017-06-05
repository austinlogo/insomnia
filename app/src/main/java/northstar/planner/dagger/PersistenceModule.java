package northstar.planner.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import northstar.planner.persistence.PlannerSqliteGateway;
import northstar.planner.persistence.PrefManager;
import northstar.planner.persistence.RecurrenceGateway;


@Module
public class PersistenceModule {

    Application ctx;

    public PersistenceModule(Application ctx) {
        this.ctx = ctx;
    }

    @Provides
    @Singleton
    PrefManager providePreferenceManager() {
        return new PrefManager(ctx);
    }

    @Provides
    @Singleton
    RecurrenceGateway provideRecurrenceGateway() {
        return new RecurrenceGateway(ctx);
    }

    @Provides
    @Singleton
    PlannerSqliteGateway providePlannerGateway() {
        return new PlannerSqliteGateway(ctx);
    }
}
