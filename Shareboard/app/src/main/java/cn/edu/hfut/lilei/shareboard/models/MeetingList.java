package cn.edu.hfut.lilei.shareboard.models;


import java.util.List;

public class MeetingList {
    public int code;//返回码
    public String msg;//提示信息
    public List<ServerModel> data;

    public List<ServerModel> getData() {
        return data;
    }

    public void setData(List<ServerModel> data) {
        this.data = data;
    }

    public class ServerModel {
        public int meeting_id;
        public String meeting_url;
        public String meeting_theme;
        public int meeting_is_drawable;
        public int meeting_is_talkable;
        public int meeting_is_add_to_calendar;
        public String meeting_password;
        public String meeting_start_time;
        public String meeting_end_time;

        public int getMeeting_id() {
            return meeting_id;
        }

        public void setMeeting_id(int meeting_id) {
            this.meeting_id = meeting_id;
        }

        public String getMeeting_url() {
            return meeting_url;
        }

        public void setMeeting_url(String meeting_url) {
            this.meeting_url = meeting_url;
        }

        public String getMeeting_theme() {
            return meeting_theme;
        }

        public void setMeeting_theme(String meeting_theme) {
            this.meeting_theme = meeting_theme;
        }

        public int getMeeting_is_drawable() {
            return meeting_is_drawable;
        }

        public void setMeeting_is_drawable(int meeting_is_drawable) {
            this.meeting_is_drawable = meeting_is_drawable;
        }

        public int getMeeting_is_talkable() {
            return meeting_is_talkable;
        }

        public void setMeeting_is_talkable(int meeting_is_talkable) {
            this.meeting_is_talkable = meeting_is_talkable;
        }

        public int getMeeting_is_add_to_calendar() {
            return meeting_is_add_to_calendar;
        }

        public void setMeeting_is_add_to_calendar(int meeting_is_add_to_calendar) {
            this.meeting_is_add_to_calendar = meeting_is_add_to_calendar;
        }

        public String getMeeting_password() {
            return meeting_password;
        }

        public void setMeeting_password(String meeting_password) {
            this.meeting_password = meeting_password;
        }

        public String getMeeting_start_time() {
            return meeting_start_time;
        }

        public void setMeeting_start_time(String meeting_start_time) {
            this.meeting_start_time = meeting_start_time;
        }

        public String getMeeting_end_time() {
            return meeting_end_time;
        }

        public void setMeeting_end_time(String meeting_end_time) {
            this.meeting_end_time = meeting_end_time;
        }
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
