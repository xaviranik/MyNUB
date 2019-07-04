package com.triamatter.mynub.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {

    private String courseCode;
    private String bookName;
    private String dept;
    private String bookUrl;

    public Book()
    {
    }

    public Book(String courseCode, String bookName, String dept, String bookUrl)
    {
        this.courseCode = courseCode;
        this.bookName = bookName;
        this.dept = dept;
        this.bookUrl = bookUrl;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getBookName()
    {
        return bookName;
    }

    public void setBookName(String bookName)
    {
        this.bookName = bookName;
    }

    public String getDept()
    {
        return dept;
    }

    public void setDept(String dept)
    {
        this.dept = dept;
    }

    public String getBookUrl()
    {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl)
    {
        this.bookUrl = bookUrl;
    }
}
