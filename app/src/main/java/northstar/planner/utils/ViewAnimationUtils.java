package northstar.planner.utils;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewAnimationUtils {

    private static final int DEFAULT_ANIMATION_DURATION = 350;

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(DEFAULT_ANIMATION_DURATION);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(DEFAULT_ANIMATION_DURATION);
        v.startAnimation(a);
    }

    public static void getSlideLayoutTransition(ViewGroup parent, View fromView, View toView) {
        int width = fromView.getMeasuredWidth();
        ObjectAnimator hideOld = ObjectAnimator.ofFloat(null, ViewGroup.TRANSLATION_X, 0, -width);
        ObjectAnimator showNew = ObjectAnimator.ofFloat(null, ViewGroup.TRANSLATION_X, width, 0);

        LayoutTransition trans = new LayoutTransition();
        trans.setAnimator(LayoutTransition.APPEARING, showNew);
        trans.setAnimator(LayoutTransition.DISAPPEARING, hideOld);
        trans.setStartDelay(LayoutTransition.APPEARING, 0);
        trans.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        trans.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        trans.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        trans.setDuration(350);

        parent.setLayoutTransition(trans);
        fromView.setVisibility(View.GONE);
        toView.setVisibility(View.VISIBLE);
        toView.requestFocus();
    }

    public static void getSlideDownLayoutTransition(ViewGroup view) {
        int height = view.getMeasuredHeight();
        ObjectAnimator show = ObjectAnimator.ofFloat(null, ViewGroup.TRANSLATION_Y, -height, 0);
        ObjectAnimator hide = ObjectAnimator.ofFloat(null, ViewGroup.TRANSLATION_Y, 0, -height);

        LayoutTransition trans = new LayoutTransition();
        trans.setAnimator(LayoutTransition.DISAPPEARING, hide);
        trans.setStartDelay(LayoutTransition.APPEARING, 2000);
        trans.setStartDelay(LayoutTransition.DISAPPEARING, 2000);
        trans.setStartDelay(LayoutTransition.CHANGE_APPEARING, 2000);
        trans.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 2000);
        trans.setDuration(5000);

        view.setLayoutTransition(trans);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || listView.getVisibility() != View.VISIBLE) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
