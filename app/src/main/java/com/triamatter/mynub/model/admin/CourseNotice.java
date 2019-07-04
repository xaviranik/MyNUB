package com.triamatter.mynub.model.admin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CourseNotice {
    private String courseTitle;
    private String notice;
    private Timestamp timestamp;

    public CourseNotice()
    {
    }

    public CourseNotice(String courseTitle, String courseNotice, Timestamp timestamp)
    {
        this.courseTitle = courseTitle;
        this.notice = courseNotice;
        this.timestamp = timestamp;
    }

    public String getCourseTitle()
    {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle)
    {
        this.courseTitle = courseTitle;
    }

    public String getNotice()
    {
        return notice;
    }

    public void setNotice(String notice)
    {
        this.notice = notice;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
}
