package school.campusconnect.datamodel;

import java.util.ArrayList;

public class ReadUnreadResponse extends BaseResponse {
    private ArrayList<UnreadReadUser> data;

    public ArrayList<UnreadReadUser> getData() {
        return data;
    }

    public void setData(ArrayList<UnreadReadUser> data) {
        this.data = data;
    }

    public class UnreadReadUser {
        ArrayList<UserData> unreadUsers;
        ArrayList<UserData> readUsers;

        public ArrayList<UserData> getUnreadUsers() {
            return unreadUsers;
        }

        public void setUnreadUsers(ArrayList<UserData> unreadUsers) {
            this.unreadUsers = unreadUsers;
        }

        public ArrayList<UserData> getReadUsers() {
            return readUsers;
        }

        public void setReadUsers(ArrayList<UserData> readUsers) {
            this.readUsers = readUsers;
        }
    }
    public class UserData {
        private String userId;
        private String phone;
        private String name;
        private String image;
        private String postSeenTime;

        public String getPostSeenTime() {
            return postSeenTime;
        }

        public void setPostSeenTime(String postSeenTime) {
            this.postSeenTime = postSeenTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
