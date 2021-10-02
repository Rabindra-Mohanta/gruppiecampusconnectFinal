package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "EBookClassItem")
public class EBookClassItem extends Model {
    @Column(name = "teacherName")
    public String teacherName;

    @Column(name = "category")
    public String category;

    @Column(name = "countryCode")
    public String countryCode;

    @Column(name = "phone")
    public String phone;

    @Column(name = "className")
    public String className;

    @Column(name = "classImage")
    public String classImage;

    @Column(name = "members")
    public String members;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "teamId")
    public String teamId;

    public EBookClassItem(){
        super();
    }

    public static List<EBookClassItem> getAll(String groupId) {
        return new Select().from(EBookClassItem.class).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll(String groupId) {
        new Delete().from(EBookClassItem.class).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(EBookClassItem.class).execute();
    }
}
