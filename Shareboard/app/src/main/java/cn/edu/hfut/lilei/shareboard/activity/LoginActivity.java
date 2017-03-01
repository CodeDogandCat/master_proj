package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.Common;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.string.familyName;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_LOGIN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_client_key;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;


public class LoginActivity extends Activity {
    //控件
    private LinearLayout mLlBottomlineofemail;
    private LinearLayout mLlBottomlineofpass;
    private ImageView mImgEmail;
    private ImageView mImgPassword;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegisteraccount;
    private Button mBtnResetpassword;
    //数据

    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init() {
        mContext = this;
        mImgEmail = (ImageView) findViewById(R.id.img_login_email);
        mImgPassword = (ImageView) findViewById(R.id.img_login_password);
        mEtEmail = (EditText) findViewById(R.id.et_login_email);
        mEtPassword = (EditText) findViewById(R.id.et_login_password);
        mLlBottomlineofemail = (LinearLayout) findViewById(R.id.ll_login_bottomlineofemail);
        mLlBottomlineofpass = (LinearLayout) findViewById(R.id.ll_login_bottomlineofpass);
        mBtnRegisteraccount = (Button) findViewById(R.id.btn_login_registeraccount);
        mBtnResetpassword = (Button) findViewById(R.id.btn_login_resetpassword);
        mBtnLogin = (Button) findViewById(R.id.btn_login_login);
        mEtEmail.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofemail.setBackgroundColor(
                            getResources().getColor(R.color.my_yellow));
                    ImageUtil.load(mContext, R.drawable.ic_yellow_34, mImgEmail);
                } else {

                    // 失去焦点
                    mLlBottomlineofemail.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_34, mImgEmail);
                }

            }


        });
        mEtPassword.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    // 获得焦点
                    mLlBottomlineofpass.setBackgroundColor(
                            getResources().getColor(R.color.my_yellow));
                    ImageUtil.load(mContext, R.drawable.ic_yellow_32, mImgPassword);
                } else {

                    // 失去焦点
                    mLlBottomlineofpass.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_32, mImgPassword);
                }

            }


        });
        //注册
        mBtnRegisteraccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //重置密码
        mBtnResetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        //登录
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEtEmail.getText()
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

                        if (!StringUtil.isEmail(email) || StringUtil.isEmpty(email)) {
                            //邮箱格式不对
                            return WRONG_FORMAT_INPUT_NO1;
                        }
                        if (!StringUtil.isValidPassword(password)) {
                            //密码格式不对
                            return WRONG_FORMAT_INPUT_NO2;
                        }
                        /**
                         * 3.上传用户数据
                         */
//                        showLog("加密前的密码：" + password);
                        String passEncrypted = StringUtil.getMD5(password);
                        if (passEncrypted == null) {
                            return -1;
                        }
//                        showLog("加密后的密码：" + passEncrypted);
                        OkGo.post(URL_LOGIN)
                                .tag(this)
                                .params(post_user_email, email)
                                .params(post_user_family_name, familyName)
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
                                //提示姓邮箱格式不对
                                showToast(mContext, R.string.can_not_recognize_email);
                                break;
                            case WRONG_FORMAT_INPUT_NO2:
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
