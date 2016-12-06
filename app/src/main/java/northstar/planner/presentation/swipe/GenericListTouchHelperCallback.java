package northstar.planner.presentation.swipe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;

import java.util.List;

import northstar.planner.R;
import northstar.planner.models.BaseModel;
import northstar.planner.models.drawable.SlideListSquareIcon;
import northstar.planner.presentation.BaseActivity;

public class GenericListTouchHelperCallback extends ItemTouchHelper.Callback {

    private SlideListSquareIcon deleteIcon;
    private BaseActivity activity;
    private SlideListSquareIcon doneIcon;
    private final ItemTouchHelperAdapter itemTouchHelperAdapter;

    public GenericListTouchHelperCallback(ItemTouchHelperAdapter ad, BaseActivity ctx) {
        itemTouchHelperAdapter = ad;
        float length = ctx.getResources().getDimension(R.dimen.slide_list_icon_length);
        activity = ctx;

        doneIcon = new SlideListSquareIcon(ctx.getDrawable(R.drawable.ic_done_white_36dp), Color.rgb(0, 150, 0), Gravity.START, length);
        deleteIcon = new SlideListSquareIcon(ctx.getDrawable(R.drawable.ic_delete_white_36dp), Color.RED, Gravity.END, length);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END) {
            itemTouchHelperAdapter.onItemComplete(viewHolder.getAdapterPosition());
        } else {
            itemTouchHelperAdapter.onItemDeleted(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setViewAlpha(viewHolder.itemView, dX);

            doneIcon.setBounds(viewHolder.itemView);
            deleteIcon.setBounds(viewHolder.itemView);

            if (dX > 0) { //swipe right
                doneIcon.draw(c, viewHolder.itemView, dX);
            } else {
                deleteIcon.draw(c, viewHolder.itemView, dX);
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive);
        }
    }

    private boolean undoNotPresssed(int event) {
        return event != Snackbar.Callback.DISMISS_EVENT_ACTION;
    }

    private void setViewAlpha(View itemView, float dX) {
        float vanishingPoint = (float) ( 0.75 * itemView.getWidth());
        float alpha = 1.0f - Math.abs(dX) / vanishingPoint;
        itemView.setAlpha(alpha);
        itemView.setTranslationX(dX);
    }
}
