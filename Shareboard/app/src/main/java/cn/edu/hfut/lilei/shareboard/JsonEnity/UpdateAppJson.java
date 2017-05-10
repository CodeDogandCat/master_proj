package cn.edu.hfut.lilei.shareboard.JsonEnity;


public class UpdateAppJson {

    public UpdateInfo data; // 信息
    public int code; // 错误代码
    public String msg; // 错误信息

    public UpdateInfo getData() {
        return data;
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

    public void setData(UpdateInfo data) {
        this.data = data;
    }

    public static class UpdateInfo {
        // app名字
        public String appname;
        //服务器版本
        public String serverVersion;
        //服务器标志
        public int serverFlag;
        //强制升级
        public int lastForce;
        //app最新版本地址
        public String updateurl;
        //升级信息
        public String upgradeinfo;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getServerVersion() {
            return serverVersion;
        }

        public void setServerVersion(String serverVersion) {
            this.serverVersion = serverVersion;
        }

        public int getServerFlag() {
            return serverFlag;
        }

        public void setServerFlag(int serverFlag) {
            this.serverFlag = serverFlag;
        }

        public int getLastForce() {
            return lastForce;
        }

        public void setLastForce(int lastForce) {
            this.lastForce = lastForce;
        }

        public String getUpdateurl() {
            return updateurl;
        }

        public void setUpdateurl(String updateurl) {
            this.updateurl = updateurl;
        }

        public String getUpgradeinfo() {
            return upgradeinfo;
        }

        public void setUpgradeinfo(String upgradeinfo) {
            this.upgradeinfo = upgradeinfo;
        }
    }
}
