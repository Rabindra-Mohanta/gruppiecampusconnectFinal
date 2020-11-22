package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "group_items")
public class GroupDataItem extends Model {

    @Column(name = "type")
    public String type;

    @Column(name = "totalUsers")
    public int totalUsers;

    @Column(name = "totalPostsCount")
    public int totalPostsCount;

    @Column(name = "totalCommentsCount")
    public int totalCommentsCount;

    @Column(name = "subCategory")
    public String subCategory;

    @Column(name = "name")
    public String name;

    @Column(name = "isPostShareAllowed")
    public boolean isPostShareAllowed;

    @Column(name = "isAdminChangeAllowed")
    public boolean isAdminChangeAllowed;

    @Column(name = "isAdmin")
    public boolean isAdmin;

    @Column(name = "image")
    public String image;

    @Column(name = "group_id")
    public String id;

    @Column(name = "groupPostUnreadCount")
    public int groupPostUnreadCount;

    @Column(name = "category")
    public String category;

    @Column(name = "canPost")
    public boolean canPost;

    @Column(name = "allowPostAll")
    public boolean allowPostAll;

    @Column(name = "adminPhone")
    public String adminPhone;

    @Column(name = "adminName")
    public String adminName;

    @Column(name = "adminId")
    public String adminId;

    @Column(name = "createdBy")
    public String createdBy;

    @Column(name = "shortDescription")
    public String shortDescription;

    @Column(name = "aboutGroup")
    public String aboutGroup;

    @Column(name = "allowPostQuestion")
    public boolean allowPostQuestion;

    public GroupDataItem() {
        super();
    }

    public static List<GroupDataItem> getAll() {
        return new Select().from(GroupDataItem.class).execute();
    }


    public static boolean isGroupAvailable(String group_id) {
        List<GroupDataItem> groupDataItems = getAll();

        for (int i = 0; i < groupDataItems.size(); i++) {
            if (group_id.equals(groupDataItems.get(i).id)) {
                return true;
            }
        }
        return false;
    }

    public static GroupDataItem getGroup(String group_id) {
        return new Select().from(GroupDataItem.class).where("group_id = ?", group_id).executeSingle();
    }

    public static void deleteGroup(String group_id) {
        new Delete().from(GroupDataItem.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteAll(){
        new Delete().from(GroupDataItem.class).execute();
    }

}
