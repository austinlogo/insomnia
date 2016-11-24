package northstar.planner.models;


public class DrawerItem {
    private int drawableResource;
    private String text;

    public DrawerItem(int drawableResource, String text) {
        this.drawableResource = drawableResource;
        this.text = text;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public String getText() {
        return text;
    }
}
