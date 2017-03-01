package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.Common;
import cn.edu.hfut.lilei.shareboard.utils.InstallationIdUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_SAVE_USR_INFO;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO3;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_client_key;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class SetUserInfoActivity extends SwipeBackActivity {
    //控件
    private EditText mEtFamilyName;
    private EditText mEtGivenName;
    private EditText mEtPassword;
    private Button mBtnComplete;

    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
        init();


    }

    private void init() {
        mContext = this;
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow),
                SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
            }
        });
        mBtnComplete = (Button) findViewById(R.id.btn_setuserinfo_complete);
        mEtFamilyName = (EditText) findViewById(R.id.et_setuserinfo_family_name);
        mEtGivenName = (EditText) findViewById(R.id.et_setuserinfo_given_name);
        mEtPassword = (EditText) findViewById(R.id.et_setuserinfo_password);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String familyName = mEtFamilyName.getText()
                        .toString();
                final String givenName = mEtGivenName.getText()
                        .toString();
                final String password = mEtPassword.getText()
                        .toString();
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
                         * 2.检查用户输入格式
                         */

                        if (!StringUtil.isValidName(familyName)) {
                            //姓格式不对
                            return WRONG_FORMAT_INPUT_NO1;
                        }

                        if (!StringUtil.isValidName(givenName)) {
                            //名格式不对
                            return WRONG_FORMAT_INPUT_NO2;
                        }
                        if (!StringUtil.isValidPassword(password)) {
                            //密码格式不对
                            return WRONG_FORMAT_INPUT_NO3;
                        }
                        /**
                         * 3.上传用户数据
                         */
                        showLog("加密前的密码：" + password);
                        String passEncrypted = StringUtil.getMD5(password);
                        if (passEncrypted == null) {
                            return -1;
                        }
                        showLog("加密后的密码：" + passEncrypted);
                        OkGo.post(URL_SAVE_USR_INFO)
                                .tag(this)
                                .params(post_user_email, (String) SharedPrefUtil.getInstance()
                                        .getData(share_user_email, ""))
                                .params(post_user_family_name, familyName)
                                .params(post_user_given_name, givenName)
                                .params(post_user_login_password, passEncrypted)
                                .params(post_user_client_key, InstallationIdUtil.id(mContext))
                                .execute(new JsonCallback<Common>() {
                                    @Override
                                    public void onSuccess(Common o, Call call,
                                                          Response response) {
                                        if (o.getCode() == SUCCESS) {
                                            /**
                                             * 4.注册成功
                                             */

                                            showToast(mContext, o.getMsg());
                                            /**
                                             * 5.保存token 到本地
                                             */
                                            SharedPrefUtil.getInstance()
                                                    .saveData(share_token, o.getMsg());
                                            /**
                                             * 6.跳转
                                             */
                                            Intent intent = new Intent();
                                            intent.setClass(SetUserInfoActivity.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                            finish();


                                        } else {
                                            //提示所有错误
                                            showToast(mContext, o.getMsg());
                                        }
                                    }
                                });


                        return -1;

                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        switch (integer) {
                            case NET_DISCONNECT:
                                //弹出对话框，让用户开启网络
                                NetworkUtil.setNetworkMethod(mContext);
                                break;
                            case WRONG_FORMAT_INPUT_NO1:
                                //提示姓格式不对
                                showToast(mContext, R.string.can_not_recognize_family_name);
                                break;
                            case WRONG_FORMAT_INPUT_NO2:
                                //提示名格式不对
                                showToast(mContext, R.string.can_not_recognize_given_name);
                                break;
                            case WRONG_FORMAT_INPUT_NO3:
                                //提示登录密码格式不对
                                showToast(mContext, R.string.can_not_recognize_login_password);
                                break;
                            case -1:
                                break;

                            default:
                                showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();

            }
        });

    }
}
