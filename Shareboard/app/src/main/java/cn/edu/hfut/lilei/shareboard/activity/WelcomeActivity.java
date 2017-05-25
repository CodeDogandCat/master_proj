package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NO_TOKEN_FOUND;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_CHECK_TOKEN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;


public class WelcomeActivity extends Activity {

    private LodingDialog.Builder mlodingDialog;

    private boolean shouldCallUpdate;

    //上下文参数
    private Context mContext;

    @Override
    protected void onStop() {
        super.onStop();
        shouldCallUpdate = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (shouldCallUpdate) {
            init();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        shouldCallUpdate = false;
        init();
    }


    private void init() {
        mContext = this;
        mlodingDialog = loding(mContext, R.string.loding);
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
                 * 2.加载token
                 */
                String token = (String) SharedPrefUtil.getInstance()
                        .getData(share_token, "空");

                //如果没有token,跳转到登录界面
                if (token.equals("空")) {
                    return NO_TOKEN_FOUND;
                }
                //如果有,带着token去服务器比较

                OkGo.post(URL_CHECK_TOKEN)
                        .tag(this)
                        .params(post_token, token)
                        .execute(new JsonCallback<CommonJson>() {
                            @Override
                            public void onSuccess(CommonJson o, Call call,
                                                  Response response) {
                                if (o != null) {
                                    /**
                                     * 3.本地token过期，删除
                                     */
                                    SharedPrefUtil.getInstance()
                                            .deleteData(share_token);

                                    mlodingDialog.cancle();
                                    /**
                                     * 4.跳到登录界面
                                     */
                                    Intent intent = new Intent();
                                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                    beforeFinish();
                                    finish();
                                } else {
                                    /**
                                     * 5.自动登录成功,不会返回可打印信息,直接跳转
                                     */
                                    mlodingDialog.cancle();
                                    Intent intent = new Intent();
                                    intent.setClass(WelcomeActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                    beforeFinish();
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                mlodingDialog.cancle();
//                                showToast(mContext, R.string.system_error);
                            }
                        });

                return -1;


            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
//                mlodingDialog.cancle();
                switch (integer) {
                    case NET_DISCONNECT:
                        //弹出对话框，让用户开启网络
                        NetworkUtil.setNetworkMethod(mContext);
                        break;
                    case NO_TOKEN_FOUND:
                        //没有可用本地token,跳转到登录界面
                        Intent intent = new Intent();
                        intent.setClass(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:

                        break;
                }
            }
        }.execute();


    }

    public void beforeFinish() {
        getWindow().setFlags(WindowManager
                        .LayoutParams
                        .FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

}
