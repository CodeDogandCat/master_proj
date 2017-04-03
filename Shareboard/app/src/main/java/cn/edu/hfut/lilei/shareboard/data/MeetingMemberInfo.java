package cn.edu.hfut.lilei.shareboard.data;

public class MeetingMemberInfo {

    public String client_family_name;//参与者姓
    public String client_given_name;//参与者名
    public String client_email;//参与者邮箱
    public String client_avatar;//参与者头像地址
    public String client_type;//参与者类型
    public boolean client_is_drawable;//是否可以画
    public boolean client_is_talkable;//是否可以说


    @Override
    public String toString() {

        return "(client_family_name=>" + client_family_name
                + "," + "client_given_name=>" + client_given_name
                + "," + "client_email=>" + client_email + "," +
                "client_avatar=>" + client_avatar + "," +
                "client_is_drawable=>" + client_is_drawable + "," +
                "client_is_talkable=>" + client_is_talkable + "," +
                "client_type=>" + client_type + ")";
    }

    public MeetingMemberInfo(String client_family_name, String client_given_name,
                             String client_email, String client_avatar, String client_type,
                             boolean client_is_drawable, boolean client_is_talkable) {
        this.client_family_name = client_family_name;
        this.client_given_name = client_given_name;
        this.client_email = client_email;
        this.client_avatar = client_avatar;
        this.client_type = client_type;
        this.client_is_drawable = client_is_drawable;
        this.client_is_talkable = client_is_talkable;
    }

    public boolean isClient_is_drawable() {
        return client_is_drawable;
    }

    public void setClient_is_drawable(boolean client_is_drawable) {
        this.client_is_drawable = client_is_drawable;
    }

    public boolean isClient_is_talkable() {
        return client_is_talkable;
    }

    public void setClient_is_talkable(boolean client_is_talkable) {
        this.client_is_talkable = client_is_talkable;
    }

    public String getClient_family_name() {
        return client_family_name;
    }

    public void setClient_family_name(String client_family_name) {
        this.client_family_name = client_family_name;
    }

    public String getClient_given_name() {
        return client_given_name;
    }

    public void setClient_given_name(String client_given_name) {
        this.client_given_name = client_given_name;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getClient_avatar() {
        return client_avatar;
    }

    public void setClient_avatar(String client_avatar) {
        this.client_avatar = client_avatar;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }
}
