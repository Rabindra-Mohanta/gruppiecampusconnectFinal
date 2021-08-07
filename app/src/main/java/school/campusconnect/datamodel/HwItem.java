package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "HwItem")
public class HwItem extends Model {
    @Column(name = "topic")
    public String topic;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "lastSubmissionDate")
    public String lastSubmissionDate;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "description")
    public String description;

    @Column(name = "createdByName")
    public String createdByName;

    @Column(name = "postedAt")
    public String postedAt;

    @Column(name = "createdByImage")
    public String createdByImage;

    @Column(name = "createdById")
    public String createdById;

    @Column(name = "canPost")
    public boolean canPost;

    @Column(name = "assignmentId")
    public String assignmentId;

    @Column(name = "studentAssignmentId")
    public String studentAssignmentId;

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



    public HwItem(){
        super();
    }

    public static List<HwItem> getAll(String subjectId, String teamId, String groupId) {
        return new Select().from(HwItem.class).where("subjectId = ?", subjectId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(HwItem.class).execute();
    }
}
