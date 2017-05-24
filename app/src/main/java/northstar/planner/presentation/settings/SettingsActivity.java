package northstar.planner.presentation.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.metrics.AnalyticsEventAttribute;
import northstar.planner.metrics.AnalyticsEventType;
import northstar.planner.metrics.MetricsLogger;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.activity_settings_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    MetricsLogger metricsLogger;

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        settingsFragment = SettingsFragment.newInstance();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_settings_edit_fragment, settingsFragment)
                .commit();

        ((PlannerApplication) getApplication()).getComponent().inject(this);
        metricsLogger.recordEvent(AnalyticsEventType.STARTED, AnalyticsEventAttribute.VISITED_PAGE, this.getClass().getSimpleName());

    }
}
