package cn.edu.hfut.lilei.shareboard.JsonEnity;


public class RegisterJson {
    public int code;//返回码
    public String msg;//提示信息
    public ServerModel data;

    public ServerModel getData() {
        return data;
    }

    public void setData(ServerModel data) {
        this.data = data;
    }

    public class ServerModel {
        public String token;
        public String avatar;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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
