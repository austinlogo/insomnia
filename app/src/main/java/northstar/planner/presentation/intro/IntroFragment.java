package northstar.planner.presentation.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.presentation.BaseFragment;

public class IntroFragment
        extends BaseFragment {

    public static IntroFragment newInstance() {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_focus, container, false);
        ButterKnife.bind(this, v);

        return v;
    }
}
