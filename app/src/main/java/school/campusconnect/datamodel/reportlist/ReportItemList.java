package school.campusconnect.datamodel.reportlist;

/**
 * Created by frenzin04 on 2/1/2017.
 */

public class ReportItemList {
    public int type;
    public String reasons;
    public boolean selected;

    public ReportItemList(int type, String reasons, boolean selected) {
        this.type = type;
        this.reasons = reasons;
        this.selected = selected;
    }

    public int getId() {
        return type;
    }

    public void setId(int type) {
        this.type = type;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
