package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FEEDBACK;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.feedback;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_message_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class FeedbackActivity extends SwipeBackActivity {

    private ImageView mBtnBack;
    private Button mBtnSend;
    private EditText editText;
    private TextView textView;
    private LodingDialog.Builder mlodingDialog;
    //数据
    //上下文参数
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();

    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_feedback_goback);
        mBtnSend = (Button) findViewById(R.id.btn_feedback_send);
        editText = (EditText) findViewById(R.id.et_feedback);
        textView = (TextView) findViewById(R.id.tv_feedback);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        //右滑返回
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

        final int maxNum = 150;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText("剩余字数：" + (maxNum - s.length()));
            }
        });


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = editText.getText()
                        .toString()
                        .trim();
                if (content.length() < 20) {
                    showToast(mContext, getString(R.string.content_min_20));
                    return;
                }
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

                        /**
                         * 3.发送密码数据
                         */

                        OkGo.post(URL_FEEDBACK)
                                .tag(this)
                                .params(post_need_feature, feedback)
                                .params(post_token, token)
                                .params(post_user_email, email)
                                .params(post_message_data, content)
                                .execute(new JsonCallback<CommonJson>() {
                                             @Override
                                             public void onSuccess(CommonJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {

                                                     mlodingDialog.cancle();
                                                     showToast(mContext, o.getMsg());
                                                     finish();

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
//                                                 showToast(mContext, R.string.system_error);
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
                            case -1:
                                break;
                            case -2:
                                showToast(mContext, R.string.please_relogin);
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
