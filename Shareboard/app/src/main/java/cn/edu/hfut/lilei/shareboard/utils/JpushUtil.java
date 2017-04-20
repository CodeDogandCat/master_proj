package cn.edu.hfut.lilei.shareboard.utils;

import android.content.Context;
import android.os.Handler;

import java.util.Set;

import cn.edu.hfut.lilei.shareboard.R;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;


public class JpushUtil {

    private static final int MSG_SET_ALIAS = 1001;
    private Context mContext;

    public JpushUtil(Context mContext) {
        this.mContext = mContext;
    }


    public void setAlias() {


        String alias = (String) SharedPrefUtil.getInstance()
                .getData(SettingUtil.share_user_email, "");
        if (!alias.equals("")) {
            // 调用 Handler 来异步设置别名
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
        } else {
            showToast(mContext, mContext.getString(R.string.please_relogin));
        }

    }


    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    showLog(logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    showLog(logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias),
                            1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    showLog(logs);
            }
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    showLog("Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(mContext.getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    showLog("Unhandled msg - " + msg.what);
            }
        }
    };
}
