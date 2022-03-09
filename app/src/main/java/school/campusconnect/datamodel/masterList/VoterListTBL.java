package school.campusconnect.datamodel.masterList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "VoterListTBL")
public class VoterListTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "voterId")
    public String voterId;

    @Column(name = "serialNumber")
    public String serialNumber;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;

    @Column(name = "husbandName")
    public String husbandName;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "gender")
    public String gender;

    @Column(name = "fatherName")
    public String fatherName;

    @Column(name = "email")
    public String email;

    @Column(name = "dob")
    public String dob;

    @Column(name = "bloodGroup")
    public String bloodGroup;

    @Column(name = "age")
    public String age;

    @Column(name = "address")
    public String address;

    @Column(name = "aadharNumber")
    public String aadharNumber;

    @Column(name = "now")
    public long now;

    public static List<VoterListTBL> getAll()
    {
        return new Select().from(VoterListTBL.class).execute();
    }
    public static List<VoterListTBL> getVoterListTBLAll(String teamId)
    {
        return new Select().from(VoterListTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<VoterListTBL> deleteVoterList(String teamId)
    {
        return new Delete().from(VoterListTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<VoterListTBL> deleteAll()
    {
        return new Delete().from(VoterListTBL.class).execute();
    }
}
