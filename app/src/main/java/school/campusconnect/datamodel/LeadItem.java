package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LeadItem implements Parcelable {


    @SerializedName(value = "id",alternate = "userId")
    public String id;
    public String name;
    public String phone;
    public String image;
    @SerializedName("isAllowedToPost")
    @Expose
    public boolean isPost;

    public boolean staff;


    public boolean allowedToAddUser;
    public boolean allowedToAddTeamPost;
    public boolean allowedToAddTeamPostComment;


    public String email;

    @SerializedName(value = "leadCount",alternate = "membersCount")
    public int leadCount;
    public String dob;
    public String qualification;
    public String occupation;
    public String[] otherLeads;
    public AddressItem address;
    public String gender;

    public int teamCount;

    public boolean isSelected;

    public boolean roleOnConstituency;
    public String voterId;
    public String bloodGroup;
    public String aadharNumber;


    public boolean isRoleOnConstituency() {
        return roleOnConstituency;
    }

    public void setRoleOnConstituency(boolean roleOnConstituency) {
        this.roleOnConstituency = roleOnConstituency;
    }

    public String isVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public boolean isAllowedToAddUser() {
        return allowedToAddUser;
    }

    public void setAllowedToAddUser(boolean allowedToAddUser) {
        this.allowedToAddUser = allowedToAddUser;
    }

    public boolean isAllowedToAddTeamPost() {
        return allowedToAddTeamPost;
    }

    public void setAllowedToAddTeamPost(boolean allowedToAddTeamPost) {
        this.allowedToAddTeamPost = allowedToAddTeamPost;
    }

    public boolean isAllowedToAddTeamPostComment() {
        return allowedToAddTeamPostComment;
    }

    public void setAllowedToAddTeamPostComment(boolean allowedToAddTeamPostComment) {
        this.allowedToAddTeamPostComment = allowedToAddTeamPostComment;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    protected LeadItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        image = in.readString();
        isPost = in.readByte() != 0;
        allowedToAddUser = in.readByte() != 0;
        allowedToAddTeamPost = in.readByte() != 0;
        allowedToAddTeamPostComment = in.readByte() != 0;
        email = in.readString();
        leadCount = in.readInt();
        dob = in.readString();
        qualification = in.readString();
        occupation = in.readString();
        otherLeads = in.createStringArray();
        address = in.readParcelable(AddressItem.class.getClassLoader());
        gender = in.readString();
        teamCount = in.readInt();
    }

    public static final Creator<LeadItem> CREATOR = new Creator<LeadItem>() {
        @Override
        public LeadItem createFromParcel(Parcel in) {
            return new LeadItem(in);
        }

        @Override
        public LeadItem[] newArray(int size) {
            return new LeadItem[size];
        }
    };

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String profile) {
        this.image = profile;
    }

    public void setIsPost(String string)
    {
        if(string.equalsIgnoreCase("false"))
            this.isPost = false;
        else
            this.isPost = true;
    }

    public boolean getIsPost()
    {
       return isPost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLeadCount() {
        return leadCount;
    }

    public void setLeadCount(int leadCount) {
        this.leadCount = leadCount;
    }

    public String[] getOtherLeads() {
        return otherLeads;
    }

    public void setOtherLeads(String[] otherLeads) {
        this.otherLeads = otherLeads;
    }


    public AddressItem getAddress() {
        return address;
    }

    public void setAddress(AddressItem address) {
        this.address = address;
    }

    public LeadItem() {

    }



    @Override
    public String toString() {
        return id+","+name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(image);
        dest.writeByte((byte) (isPost ? 1 : 0));
        dest.writeByte((byte) (allowedToAddUser ? 1 : 0));
        dest.writeByte((byte) (allowedToAddTeamPost ? 1 : 0));
        dest.writeByte((byte) (allowedToAddTeamPostComment ? 1 : 0));
        dest.writeString(email);
        dest.writeInt(leadCount);
        dest.writeString(dob);
        dest.writeString(qualification);
        dest.writeString(occupation);
        dest.writeStringArray(otherLeads);
        dest.writeParcelable(address, flags);
        dest.writeString(gender);
        dest.writeInt(teamCount);
    }
}
