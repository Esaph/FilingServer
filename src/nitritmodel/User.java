/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;

import java.io.Serializable;

public class User extends PipeData<User> implements Serializable
{
    @org.dizitart.no2.objects.Id
    private String Uid;
    private String Username;
    private String GivenName;
    private String FamilyName;
    private String Email;
    private String FcmToken;

    public User() {
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Uid);
        stringBuilder.append(" : ");
        stringBuilder.append(Username);
        stringBuilder.append(" : ");
        stringBuilder.append(GivenName);
        stringBuilder.append(" : ");
        stringBuilder.append(FamilyName);
        stringBuilder.append(" : ");
        stringBuilder.append(Email);
        return stringBuilder.toString();
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getGivenName() {
        return GivenName;
    }

    public void setGivenName(String givenName) {
        GivenName = givenName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String familyName) {
        FamilyName = familyName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}