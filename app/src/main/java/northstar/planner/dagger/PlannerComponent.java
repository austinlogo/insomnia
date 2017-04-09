package northstar.planner.dagger;

import javax.inject.Singleton;

import dagger.Component;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.settings.SettingsFragment;

@Singleton
@Component(modules = {
        AnalyticsModule.class,
        PersistenceModule.class
})
public interface PlannerComponent {
    void inject(BaseActivity activity);
    void inject(SettingsFragment activity);
    void inject(BaseFragment fragment);
}
