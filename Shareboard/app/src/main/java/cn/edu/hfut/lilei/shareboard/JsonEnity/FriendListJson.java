package cn.edu.hfut.lilei.shareboard.JsonEnity;


import java.util.List;

public class FriendListJson {
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
        public String email;
        public String familyName;
        public String givenName;
        public String avatar;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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
