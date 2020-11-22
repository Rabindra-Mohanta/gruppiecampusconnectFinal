package school.campusconnect.datamodel.publicgroup;

/**
 * Created by frenzin04 on 11/17/2017.
 */

public class PublicGroupItem {
    public String id;
    public String name;
    public String image;
    public String members;
    public String createdBy;
    public String aboutGroup;
    public int totalPostCount;
    public int totalCommentsCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
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


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAboutGroup() {
        return aboutGroup;
    }

    public void setAboutGroup(String aboutGroup) {
        this.aboutGroup = aboutGroup;
    }

    public int getTotalPostCount() {
        return totalPostCount;
    }

    public void setTotalPostCount(int totalPostCount) {
        this.totalPostCount = totalPostCount;
    }

    public int getTotalCommentsCount() {
        return totalCommentsCount;
    }

    public void setTotalCommentsCount(int totalCommentsCount) {
        this.totalCommentsCount = totalCommentsCount;
    }
}
