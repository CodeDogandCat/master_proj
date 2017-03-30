package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.ScreenUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.HugeImageRegionLoader;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.TileDrawable;
import cn.edu.hfut.lilei.shareboard.view.DragFloatActionButton;
import cn.edu.hfut.lilei.shareboard.view.customdialog.CommonAlertDialog;
import cn.edu.hfut.lilei.shareboard.view.customdialog.LodingDialog;
import cn.edu.hfut.lilei.shareboard.view.customdialog.ShareChooseDialog;
import cn.edu.hfut.lilei.shareboard.view.customdialog.UrlInputDialog;
import cn.edu.hfut.lilei.shareboard.view.imageview.PinchImageView;

import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_pic;
import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_web;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.ALBUM_REQUEST_CODE;
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


public class MeetingActivity extends AppCompatActivity implements ShareChooseDialog.Builder.IShareWebPage, UrlInputDialog.Builder.IUrlInput, View.OnClickListener {
    //控件
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPm;
    private WebView mWvCanvas, mWvShareWeb;
    private LodingDialog.Builder mlodingDialog;
    private TileDrawable mTileDrawable;
    private DragFloatActionButton fab;
    private LinearLayout mLlWebviewCanvas, mLlActionGroup, mLlMeetingStage;
    private RelativeLayout mRlSharePic, mRlActionbar, mRlShareWeb, mRlAvatar;
    private PinchImageView pinchImageView;
    private AvatarImageView mAvatar;
    private Button mBtnLeave;
    private TextView mTvMeetingUrl;
    private RadioButton mBtnMember, mBtnShare, mBtnLock;

    //数据
    //按钮的没选中显示的图标
    private int[] unselectedIconIds = {R.drawable.ic_default_members,
            R.drawable.ic_default_share, R.drawable.ic_default_lock
    };
    //按钮的选中显示的图标
    private int[] selectedIconIds = {R.drawable.ic_press_members,
            R.drawable.ic_press_share, R.drawable.ic_press_lock
    };
    private int check_in_type = -1;
    private int meeting_id = -1;
    private long meeting_url = -1L;
    private boolean isDrawing = false;
    private String meeting_host_email;
    private int shareType = 0;//对主持人有效   0 :没有分享 1:分享图片 2:分享网页
    private boolean isLock = false;//会议是否锁定

    //上下文参数
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_meeting_test);
        init();


    }


    public void init() {
        mContext = this;
        check_in_type = 1;
        //会议url
        mTvMeetingUrl = (TextView) findViewById(R.id.tv_meeting_url);
        //离开会议
        mBtnLeave = (Button) findViewById(R.id.btn_meeting_leave);
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
        //默认背景  头像
        mAvatar = (AvatarImageView) findViewById(R.id.img_meeting_avatar);
        //默认背景层
        mRlAvatar = (RelativeLayout) findViewById(R.id.rl_meeting_avatar);
        //加载 默认背景  头像
        mAvatar.setTextAndColor((String) SharedPrefUtil.getInstance()
                .getData(share_given_name,
                        "未设置"), R.color.skyblue);
        ImageUtil.loadMyAvatar(mContext, mAvatar);
        //参与者按钮
        mBtnMember = (RadioButton) findViewById(R.id.btn_meeting_members);
        //共享按钮
        mBtnShare = (RadioButton) findViewById(R.id.btn_meeting_share);
        //锁定按钮
        mBtnLock = (RadioButton) findViewById(R.id.btn_meeting_lock);

        /**
         * 设置底部按钮栏
         */
        mBtnMember.setOnClickListener(this);
        mBtnMember.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        changeToSelected((RadioButton) view, 0, null);
                        break;
                    case MotionEvent.ACTION_UP:
                        changeToUnSelected((RadioButton) view, 0, null);
                        break;
                }
                return false;
            }

        });
        mBtnShare.setOnClickListener(this);
        mBtnLock.setOnClickListener(this);

        // 将所有的tab的icon变成灰色的
        changeToUnSelected(mBtnMember, 0, null);
        changeToUnSelected(mBtnShare, 1, null);
        changeToUnSelected(mBtnLock, 2, null);

        /**
         * 离会按钮
         */
        mBtnLeave.setOnClickListener(this);

        /**
         * 设置主画板
         */
        mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SCREEN_DIM_WAKE_LOCK");
        mWvCanvas = (WebView) findViewById(R.id.wv_meeting_canvas);
        startWebView();

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


    }

    /**
     * 设置离会按钮操作
     */
    public void leaveMeetingAction() {
        //加会者离会
        if (check_in_type == 1) {
            new CommonAlertDialog.Builder(mContext)
                    .setTitle(getString(R.string.confirm_leave_meeting))
                    .setPositiveButton(
                            mContext.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //离会操作
                                    showToast(mContext, "离会");
                                    syncLeaveMeeting();
                                    //离开当前界面
                                    finish();

                                }
                            })
                    .setNegativeButton(
                            mContext.getString(R.string.no),
                            null)
                    .show();
        } else
            if (check_in_type == 2) {//主持人离会
                new CommonAlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.confirm_leave_meeting))
                        .setMessage(getString(R.string.confirm_host_leave))
                        .setPositiveButton(
                                getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //离会操作
                                        showToast(mContext, "主持人结束会议");
                                        syncLeaveMeeting();
                                        //离开当前界面
                                        finish();
                                    }
                                })
                        .setNegativeButton(
                                mContext.getString(R.string.no),
                                null)
                        .show();
            }

    }

    /**
     * 接受加会者的离会消息
     *
     * @param str
     */
    @android.webkit.JavascriptInterface
    public void memberLeave(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(mContext, str + "离开了会议");

            }
        });

    }

    /**
     * 接受主持人的离会消息
     *
     * @param str
     */
    @android.webkit.JavascriptInterface
    public void hostLeave(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //弹出框提示 主持人离会,退出
                new CommonAlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.host_left_meeting_end))
                        .setPositiveButton(
                                mContext.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //离会操作
                                        String call =
                                                "javascript:leaveForHostLeave()";

                                        showLog(call);
                                        //调用js函数
                                        mWvCanvas.loadUrl(call);
                                        //离开当前界面
                                        finish();

                                    }
                                })
                        .show();


            }
        });

    }

    /**
     * 通知js  离会
     */
    public void syncLeaveMeeting() {
        String tmpFamilyName = (String) SharedPrefUtil.getInstance()
                .getData
                        (share_family_name, "");
        String tmpGivenName = (String) SharedPrefUtil.getInstance()
                .getData
                        (share_given_name, "");
        String name = tmpFamilyName + tmpGivenName;
        if (!name.equals("")) {
            String call =
                    "javascript:syncLeaveMeeting('" + name + "')";

            showLog(call);
            //调用js函数
            mWvCanvas.loadUrl(call);
        }
    }

    /**
     * 共享图片
     */
    public void sharePic() {
        shareType = 1;
        mRlSharePic.setVisibility(View.VISIBLE);

    }


    /**
     * 初始化主持人设置
     */
    public void initHost() {
        //主持人设置
        if (check_in_type == 2) {

            /**
             * 设置底部状态栏
             */
            mBtnShare.setVisibility(View.VISIBLE);
            mBtnLock.setVisibility(View.VISIBLE);

            fab.setOnClickListener(null);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //如果主持人进入画板,再同步图片一遍了
                    if (!isDrawing) {
                        Bitmap bmp = null;
                        /**
                         * 同步共享
                         */
                        if (shareType == 1) {
                            //共享的是图片
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

                        String base64 = "nothing";

                        if (bmp != null) {
                            //从bitmap获取base64
                            base64 = ImageUtil.bitmapToBase64(bmp);
                            showLog("native base64长度" + base64.length());
                        }
                        String call = "javascript:syncPic('" + base64 + "')";

                        showLog("native call长度" + call.length());
//                            showLog(call);

                        //调用js函数
                        mWvCanvas.loadUrl(call);


                    } else {
                        //退出画板界面
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
    public void initParticipate() {
        //加会者设置
        if (check_in_type == 1) {

            //与会者刚刚进会是看不到悬浮按钮的,也就不能进入画板
            fab.setVisibility(View.GONE);
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
                        mRlAvatar.setVisibility(View.GONE);
                        mLlWebviewCanvas.setVisibility(View.VISIBLE);
                        mRlSharePic.setVisibility(View.VISIBLE);//无论主持人在没在共享

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

                        /**
                         * 设置布局的可见性
                         */
                        mRlAvatar.setVisibility(View.VISIBLE);
                        mLlWebviewCanvas.setVisibility(View.GONE);
                        mRlSharePic.setVisibility(View.GONE);//无论主持人在没在共享

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

                    //悬浮按钮可见
                    fab.setVisibility(View.VISIBLE);
                    /**
                     * 设置悬浮按钮的活动范围
                     */
                    fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                    fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                    /**
                     * 设置布局的可见性
                     */
                    mLlWebviewCanvas.setVisibility(View.VISIBLE);
                    mRlAvatar.setVisibility(View.GONE);

                    mRlActionbar.setVisibility(View.GONE);
                    mLlActionGroup.setVisibility(View.GONE);
                    isDrawing = true;
                    /**
                     * 改变舞台的背景色
                     */
                    mLlMeetingStage.setBackgroundColor(
                            getResources().getColor(R.color.my_white));
                    //获取share图片的base64,且不为空,说明 当前主持人在 share_type= 1 or 2
                    if (!str.equals("nothing")) {

                        /**
                         * 设置共享图片
                         */
                        mRlSharePic.setVisibility(View.VISIBLE);//可见
                        pinchImageView.setImageBitmap(ImageUtil.base64ToBitmap(str));

                        showToast(mContext, "主持人正在共享");
                    } else {
                        mRlSharePic.setVisibility(View.GONE);//不可见
                        showToast(mContext, "主持人正在使用白板");
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
                                  /**
                                   * 无论主持人是否在共享,是否在使用画板,都同步一下
                                   */
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


                                  String base64 = "nothing";

                                  if (bmp != null) {
                                      //从bitmap获取base64
                                      base64 = ImageUtil.bitmapToBase64(bmp);
                                      showLog("native base64长度" + base64.length());
                                  }
                                  String call = "javascript:syncPicToNewer('" + client_email + "','" + base64
                                          + "')";

                                  showLog("native call长度" + call.length());
//                            showLog(call);

                                  //调用js函数
                                  mWvCanvas.loadUrl(call);

                              }
                          }

                      }

        );

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
                    //悬浮按钮可见
                    fab.setVisibility(View.VISIBLE);
                    /**
                     * 设置悬浮按钮的活动范围
                     */
                    fab.setTitleBarSize(ScreenUtil.convertDpToPx(mContext, 40));
                    fab.setBottomBarSize(ScreenUtil.convertDpToPx(mContext, 60));
                    /**
                     * 设置布局的可见性
                     */
                    mLlWebviewCanvas.setVisibility(View.VISIBLE);
                    mRlAvatar.setVisibility(View.GONE);

                    mRlActionbar.setVisibility(View.GONE);
                    mLlActionGroup.setVisibility(View.GONE);
                    isDrawing = true;
                    /**
                     * 改变舞台的背景色
                     */
                    mLlMeetingStage.setBackgroundColor(
                            getResources().getColor(R.color.my_white));
                    //获取share图片的base64,且不为空,说明 当前主持人在 share_type= 1 or 2
                    if (!str.equals("")) {

                        /**
                         * 设置共享图片
                         */
                        mRlSharePic.setVisibility(View.VISIBLE);//可见
                        pinchImageView.setImageBitmap(ImageUtil.base64ToBitmap(str));

                        showToast(mContext, "主持人正在共享");
                    } else {
                        showToast(mContext, "主持人正在使用白板");
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
    public void startWebView() {
        //初始化 webview 基本设置
        initWebView(mWvCanvas);
        mWvCanvas.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
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
                if (mlodingDialog != null) {
                    mlodingDialog.cancle();
                }
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

        //设置会议号
        String tmp = String.valueOf(meeting_url);
        String mid = "";
        if (tmp.length() == 12) {

            mid = tmp.substring(0, 4) + "-" + tmp.substring(4, 8) + "-" + tmp.substring(8, 12);
        } else {
            mid = tmp;
        }
        mTvMeetingUrl.setText(mid);

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
    private void setShareImage(final PinchImageView pinchImageView, final Uri path) {
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
                                path),
                        new Point(pinchImageView.getWidth(), pinchImageView.getHeight()));
            }
        });
    }

    /**
     * 切换图标到选中
     *
     * @param position
     */
    public void changeToSelected(RadioButton child, int position, String str) {
        Drawable yellow = getResources().getDrawable(selectedIconIds[position]);
        yellow.setBounds(0, 0, 80,
                80);
        child.setCompoundDrawables(null, yellow, null, null);
        if (str != null) {
            child.setText(str);
        }
    }

    /**
     * 切换图标到未选中
     */
    public void changeToUnSelected(RadioButton child, int position, String str) {
        Drawable gray = getResources().getDrawable(unselectedIconIds[position]);
        gray.setBounds(0, 0, 80,
                80);
        child.setCompoundDrawables(null, gray, null, null);
        child.setTextColor(getResources().getColor(
                R.color.my_white));
        if (str != null) {
            child.setText(str);
        }
    }

    /**
     * 得到返回结果状态码
     *
     * @param requestCode
     * @param resultCode  结果状态
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                if (data == null) {
                    showLog("相册返回为空");
                    return;
                }
                mRlAvatar.setVisibility(View.GONE);
                mRlSharePic.setVisibility(View.VISIBLE);
                setShareImage(pinchImageView, data.getData());
                shareType = 1;
                if (check_in_type == 2) {
                    changeToSelected(mBtnShare, 1, getString(R.string.end_share));
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void openWebSuccess() {
        if (check_in_type == 2) {
            //可见性
            mRlAvatar.setVisibility(View.GONE);
            mRlShareWeb.setVisibility(View.VISIBLE);
            shareType = 2;
            changeToSelected(mBtnShare, 1, getString(R.string.end_share));

            //提示
        }

    }

    @Override
    public void openWebError() {
        if (check_in_type == 2) {
            //可见性
            mRlShareWeb.setVisibility(View.GONE);
            mRlAvatar.setVisibility(View.VISIBLE);
            shareType = 0;

            //提示
            MyAppUtil.showToast(mContext,
                    mContext.getString(R.string.open_web_page_error));
        }


    }

    /**
     * 分享web的回调
     */
    @Override
    public void shareWebPage() {
        //打开网址输入框
        new UrlInputDialog.Builder(mContext)
                .setHint(R.string.please_input_url)
                .setTitle(getString(R.string.share_web_page))
                .setShareWeb(mWvShareWeb)
                .setCallBack(this)
                .setPositiveButton(getString(R.string.confirm), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .show();


    }

    /**
     * 构造选择分享方式的弹框
     */
    private void createShareChooseDialog() {

        new ShareChooseDialog.Builder(mContext)
                .setTitle(getString(R.string.choose_share))
                .setShareWebPageCallback(this)
                .show();

    }

    /**
     * 获取权限
     */
    public void requestPermission() {
        PermissionsUtil.TipInfo tip =
                new PermissionsUtil.TipInfo(null,
                        getString(R.string.should_get_this_to_alter_head), null,
                        null);

        if (PermissionsUtil
                .hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            createShareChooseDialog();

        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    createShareChooseDialog();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                }
            }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, true, tip);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             * page 0 参与者
             */
            case R.id.btn_meeting_members:
                //打开参与者页面
                showToast(mContext, "打开参与者页面");
                break;
            /**
             * page 1 选择共享类型
             */
            case R.id.btn_meeting_share:
                if (shareType == 0) {
                    //弹出选择框
                    requestPermission();

                } else {
                    //退出共享
                    mRlSharePic.setVisibility(View.GONE);
                    mRlShareWeb.setVisibility(View.GONE);
                    shareType = 0;
                    mRlAvatar.setVisibility(View.VISIBLE);
                    changeToUnSelected(mBtnShare, 1, "共享");

                    //通知加会者:主持人关闭了共享
                    mWvCanvas.loadUrl("javascript:cancleSyncPic()");

                }
                break;
            /**
             * page 2 锁定会议
             */
            case R.id.btn_meeting_lock:
                if (!isLock) {
                    //发送http锁定会议

                    changeToSelected(mBtnLock, 2, "解锁");
                    isLock = true;
                } else {
                    //发送http 解锁
                    changeToUnSelected(mBtnLock, 2, "锁定");
                    isLock = false;

                }

                break;
            /**
             * 离会
             */
            case R.id.btn_meeting_leave:
                leaveMeetingAction();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //共享网页时候,还没进入画板的时候,主持人用来控制webview 的返回
            if (!isDrawing && shareType == 2 && check_in_type == 2) {
                if (mWvShareWeb.canGoBack()) {
                    // 返回页面的上一页
                    mWvShareWeb.goBack();
                } else {
                    // 监控返回键
                    leaveMeetingAction();
                }
            } else {
                // 监控返回键
                leaveMeetingAction();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
