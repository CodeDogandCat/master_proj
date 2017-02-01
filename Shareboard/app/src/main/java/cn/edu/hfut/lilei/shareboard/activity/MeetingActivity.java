package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.edu.hfut.lilei.shareboard.R;


public class MeetingActivity extends Activity {
    private WebView mWvCanvas;
    private PowerManager mPm;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        init();


    }

    private void init() {
        mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SCREEN_DIM_WAKE_LOCK");
        mWvCanvas = (WebView) findViewById(R.id.wv_meeting_canvas);
        WebSettings webSettings = mWvCanvas.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        mWvCanvas.loadUrl("http://192.168.124.7:55151");
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //常亮锁
        mWakeLock.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWakeLock.release();
        super.onPause();
    }
}
