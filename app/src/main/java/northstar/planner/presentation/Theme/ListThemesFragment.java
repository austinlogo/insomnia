package northstar.planner.presentation.Theme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Theme;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.adapter.ThemeListAdapter;
import northstar.planner.presentation.swipe.ThemeListTouchHelperCallback;
import northstar.planner.presentation.swipe.ThemeRecyclerViewAdapter;

public class ListThemesFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private ThemeListAdapter themeListAdapter;
    private PlannerSqliteDAO dao;
    private Vibrator vibrator;
    private ListThemesFragmentListener activityListener;

    @BindView(R.id.activity_theme_list)
    RecyclerView themeList;

    public static ListThemesFragment newInstance() {
        return new ListThemesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme_view, container, false);
        ButterKnife.bind(this, v);

        dao = new PlannerSqliteDAO();

        vibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        return v;
    }

    public void initRecyleView(List<Theme> ts) {
        themeList.setHasFixedSize(true);
        themeList.setLayoutManager(new LinearLayoutManager(getActivity()));


        ThemeRecyclerViewAdapter adapter = new ThemeRecyclerViewAdapter(ts, activityListener);
        themeList.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ThemeListTouchHelperCallback(adapter, getActivity());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(themeList);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

//        themeListAdapter = new ThemeListAdapter(
//                getActivity().getApplicationContext(),
//                android.R.layout.simple_list_item_1,
//                dao.getAllThemes());

//        themeList.setAdapter();
//        themeList.setOnItemClickListener(this);
        initRecyleView(dao.getAllThemes());


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        activityListener = (ListThemesFragmentListener) activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Theme currentTheme = themeListAdapter.getItem(position);

        activityListener.startThemeEdit(currentTheme);
    }

    @OnClick(R.id.fragment_theme_fab)
    public void createNewTheme() {
        activityListener.startThemeEdit(null);
    }


    public interface ListThemesFragmentListener {
        void startThemeEdit(Theme theme);
    }

}
