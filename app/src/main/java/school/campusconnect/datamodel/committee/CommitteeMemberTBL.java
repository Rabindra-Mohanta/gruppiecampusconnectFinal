package school.campusconnect.datamodel.committee;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Table(name = "CommitteeMemberTBL")
public class CommitteeMemberTBL extends Model {


    @Column(name = "groupId")
    public String groupId;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "committeeId")
    public String committeeId;

    @Column(name = "phone")
    public String phone;

    @Column(name = "countryCode")
    public String countryCode;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;
    @Column(name = "user_id")
    public String id;

    @Column(name = "voterId")
    public String voterId;

    @Column(name = "gender")
    public String gender;

    @Column(name = "dob")
    public String dob;

    @Column(name = "bloodGroup")
    public String bloodGroup;

    @Column(name = "aadharNumber")
    public String aadharNumber;

    @Column(name = "allowedToAddUser")
    public boolean allowedToAddUser;

    @Column(name = "allowedToAddTeamPostComment")
    public boolean allowedToAddTeamPostComment;

    @Column(name = "allowedToAddTeamPost")
    public boolean allowedToAddTeamPost;

    @Column(name = "roleOnConstituency")
    public String roleOnConstituency;

    @Column(name = "salary")
    public String salary;


    public static List<CommitteeMemberTBL> getCommitteeMember(String groupId,String teamId,String committeeId)
    {
        return new Select().from(CommitteeMemberTBL.class).where("groupId = ?",groupId).where("teamId = ?",teamId).where("committeeId = ?",committeeId).execute();
    }

    public static List<CommitteeMemberTBL> deleteCommitteeMember(String groupId,String teamId,String committeeId)
    {
        return new Delete().from(CommitteeMemberTBL.class).where("groupId = ?",groupId).where("teamId = ?",teamId).where("committeeId = ?",committeeId).execute();
    }

    public static List<CommitteeMemberTBL> deleteAllMember()
    {
        return new Delete().from(CommitteeMemberTBL.class).execute();
    }
}
