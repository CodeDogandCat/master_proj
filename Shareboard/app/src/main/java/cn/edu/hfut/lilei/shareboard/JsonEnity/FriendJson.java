package cn.edu.hfut.lilei.shareboard.JsonEnity;


public class FriendJson {

    public int code;//返回码
    public String msg;//提示信息
    public ServerModel data;

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

    public ServerModel getData() {
        return data;
    }

    public void setData(ServerModel data) {
        this.data = data;
    }

    public class ServerModel {

        public String email;
        public String familyName;
        public String givenName;
        public String avatar;


        public String getFamilyName() {
            return this.familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return this.givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getAvatar() {
            return this.avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

