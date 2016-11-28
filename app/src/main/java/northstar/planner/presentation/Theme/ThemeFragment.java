package northstar.planner.presentation.Theme;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.GoalListAdapter;

public class ThemeFragment
        extends BaseFragment
        implements View.OnKeyListener, AdapterView.OnItemClickListener {

    @BindView(R.id.fragment_theme_edit_title)
    EditText editTitle;

    @BindView(R.id.fragment_theme_edit_description)
    EditText editDescription;

    @BindView(R.id.fragment_theme_edit_new_goal_text)
    EditText newGoalText;

    @BindView(R.id.fragment_theme_edit_goals)
    ListView goals;

    private ThemeFragmentListener activityListener;
    private GoalListAdapter goalsListAdapter;

    public static northstar.planner.presentation.Theme.ThemeFragment newInstance(Theme newTheme) {
        northstar.planner.presentation.Theme.ThemeFragment newFragment = new northstar.planner.presentation.Theme.ThemeFragment();
        Bundle b = new Bundle();
        b.putSerializable(ThemeTable.TABLE_NAME, newTheme);
        newFragment.setArguments(b);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme, container, false);
        ButterKnife.bind(this, v);

        goals.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initText((Theme) getArguments().get(ThemeTable.TABLE_NAME));
    }

    public void initGoalsList(List<Goal> goalsList) {
        goalsListAdapter = new GoalListAdapter(getActivity().getApplicationContext(), goalsList);
        goals.setAdapter(goalsListAdapter);

        setHint();
    }

    private void setHint() {
        if (goalsListAdapter.getCount() > 0) {
            newGoalText.setHint(R.string.anything_else);
        }
    }

    private void initText(Theme currentTheme) {
        editTitle.setText(currentTheme.getTitle());
        editDescription.setText(currentTheme.getDescription());
        newGoalText.setOnKeyListener(this);

        if (currentTheme.isNew()) {
            toggleEditing();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityListener = (ThemeFragmentListener) activity;
    }

    public Theme getNewThemeValues() {
        return new Theme(editTitle.getText().toString(), editDescription.getText().toString());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            return startGoalCreation(v);
        }

        return false;
    }

    public boolean startGoalCreation(View v) {
        if (!newGoalText.getText().toString().isEmpty()) {
            activityListener.newGoal(newGoalText.getText().toString());
            v.requestFocus();
            return true;
        } else {
            Toast.makeText(getActivity(), getString(R.string.goal_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goal selectedGoal = goalsListAdapter.getItem(position);
        activityListener.openGoal(selectedGoal);
    }

    public void toggleEditing() {
        boolean isEditable = editTitle.getVisibility() != View.VISIBLE;
        int inputType = isEditable
                ? InputType.TYPE_CLASS_TEXT
                : InputType.TYPE_NULL;

        int visibility = isEditable
                ? View.VISIBLE
                : View.GONE;

        editTitle.setInputType(inputType);
        editTitle.setFocusable(isEditable);
        editTitle.setFocusableInTouchMode(isEditable);
        editTitle.setVisibility(visibility);

        editDescription.setInputType(inputType);
        editDescription.setFocusable(isEditable);
        editDescription.setFocusableInTouchMode(isEditable);

        if (isEditable) {
            editTitle.requestFocus();
        }
    }

    public void addedGoal(Goal newGoal) {
        goalsListAdapter.add(newGoal);
        newGoalText.setText("");
        setHint();
    }

    public interface ThemeFragmentListener {
        void newGoal(String newGoalTitle);
        void openGoal(Goal goal);
    }
}
