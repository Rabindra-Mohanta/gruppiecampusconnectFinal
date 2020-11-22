package school.campusconnect.datamodel;

/**
 * Created by Frenzin4 on 9/14/2015.
 */
public class Item_NavDrawer {

    private boolean showNotify;
    private String title;
    private int icon;

    public Item_NavDrawer() {

    }

    public Item_NavDrawer(boolean showNotify, String title , int icon) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon = icon;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
