package geneowak.stella.com.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim.
 */

public final class CourseInfo implements Parcelable {
    private final String eCourseId;
    private final String eTitle;
    private final List<ModuleInfo> eModules;

    public CourseInfo(String courseId, String title, List<ModuleInfo> modules) {
        eCourseId = courseId;
        eTitle = title;
        eModules = modules;
    }

    public final static Parcelable.Creator<CourseInfo> CREATOR = new Parcelable.Creator<CourseInfo>() {
        @Override
        public CourseInfo createFromParcel(Parcel source) {
            return new CourseInfo(source);
        }

        @Override
        public CourseInfo[] newArray(int size) {
            return new CourseInfo[size];
        }
    };

    private CourseInfo(Parcel source) {
        eTitle = source.readString();
        eCourseId = source.readString();
        eModules = new ArrayList<>();
        source.readTypedList(eModules, ModuleInfo.CREATOR);
    }

    public String getCourseId() {
        return eCourseId;
    }

    public String getTitle() {
        return eTitle;
    }

    public List<ModuleInfo> getModules() {
        return eModules;
    }

    public boolean[] getModulesCompletionStatus() {
        boolean[] status = new boolean[eModules.size()];

        for (int i = 0; i < eModules.size(); i++)
            status[i] = eModules.get(i).isComplete();

        return status;
    }

    public void setModulesCompletionStatus(boolean[] status) {
        for (int i = 0; i < eModules.size(); i++)
            eModules.get(i).setComplete(status[i]);
    }

    public ModuleInfo getModule(String moduleId) {
        for (ModuleInfo moduleInfo : eModules) {
            if (moduleId.equals(moduleInfo.getModuleId()))
                return moduleInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return eTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseInfo that = (CourseInfo) o;

        return eCourseId.equals(that.eCourseId);

    }

    @Override
    public int hashCode() {
        return eCourseId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eTitle);
        parcel.writeString(eCourseId);
        parcel.writeTypedList(eModules);
    }
}
