package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "ClassListTBL")
public class ClassListTBL extends Model {
    @Column(name = "teamId")
    public String teamId;

    @Column(name = "teacherName")
    public String teacherName;

    @Column(name = "phone")
    public String phone;

    @Column(name = "countryCode")
    public String countryCode;

    @Column(name = "name")
    public String name;

    @Column(name = "members")
    public String members;

    @Column(name = "image")
    public String image;

    @Column(name = "category")
    public String category;

    @Column(name = "jitsiToken")
    public String jitsiToken;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "userId")
    public String userId;

    @Column(name = "rollNumber")
    public String rollNumber;

    public ClassListTBL() {
        super();
    }

    public static List<ClassListTBL> getAll(String groupId) {
        return new Select().from(ClassListTBL.class).where("groupId = ?", groupId).execute();
    }

    public static void deleteAll(String groupId) {
        new Delete().from(ClassListTBL.class).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(ClassListTBL.class).execute();
    }
}
