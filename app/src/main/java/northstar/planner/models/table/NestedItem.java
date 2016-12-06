package northstar.planner.models.table;

import java.util.ArrayList;
import java.util.List;

public class NestedItem {

    private long id;
    private String title;
    private List<NestedItem> children;
    private boolean expanded;

    public NestedItem(long id, String title) {
        this.id = id;
        this.title = title;
        expanded = false;
        children = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<NestedItem> getChildren() {
        return children;
    }

    public void setChildren(List<NestedItem> children) {
        this.children = children;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int countVisibleItems() {
        int visibleItems = 1;

        if (isExpanded()) {
            for (NestedItem item: children) {
                visibleItems += item.countVisibleItems();
            }
        }

        return visibleItems;
    }
}
