package school.campusconnect.datamodel.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AdminFeederResponse extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    public ArrayList<FeedData> feedData;

    public ArrayList<FeedData> getFeedData() {
        return feedData;
    }

    public void setFeedData(ArrayList<FeedData> feedData) {
        this.feedData = feedData;
    }

    public static class FeedData implements Serializable
    {
        @SerializedName("totalSubBoothsCount")
        @Expose
        private int totalSubBoothsCount;

        @SerializedName("totalSubBoothDiscussion")
        @Expose
        private int totalSubBoothDiscussion;

        @SerializedName("totalOpenIssuesCount")
        @Expose
        private int totalOpenIssuesCount;

        @SerializedName("totalBoothsDiscussion")
        @Expose
        private int totalBoothsDiscussion;

        @SerializedName("totalBoothsCount")
        @Expose
        private int totalBoothsCount;

        @SerializedName("totalAnnouncementCount")
        @Expose
        private int totalAnnouncementCount;

        public int getTotalSubBoothsCount() {
            return totalSubBoothsCount;
        }

        public void setTotalSubBoothsCount(int totalSubBoothsCount) {
            this.totalSubBoothsCount = totalSubBoothsCount;
        }

        public int getTotalSubBoothDiscussion() {
            return totalSubBoothDiscussion;
        }

        public void setTotalSubBoothDiscussion(int totalSubBoothDiscussion) {
            this.totalSubBoothDiscussion = totalSubBoothDiscussion;
        }

        public int getTotalOpenIssuesCount() {
            return totalOpenIssuesCount;
        }

        public void setTotalOpenIssuesCount(int totalOpenIssuesCount) {
            this.totalOpenIssuesCount = totalOpenIssuesCount;
        }

        public int getTotalBoothsDiscussion() {
            return totalBoothsDiscussion;
        }

        public void setTotalBoothsDiscussion(int totalBoothsDiscussion) {
            this.totalBoothsDiscussion = totalBoothsDiscussion;
        }

        public int getTotalBoothsCount() {
            return totalBoothsCount;
        }

        public void setTotalBoothsCount(int totalBoothsCount) {
            this.totalBoothsCount = totalBoothsCount;
        }

        public int getTotalAnnouncementCount() {
            return totalAnnouncementCount;
        }

        public void setTotalAnnouncementCount(int totalAnnouncementCount) {
            this.totalAnnouncementCount = totalAnnouncementCount;
        }
    }
}

