package northstar.planner.presentation.swipe;


public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemComplete(int position);
    void onItemDeleted(int position);
}
