package school.campusconnect.datamodel.gruppiecontacts;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 3/20/2017.
 */

@Table(name = "gruppie_contacts")
public class GruppieContactsModel extends Model {

    @Column(name = "contact_id")
    public String contact_id;

    @Column(name = "contact_name")
    public String contact_name;

    @Column(name = "contact_phone")
    public String contact_phone;

    @Column(name = "contact_image")
    public String contact_image;


    public static List<GruppieContactsModel> getAll() {
        return new Select().from(GruppieContactsModel.class).orderBy("contact_name COLLATE NOCASE ASC").execute();
    }


    public static GruppieContactsModel getUserDetail(String  contact_id) {
        return new Select().from(GruppieContactsModel.class).where("contact_id = ?", contact_id).executeSingle();
    }

    public static List<GruppieContactsModel> getFilteredList(String input) {
        return new Select().from(GruppieContactsModel.class).where("contact_name LIKE ?", "%" + input + "%").orderBy("contact_name COLLATE NOCASE ASC").execute();
    }
    public static void deleteContact(String contact_id) {
        new Delete().from(GruppieContactsModel.class).where("contact_id = ?", contact_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(GruppieContactsModel.class).execute();
    }

}
