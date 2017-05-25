package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.JsonEnity.UpdateAppJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.id.ll_about_private;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.LOCAL_VERSION;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_UPGRADE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_VERSION_UPDATE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class SettingsAboutActivity extends SwipeBackActivity implements View.OnClickListener {
    //控件
    private LinearLayout mLlVersion, mLlFeedback, mLlRecommend, mLlPrivate;
    private TextView mTvVersion, mTvFeedBack, mTvRecommend, mTvPrivate, mTvCurrentVersion;
    private ImageView next1, next2, next3, next4;
    private LodingDialog.Builder mlodingDialog;
    private AlertDialog.Builder mDialog;
    //数据
    private String current_version = "";

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
        init();

    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_about_goback);
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
        mLlVersion = (LinearLayout) findViewById(R.id.ll_about_version_update);
        mLlFeedback = (LinearLayout) findViewById(R.id.ll_about_feedback);
        mLlRecommend = (LinearLayout) findViewById(R.id.ll_about_recommend);
        mLlPrivate = (LinearLayout) findViewById(ll_about_private);

        mTvVersion = (TextView) findViewById(R.id.tv_about_version);
        mTvCurrentVersion = (TextView) findViewById(R.id.tv_about_current_version);
        mTvFeedBack = (TextView) findViewById(R.id.tv_about_feedback);
        mTvRecommend = (TextView) findViewById(R.id.tv_about_recommend);
        mTvPrivate = (TextView) findViewById(R.id.tv_about_private);

        current_version = (String) SharedPrefUtil.getInstance()
                .getData(LOCAL_VERSION, "1.0.0");
        mTvCurrentVersion.setText(current_version);


        next1 = (ImageView) findViewById(R.id.img_about_next1);
        next2 = (ImageView) findViewById(R.id.img_about_next2);
        next3 = (ImageView) findViewById(R.id.img_about_next3);
        next4 = (ImageView) findViewById(R.id.img_about_next4);

        mLlVersion.setOnClickListener(this);
        mLlFeedback.setOnClickListener(this);
        mLlRecommend.setOnClickListener(this);
        mLlPrivate.setOnClickListener(this);


        new TouchListener.Builder(mContext).setLinearLayout(mLlVersion)
                .setTextView1(mTvVersion)
                .setImageView(next1)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlFeedback)
                .setTextView1(mTvFeedBack)
                .setImageView(next2)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlRecommend)
                .setTextView1(mTvRecommend)
                .setImageView(next3)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlPrivate)
                .setTextView1(mTvPrivate)
                .setImageView(next4)
                .create();

    }


    /**
     * 设置监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_about_private:
                startActivity(new Intent(mContext, PrivacyStrategyActivity.class));
                break;
            case R.id.ll_about_feedback:
                startActivity(new Intent(mContext, FeedbackActivity.class));
                break;

            case R.id.ll_about_version_update://检查版本更新
                checkUpgrade();
                break;

        }
    }

    /**
     * 检查版本更新
     */
    public void checkUpgrade() {

        mlodingDialog = loding(mContext, R.string.get_version_info);
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
                 * 3.发送数据
                 */

                OkGo.post(URL_VERSION_UPDATE)
                        .tag(this)
                        .params(post_token, token)
                        .params(post_user_email, email)
                        .execute(new JsonCallback<UpdateAppJson>() {
                                     @Override
                                     public void onSuccess(UpdateAppJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             String server_version = o.getData()
                                                     .getServerVersion();
                                             mlodingDialog.cancle();
//                                             showToast(mContext, o.getMsg() + " 服务器最新版本: " + server_version);
                                             //比较服务器最新版本号和当前已安装版本号
                                             if (StringUtil.appVersionCompare(server_version,
                                                     current_version) <= 0) {
                                                 //提示所有错误
                                                 mlodingDialog.cancle();
//                                             showToast(mContext, o.getMsg());
                                                 showLog("StringUtil.appVersionCompare无更新");
                                                 MyAppUtil.noneUpdate(mContext);
                                                 return;
                                             }


                                             int isForce = o.data.getLastForce();//是否需要强制更新
                                             String downUrl = URL_UPGRADE + o.data.getUpdateurl();
                                             //apk下载地址
                                             String updateinfo = o.data.getUpgradeinfo();//apk更新详情
                                             String appName = o.data.getAppname();
                                             showLog("################################");
                                             showLog("isforce " + isForce);
                                             showLog("downUrl " + downUrl);
                                             showLog("updateinfo " + updateinfo);
                                             showLog("updateinfo " + appName);
                                             showLog("################################");

                                             if (isForce == 1) {//强制更新
                                                 showLog("#################强制更新");
                                                 MyAppUtil.forceUpdate(mContext, appName, downUrl,
                                                         updateinfo);
                                             } else {//非强制更新
                                                 //正常升级
                                                 showLog("#################非强制更新");
                                                 MyAppUtil.normalUpdate(mContext, appName, downUrl,
                                                         updateinfo);
                                             }

                                         } else {
                                             //提示所有错误
                                             mlodingDialog.cancle();
//                                             showToast(mContext, o.getMsg());
                                             showLog("#################无更新");
                                             MyAppUtil.noneUpdate(mContext);
                                         }

                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         mlodingDialog.cancle();
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


}
