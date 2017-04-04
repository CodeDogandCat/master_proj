package cn.edu.hfut.lilei.shareboard.enity;

import android.graphics.drawable.Drawable;

public class GroupMemberInfo {

    private String name;   //
    private String sortLetters;  //
    private Drawable photo;
    private String status;

    public Drawable getPhoto() {
        return photo;
    }

    public void setPhoto(Drawable photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
