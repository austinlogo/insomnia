package northstar.planner.presentation.Theme;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.persistence.PlannerSqliteDAO;
import northstar.planner.presentation.adapter.GoalListAdapter;

public class ThemeEditFragment
        extends Fragment
        implements View.OnKeyListener, AdapterView.OnItemClickListener {

    @BindView(R.id.fragment_theme_edit_title)
    EditText editTitle;

    @BindView(R.id.fragment_theme_edit_description)
    EditText editDescription;

    @BindView(R.id.fragment_theme_edit_edit)
    ImageButton editButton;

    @BindView(R.id.fragment_theme_edit_new_goal_text)
    EditText newGoalText;

    @BindView(R.id.fragment_theme_edit_goals)
    ListView goals;

    private ThemeEditFragmentListener activityListener;
    private Theme currentTheme;
    private PlannerSqliteDAO dao;
    private GoalListAdapter goalsListAdapter;

    public static ThemeEditFragment newInstance(Bundle b) {
        ThemeEditFragment newFragment = new ThemeEditFragment();
        newFragment.setArguments(b);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dao = new PlannerSqliteDAO();
        currentTheme = new Theme(getArguments());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme_edit, container, false);
        ButterKnife.bind(this, v);

        initText();
        setEditable(currentTheme.isNew());
        initGoalsList(currentTheme.getGoals());

        goals.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Goal> goals = dao.getGoalsByThemeId(currentTheme.getId());
        initGoalsList(goals);

    }

    private void initGoalsList(List<Goal> goalsList) {
        goalsListAdapter = new GoalListAdapter(getActivity().getApplicationContext(), goalsList);
        goals.setAdapter(goalsListAdapter);
    }

    private void initText() {
        editTitle.setText(currentTheme.getTitle());
        editDescription.setText(currentTheme.getDescription());
        newGoalText.setOnKeyListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        activityListener = (ThemeEditFragmentListener) activity;
    }

    @Override
    public void onPause() {
        super.onPause();

        currentTheme.updateTheme(editTitle.getText().toString(), editDescription.getText().toString());
        new PlannerSqliteDAO().updateTheme(currentTheme);
    }

    @OnClick(R.id.fragment_theme_edit_edit)
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_theme_edit_edit:
                setEditable(!isEditing());
                break;
        }
    }

    public void newGoal(String newGoalTitle) {
        Goal newGoal = new Goal(currentTheme.getId(), newGoalTitle);
        long id = dao.addGoal(newGoal);
        newGoal.setId(id);

        goalsListAdapter.add(newGoal);
        newGoalText.setText("");

    }

    private void setEditable(boolean isEditable) {

        if (isEditable) {
            editButton.setImageResource(R.drawable.ic_done_black_36dp);

            editTitle.setInputType(InputType.TYPE_CLASS_TEXT);
            editDescription.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editButton.setImageResource(R.drawable.ic_mode_edit_black_36dp);

            editTitle.setKeyListener(null);
            editDescription.setKeyListener(null);

        }
    }

    private boolean isEditing() {
        return editTitle.getKeyListener() != null;
    }

    @OnClick(R.id.fragment_theme_edit_add)
    public void onAddButtonClick(View v) {

        newGoal(newGoalText.getText().toString());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            newGoal(newGoalText.getText().toString());
            return true;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goal selectedGoal = goalsListAdapter.getItem(position);
        activityListener.openGoal(selectedGoal);
    }

    public interface ThemeEditFragmentListener {
        void openGoal(Goal goal);
    }
}
