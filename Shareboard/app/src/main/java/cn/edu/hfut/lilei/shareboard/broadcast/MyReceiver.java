package cn.edu.hfut.lilei.shareboard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.edu.hfut.lilei.shareboard.activity.MainActivity;
import cn.jpush.android.api.JPushInterface;

public class MyReceiver extends BroadcastReceiver {
    private Bundle bundle_startmeeting = new Bundle();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String message2 = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Toast.makeText(context, "msg title:" + title + "content:" + message + "@@@:" + message2,
                    Toast.LENGTH_LONG)
                    .show();
//            showToast(context, "收到消息");


            /**
             * 存入本地数据库
             */

            /**
             * 状态栏显示
             */

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
