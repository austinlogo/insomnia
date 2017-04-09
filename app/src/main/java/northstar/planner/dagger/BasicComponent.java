package northstar.planner.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AnalyticsModule.class,
        PersistenceModule.class
})
public interface BasicComponent {
    void  inject(Application activity);
}
