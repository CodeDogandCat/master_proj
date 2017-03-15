package cn.edu.hfut.lilei.shareboard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.view.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class MeetingActivity extends Activity {
    //控件
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPm;
    private WebView mWvCanvas;
    private LodingDialog.Builder mlodingDialog;

    //数据

    //上下文参数
    private Context mContext;
    private int check_in_type = -1;
    private int meeting_id = -1;
    private long meeting_url = -1L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_meeting);
        init();


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mContext = this;
        /**
         * 设置webview
         */
        mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SCREEN_DIM_WAKE_LOCK");
        mWvCanvas = (WebView) findViewById(R.id.wv_meeting_canvas);

        WebSettings webSettings = mWvCanvas.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        WebView.setWebContentsDebuggingEnabled(true);
        mWvCanvas.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWvCanvas.requestFocus();
        mWvCanvas.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
//              return super.shouldOverrideUrlLoading(view, url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载网页时处理 如：显示"加载提示" 的加载对话框
                mlodingDialog = loding(mContext, R.string.loding);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 网页加载完成时处理  如：让 加载对话框 消失
                mlodingDialog.cancle();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载网页失败时处理  如：
                view.loadDataWithBaseURL(null,
                        "<span style=\"color:#FF0000\">加载失败</span>",
                        "text/html",
                        "utf-8",
                        null);
                finish();
            }
        });
        /**
         * 加载本地数据
         */
        check_in_type = getIntent().getExtras()
                .getInt("check_in_type");

        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        keyList.add(share_token);
        keyList.add(share_user_email);
        keyList.add(share_family_name);
        keyList.add(share_given_name);
        meeting_url = (long) SharedPrefUtil.getInstance()
                .getData(share_meeting_url, -1L);

        if (check_in_type == 2) {//host
            meeting_id = (int) SharedPrefUtil.getInstance()
                    .getData(share_meeting_id, -1);

        }

        valueList = SharedPrefUtil.getInstance()
                .getStringDatas(keyList);
        if (valueList != null && meeting_url != -1L) {
            String params = "";
            if (check_in_type == 2 && meeting_id != -1) {//host

                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_meeting_id + "=" + meeting_id + "&" +
                        post_meeting_url + "=" + meeting_url;

            } else {
                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_meeting_url + "=" + meeting_url;
            }
            mWvCanvas.loadUrl(URL_MEETING + params);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //常亮锁
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
    }
}
