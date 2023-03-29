package com.example.aliantebadge.roomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity
public class OtherUser implements Comparable<OtherUser> {



    @PrimaryKey
    @NotNull
    public String uid;


    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String secondName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "accepted")
    public boolean accepted;

    @ColumnInfo(name = "versionApp")
    public String versionApp ;


    public OtherUser() {
        uid = "null";
    }

    public OtherUser(User user){
        this.uid = user.uid;
        this.email = user.email;
        this.firstName = user.firstName;
        this.secondName = user.secondName;

    }


    @Override
    public boolean equals(Object o) {
        OtherUser user;

        if (this.getClass() == o.getClass()) {
            user = (OtherUser) o;
            return Objects.equals(user.getUid(), this.uid);
        }
        return false;

    }

    @NonNull
    private String getUid() {
        return  this.uid;
    }

    @Override
    public int hashCode() {
        return this.uid.hashCode();
    }


    @Override
    public int compareTo(OtherUser otherUser) {
        return this.uid.compareTo(otherUser.getUid());
    }
}
