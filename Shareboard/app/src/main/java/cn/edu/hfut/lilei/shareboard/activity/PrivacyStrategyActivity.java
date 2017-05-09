package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import cn.edu.hfut.lilei.shareboard.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_PRIVACY_STRATEGY;


public class PrivacyStrategyActivity extends SwipeBackActivity {

    private ImageView mBtnBack;
    private WebView wv;
    //数据
    //上下文参数
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_strategy);
        init();

    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        wv = (WebView) findViewById(R.id.wv_privacy);
        mBtnBack = (ImageView) findViewById(R.id.img_privacy_goback);
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

        WebSettings webSettings = wv.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        WebView.setWebContentsDebuggingEnabled(true);

        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setBackgroundColor(getResources().getColor(R.color.white));//背景透明
        wv.requestFocus();
        wv.setVisibility(View.GONE);
        wv.loadUrl(URL_PRIVACY_STRATEGY);
        wv.setVisibility(View.VISIBLE);// 加载完之后进行设置显示，以免加载时初始化效果不好看


    }


}
