package geneowak.stella.com.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class ModuleInfo implements Parcelable {
    private final String eModuleId;
    private final String eTitle;
    private boolean eIsComplete = false;

    public ModuleInfo(String moduleId, String title) {
        this(moduleId, title, false);
    }

    public ModuleInfo(String moduleId, String title, boolean isComplete) {
        eModuleId = moduleId;
        eTitle = title;
        eIsComplete = isComplete;
    }

    public final static Parcelable.Creator<ModuleInfo> CREATOR = new Creator<ModuleInfo>() {
        @Override
        public ModuleInfo createFromParcel(Parcel source) {
            return new ModuleInfo(source);
        }

        @Override
        public ModuleInfo[] newArray(int size) {
            return new ModuleInfo[size];
        }
    };

    private ModuleInfo(Parcel source) {
        eModuleId = source.readString();
        eTitle = source.readString();
        eIsComplete = source.readByte() == 1;
    }

    public String getModuleId() {
        return eModuleId;
    }

    public String getTitle() {
        return eTitle;
    }

    public boolean isComplete() {
        return eIsComplete;
    }

    public void setComplete(boolean complete) {
        eIsComplete = complete;
    }

    @Override
    public String toString() {
        return eTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleInfo that = (ModuleInfo) o;

        return eModuleId.equals(that.eModuleId);
    }

    @Override
    public int hashCode() {
        return eModuleId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eModuleId);
        parcel.writeString(eTitle);
        parcel.writeByte((byte) (eIsComplete ? 1 : 0));
    }
}
