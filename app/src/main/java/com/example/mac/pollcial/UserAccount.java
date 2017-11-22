package com.example.mac.pollcial;

/**
 * Created by mac on 17/11/21.
 */

public class UserAccount {
    private String userName;
    private String userEmail;

    public UserAccount(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
