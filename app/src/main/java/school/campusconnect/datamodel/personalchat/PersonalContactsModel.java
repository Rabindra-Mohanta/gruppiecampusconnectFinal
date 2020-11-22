package school.campusconnect.datamodel.personalchat;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 2/28/2017.
 */

@Table(name = "personal_contacts")
public class PersonalContactsModel extends Model {


    @Column(name = "group_id")
    public String group_id;

    @Column(name = "friend_id")
    public String friend_id;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;

    @Column(name = "phone")
    public String phone;

    @Column(name = "updatedTime")
    public String updatedTime;

    @Column(name = "provideSettings")
    public boolean provideSettings;

    @Column(name = "allowToPost")
    public boolean allowToPost;

    @Column(name = "allowPostComment")
    public boolean allowPostComment;

    public static List<PersonalContactsModel> getAll(String group_id) {
        return new Select().from(PersonalContactsModel.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(PersonalContactsModel.class).execute();
    }
}
