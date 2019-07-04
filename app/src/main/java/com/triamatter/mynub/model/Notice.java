package com.triamatter.mynub.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notice {
    private String notice;
    private Timestamp timestamp;

    public  Notice() {

    }

    public Notice(String notice, Timestamp timestamp)
    {
        this.notice = notice;
        this.timestamp = timestamp;
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
