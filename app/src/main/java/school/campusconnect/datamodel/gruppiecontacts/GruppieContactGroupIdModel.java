package school.campusconnect.datamodel.gruppiecontacts;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 3/21/2017.
 */

@Table(name = "group_user_id")
public class GruppieContactGroupIdModel extends Model {

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "user_id")
    public String user_id;

    public static List<GruppieContactGroupIdModel> getAll() {
        return new Select().from(GruppieContactGroupIdModel.class).execute();
    }

    public static List<GruppieContactGroupIdModel> getRow(String group_id, String user_id) {
        return new Select().from(GruppieContactGroupIdModel.class).where("group_id = ?", group_id).where("user_id = ?", user_id).execute();
    }

    public static void deleteFriend(String user_id) {
        new Delete().from(GruppieContactGroupIdModel.class).where("user_id = ?", user_id).execute();
    }

    public static void deleteRow(String group_id, String user_id) {
        new Delete().from(GruppieContactGroupIdModel.class).where("group_id = ?", group_id).where("user_id = ?", user_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(GruppieContactGroupIdModel.class).execute();
    }

}
