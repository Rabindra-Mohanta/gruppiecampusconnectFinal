package school.campusconnect.datamodel.masterList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "StreetListTBL")
public class StreetListTBL extends Model {

    @Column(name = "teamId")
    public String teamId;


    @Column(name = "voterId")
    public String voterId;

    @Column(name = "userId")
    public String userId;

    @Column(name = "roleOnConstituency")
    public String roleOnConstituency;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;

    @Column(name = "gender")
    public String gender;

    @Column(name = "dob")
    public String dob;

    @Column(name = "bloodGroup")
    public String bloodGroup;

    @Column(name = "allowedToAddUser")
    public Boolean allowedToAddUser;

    @Column(name = "allowedToAddTeamPostComment")
    public Boolean allowedToAddTeamPostComment;

    @Column(name = "allowedToAddTeamPost")
    public Boolean allowedToAddTeamPost;

    @Column(name = "address")
    public String address;

    @Column(name = "aadharNumber")
    public String aadharNumber;

    @Column(name = "now")
    public long now;

    public static List<StreetListTBL> getAll()
    {
        return new Select().from(StreetListTBL.class).execute();
    }
    public static List<StreetListTBL> getStreetListAll(String teamId)
    {
        return new Select().from(StreetListTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<StreetListTBL> deleteStreetList(String teamId)
    {
        return new Select().from(StreetListTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<StreetListTBL> deleteAll()
    {
        return new Delete().from(StreetListTBL.class).execute();
    }
}
