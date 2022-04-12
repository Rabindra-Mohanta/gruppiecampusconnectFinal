package school.campusconnect.datamodel.gallery;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.datamodel.feed.AdminFeedTable;

@Table(name = "GalleryTable")
public class GalleryTable extends Model {

    @Column( name = "updatedAt")
    public String updatedAt;

    @Column( name = "albumId")
    public String albumId;

    @Column( name = "groupId")
    public String groupId;

    @Column( name = "fileType")
    public String fileType;

    @Column( name = "video")
    public String video;

    @Column( name = "thumbnail")
    public String thumbnail;

    @Column( name = "fileName")
    public String fileName;

    @Column( name = "thumbnailImage")
    public String thumbnailImage;

    @Column( name = "createdAt")
    public String createdAt;

    @Column( name = "canEdit")
    public Boolean canEdit;

    @Column( name = "albumName")
    public String albumName;

    @Column( name = "page")
    public int page;

    @Column( name = "_now")
    public String _now;

    public  static List<GalleryTable> getAll()
    {
        return new Select().from(GalleryTable.class).execute();
    }
    public  static List<GalleryTable> getGallery(String group_id,int page)
    {
        return new Select().from(GalleryTable.class).where("groupId = ?", group_id).where("page = ?", page).execute();
    }
    public  static List<GalleryTable> deleteGallery(String group_id,int page)
    {
        return new Delete().from(GalleryTable.class).where("groupId = ?", group_id).where("page = ?", page).execute();
    }
    public  static List<GalleryTable> deleteGallery()
    {
        return new Delete().from(GalleryTable.class).execute();
    }
}
