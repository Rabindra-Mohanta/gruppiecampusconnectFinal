package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "StudAssignementItem")
public class StudAssignementItem extends Model {
    @Column(name = "submittedById")
    public String submittedById;

    @Column(name = "studentName")
    public String studentName;

    @Column(name = "studentImage")
    public String studentImage;

    @Column(name = "studentAssignmentId")
    public String studentAssignmentId;

    @Column(name = "insertedAt")
    public String insertedAt;

    @Column(name = "description")
    public String description;

    @Column(name = "verifiedComment")
    public String verifiedComment;

    @Column(name = "reassignComment")
    public String reassignComment;

    @Column(name = "reassignedAt")
    public String reassignedAt;

    @Column(name = "assignmentVerified")
    public boolean assignmentVerified;

    @Column(name = "assignmentReassigned")
    public boolean assignmentReassigned;

    @Column(name = "fileType")
    public String fileType;

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "thumbnailImage")
    public String thumbnailImage;

    @Column(name = "thumbnail")
    public String thumbnail;

    @Column(name = "video")
    public String video;

    @Column(name = "userId")
    public String userId;

    @Column(name = "studentDbId")
    public String studentDbId;

    @Column(name = "rollNumber")
    public String rollNumber;

    @Column(name = "AssignId")
    public String AssignId;
    @Column(name = "teamId")
    public String teamId;
    @Column(name = "groupId")
    public String groupId;


    public StudAssignementItem(){
        super();
    }

    public static List<StudAssignementItem> getAll(String AssignId,String teamId, String groupId) {
        return new Select().from(StudAssignementItem.class).where("AssignId = ?", AssignId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(StudAssignementItem.class).execute();
    }
}
