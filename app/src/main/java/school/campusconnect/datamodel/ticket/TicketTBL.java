package school.campusconnect.datamodel.ticket;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.StudTestPaperItem;

@Table(name="TicketTBL")
public class TicketTBL extends Model {

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "role")
    public String role;

    @Column(name = "option")
    public String option;

    @Column(name = "page")
    public String page;

    @Column(name = "issueText")
    public String issueText;

    @Column(name = "issuePostId")
    public String issuePostId;

    @Column(name = "issuePartyTaskForceStatus")
    public String issuePartyTaskForceStatus;

    @Column(name = "issueLocation")
    public String issueLocation;

    @Column(name = "issueDepartmentTaskForceStatus")
    public String issueDepartmentTaskForceStatus;

    @Column(name = "issueCreatedByPhone")
    public String issueCreatedByPhone;

    @Column(name = "issueCreatedByName")
    public String issueCreatedByName;

    @Column(name = "issueCreatedByImage")
    public String issueCreatedByImage;

    @Column(name = "issueCreatedById")
    public String issueCreatedById;

    @Column(name = "issueCreatedAt")
    public String issueCreatedAt;

    @Column(name = "fileType")
    public String fileType;

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "constituencyIssuePartyTaskForce")
    public String constituencyIssuePartyTaskForce;

    @Column(name = "constituencyIssueJurisdiction")
    public String constituencyIssueJurisdiction;

    @Column(name = "constituencyIssueDepartmentTaskForce")
    public String constituencyIssueDepartmentTaskForce;

    @Column(name = "constituencyIssue")
    public String constituencyIssue;

    @Column(name = "boothIncharge")
    public String boothIncharge;

    @Column(name = "adminStatus")
    public String adminStatus;






    public TicketTBL()
    {
        super();
    }

    public static List<TicketTBL> getAll(String groupId,String Role,String Option,String page) {
        return new Select().from(TicketTBL.class).where("groupId = ?", groupId).where("role = ?", Role).where("option = ?", Option).where("page = ?", page).execute();
    }

    public static void deleteAll(String groupId,String Role,String Option,String page) {
        new Delete().from(TicketTBL.class).where("groupId = ?", groupId).where("role = ?", Role).where("option = ?", Option).where("page = ?", page).execute();
    }
}
