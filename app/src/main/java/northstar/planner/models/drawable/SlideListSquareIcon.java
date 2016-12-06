package northstar.planner.models.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

public class SlideListSquareIcon {

    private static final int MAX_LEVEL = 10000;

    private Drawable background;
    private ClipDrawable icon;
    private int length;
    private int anchor;

    public SlideListSquareIcon(Drawable fg, int bg, int anchor, float length) {
        icon = new ClipDrawable(fg, anchor, ClipDrawable.HORIZONTAL);
        background = new ColorDrawable(bg);
        this.anchor = anchor;
        this.length = (int) length;
    }

    public void setBounds(View view) {
        int left, top, right, bottom;
        int margin = getMargin(view);

        top = view.getTop() + margin;
        bottom = top + length;

        if (anchor == Gravity.START) {
            left = view.getLeft() + margin;
            right = view.getLeft() + margin + length;
        } else {
            left = view.getRight() - length - margin;
            right = view.getRight() - margin;
        }

        icon.setBounds(left, top, right, bottom);
    }

    private int getMargin(View view) {
        return (view.getBottom() - view.getTop() - length) / 2;
    }

    public void draw(Canvas c, View view, float dX) {
        setBackGroundBound(view, dX);
        background.draw(c);

        icon.setLevel(getLevel(dX, view));
        icon.draw(c);
    }

    private void setBackGroundBound(View view, float dX) {
        int left, top, right, bottom;

        top = view.getTop();
        bottom = view.getBottom();

        if (anchor == Gravity.START) {
            left = view.getLeft();
            right = (int) (view.getLeft() + dX);
        } else {
            left = (int) (view.getRight() + dX);
            right = view.getRight();
        }

        background.setBounds(left, top, right, bottom);
    }

    private int getLevel(float dX, View view) {
        float slideLength = Math.abs(dX) - getMargin(view);
        return (int) (( slideLength / length) * MAX_LEVEL);
    }
}
