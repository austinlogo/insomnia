package northstar.planner.presentation.hours;

import android.app.DialogFragment;

import northstar.planner.presentation.BaseActivity;

/**
 * Created by Austin on 12/23/2016.
 */
public class BaseDialogFragment extends DialogFragment {

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
