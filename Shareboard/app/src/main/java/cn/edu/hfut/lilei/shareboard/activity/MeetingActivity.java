package cn.edu.hfut.lilei.shareboard.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.ScreenUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.HugeImageRegionLoader;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.TileDrawable;
import cn.edu.hfut.lilei.shareboard.view.DragFloatActionButton;
import cn.edu.hfut.lilei.shareboard.view.customdialog.LodingDialog;
import cn.edu.hfut.lilei.shareboard.view.imageview.PinchImageView;

import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_pic;
import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_web;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.HOST_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_host_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class MeetingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    //控件
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPm;
    private WebView mWvCanvas, mWvShareWeb;
    private LodingDialog.Builder mlodingDialog;
    private TileDrawable mTileDrawable;
    private RadioGroup mRadioGroup;
    private DragFloatActionButton fab;
    private LinearLayout mLlWebviewCanvas, mLlActionGroup, mLlMeetingStage;
    private RelativeLayout mRlSharePic, mRlActionbar, mRlShareWeb;
    private PinchImageView pinchImageView;

    //数据
    //按钮的没选中显示的图标
    private int[] unselectedIconIds = {R.drawable.ic_default_members,
            R.drawable.ic_default_share, R.drawable.ic_default_lock
    };
    //按钮的选中显示的图标
    private int[] selectedIconIds = {R.drawable.ic_press_members,
            R.drawable.ic_press_share, R.drawable.ic_press_lock
    };

    //上下文参数
    private Context mContext;
    private int check_in_type = -1;
    private int meeting_id = -1;
    private long meeting_url = -1L;
    private boolean isDrawing = false;
    private String meeting_host_email;
    private int shareType = 0;//对主持人有效   0 :没有分享 1:分享图片 2:分享网页


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_meeting_test);
        init();


    }


    private void init() {
        mContext = this;
        check_in_type = 1;
        //共享图片层
        mRlSharePic = (RelativeLayout) findViewById(rl_share_pic);
        //共享图片
        pinchImageView = (PinchImageView) findViewById(R.id.share_pic);
        //共享网页层
        mRlShareWeb = (RelativeLayout) findViewById(rl_share_web);
        //共享网页
        mWvShareWeb = (WebView) findViewById(R.id.share_web);
        //顶部栏
        mRlActionbar = (RelativeLayout) findViewById(R.id.rl_meeting_actionbar);
        //底部栏
        mLlActionGroup = (LinearLayout) findViewById(R.id.ll_meeting_action_group);
        //主画板
        mLlWebviewCanvas = (LinearLayout) findViewById(R.id.ll_meeting_canvas);
        //整个舞台
        mLlMeetingStage = (LinearLayout) findViewById(R.id.ll_meeting_stage);

        /**
         * 设置主画板
         */
        mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SCREEN_DIM_WAKE_LOCK");
        mWvCanvas = (WebView) findViewById(R.id.wv_meeting_canvas);
        startWebView();
        /**
         * 设置底部按钮栏
         */
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_meeting);
        mRadioGroup.setOnCheckedChangeListener(this);
        selectPage(-1);
        /**
         * 设置悬浮按钮
         */
        fab = (DragFloatActionButton) findViewById(R.id.fab_meeting_start_draw);
        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 120));
        /**
         * 初始化加会者设置
         */
        initParticipate();
        /**
         * 初始化主持人设置
         */
        initHost();
        //测试共享网页
//        shareWeb();


    }

    /**
     * 共享图片
     */
    private void sharePic() {
        shareType = 1;
        mRlSharePic.setVisibility(View.VISIBLE);

    }

    /**
     * 共享网页
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void shareWeb() {
        shareType = 2;
        mRlShareWeb.setVisibility(View.VISIBLE);
        WebSettings webSettings = mWvShareWeb.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        //下面2行用来支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //隐藏缩放控制条
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        WebView.setWebContentsDebuggingEnabled(true);

        mWvShareWeb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWvShareWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
//              return super.shouldOverrideUrlLoading(btnStart, url);
                return false;
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

        mWvShareWeb.loadUrl("https://www.baidu.com/");
        mWvShareWeb.setVisibility(View.VISIBLE);// 加载完之后进行设置显示，以免加载时初始化效果不好看
    }

    /**
     * 初始化主持人设置
     */
    private void initHost() {
        //主持人设置
        if (check_in_type == 2) {

            /**
             * 设置底部状态栏
             */
            mRadioGroup.getChildAt(1)
                    .setVisibility(View.VISIBLE);
            mRadioGroup.getChildAt(2)
                    .setVisibility(View.VISIBLE);
            /**
             * 设置图片 在选择分享类型后
             */

//            String tmp = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() +
//                    "/" + R.drawable.bggg;
//
//            setShareImage(pinchImageView, tmp);

            fab.setOnClickListener(null);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //如果进入画板
                    if (!isDrawing) {
                        Bitmap bmp = null;
                        /**
                         * 同步共享
                         */
                        if (shareType == 1) {
                            //共享的是图片
                            mRlSharePic.setVisibility(View.VISIBLE);//可见
                            pinchImageView.setDrawingCacheEnabled(true);
                            bmp = Bitmap.createBitmap(pinchImageView.getDrawingCache());
                            pinchImageView.setDrawingCacheEnabled(false);
                        } else
                            if (shareType == 2) {
                                //共享的是网页
                                mRlShareWeb.setVisibility(View.VISIBLE);//可见
                                mWvShareWeb.setDrawingCacheEnabled(true);
                                bmp = Bitmap.createBitmap(mWvShareWeb.getDrawingCache());
                                mWvShareWeb.setDrawingCacheEnabled(false);
                            }
                        if (bmp != null) {
                            //从bitmap获取base64
                            String base64 = ImageUtil.bitmapToBase64(bmp);
                            showLog("native base64长度" + base64.length());
                            showLog(base64);
                            //调用js函数
                            mWvCanvas.loadUrl("javascript:syncPic('" + base64 + "')");
                        }


                    } else {
                        /**
                         * 取消共享(主持人跳出画板,进行调整中...)
                         */
                        mWvCanvas.loadUrl("javascript:cancleSyncPic()");
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 120));
                        mLlWebviewCanvas.setVisibility(View.GONE);
                        mRlActionbar.setVisibility(View.VISIBLE);
                        mLlActionGroup.setVisibility(View.VISIBLE);
                        isDrawing = false;
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_black));


                    }
                }
            });


        }
    }

    /**
     * 初始化加会者设置
     */
    private void initParticipate() {
        //加会者设置
        if (check_in_type == 1) {

            //与会者使用的悬浮按钮的功能
            fab.setOnClickListener(null);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //如果进入画板
                    if (!isDrawing) {
                        /**
                         * 设置悬浮按钮的活动范围
                         */
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                        /**
                         * 设置布局的可见性
                         */
                        mLlWebviewCanvas.setVisibility(View.VISIBLE);
                        mRlActionbar.setVisibility(View.GONE);
                        mLlActionGroup.setVisibility(View.GONE);
                        isDrawing = true;
                        /**
                         * 改变舞台的背景色
                         */
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_white));

                    } else {
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 120));
                        mLlWebviewCanvas.setVisibility(View.GONE);
                        mRlActionbar.setVisibility(View.VISIBLE);
                        mLlActionGroup.setVisibility(View.VISIBLE);
                        isDrawing = false;
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_black));


                    }
                }
            });
        }
    }


    /**
     * 新加会的加会者接收共享的图片
     *
     * @param str
     */
    @android.webkit.JavascriptInterface
    public void initShareContent(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLog("新加会的加会者接收共享的图片" + str);

                if (check_in_type == 1) {
                    if (!str.equals("")) {//获取share图片的base64

                        /**
                         * 设置共享图片
                         */
                        mRlSharePic.setVisibility(View.VISIBLE);//可见
                        pinchImageView =
                                (PinchImageView) findViewById(R.id.share_pic);
                        pinchImageView.setImageBitmap(ImageUtil.base64ToBitmap(str));

                        /**
                         * 设置悬浮按钮的活动范围
                         */
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                        /**
                         * 设置布局的可见性
                         */
                        mLlWebviewCanvas.setVisibility(View.VISIBLE);
                        mRlActionbar.setVisibility(View.GONE);
                        mLlActionGroup.setVisibility(View.GONE);
                        isDrawing = true;
                        /**
                         * 改变舞台的背景色
                         */
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_white));
                        showToast(mContext, "主持人正在共享");
                    }

                }
            }
        });

    }

    /**
     * 新加会的与会者请求得到share的图片
     */
    @android.webkit.JavascriptInterface
    public void getSharePic(final String client_email) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLog("新加会的与会者请求得到share的图片");
                if (check_in_type == 2) {
                    if (isDrawing) {
                        Bitmap bmp = null;
                        /**
                         * 同步共享
                         */
                        if (shareType == 1) {
                            //共享的是图片
                            mRlSharePic.setVisibility(View.VISIBLE);//可见
                            pinchImageView.setDrawingCacheEnabled(true);
                            bmp = Bitmap.createBitmap(pinchImageView.getDrawingCache());
                            pinchImageView.setDrawingCacheEnabled(false);
                        } else
                            if (shareType == 2) {
                                //共享的是网页
                                mRlShareWeb.setVisibility(View.VISIBLE);//可见
                                mWvShareWeb.setDrawingCacheEnabled(true);
                                bmp = Bitmap.createBitmap(mWvShareWeb.getDrawingCache());
                                mWvShareWeb.setDrawingCacheEnabled(false);
                            }
                        if (bmp != null) {
                            //从bitmap获取base64
                            String base64 = ImageUtil.bitmapToBase64(bmp);
                            showLog("native base64长度" + base64.length());

                            String call =
                                    "javascript:syncPicToNewer('" + client_email + "','" + base64
                                            + "')";
                            showLog("native call长度" + call.length());
                            showLog(call);
                            //调用js函数
                            mWvCanvas.loadUrl(call);
                        }


                    }
                }

            }
        });

    }


    /**
     * 加会者接收共享的图片
     *
     * @param str
     */
    @android.webkit.JavascriptInterface
    public void syncContent(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLog(str);

                if (check_in_type == 1) {
                    if (!str.equals("")) {//获取share图片的base64

                        /**
                         * 设置共享图片
                         */
                        mRlSharePic.setVisibility(View.VISIBLE);//可见
                        pinchImageView =
                                (PinchImageView) findViewById(R.id.share_pic);
                        pinchImageView.setImageBitmap(ImageUtil.base64ToBitmap(str));

                        /**
                         * 设置悬浮按钮的活动范围
                         */
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                        /**
                         * 设置布局的可见性
                         */
                        mLlWebviewCanvas.setVisibility(View.VISIBLE);
                        mRlActionbar.setVisibility(View.GONE);
                        mLlActionGroup.setVisibility(View.GONE);
                        isDrawing = true;
                        /**
                         * 改变舞台的背景色
                         */
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_white));
                        showToast(mContext, "主持人正在共享");
                    }

                }
            }
        });

    }


    /**
     * 主持人获取分享图片是否成功
     *
     * @param str
     */
    @android.webkit.JavascriptInterface
    public void syncResultCode(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLog(str);
                if (check_in_type == 2) {
                    if (str.equals("success")) {//图片同步成功

                        /**
                         * 设置悬浮按钮的活动范围
                         */
                        fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                        fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                        /**
                         * 设置布局的可见性
                         */
                        mLlWebviewCanvas.setVisibility(View.VISIBLE);
                        mRlActionbar.setVisibility(View.GONE);
                        mLlActionGroup.setVisibility(View.GONE);
                        isDrawing = true;
                        /**
                         * 改变舞台的背景色
                         */
                        mLlMeetingStage.setBackgroundColor(
                                getResources().getColor(R.color.my_white));
                        showToast(mContext, getString(R.string.you_are_sharing));
                    } else {//同步失败
                        showToast(mContext, getString(R.string.share_fail));
                    }
                }

            }
        });

    }

    /**
     * 主持人取消共享
     */
    @android.webkit.JavascriptInterface
    public void cancleSync() {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              if (check_in_type == 1) {

                                  /**
                                   * 使得共享图片不可见
                                   */
                                  mRlSharePic.setVisibility(View.GONE);//不可见
                                  showToast(mContext, getString(R.string.host_close_share));
                              }

                          }
                      }

        );

    }

    /**
     * 设置webview参数,并启动
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void startWebView() {
        //初始化 webview 基本设置
        initWebView(mWvCanvas);
        mWvCanvas.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
//              return super.shouldOverrideUrlLoading(btnStart, url);
                return false;
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
                .getInt(post_meeting_check_in_type);
        meeting_url = getIntent().getExtras()
                .getLong(post_meeting_url);
        if (check_in_type == 1) {
            meeting_host_email = getIntent().getExtras()
                    .getString(post_meeting_host_email);
        }


        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        keyList.add(share_token);
        keyList.add(share_user_email);
        keyList.add(share_family_name);
        keyList.add(share_given_name);


        if (check_in_type == HOST_CHECK_IN) {//host
            meeting_id = getIntent().getExtras()
                    .getInt(post_meeting_id);
        }

        valueList = SharedPrefUtil.getInstance()
                .getStringDatas(keyList);
        if (valueList != null && meeting_url != -1L) {
            String params = "";
            if (check_in_type == HOST_CHECK_IN && meeting_id != -1) {//host

                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_meeting_id + "=" + meeting_id + "&" +
                        post_meeting_check_in_type + "=" + check_in_type + "&" +
                        post_meeting_url + "=" + meeting_url;

            } else {
                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_meeting_check_in_type + "=" + check_in_type + "&" +
                        post_meeting_host_email + "=" + meeting_host_email + "&" +
                        post_meeting_url + "=" + meeting_url;
            }
            mWvCanvas.loadUrl(URL_MEETING + params);
            mWvCanvas.setVisibility(View.VISIBLE);// 加载完之后进行设置显示，以免加载时初始化效果不好看
            mWvCanvas.addJavascriptInterface(this, "board");
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(WebView web) {
        WebSettings webSettings = web.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        WebView.setWebContentsDebuggingEnabled(true);

        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web.setBackgroundColor(getResources().getColor(R.color.transparent));//背景透明
        web.requestFocus();

    }


    /**
     * 设置要share的图片
     *
     * @param pinchImageView
     */
    private void setShareImage(final PinchImageView pinchImageView, final String path) {
        pinchImageView.post(new Runnable() {
            @Override
            public void run() {
                mTileDrawable = new TileDrawable();
                mTileDrawable.setInitCallback(new TileDrawable.InitCallback() {
                    @Override
                    public void onInit() {
                        pinchImageView.setImageDrawable(mTileDrawable);
                    }
                });

                mTileDrawable.init(new HugeImageRegionLoader(MeetingActivity.this,
                                Uri.parse(path)),
                        new Point(pinchImageView.getWidth(), pinchImageView.getHeight()));
            }
        });
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
            gray.setBounds(0, 0, 80,
                    80);

            RadioButton child = (RadioButton) mRadioGroup.getChildAt(i);
            child.setCompoundDrawables(null, gray, null, null);
            child.setTextColor(getResources().getColor(
                    R.color.my_white));
        }
        if (position >= 0) {
            // 改变图标
            Drawable yellow = getResources().getDrawable(selectedIconIds[position]);
            yellow.setBounds(0, 0, 80,
                    80);
            RadioButton select = (RadioButton) mRadioGroup.getChildAt(position);
            select.setCompoundDrawables(null, yellow, null, null);

        }
        switch (position) {
            case 0:
                /**
                 * page 0 参与者
                 */
                break;
            case 1:
                /**
                 * page 1 选择共享类型
                 */
                //弹出选择框

                //更改分享类型 share_type


                break;
            case 2:
                /**
                 * page 2 锁定会议
                 */
                break;
        }


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

    @Override
    protected void onDestroy() {
        if (mTileDrawable != null) {
            mTileDrawable.recycle();
        }
        super.onDestroy();
    }
}
