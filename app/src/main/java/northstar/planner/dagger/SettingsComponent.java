package northstar.planner.dagger;

import javax.inject.Singleton;

import dagger.Component;
import northstar.planner.presentation.settings.SettingsActivity;

@Singleton
@Component(modules = {
        AnalyticsModule.class,
        PersistenceModule.class
})
public interface SettingsComponent {
    void  inject(SettingsActivity activity);

}
