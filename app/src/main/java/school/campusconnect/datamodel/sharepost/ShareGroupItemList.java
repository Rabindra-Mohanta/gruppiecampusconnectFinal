package school.campusconnect.datamodel.sharepost;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class ShareGroupItemList {

    private String id;
    private String name;
    private String image;
    private String phone;
    private boolean selected;

    public ShareGroupItemList(String id, String name, String image, String phone, boolean selected) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.phone = phone;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
