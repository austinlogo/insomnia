package northstar.planner.presentation.Theme;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.Theme;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.ThemeRecyclerViewAdapter;

public class ListThemesFragment extends BaseFragment {

    private ThemeRecyclerViewAdapter adapter;
    private ListThemesFragmentListener activityListener;

    @BindView(R.id.activity_theme_list)
    RecyclerView themeList;

    public static ListThemesFragment newInstance() {
        return new ListThemesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme_view, container, false);
        ButterKnife.bind(this, v);
        adapter = new ThemeRecyclerViewAdapter(new ArrayList<Theme>(), activityListener);
        initRecyclerView(themeList, adapter);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        getBaseActivity().updateThemes(adapter.getList());
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateList(getBaseActivity().getThemes());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (ListThemesFragmentListener) activity;
    }

    @OnClick(R.id.fragment_theme_fab)
    public void createNewTheme() {
        activityListener.startThemeEdit(null);
    }

    protected void removeItemWorkflow(final Theme item, final int position) {

        Snackbar.make(getBaseActivity().getRootView(), R.string.deletedItem, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (undoPressed(event)) {
                            adapter.undoDeletion(item, position);
                        } else {
                            getBaseActivity().removeFromDb(item);
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();
    }

    public interface ListThemesFragmentListener {
        void startThemeEdit(Theme theme);
        void removeItem(Theme item, int position);
    }
}
