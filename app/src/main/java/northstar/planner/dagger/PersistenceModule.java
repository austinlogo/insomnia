package northstar.planner.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import northstar.planner.persistence.PrefManager;


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
}
