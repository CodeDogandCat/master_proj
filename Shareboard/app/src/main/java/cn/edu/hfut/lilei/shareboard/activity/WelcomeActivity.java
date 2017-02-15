package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import cn.edu.hfut.lilei.shareboard.R;


public class WelcomeActivity extends Activity {
    private static final int FAILURE = 0; // 失败
    private static final int SUCCESS = 1; // 成功
    private static final int OFFLINE = 2; // 如果支持离线阅读，进入离线模式
    private static final int SHOW_TIME_MIN = 800;//最短跳转时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        /**
         *
         */
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int result;
                long startTime = System.currentTimeMillis();
                result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        //线程休眠等待
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, MainActivity.class);
//                intent.setClass(WelcomeActivity.this, LoginActivity.class);
//                intent.setClass(WelcomeActivity.this, RegisterActivity.class);
//                intent.setClass(WelcomeActivity.this, ResetPasswordActivity.class);
//                intent.setClass(WelcomeActivity.this, SetUserInfoActivity.class);
//                intent.setClass(WelcomeActivity.this, ContactsFragment.class);
                startActivity(intent);
                finish();
            };
        }.execute(new Void[]{});
    }

    /**
     *
     * @return
     */
    private int loadingCache() {
//        if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
//            return OFFLINE;
//        }
        return SUCCESS;
    }
}
