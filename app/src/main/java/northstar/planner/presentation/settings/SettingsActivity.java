package northstar.planner.presentation.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.activity_settings_edit_drawer_layout)
    DrawerLayout mDrawerLayout;

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
    }
}
