package northstar.planner.presentation.rate;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.persistence.PrefManager;

public class RateDialogFragment extends DialogFragment {

    PrefManager prefManager;

    public static RateDialogFragment nweInstance() {
        return new RateDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View v = inflater.inflate(R.layout.dialog_rate, container, false);
        ButterKnife.bind(this, v);

        prefManager = new PrefManager(getActivity());

        return v;
    }

    @OnClick(R.id.dialog_rate_cancel)
    public void onClickCancel() {
        dismiss();
    }

    @OnClick(R.id.dialog_rate_yes)
    public void onClickRating() {
        prefManager.hasRated(true);
        launchMarket();
    }


    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


}
