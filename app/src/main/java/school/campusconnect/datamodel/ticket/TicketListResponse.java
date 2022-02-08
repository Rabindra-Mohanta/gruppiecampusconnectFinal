package school.campusconnect.datamodel.ticket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TicketListResponse extends BaseResponse implements Serializable {

    @SerializedName("totalNumberOfPages")
    @Expose
    private int totalNumberOfPages;

    @SerializedName("data")
    @Expose
    private ArrayList<TicketData> ticketData;

    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    public ArrayList<TicketData> getTicketData() {
        return ticketData;
    }

    public void setTicketData(ArrayList<TicketData> ticketData) {
        this.ticketData = ticketData;
    }

    public static class TicketData implements Serializable
    {

        @SerializedName("teamDetails")
        @Expose
        private TeamDetailsData teamDetails;

        @SerializedName("issueText")
        @Expose
        private String issueText;

        @SerializedName("issuePostId")
        @Expose
        private String issuePostId;

        @SerializedName("issueLocation")
        @Expose
        private IssueLocationData issueLocation;

        @SerializedName("issueCreatedByPhone")
        @Expose
        private String issueCreatedByPhone;

        @SerializedName("issueCreatedByName")
        @Expose
        private String issueCreatedByName;

        @SerializedName("issueCreatedByImage")
        @Expose
        private String issueCreatedByImage;

        @SerializedName("issueCreatedById")
        @Expose
        private String issueCreatedById;

        @SerializedName("issueCreatedAt")
        @Expose
        private String issueCreatedAt;

        @SerializedName("issueCoordinatorStatus")
        @Expose
        private String issueCoordinatorStatus;

        @SerializedName("fileType")
        @Expose
        private String fileType;

        @SerializedName("fileName")
        @Expose
        private ArrayList<String> fileName;

        @SerializedName("constituencyIssuePartyTaskForce")
        @Expose
        private ConstituencyIssuePartyTaskForceData constituencyIssuePartyTaskForce;

        @SerializedName("constituencyIssueJurisdiction")
        @Expose
        private String constituencyIssueJurisdiction;

        @SerializedName("constituencyIssueDepartmentTaskForce")
        @Expose
        private ConstituencyIssueDepartmentTaskForce constituencyIssueDepartmentTaskForce;

        @SerializedName("constituencyIssue")
        @Expose
        private String constituencyIssue;

        @SerializedName("boothCoordinators")
        @Expose
        private ArrayList<BoothCoordinatorData> boothCoordinators;


        public String getIssueText() {
            return issueText;
        }

        public void setIssueText(String issueText) {
            this.issueText = issueText;
        }

        public String getIssuePostId() {
            return issuePostId;
        }

        public void setIssuePostId(String issuePostId) {
            this.issuePostId = issuePostId;
        }

        public IssueLocationData getIssueLocation() {
            return issueLocation;
        }

        public void setIssueLocation(IssueLocationData issueLocation) {
            this.issueLocation = issueLocation;
        }

        public String getIssueCreatedByPhone() {
            return issueCreatedByPhone;
        }

        public void setIssueCreatedByPhone(String issueCreatedByPhone) {
            this.issueCreatedByPhone = issueCreatedByPhone;
        }

        public String getIssueCreatedByName() {
            return issueCreatedByName;
        }

        public void setIssueCreatedByName(String issueCreatedByName) {
            this.issueCreatedByName = issueCreatedByName;
        }

        public String getIssueCreatedByImage() {
            return issueCreatedByImage;
        }

        public void setIssueCreatedByImage(String issueCreatedByImage) {
            this.issueCreatedByImage = issueCreatedByImage;
        }

        public String getIssueCreatedById() {
            return issueCreatedById;
        }

        public void setIssueCreatedById(String issueCreatedById) {
            this.issueCreatedById = issueCreatedById;
        }

        public String getIssueCreatedAt() {
            return issueCreatedAt;
        }

        public void setIssueCreatedAt(String issueCreatedAt) {
            this.issueCreatedAt = issueCreatedAt;
        }

        public String getIssueCoordinatorStatus() {
            return issueCoordinatorStatus;
        }

        public void setIssueCoordinatorStatus(String issueCoordinatorStatus) {
            this.issueCoordinatorStatus = issueCoordinatorStatus;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public ArrayList<String> getFileName() {
            return fileName;
        }

        public void setFileName(ArrayList<String> fileName) {
            this.fileName = fileName;
        }

        public ConstituencyIssuePartyTaskForceData getConstituencyIssuePartyTaskForce() {
            return constituencyIssuePartyTaskForce;
        }

        public void setConstituencyIssuePartyTaskForce(ConstituencyIssuePartyTaskForceData constituencyIssuePartyTaskForce) {
            this.constituencyIssuePartyTaskForce = constituencyIssuePartyTaskForce;
        }

        public String getConstituencyIssueJurisdiction() {
            return constituencyIssueJurisdiction;
        }

        public void setConstituencyIssueJurisdiction(String constituencyIssueJurisdiction) {
            this.constituencyIssueJurisdiction = constituencyIssueJurisdiction;
        }

        public ConstituencyIssueDepartmentTaskForce getConstituencyIssueDepartmentTaskForce() {
            return constituencyIssueDepartmentTaskForce;
        }

        public void setConstituencyIssueDepartmentTaskForce(ConstituencyIssueDepartmentTaskForce constituencyIssueDepartmentTaskForce) {
            this.constituencyIssueDepartmentTaskForce = constituencyIssueDepartmentTaskForce;
        }

        public String getConstituencyIssue() {
            return constituencyIssue;
        }

        public void setConstituencyIssue(String constituencyIssue) {
            this.constituencyIssue = constituencyIssue;
        }

        public TeamDetailsData getTeamDetails() {
            return teamDetails;
        }

        public void setTeamDetails(TeamDetailsData teamDetails) {
            this.teamDetails = teamDetails;
        }

        public ArrayList<BoothCoordinatorData> getBoothCoordinators() {
            return boothCoordinators;
        }

        public void setBoothCoordinators(ArrayList<BoothCoordinatorData> boothCoordinators) {
            this.boothCoordinators = boothCoordinators;
        }
    }
    public static class TeamDetailsData implements Serializable
    {
        @SerializedName("userTeamName")
        @Expose
        private String userTeamName;

        @SerializedName("userTeamId")
        @Expose
        private String userTeamId;

        @SerializedName("userTeamCategory")
        @Expose
        private String userTeamCategory;

        @SerializedName("boothPresidentPhone")
        @Expose
        private String boothPresidentPhone;

        @SerializedName("boothPresidentName")
        @Expose
        private String boothPresidentName;

        @SerializedName("boothPresidentId")
        @Expose
        private String boothPresidentId;

        public String getUserTeamName() {
            return userTeamName;
        }

        public void setUserTeamName(String userTeamName) {
            this.userTeamName = userTeamName;
        }

        public String getUserTeamId() {
            return userTeamId;
        }

        public void setUserTeamId(String userTeamId) {
            this.userTeamId = userTeamId;
        }

        public String getUserTeamCategory() {
            return userTeamCategory;
        }

        public void setUserTeamCategory(String userTeamCategory) {
            this.userTeamCategory = userTeamCategory;
        }

        public String getBoothPresidentPhone() {
            return boothPresidentPhone;
        }

        public void setBoothPresidentPhone(String boothPresidentPhone) {
            this.boothPresidentPhone = boothPresidentPhone;
        }

        public String getBoothPresidentName() {
            return boothPresidentName;
        }

        public void setBoothPresidentName(String boothPresidentName) {
            this.boothPresidentName = boothPresidentName;
        }

        public String getBoothPresidentId() {
            return boothPresidentId;
        }

        public void setBoothPresidentId(String boothPresidentId) {
            this.boothPresidentId = boothPresidentId;
        }
    }
    public static class IssueLocationData implements Serializable
    {
        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("landmark")
        @Expose
        private String landmark;

        @SerializedName("address")
        @Expose
        private String address;

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
    public static class ConstituencyIssuePartyTaskForceData implements Serializable
    {
        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("constituencyDesignation")
        @Expose
        private String constituencyDesignation;

        @SerializedName("_id")
        @Expose
        private String _id;

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

        public String getConstituencyDesignation() {
            return constituencyDesignation;
        }

        public void setConstituencyDesignation(String constituencyDesignation) {
            this.constituencyDesignation = constituencyDesignation;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
    public static class ConstituencyIssueDepartmentTaskForce implements Serializable
    {
        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("constituencyDesignation")
        @Expose
        private String constituencyDesignation;

        @SerializedName("_id")
        @Expose
        private String _id;

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

        public String getConstituencyDesignation() {
            return constituencyDesignation;
        }

        public void setConstituencyDesignation(String constituencyDesignation) {
            this.constituencyDesignation = constituencyDesignation;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }

    public static class BoothCoordinatorData implements Serializable
    {
        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("constituencyDesignation")
        @Expose
        private String constituencyDesignation;

        @SerializedName("_id")
        @Expose
        private String _id;

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

        public String getConstituencyDesignation() {
            return constituencyDesignation;
        }

        public void setConstituencyDesignation(String constituencyDesignation) {
            this.constituencyDesignation = constituencyDesignation;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }
    }
}
