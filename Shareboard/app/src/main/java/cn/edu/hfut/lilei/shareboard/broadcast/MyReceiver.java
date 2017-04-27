package cn.edu.hfut.lilei.shareboard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import cn.edu.hfut.lilei.shareboard.JsonEnity.JpushFriendJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.MainActivity;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import cn.jpush.android.api.JPushInterface;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;

public class MyReceiver extends BroadcastReceiver {
    private Bundle bundle_startmeeting = new Bundle();
    private MsgDao msgDao = GreenDaoManager.getInstance()
            .getSession()
            .getMsgDao();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

            showLog("msg title:" + title + "content:" + message + "@@@:" + extra);

            Gson gson = new Gson();
            JpushFriendJson o = gson.fromJson(extra, JpushFriendJson.class);
            switch (o.getFeature()) {
                case "requestAddFriend":
                    /**
                     * 存入本地数据库
                     */
                    //新建msg
                    Msg tmp =
                            new Msg(null, o.getEmail(), o.getFamilyName() + " " + o.getGivenName(),
                                    context.getString(R.string.he_request_add_friend),
                                    o.getFamilyName(), o
                                    .getGivenName(), o.getFeature(), o.getAvatar(), 0, DateTimeUtil
                                    .millisNow(), o.getTag());
                    msgDao.insert(tmp);

                    /**
                     * 状态栏显示
                     */

                    showToast(context, "收到message");


                    break;

                case "deleteFriend":
                    break;

                case "acceptFriend":
                    break;

                case "rejectFriend":
                    break;
            }


            /**
             *点击状态栏,触发事件
             * 系统消息页面分类:
             * 左滑删除
             * 1 加好友申请 :a.xx请求加好友[同意,拒绝]->点击同意:你们已经是好友了
             * 2 对方同意了你的好友申请,你们已经是好友了
             * 3 对方拒绝了你的好友申请
             * 4 对方把你移除了好友列表->本地数据库 同步云端数据
             * 5 对方邀请你加会 点击加会
             */


        } else
            if (intent.getAction()
                    .equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
                bundle_startmeeting = intent.getExtras();

            } else
                if (intent.getAction()
                        .equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {

                    intent.putExtras(bundle_startmeeting);
                    intent.setClass(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
    }
}
