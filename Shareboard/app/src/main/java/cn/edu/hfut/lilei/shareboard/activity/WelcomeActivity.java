package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SettingUtil;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SHOW_TIME_MIN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;


public class WelcomeActivity extends Activity {

    //上下文参数
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        init();
    }


    private void init() {
        mContext = this;

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                /**
                 * 1.检查网络状态并提醒
                 */
                if (!NetworkUtil.isNetworkConnected(mContext)) {
                    //网络连接不可用
                    return NET_DISCONNECT;
                }
                /**
                 * 2.检查登录状态，加载缓存
                 */
                long startTime = System.currentTimeMillis();
                loadingCache();


                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        //线程休眠等待
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return SUCCESS;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                switch (integer) {
                    case NET_DISCONNECT:
                        //弹出对话框，让用户开启网络
                        NetworkUtil.setNetworkMethod(mContext);
                        break;
                    case SUCCESS:
                        Intent intent = new Intent();
//                        intent.setClass(WelcomeActivity.this, RegisterActivity.class);
                        intent.setClass(WelcomeActivity.this, MainActivity.class);
//                        intent.setClass(WelcomeActivity.this, LoginActivity.class);
//                        intent.setClass(WelcomeActivity.this, ResetPasswordActivity.class);
//                        intent.setClass(WelcomeActivity.this, SetUserInfoActivity.class);
//                        intent.setClass(WelcomeActivity.this, ContactsFragment.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        break;
                }
            }
        }.execute();


    }

    /**
     * @return
     */
    private int loadingCache() {
//        if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
//            return OFFLINE;
//        }
        return SettingUtil.SUCCESS;
    }
}
