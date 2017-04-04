package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.utils.CountDownTimerUtils;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_SEND_VERIFY_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_check_verify_code;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class RegisterActivity extends SwipeBackActivity {
    //控件
    private EditText mEtEmail;
    private EditText mEtSendVerifyCode;
    private TextView mTvSendVerifyCode;
    private Button mBtnNextstep;
    private LodingDialog.Builder mlodingDialog;

    //上下文参数
    private Context mContext;
    private String userEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();


    }

    private void init() {
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
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
        mEtEmail = (EditText) findViewById(R.id.et_register_email);
        mTvSendVerifyCode = (TextView) findViewById(R.id.tv_register_send_verify_code);
        mEtSendVerifyCode = (EditText) findViewById(R.id.et_register_verify_code);
        mBtnNextstep = (Button) findViewById(R.id.btn_register_nextstep);
        //验证邮箱，发送验证码
        mTvSendVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEtEmail.getText()
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
                         * 2.检查用户输入格式
                         */

                        if (StringUtil.isEmpty(email) || !StringUtil.isEmail(email)) {
                            //邮箱格式不对
                            return WRONG_FORMAT_INPUT;
                        }

                        /**
                         * 3.检查是否已经注册过,发送验证码
                         */

                        OkGo.post(URL_SEND_VERIFY_CODE)
                                .tag(this)
                                .params(post_user_email, email)
                                .execute(new JsonCallback<CommonJson>() {
                                             @Override
                                             public void onSuccess(CommonJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {

                                                     /**
                                                      * 缓存 email
                                                      */

                                                     SharedPrefUtil.getInstance()
                                                             .saveData(share_user_email, email);

                                                     //2分钟后可以重新发送验证码
                                                     CountDownTimerUtils mCountDownTimerUtils = new
                                                             CountDownTimerUtils(mContext, mTvSendVerifyCode,
                                                             mContext.getResources()
                                                                     .getString(
                                                                             R.string
                                                                                     .resend_verify_code_later),
                                                             120000, 1000);
                                                     mCountDownTimerUtils.start();
                                                     mlodingDialog.cancle();
                                                     //验证码发送成功
                                                     showToast(mContext, o.getMsg());


                                                 } else {
                                                     //提示所有错误
                                                     mlodingDialog.cancle();
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
                            case WRONG_FORMAT_INPUT:
                                //提示邮箱格式不对
                                showToast(mContext, R.string.can_not_recognize_email);
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
        //比较验证码，跳转到下一页面
        mBtnNextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlodingDialog = loding(mContext, R.string.checking);
                final String verifyCode = mEtSendVerifyCode.getText()
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

                        if (StringUtil.isEmpty(verifyCode) || !StringUtil.isNumeric(verifyCode) ||
                                (StringUtil.length(verifyCode) != 6)) {
                            //验证码格式不对
                            return WRONG_FORMAT_INPUT;
                        }
                        OkGo.post(URL_SEND_VERIFY_CODE)
                                .tag(this)
                                .params(post_check_verify_code, verifyCode)
                                .execute(new JsonCallback<CommonJson>() {
                                    @Override
                                    public void onSuccess(CommonJson o, Call call,
                                                          Response response) {
                                        if (o.getCode() == SUCCESS) {
                                            //验证码发匹配正确
                                            mlodingDialog.cancle();
                                            /**
                                             * 2.跳转
                                             */
                                            Intent intent = new Intent();
                                            intent.setClass(RegisterActivity.this,
                                                    SetUserInfoActivity.class);
                                            startActivity(intent);


                                        } else {
                                            mlodingDialog.cancle();
                                            showLog(o.getMsg());
                                            //提示所有错误
                                            showToast(mContext, o.getMsg());
                                        }
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        super.onError(call, response, e);
                                        mlodingDialog.cancle();
                                        showToast(mContext, R.string.system_error);
                                    }
                                });


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
                            case WRONG_FORMAT_INPUT:
                                //提示邮箱格式不对
                                showToast(mContext, R.string.can_not_recognize_email);
                                break;

                            default:
                                break;
                        }
                    }
                }.execute();


            }
        });

    }
}
