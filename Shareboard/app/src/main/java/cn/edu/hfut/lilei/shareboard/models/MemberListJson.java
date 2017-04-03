package cn.edu.hfut.lilei.shareboard.models;


import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.data.MeetingMemberInfo;

public class MemberListJson {

    public String type;//信息类型
    public String client_email;//用户邮箱
    public List<ServerModel> client_list;


    public List<MeetingMemberInfo> toMemberInfoList() {
        List<MeetingMemberInfo> tmp = new ArrayList<>();
        for (int i = 0; i < client_list.size(); i++) {
            tmp.add(new MeetingMemberInfo(client_list.get(i)
                    .getClient_family_name(), client_list.get(i)
                    .getClient_given_name(),
                    client_list.get(i)
                            .getClient_email(),
                    client_list.get(i)
                            .getClient_avatar(),
                    client_list.get(i)
                            .getClient_type(), client_list.get(i)
                    .isClient_is_drawable(),
                    client_list.get(i)
                            .isClient_is_talkable()));
        }
        return tmp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public List<ServerModel> getClient_list() {
        return client_list;
    }

    public void setClient_list(
            List<ServerModel> client_list) {
        this.client_list = client_list;
    }

    public class ServerModel {
        public String client_family_name;//参与者姓
        public String client_given_name;//参与者名
        public String client_email;//参与者邮箱
        public String room_id;//会议号
        public String client_avatar;//参与者头像地址
        public String client_type;//参与者类型
        public boolean client_is_drawable;//是否可以画
        public boolean client_is_talkable;//是否可以说

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

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
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


}
