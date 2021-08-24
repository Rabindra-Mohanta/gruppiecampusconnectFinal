package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "StudTestPaperItem")
public class StudTestPaperItem extends Model {
    @Column(name = "submittedById")
    public String submittedById;

    @Column(name = "testexamVerified")
    public boolean testexamVerified;

    @Column(name = "verifiedComment")
    public String verifiedComment;

    @Column(name = "studentTestExamId")
    public String studentTestExamId;

    @Column(name = "studentName")
    public String studentName;

    @Column(name = "studentImage")
    public String studentImage;

    @Column(name = "insertedAt")
    public String insertedAt;
    @Column(name = "description")
    public String description;

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

    @Column(name = "TestId")
    public String TestId;
    @Column(name = "teamId")
    public String teamId;
    @Column(name = "groupId")
    public String groupId;


    public StudTestPaperItem() {
        super();
    }

    public static List<StudTestPaperItem> getAll(String TestId, String teamId, String groupId) {
        return new Select().from(StudTestPaperItem.class).where("TestId = ?", TestId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }

    public static void deleteAll(String TestId, String teamId, String groupId) {
        new Delete().from(StudTestPaperItem.class).where("TestId = ?", TestId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }

    public static void deleteAll() {
        new Delete().from(StudTestPaperItem.class).execute();
    }
}
