package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.TileDrawable;
import cn.edu.hfut.lilei.shareboard.view.customdialog.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.R.color.yellow;


public class MeetingTestActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    //控件
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPm;
    private WebView mWvCanvas;
    private LodingDialog.Builder mlodingDialog;
    private TileDrawable mTileDrawable;
    private RadioGroup mRadioGroup;

    //数据
    //按钮的没选中显示的图标
    private int[] unselectedIconIds = {R.drawable.ic_white_04,
            R.drawable.ic_white_39, R.drawable.ic_white_48
    };
    //按钮的选中显示的图标
    private int[] selectedIconIds = {R.drawable.ic_yellow_04,
            R.drawable.ic_yellow_39, R.drawable.ic_yellow_48
    };

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

        setContentView(R.layout.activity_meeting_test);
//        final PinchImageView pinchImageView = (PinchImageView) findViewById(R.id.share_pic);
//        pinchImageView.post(new Runnable() {
//            @Override
//            public void run() {
//                mTileDrawable = new TileDrawable();
//                mTileDrawable.setInitCallback(new TileDrawable.InitCallback() {
//                    @Override
//                    public void onInit() {
//                        pinchImageView.setImageDrawable(mTileDrawable);
//                    }
//                });
//                String tmp = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() +
//                        "/" + R.drawable.card;
//                mTileDrawable.init(new HugeImageRegionLoader(MeetingTestActivity.this,
//                                Uri.parse(tmp)),
//                        new Point(pinchImageView.getWidth(), pinchImageView.getHeight()));
//            }
//        });
//        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_meeting);
//        mRadioGroup.setOnCheckedChangeListener(this);
//        selectPage(0);
//        init();


    }

    /**
     * 选择某页
     *
     * @param position 页面的位置
     */
    private void selectPage(int position) {
        //公共部分
        // 将所有的tab的icon变成灰色的
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            Drawable gray = getResources().getDrawable(unselectedIconIds[i]);
            gray.setBounds(0, 0, 60,
                    60);

            RadioButton child = (RadioButton) mRadioGroup.getChildAt(i);
            child.setCompoundDrawables(null, gray, null, null);
            child.setTextColor(getResources().getColor(
                    R.color.my_white));
        }
        // 改变图标
        Drawable press = getResources().getDrawable(selectedIconIds[position]);
        press.setBounds(0, 0, 60,
                60);
        RadioButton select = (RadioButton) mRadioGroup.getChildAt(position);
        select.setCompoundDrawables(null, press, null, null);
//        select.setTextColor(getResources().getColor(
//                R.color.my_yellow));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_meeting_members:
                selectPage(0);
                break;
            case R.id.btn_meeting_share:
                selectPage(1);
                break;
            case R.id.btn_meeting_lock:
                selectPage(2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mTileDrawable != null) {
            mTileDrawable.recycle();
        }
        super.onDestroy();
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    private void init() {
//        mContext = this;
//        /**
//         * 设置webview
//         */
//        mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SCREEN_DIM_WAKE_LOCK");
//        mWvCanvas = (WebView) findViewById(R.id.wv_meeting_canvas);
//
//        WebSettings webSettings = mWvCanvas.getSettings();
//        webSettings.setSaveFormData(false);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(false);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setAppCacheEnabled(false);
//        webSettings.setDefaultTextEncodingName("utf-8");
//        WebView.setWebContentsDebuggingEnabled(true);
//
//        mWvCanvas.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        mWvCanvas.setBackgroundColor(getResources().getColor(R.color.transparent));//背景透明
////        mWvCanvas.getBackground()
////                .setAlpha(0); // 设置填充透明度 范围：0-255
//        mWvCanvas.setBackgroundResource(R.drawable.bggg);
//        mWvCanvas.requestFocus();
//        mWvCanvas.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                // TODO Auto-generated method stub
////              return super.shouldOverrideUrlLoading(btnStart, url);
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                // 开始加载网页时处理 如：显示"加载提示" 的加载对话框
//                mlodingDialog = loding(mContext, R.string.loding);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                // 网页加载完成时处理  如：让 加载对话框 消失
//                mlodingDialog.cancle();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description,
//                                        String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//                // 加载网页失败时处理  如：
//                view.loadDataWithBaseURL(null,
//                        "<span style=\"color:#FF0000\">加载失败</span>",
//                        "text/html",
//                        "utf-8",
//                        null);
//                finish();
//            }
//        });
//
////        mWvCanvas.loadUrl(URL_TEST);
////        mWvCanvas.setVisibility(View.VISIBLE);// 加载完之后进行设置显示，以免加载时初始化效果不好看
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //常亮锁
//        mWakeLock.acquire();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mWakeLock.release();
//    }
}
