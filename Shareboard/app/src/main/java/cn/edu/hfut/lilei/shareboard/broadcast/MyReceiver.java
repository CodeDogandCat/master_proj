package cn.edu.hfut.lilei.shareboard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.edu.hfut.lilei.shareboard.activity.MainActivity;
import cn.jpush.android.api.JPushInterface;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;

public class MyReceiver extends BroadcastReceiver {
    private Bundle bundle_startmeeting = new Bundle();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()
                .equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
//            Bundle bundle = intent.getExtras();
//            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String message2 = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Toast.makeText(context, "msg title:" + title + "content:" + message+"@@@:"+message2, Toast.LENGTH_LONG).show();
            showToast(context, "收到消息");

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
