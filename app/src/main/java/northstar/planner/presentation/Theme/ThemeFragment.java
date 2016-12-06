package northstar.planner.presentation.Theme;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import northstar.planner.R;
import northstar.planner.models.Goal;
import northstar.planner.models.Theme;
import northstar.planner.models.tables.ThemeTable;
import northstar.planner.presentation.BaseFragment;
import northstar.planner.presentation.adapter.GoalRecyclerViewAdapter;
import northstar.planner.presentation.adapter.ThemeRecyclerViewAdapter;

public class ThemeFragment
        extends BaseFragment
        implements View.OnKeyListener {

    @BindView(R.id.fragment_theme_edit_title)
    EditText editTitle;

    @BindView(R.id.fragment_theme_edit_description)
    EditText editDescription;

    @BindView(R.id.fragment_theme_edit_new_goal_text)
    EditText newGoalText;

    @BindView(R.id.fragment_theme_edit_goals)
    RecyclerView goalsRecyclerView;

    private ThemeFragmentListener activityListener;
//    private GoalListAdapter goalsListAdapter;

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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initText((Theme) getArguments().get(ThemeTable.TABLE_NAME));
    }

    public void initGoalsList(List<Goal> goalsList) {
        initRecyclerView(goalsRecyclerView, goalsList, activityListener);
        setHint();
    }

    private void setHint() {
        if (goalsRecyclerView.getAdapter().getItemCount() > 0) {
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
//        goalsListAdapter.add(newGoal);
        ((GoalRecyclerViewAdapter) goalsRecyclerView.getAdapter()).addItem(newGoal);
        newGoalText.setText("");
        setHint();
    }

    public interface ThemeFragmentListener {
        void newGoal(String newGoalTitle);
        void openGoal(Goal goal);
    }
}
