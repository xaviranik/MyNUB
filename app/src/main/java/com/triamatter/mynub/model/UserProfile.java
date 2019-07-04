package com.triamatter.mynub.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserProfile {
    private String userFullName;
    private String userId;
    private String userDept;
    private String userSection;

    public UserProfile()
    {

    }

    public UserProfile(String userFullName, String userId, String userDept, String userSection)
    {
        this.userFullName = userFullName;
        this.userId = userId;
        this.userDept = userDept;
        this.userSection = userSection;
    }

    public String getUserFullName()
    {
        return userFullName;
    }

    public void setUserFullName(String userFullName)
    {
        this.userFullName = userFullName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserDept()
    {
        return userDept;
    }

    public void setUserDept(String userDept)
    {
        this.userDept = userDept;
    }

    public String getUserSection()
    {
        return userSection;
    }

    public void setUserSection(String userSection)
    {
        this.userSection = userSection;
    }
}
