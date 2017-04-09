package northstar.planner.presentation.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import northstar.planner.PlannerApplication;
import northstar.planner.R;
import northstar.planner.persistence.PrefManager;
import northstar.planner.presentation.intro.IntroActivity;


public class SettingsFragment extends Fragment {

    @BindView(R.id.fragment_settings_notify_due_switch)
    Switch notifyOnDue;

    @BindView(R.id.fragment_settings_notify_snooze_switch)
    Switch notifyAfterSnooze;

    @Inject
    PrefManager prefs;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
        ((PlannerApplication) getActivity().getApplication()).getComponent().inject(this);

        notifyOnDue.setChecked(prefs.remindWhenDue());
        notifyAfterSnooze.setChecked(prefs.remindAfterSnooze());

        return v;
    }



    @OnCheckedChanged({R.id.fragment_settings_notify_due_switch, R.id.fragment_settings_notify_snooze_switch})
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.fragment_settings_notify_due_switch:
                prefs.remindWhenDue(checked);
                break;
            case R.id.fragment_settings_notify_snooze_switch:
                prefs.remindAfterSnooze(checked);
        }
    }

    @OnClick(R.id.fragment_settings_getting_started_container)
    public void onClick() {
        Intent i = new Intent(getActivity(), IntroActivity.class);
        startActivity(i);
    }
}
