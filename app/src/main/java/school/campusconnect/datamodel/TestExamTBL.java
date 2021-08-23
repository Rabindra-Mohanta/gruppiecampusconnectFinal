package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "TestExamTBL")
public class TestExamTBL extends Model {
    @Column(name = "topic")
    public String topic;

    @Column(name = "testStartTime")
    public String testStartTime;

    @Column(name = "testEndTime")
    public String testEndTime;

    @Column(name = "testExamId")
    public String testExamId;

    @Column(name = "testDate")
    public String testDate;

    @Column(name = "lastSubmissionTime")
    public String lastSubmissionTime;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "description")
    public String description;

    @Column(name = "createdByName")
    public String createdByName;

    @Column(name = "createdByImage")
    public String createdByImage;

    @Column(name = "createdById")
    public String createdById;

    @Column(name = "canPost")
    public boolean canPost;

    @Column(name = "proctoring")
    public boolean proctoring;

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


    public TestExamTBL() {
        super();
    }

    public static List<TestExamTBL> getAll(String subjectId, String teamId, String groupId) {
        return new Select().from(TestExamTBL.class).where("subjectId = ?", subjectId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }

    public static void deleteAll() {
        new Delete().from(TestExamTBL.class).execute();
    }

    public static void deleteAll(String subjectId) {
        new Delete().from(TestExamTBL.class).where("subjectId = ?", subjectId).execute();
    }
}
