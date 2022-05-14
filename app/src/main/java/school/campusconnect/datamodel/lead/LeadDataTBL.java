package school.campusconnect.datamodel.lead;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "LeadDataTBL")
public class LeadDataTBL extends Model {

    @Column(name = "voterId")
    public String voterId;

    @Column(name = "roleOnConstituency")
    public boolean roleOnConstituency;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;

    @Column(name = "user_id")
    public String id;

    @Column(name = "gender")
    public String gender;

    @Column(name = "dob")
    public String dob;

    @Column(name = "bloodGroup")
    public String bloodGroup;

    @Column(name = "allowedToAddUser")
    public boolean allowedToAddUser;

    @Column(name = "allowedToAddTeamPostComment")
    public boolean allowedToAddTeamPostComment;

    @Column(name = "allowedToAddTeamPost")
    public boolean allowedToAddTeamPost;

    @Column(name = "aadharNumber")
    public String aadharNumber;

    @Column(name = "groupID")
    public String groupID;

    @Column(name = "teamID")
    public String teamID;

    @Column(name = "page")
    public int page;

    @Column(name = "_now")
    public String _now;

    @Column(name = "isLive")
    public boolean isLive;


    public static List<LeadDataTBL> getLead(String groupID,String teamID,int page)
    {
        return new Select().from(LeadDataTBL.class).where("groupID = ?",groupID).where("teamID = ?",teamID).where("page = ?",page).execute();
    }

    public static List<LeadDataTBL> getLeadData(String groupID,String teamID)
    {
        return new Select().from(LeadDataTBL.class).where("groupID = ?",groupID).where("teamID = ?",teamID).execute();
    }

    public static List<LeadDataTBL> deleteLead(String groupID,String teamID,int page){
        return new Delete().from(LeadDataTBL.class).where("groupID = ?",groupID).where("teamID = ?",teamID).where("page = ?",page).execute();
    }
    public static List<LeadDataTBL> deleteLead(String groupID,String teamID){
        return new Delete().from(LeadDataTBL.class).where("groupID = ?",groupID).where("teamID = ?",teamID).execute();
    }

    public static List<LeadDataTBL> deleteAll(){
        return new Delete().from(LeadDataTBL.class).execute();
    }
}
