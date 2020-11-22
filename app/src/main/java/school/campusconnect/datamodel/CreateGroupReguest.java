package school.campusconnect.datamodel;



public class CreateGroupReguest {


    public String name;
    public String shortDescription;
    public String aboutGroup;
    public String avatar;
    public String type;
    public String category;
    public String subCategory;

    @Override
    public String toString() {
        return "CreateGroupReguest{" +
                "name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", aboutGroup='" + aboutGroup + '\'' +
                ", imageFile='" + avatar + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
