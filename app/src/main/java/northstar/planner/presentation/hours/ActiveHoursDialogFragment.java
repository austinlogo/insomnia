package northstar.planner.presentation.hours;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.checkboxgroup.CheckboxGroup;
import northstar.planner.models.checkboxgroup.CheckboxGroup.CheckboxGroupIndex;
import northstar.planner.models.tables.ActiveHoursTable;


public class ActiveHoursDialogFragment extends BaseDialogFragment {

    @BindView(R.id.active_checkbox_container_monday)
    LinearLayout mondayContainer;

    @BindView(R.id.active_checkbox_container_tuesday)
    LinearLayout tuesdayContainer;

    @BindView(R.id.active_checkbox_container_wednesday)
    LinearLayout wednesdayContainer;

    @BindView(R.id.active_checkbox_container_thursday)
    LinearLayout thursdayContainer;

    @BindView(R.id.active_checkbox_container_friday)
    LinearLayout fridayContainer;

    @BindView(R.id.active_checkbox_container_saturday)
    LinearLayout saturdayContainer;

    @BindView(R.id.active_checkbox_container_sunday)
    LinearLayout sundayContainer;

    @BindView(R.id.active_checkbox_weekdays_container)
    LinearLayout weekdaysContainer;

    @BindView(R.id.active_checkbox_weekend_container)
    LinearLayout weekendsContainer;

    @BindView(R.id.active_weekdays_container)
    LinearLayout weekdayEnumeratedValuesContainer;

    @BindView(R.id.active_weekends_container)
    LinearLayout weekendEnumeratedValuesContainer;

    private long themeId;
    Map<CheckboxGroupIndex, CheckboxGroup> checkBoxGroups;

    public static ActiveHoursDialogFragment newInstance(long themeId) {
        Bundle b = new Bundle();
        b.putLong(ActiveHoursTable.THEME_COLUMN, themeId);

        ActiveHoursDialogFragment dialog =  new ActiveHoursDialogFragment();
        dialog.setArguments(b);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeId = (long) getArguments().get(ActiveHoursTable.THEME_COLUMN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.dialog_active_hours, container, false);
        ButterKnife.bind(this, v);

        checkBoxGroups = new HashMap<>();
        checkBoxGroups.put(CheckboxGroupIndex.MONDAY, new CheckboxGroup(mondayContainer, getString(R.string.monday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.TUESDAY, new CheckboxGroup(tuesdayContainer, getString(R.string.tuesday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.WEDNESDAY, new CheckboxGroup(wednesdayContainer, getString(R.string.wednesday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.THURSDAY, new CheckboxGroup(thursdayContainer, getString(R.string.thursday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.FRIDAY, new CheckboxGroup(fridayContainer, getString(R.string.friday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.SATURDAY, new CheckboxGroup(saturdayContainer, getString(R.string.saturday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.SUNDAY, new CheckboxGroup(sundayContainer, getString(R.string.sunday), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.WEEKDAYS, new CheckboxGroup(weekdaysContainer, getString(R.string.weekdays), getActivity()));
        checkBoxGroups.put(CheckboxGroupIndex.WEEKENDS, new CheckboxGroup(weekendsContainer, getString(R.string.weekends), getActivity()));

        checkBoxGroups = getBaseActivity().getDao().getActiveHours(themeId, checkBoxGroups);

        return v;
    }

    public void setContainerVisibility() {
        CheckboxGroup weekdays = checkBoxGroups.get(CheckboxGroupIndex.WEEKDAYS);
        CheckboxGroup weekends = checkBoxGroups.get(CheckboxGroupIndex.WEEKENDS);


        if (weekdays.isChecked()) {
            weekdayEnumeratedValuesContainer.setVisibility(View.GONE);
        } else {
            weekdayEnumeratedValuesContainer.setVisibility(View.VISIBLE);
        }

        if (weekends.isChecked()) {
            weekendEnumeratedValuesContainer.setVisibility(View.GONE);
        } else {
            weekendEnumeratedValuesContainer.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.active_done)
    public void onDoneClicked() {
        getBaseActivity().getDao().updateActiveHours(themeId, checkBoxGroups);
        dismiss();
    }
}

