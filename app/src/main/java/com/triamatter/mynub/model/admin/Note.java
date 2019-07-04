package com.triamatter.mynub.model.admin;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.Timestamp;

@IgnoreExtraProperties
public class Note {
    private String noteTitle;
    private String noteUrl;
    private Timestamp timestamp;

    public Note()
    {
    }

    public Note(String noteTitle, String noteUrl, Timestamp timestamp)
    {
        this.noteTitle = noteTitle;
        this.noteUrl = noteUrl;
        this.timestamp = timestamp;
    }

    public String getNoteTitle()
    {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle)
    {
        this.noteTitle = noteTitle;
    }

    public String getNoteUrl()
    {
        return noteUrl;
    }

    public void setNoteUrl(String noteUrl)
    {
        this.noteUrl = noteUrl;
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
