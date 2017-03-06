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
import cn.edu.hfut.lilei.shareboard.models.Login;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.view.LodingDialog;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_LOGIN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


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
    private LodingDialog.Builder mlodingDialog;
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
                    ImageUtil.load(mContext, R.drawable.ic_yellow_34_small,
                            R.drawable.ic_yellow_34_small,
                            mImgEmail);
                } else {

                    // 失去焦点
                    mLlBottomlineofemail.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_34,
                            R.drawable.ic_white_34, mImgEmail);
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
                    ImageUtil.load(mContext, R.drawable.ic_yellow_32, R.drawable
                            .ic_yellow_32, mImgPassword);
                } else {

                    // 失去焦点
                    mLlBottomlineofpass.setBackgroundColor(
                            getResources().getColor(R.color.my_lightgray));
                    ImageUtil.load(mContext, R.drawable.ic_white_32, R.drawable
                            .ic_yellow_32, mImgPassword);
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
                mlodingDialog = loding(mContext, R.string.logingin);
                final String email = mEtEmail.getText()
                        .toString()
                        .trim();
                final String password = mEtPassword.getText()
                        .toString()
                        .trim();
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
                                .params(post_user_login_password, passEncrypted)
                                .execute(new JsonCallback<Login>() {
                                             @Override
                                             public void onSuccess(Login o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {
                                                     /**
                                                      * 4.登陆成功,缓存token,email,姓,名
                                                      */
                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_token, o.getData()
                                                                     .getToken()
                                                             );
                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_user_email, email);
                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_family_name, o.getData()
                                                                     .getFamilyName()
                                                             );
                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_given_name, o.getData()
                                                                     .getGivenName()
                                                             );
                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_avatar, URL_AVATAR + o
                                                                     .getData()
                                                                     .getAvatar());
                                                     mlodingDialog.cancle();

                                                     /**
                                                      * 5.跳转
                                                      */
                                                     Intent intent = new Intent();
                                                     intent.setClass(LoginActivity.this,
                                                             MainActivity.class);
                                                     startActivity(intent);
                                                     finish();


                                                 } else {
                                                     mlodingDialog.cancle();
                                                     //提示所有错误
                                                     showLog(o.getMsg());
                                                     showToast(mContext, o.getMsg());
                                                 }
                                             }

                                             @Override
                                             public void onError(Call call, Response response, Exception e) {
                                                 super.onError(call, response, e);
                                                 mlodingDialog.cancle();
                                                 showToast(mContext, R.string.system_error);
                                             }
                                         }

                                );


                        return -1;

                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        mlodingDialog.cancle();
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
//                            case -1:
//                                break;

                            default:
//                                showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();
            }
        });


    }
}
