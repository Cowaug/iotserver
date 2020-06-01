package vn.edu.hcmut.iotserver.Entities;

public class User {
String userId;
Permissions permission;

    public User(String userId, Permissions permission) {
        this.userId = userId;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public Permissions getPermission() {
        return permission;
    }
}
