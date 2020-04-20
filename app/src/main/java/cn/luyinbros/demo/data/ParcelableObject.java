package cn.luyinbros.demo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableObject implements Parcelable {
    private String name;

    public ParcelableObject(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected ParcelableObject(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<ParcelableObject> CREATOR = new Creator<ParcelableObject>() {
        @Override
        public ParcelableObject createFromParcel(Parcel source) {
            return new ParcelableObject(source);
        }

        @Override
        public ParcelableObject[] newArray(int size) {
            return new ParcelableObject[size];
        }
    };

    @Override
    public String toString() {
        return "ParcelableObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
