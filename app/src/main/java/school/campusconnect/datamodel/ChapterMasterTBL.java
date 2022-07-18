package school.campusconnect.datamodel;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name="chapterMasterTBL")
public class ChapterMasterTBL extends Model{

    @Column(name="totalTopicsCount")

    public Integer totalTopicsCount;
    @Column(name="topicsList")

    public String TopicData;

    @Column(name="chapterName")

    public String chapterName;

    @Column(name="chapterId")

    public String chapterId;


    @Column(name = "teamId")
    public String teamId;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "groupId")
    public String groupId;

    public ChapterMasterTBL(){
        super();
    }
    public static List<ChapterMasterTBL> getAll(String subjectId, String teamId, String groupId) {
        return new Select().from(ChapterMasterTBL.class).where("subjectId = ?", subjectId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }

    public static void deleteAll() {
        new Delete().from(ChapterMasterTBL.class).execute();
    }

    public static void deleteAll(String subjectId) {
        new Delete().from(ChapterMasterTBL.class).where("subjectId = ?", subjectId).execute();
    }

    public static void update(String chapterId, String jsonData) {
        new Update(ChapterMasterTBL.class).set("TopicData = ?", jsonData).where("chapterId = ?", chapterId).execute();
    }

}
