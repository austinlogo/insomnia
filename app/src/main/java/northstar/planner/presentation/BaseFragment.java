package northstar.planner.presentation;

import android.app.Fragment;

/**
 * Created by Austin on 11/24/2016.
 */

public class BaseFragment extends Fragment {

    public BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }
}
