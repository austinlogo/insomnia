package northstar.planner.presentation.hours;

import android.app.DialogFragment;

import northstar.planner.presentation.BaseActivity;

public class BaseDialogFragment extends DialogFragment {

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
