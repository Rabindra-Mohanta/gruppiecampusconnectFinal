package school.campusconnect.datamodel;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GroupDetailResponse extends BaseResponse implements Parcelable {

    public ArrayList<GroupItem> data;

    protected GroupDetailResponse(Parcel in) {
        data = in.createTypedArrayList(GroupItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupDetailResponse> CREATOR = new Creator<GroupDetailResponse>() {
        @Override
        public GroupDetailResponse createFromParcel(Parcel in) {
            return new GroupDetailResponse(in);
        }

        @Override
        public GroupDetailResponse[] newArray(int size) {
            return new GroupDetailResponse[size];
        }
    };
}
