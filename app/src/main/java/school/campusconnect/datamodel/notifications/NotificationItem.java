package school.campusconnect.datamodel.notifications;

/**
 * Created by frenzin04 on 1/2/2017.
 */
public class NotificationItem {

    private String strTitle;
    private String strIcon;
    private String groupId;
    private String teamId;
    private String postId;
    private String commentId;
    private int postType;
    private String dateTime;
    private String friendId;
    private String strName;
    private String phone;
    private boolean seen;
    private String id;

    public NotificationItem(String strTitle, String strIcon, String  groupId, String teamId, String postId,
                            String commentId, int postType, String dateTime, String friendId, String strName,
                            String phone, boolean seen, String id) {
        this.strTitle = strTitle;
        this.strIcon = strIcon;
        this.groupId = groupId;
        this.teamId = teamId;
        this.postId = postId;
        this.commentId = commentId;
        this.postType = postType;
        this.dateTime = dateTime;
        this.friendId = friendId;
        this.strName = strName;
        this.phone = phone;
        this.seen = seen;
        this.id = id;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrIcon() {
        return strIcon;
    }

    public void setStrIcon(String strIcon) {
        this.strIcon = strIcon;
    }


    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
