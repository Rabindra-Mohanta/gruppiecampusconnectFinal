package school.campusconnect.datamodel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VideoOfflineObject
{

        @SerializedName("video_filename")
        @Expose
        public String video_filename;

    public String getVideo_filepath() {
        return video_filepath;
    }

    public void setVideo_filepath(String video_filepath) {
        this.video_filepath = video_filepath;
    }

    @SerializedName("video_filepath")
    @Expose
    public String video_filepath;

    public String getVideo_filename() {
        return video_filename;
    }

    public void setVideo_filename(String video_filename) {
        this.video_filename = video_filename;
    }

    public String getVideo_date() {
        return video_date;
    }

    public void setVideo_date(String video_date) {
        this.video_date = video_date;
    }

    @SerializedName("video_date")
        @Expose
        public String video_date;


}
