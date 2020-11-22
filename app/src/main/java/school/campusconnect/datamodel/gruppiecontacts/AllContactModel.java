package school.campusconnect.datamodel.gruppiecontacts;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 3/23/2017.
 */

@Table(name = "all_contacts")
public class AllContactModel extends Model {


    @Column(name = "group_id")
    public String group_id;

    @Column(name = "all_id")
    public String all_id;

    @Column(name = "all_name")
    public String all_name;

    @Column(name = "all_phone")
    public String all_phone;

    @Column(name = "all_email")
    public String all_email;

    @Column(name = "all_leadCount")
    public int all_leadCount;

    @Column(name = "all_dob")
    public String all_dob;

    @Column(name = "all_qualification")
    public String all_qualification;

    @Column(name = "all_occupation")
    public String all_occupation;

    @Column(name = "all_image")
    public String all_image;

    @Column(name = "all_otherLeads")
    public String all_otherLeads;

    @Column(name = "all_groups")
    public String all_groups;

    @Column(name = "all_group_ids")
    public String all_group_ids;

    @Column(name = "all_gender")
    public String all_gender;
    //public boolean isAllowedToPost;

    @Column(name = "line1")
    public String line1;

    @Column(name = "line2")
    public String line2;

    @Column(name = "district")
    public String district;

    @Column(name = "state")
    public String state;

    @Column(name = "countryCode")
    public String country;

    @Column(name = "pin")
    public String pin;

    @Column(name = "is_post")
    public boolean is_post;

    public static List<AllContactModel> getAll(String group_id) {
        return new Select().from(AllContactModel.class).where("group_id = ?", group_id).orderBy("all_name COLLATE NOCASE ASC").execute();
    }

    public static List<AllContactModel> getFilteredList(String group_id, String input) {
        return new Select().from(AllContactModel.class).where("group_id = ?", group_id).where("all_name LIKE ?", "%" + input + "%").orderBy("all_name COLLATE NOCASE ASC").execute();
    }

    public static List<AllContactModel> getByGroup(String all_id) {
        return new Select().from(AllContactModel.class).where("all_id = ?", all_id).execute();
    }

    public static void deleteContact(String group_id, String all_id) {
        new Delete().from(AllContactModel.class).where("group_id = ?", group_id).where("all_id = ?", all_id).execute();
    }
    public static void deleteAll() {
        new Delete().from(AllContactModel.class).execute();
    }

}
