package geneowak.stella.com.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class NoteInfo implements Parcelable {
    private CourseInfo eCourse;
    private String eTitle;
    private String eText;

    public NoteInfo(CourseInfo course, String title, String text) {
        eCourse = course;
        eTitle = title;
        eText = text;
    }

    public final static Parcelable.Creator<NoteInfo> CREATOR = new Parcelable.Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel parcel) {
            return new NoteInfo(parcel);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };

    private NoteInfo(Parcel parcel) {
        eCourse = parcel.readParcelable(CourseInfo.class.getClassLoader());
        eTitle = parcel.readString();
        eText = parcel.readString();
    }

    public CourseInfo getCourse() {
        return eCourse;
    }

    public void setCourse(CourseInfo course) {
        eCourse = course;
    }

    public String getTitle() {
        return eTitle;
    }

    public void setTitle(String title) {
        eTitle = title;
    }

    public String getText() {
        return eText;
    }

    public void setText(String text) {
        eText = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    private String getCompareKey() {
        return eCourse.getCourseId() + "|" + eTitle + "|" + eText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(eCourse, 0);
        parcel.writeString(eTitle);
        parcel.writeString(eText);
    }
}
