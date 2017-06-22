package northstar.planner.dagger;

import android.app.DialogFragment;

import javax.inject.Singleton;

import dagger.Component;
import northstar.planner.models.checkboxgroup.CheckboxGroup;
import northstar.planner.notification.NotificationPublisher;
import northstar.planner.notification.PlannerNotificationManager;
import northstar.planner.presentation.BaseActivity;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.settings.SettingsActivity;
import northstar.planner.presentation.settings.SettingsFragment;
import northstar.planner.utils.DateTimeSetter;

@Singleton
@Component(modules = {
        AnalyticsModule.class,
        PersistenceModule.class
})
public interface PlannerComponent {
    void inject(BaseActivity activity);
    void inject(SettingsActivity activity);
    void inject(SettingsFragment activity);
    void inject(BaseFragment fragment);
    void inject(DialogFragment fragment);
    void inject(PlannerNotificationManager manager);
    void inject(DateTimeSetter setter);
    void inject(CheckboxGroup cbg);
    void inject(NotificationPublisher np);
//    void inject(Object obj);
}
