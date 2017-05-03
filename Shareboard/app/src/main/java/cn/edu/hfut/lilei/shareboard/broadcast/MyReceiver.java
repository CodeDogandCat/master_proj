package cn.edu.hfut.lilei.shareboard.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.edu.hfut.lilei.shareboard.JsonEnity.JpushFriendJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.MainActivity;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.model.Event;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_new_msg_num;

public class MyReceiver extends BroadcastReceiver {
    private Bundle bundle_startmeeting = new Bundle();
    private MsgDao msgDao = GreenDaoManager.getInstance()
            .getSession()
            .getMsgDao();


    @Override
    public void onReceive(Context context, Intent intent) {

//        EventBus.getDefault()
//                .register(this);

        Event e = new Event();
        e.flag = 1;

        if (intent.getAction()
                .equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

            showLog("msg title:" + title + "content:" + message + "@@@:" + extra);

            Gson gson = new Gson();
            JpushFriendJson o = gson.fromJson(extra, JpushFriendJson.class);
            //第一步：获取状态通知栏管理
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService
                            (NOTIFICATION_SERVICE);
            //第二步：实例化通知栏构造器NotificationCompat.Builder
            NotificationCompat.Builder mBuilder = NotificationUtil(context);

            int currenUnreadNews = (int) SharedPrefUtil.getInstance()
                    .getData(share_new_msg_num, 0);
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
                                    .getGivenName(), o.getFeature(), URL_AVATAR + o.getAvatar(), 0,
                                    DateTimeUtil
                                            .millisNow(), o.getTag());
                    msgDao.insert(tmp);

                    /**
                     * 通知显示
                     */
                    showLog("收到message:" + new Gson().toJson(tmp));
//                    showToast(context, "收到message:" + new Gson().toJson(tmp));
                    //设置通知栏显示内容
                    mBuilder.setContentTitle("好友申请")//设置通知栏标题
                            .setContentText(
                                    o.getFamilyName() + o.getGivenName() + context.getString(
                                            R.string.request_to_be_your_friend));


                    Intent requestAddFriend_intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(context, 111, requestAddFriend_intent, 0);
                    mBuilder.setContentIntent(pendingIntent);

                    mNotificationManager.notify(111, mBuilder.build());
                    SharedPrefUtil.getInstance()
                            .saveData(share_new_msg_num, currenUnreadNews + 1);
                    EventBus.getDefault()
                            .postSticky(e);

                    break;

                case "deleteFriend":
                    /**
                     * 通知显示
                     */
                    showLog("收到message:" + extra);
//                    showToast(context, "收到message:" + extra);
                    //设置通知栏显示内容
                    mBuilder.setContentTitle("好友移除")//设置通知栏标题
                            .setContentText(
                                    o.getFamilyName() + o.getGivenName() +
                                            context.getString(R.string
                                                    .other_remove_you_from_friend_list));


                    Intent requestAddFriend_intent4 = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent4 =
                            PendingIntent.getActivity(context, 222, requestAddFriend_intent4, 0);
                    mBuilder.setContentIntent(pendingIntent4);

                    mNotificationManager.notify(222, mBuilder.build());

                    //更新好友列表
                    e.flag = 3;
                    EventBus.getDefault()
                            .postSticky(e);
                    showLog("deleteFriend e.flag = 3 EventBus.getDefault().postSticky(e)");

                    break;

                case "acceptFriend":
                    /**
                     * 更新本地数据库
                     */

                    QueryBuilder<Msg> qb = msgDao.queryBuilder();
                    qb.where(MsgDao.Properties.Tag.eq(o.getTag()));
                    List<Msg> results = qb.list();
                    if (results.size() == 0) {
                        return;
                    }

                    //更新msg
                    Msg tmp2 = results.get(0);
                    tmp2.setStatus(1);//同意
                    msgDao.update(tmp2);

                    /**
                     * 通知显示
                     */
                    showLog("收到message:" + extra);
//                    showToast(context, "收到message:" + extra);
                    //设置通知栏显示内容
                    mBuilder.setContentTitle("好友申请")//设置通知栏标题
                            .setContentText(
                                    o.getFamilyName() + o.getGivenName() +
                                            context.getString(R.string
                                                    .other_accept_your_friend_request));


                    Intent requestAddFriend_intent2 = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent2 =
                            PendingIntent.getActivity(context, 333, requestAddFriend_intent2, 0);
                    mBuilder.setContentIntent(pendingIntent2);

                    mNotificationManager.notify(333, mBuilder.build());
                    SharedPrefUtil.getInstance()
                            .saveData(share_new_msg_num, currenUnreadNews + 1);
                    //更新未读消息
                    EventBus.getDefault()
                            .postSticky(e);
                    //更新好友列表
                    e.flag = 2;
                    EventBus.getDefault()
                            .postSticky(e);
                    break;

                case "rejectFriend":
                    /**
                     * 更新本地数据库
                     */

                    QueryBuilder<Msg> qb2 = msgDao.queryBuilder();
                    qb2.where(MsgDao.Properties.Tag.eq(o.getTag()));
                    List<Msg> results2 = qb2.list();
                    if (results2.size() == 0) {
                        return;
                    }

                    //更新msg
                    Msg tmp3 = results2.get(0);
                    tmp3.setStatus(2);//拒绝
                    msgDao.update(tmp3);

                    /**
                     * 通知显示
                     */
                    showLog("收到message:" + extra);
//                    showToast(context, "收到message:" + extra);
                    //设置通知栏显示内容
                    mBuilder.setContentTitle("好友申请")//设置通知栏标题
                            .setContentText(
                                    o.getFamilyName() + o.getGivenName() +
                                            context.getString(R.string
                                                    .other_reject_your_friend_request));


                    Intent requestAddFriend_intent3 = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent3 =
                            PendingIntent.getActivity(context, 444, requestAddFriend_intent3, 0);
                    mBuilder.setContentIntent(pendingIntent3);

                    mNotificationManager.notify(444, mBuilder.build());
                    SharedPrefUtil.getInstance()
                            .saveData(share_new_msg_num, currenUnreadNews + 1);
                    EventBus.getDefault()
                            .postSticky(e);

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

//
//        EventBus.getDefault()
//                .unregister(this);
    }

    public NotificationCompat.Builder NotificationUtil(Context context) {


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder
                .setTicker(" 小喵白板通知来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);

        return mBuilder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateUnreadMsgNum(Event e) {
    }
}
