package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;


public class GruppieContactListItem implements Parcelable {

    private String id;
    private String name;
    private String phone;
    private String email;
    private int leadCount;
    private String dob;
    private String qualification;
    private String occupation;
    private String image;
    private String[] otherLeads;

    private AddressItem address;
    public String gender;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected;

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

    /* public void setIsPost(String string)
     {
         if(string.equalsIgnoreCase("false"))
             this.isAllowedToPost = false;
         else
             this.isAllowedToPost = true;
     }

     public boolean getIsPost()
     {
         return isAllowedToPost;
     }
 */
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

    public GruppieContactListItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(leadCount);
        // dest.writeArray(otherLeads);
        dest.writeValue(address);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(occupation);

    }

    public GruppieContactListItem(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.leadCount = in.readInt();
        // in.readStringArray(this.otherLeads);
        this.address = (AddressItem) in.readValue(AddressItem.class.getClassLoader());
        this.dob = in.readString();
        this.gender = in.readString();
        this.occupation = in.readString();

    }

    public static final Creator<GruppieContactListItem> CREATOR = new Creator<GruppieContactListItem>() {

        @Override
        public GruppieContactListItem createFromParcel(Parcel source) {
            return new GruppieContactListItem(source);
        }

        @Override
        public GruppieContactListItem[] newArray(int size) {
            return new GruppieContactListItem[size];
        }
    };


    @Override
    public String toString() {
        return name;
    }
}
