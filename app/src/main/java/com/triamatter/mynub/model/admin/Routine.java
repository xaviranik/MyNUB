package com.triamatter.mynub.model.admin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Routine {

    private String courseTitle;
    private String roomNum;
    private Timestamp timeStart;
    private Timestamp timeEnd;

    public Routine()
    {
    }

    public Routine(String courseTitle, String roomNum, Timestamp timeStart, Timestamp timeEnd)
    {
        this.courseTitle = courseTitle;
        this.roomNum = roomNum;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getCourseTitle()
    {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle)
    {
        this.courseTitle = courseTitle;
    }

    public String getRoomNum()
    {
        return roomNum;
    }

    public void setRoomNum(String roomNum)
    {
        this.roomNum = roomNum;
    }

    public Timestamp getTimeStart()
    {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart)
    {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd()
    {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd)
    {
        this.timeEnd = timeEnd;
    }
}
