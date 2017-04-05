package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.RegisterJson;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NO_TOKEN_FOUND;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_UPDATE_SETTINGS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO3;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO4;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO5;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password_new;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_login_password_old;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.update_password;
import static cn.edu.hfut.lilei.shareboard.utils.StringUtil.isValidPassword;


public class AlterPasswordActivity extends SwipeBackActivity {
    //控件
    private Button mBtnSave;
    private SwipeBackLayout mSwipeBackLayout;
    private EditText mEtOldPwd, mEtNewPwd, mEtConfirmPwd;
    private LodingDialog.Builder mlodingDialog;
    //数据

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_myinfo_alterpassword);
        init();


    }

    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_alterpassword_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        mSwipeBackLayout = getSwipeBackLayout();
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

        mEtOldPwd = (EditText) findViewById(R.id.et_alterpassword_oldpassword);
        mEtNewPwd = (EditText) findViewById(R.id.et_alterpassword_newpassword);
        mEtConfirmPwd = (EditText) findViewById(R.id.et_alterpassword_confirmpassword);

        mBtnSave = (Button) findViewById(R.id.btn_alterpassword_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String oldPwd = mEtOldPwd.getText()
                        .toString()
                        .trim();
                final String newPwd = mEtNewPwd.getText()
                        .toString()
                        .trim();
                final String confirmPwd = mEtConfirmPwd.getText()
                        .toString()
                        .trim();
                mlodingDialog = loding(mContext, R.string.sending);


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
                         * 2.加载token和email
                         */
                        String token = (String) SharedPrefUtil.getInstance()
                                .getData(share_token, "空");

                        //如果没有token,跳转到登录界面
                        if (token.equals("空")) {
                            return -2;
                        }
                        String email = (String) SharedPrefUtil.getInstance()
                                .getData(share_user_email, "空");

                        //如果没有email
                        if (token.equals("空")) {
                            return -2;
                        }
                        if (!isValidPassword(oldPwd)) {

                            return WRONG_FORMAT_INPUT_NO1;
                        }
                        if (!isValidPassword(newPwd)) {

                            return WRONG_FORMAT_INPUT_NO2;
                        }
                        if (!isValidPassword(confirmPwd)) {

                            return WRONG_FORMAT_INPUT_NO3;
                        }
                        if (!confirmPwd.equals(newPwd)) {

                            return WRONG_FORMAT_INPUT_NO4;
                        }
                        if (oldPwd.equals(newPwd)) {

                            return WRONG_FORMAT_INPUT_NO5;
                        }
                        String oldPwdErypt = StringUtil.getMD5(oldPwd);
                        String newPwdErypt = StringUtil.getMD5(newPwd);
                        if (oldPwdErypt == null || newPwdErypt == null) {
                            return -1;
                        }
                        /**
                         * 3.发送密码数据
                         */

                        OkGo.post(URL_UPDATE_SETTINGS)
                                .tag(this)
                                .params(post_need_feature, update_password)
                                .params(post_user_email, email)
                                .params(post_token, token)
                                .params(post_user_login_password_old, oldPwdErypt)
                                .params(post_user_login_password_new, newPwdErypt)
                                .execute(new JsonCallback<RegisterJson>() {
                                             @Override
                                             public void onSuccess(RegisterJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {

                                                     /**
                                                      * 清除本地 token
                                                      */

                                                     SharedPrefUtil.getInstance()
                                                             .deleteData(share_token);
                                                     /**
                                                      * 跳到登录界面
                                                      */
                                                     mlodingDialog.cancle();
                                                     showToast(mContext, o.getMsg());
//                                                     Intent intent = new Intent();
//                                                     intent.setClass(AlterPasswordActivity.this,
//                                                             LoginActivity.class);
//                                                     startActivity(intent);
//                                                     finish();

                                                 } else {
                                                     //提示所有错误
                                                     mlodingDialog.cancle();
                                                     showToast(mContext, o.getMsg());
                                                 }

                                             }

                                             @Override
                                             public void onError(Call call, Response response,
                                                                 Exception e) {
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
                            case NO_TOKEN_FOUND:
                                //没有可用本地token,跳转到登录界面
                                Intent intent = new Intent();
                                intent.setClass(AlterPasswordActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case WRONG_FORMAT_INPUT_NO1:
                                showToast(mContext, R.string.can_not_recognize_old_pwd);
                                break;
                            case WRONG_FORMAT_INPUT_NO2:
                                showToast(mContext, R.string.can_not_recognize_new_pwd);
                                break;
                            case WRONG_FORMAT_INPUT_NO3:
                                showToast(mContext, R.string.can_not_recognize_confirm_pwd);
                                break;
                            case WRONG_FORMAT_INPUT_NO4:
                                showToast(mContext, R.string.new_pwd_diff_confirm_pwd);
                                break;
                            case WRONG_FORMAT_INPUT_NO5:
                                showToast(mContext, R.string.new_pwd_equal_old_pwd);
                                break;
                            case -1:
                                break;
                            case -2:
                                showToast(mContext, R.string.please_relogin);
                                break;
                            default:
//                                    showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();
            }

        });
    }
}
