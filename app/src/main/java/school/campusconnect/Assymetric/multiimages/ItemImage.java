package school.campusconnect.Assymetric.multiimages;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemImage extends ItemPosition {

	private String ImagePath;

	
	public ItemImage(String imagePath) {
		super();
		ImagePath = imagePath;

	}

	protected ItemImage(Parcel in) {
		ImagePath = in.readString();


	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ImagePath);

	}

	@Override
	public String toString() {
		return "ItemImage{" +
				", ImagePath='" + ImagePath + '\'' +
				'}';
	}


	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}



	public static final Parcelable.Creator<ItemImage> CREATOR = new Parcelable.Creator<ItemImage>() {
		@Override
		public ItemImage createFromParcel(Parcel in) {
			return new ItemImage(in);
		}

		@Override
		public ItemImage[] newArray(int size) {
			return new ItemImage[size];
		}
	};

}
