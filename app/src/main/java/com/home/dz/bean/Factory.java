package com.home.dz.bean;

public class Factory {

    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Factory.user = user;
    }
}
