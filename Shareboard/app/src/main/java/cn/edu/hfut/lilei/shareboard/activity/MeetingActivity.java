package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.ChatAdapter;
import cn.edu.hfut.lilei.shareboard.adapter.CommonFragmentPagerAdapter;
import cn.edu.hfut.lilei.shareboard.adapter.MemberListAdapter;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.enity.MeetingMemberInfo;
import cn.edu.hfut.lilei.shareboard.enity.MessageInfo;
import cn.edu.hfut.lilei.shareboard.fragment.ChatEmotionFragment;
import cn.edu.hfut.lilei.shareboard.fragment.ChatFunctionFragment;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.models.MemberJson;
import cn.edu.hfut.lilei.shareboard.models.MemberListJson;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.ScreenUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.HugeImageRegionLoader;
import cn.edu.hfut.lilei.shareboard.utils.hugeimageutil.TileDrawable;
import cn.edu.hfut.lilei.shareboard.widget.DragFloatActionButton;
import cn.edu.hfut.lilei.shareboard.widget.EmotionInputDetector;
import cn.edu.hfut.lilei.shareboard.widget.NoScrollViewPager;
import cn.edu.hfut.lilei.shareboard.widget.StateButton;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.CommonAlertDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.ShareChooseDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.UrlInputDialog;
import cn.edu.hfut.lilei.shareboard.widget.imageview.PinchImageView;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_pic;
import static cn.edu.hfut.lilei.shareboard.R.id.rl_share_web;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.HOST_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_HOST_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_LEAVE_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_host_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class MeetingActivity extends AppCompatActivity implements ShareChooseDialog.Builder.IShareWebPage, UrlInputDialog.Builder.IUrlInput, View.OnClickListener, MemberListAdapter.Callback, AdapterView.OnItemClickListener {
    /**
     * meeting 页面的变量
     */
    //控件
    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPm;
    private WebView mWvCanvas, mWvShareWeb;
    private LodingDialog.Builder mlodingDialog;
    private TileDrawable mTileDrawable;
    private DragFloatActionButton fab;
    private LinearLayout mLlWebviewCanvas, mLlActionGroup, mLlMeetingStage;
    private RelativeLayout mRlSharePic, mRlActionbar, mRlShareWeb, mRlAvatar, mRlMeeting;
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
    private String meeting_host_email, my_email;
    private int shareType = 0;//对主持人有效   0 :没有分享 1:分享图片 2:分享网页
    private boolean isLock = false;//会议是否锁定
    private boolean leaveForHostLeave = false;//因为主持人离开了,我必须离开
    private List<MeetingMemberInfo> memberInfoList = new ArrayList<>();
    private boolean isTalkable, isDrawable;

    //上下文参数
    private Context mContext;

    /**
     * 参与者页面的 变量
     */
    private RelativeLayout mRlMember;
    private TextView mTvMemberTitle;
    private Button mBtnChat, mBtnInvite;
    private ListView listContent = null;
    private MemberListAdapter memberListAdapter;

    /**
     * 聊天页面的 变量
     */
    @Bind(R.id.chat_list)
    EasyRecyclerView chatList;
    @Bind(R.id.emotion_voice)
    ImageView emotionVoice;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.voice_text)
    TextView voiceText;
    @Bind(R.id.emotion_button)
    ImageView emotionButton;
    @Bind(R.id.emotion_add)
    ImageView emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.emotion_layout)
    RelativeLayout emotionLayout;
    private LinearLayout mLlChat;
    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter fragmentPagerAdapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_meeting_test);
        init();
        initMeeting();


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
        super.onDestroy();
        if (mTileDrawable != null) {
            mTileDrawable.recycle();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    public void beforeFinish() {
        getWindow().setFlags(WindowManager
                        .LayoutParams
                        .FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    /**
     * 公共部分的初始化
     */
    public void init() {
        mContext = this;
        mRlMeeting = (RelativeLayout) findViewById(R.id.rl_meeting);
        mRlMember = (RelativeLayout) findViewById(R.id.rl_member);
//        mLlChat = (LinearLayout) findViewById(R.id.ll_chat);

    }


    /**
     * meeting 页面的 初始化
     */
    public void initMeeting() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mRlMember.setVisibility(View.GONE);
        mRlMeeting.setVisibility(View.VISIBLE);
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
     * 参与者页面的 初始化
     */
    public void initMember() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);

        mRlMeeting.setVisibility(View.GONE);
        mRlMember.setVisibility(View.VISIBLE);
        //资源释放

        //资源释放结束
        mTvMemberTitle = (TextView) findViewById(R.id.tv_member_title);
        mBtnChat = (Button) findViewById(R.id.btn_member_chat);
        mBtnInvite = (Button) findViewById(R.id.btn_member_invite);
        listContent = (ListView) findViewById(R.id.lv_meeting_member);
        mTvMemberTitle.setText(String.format(getResources().getString(R.string.current_members),
                memberInfoList.size() + ""));
        mBtnChat.setOnClickListener(this);
        mBtnInvite.setOnClickListener(this);
        memberListAdapter = new MemberListAdapter(mContext, my_email, check_in_type, this);
        listContent.setAdapter(memberListAdapter);
        listContent.setOnItemClickListener(this);
        //
        memberListAdapter.clear();
        memberListAdapter.addAll(memberInfoList);
        memberListAdapter.notifyDataSetChanged();


    }

    /**
     * 聊天页面初始化
     */
//    public void initChat() {
//        View decorView = getWindow().getDecorView();
//        // Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
//        decorView.setSystemUiVisibility(uiOptions);
//
//        ButterKnife.bind(this);
//        EventBus.getDefault()
//                .register(this);
//        fragments = new ArrayList<>();
//        chatEmotionFragment = new ChatEmotionFragment();
//        fragments.add(chatEmotionFragment);
//        chatFunctionFragment = new ChatFunctionFragment();
//        fragments.add(chatFunctionFragment);
//        fragmentPagerAdapter =
//                new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//        viewpager.setAdapter(fragmentPagerAdapter);
//        viewpager.setCurrentItem(0);
//
//        mDetector = EmotionInputDetector.with(this)
//                .setEmotionView(emotionLayout)
//                .setViewPager(viewpager)
//                .bindToContent(chatList)
//                .bindToEditText(editText)
//                .bindToEmotionButton(emotionButton)
//                .bindToAddButton(emotionAdd)
//                .bindToSendButton(emotionSend)
//                .bindToVoiceButton(emotionVoice)
//                .bindToVoiceText(voiceText)
//                .build();
//
//        GlobalOnItemClickManagerUtils globalOnItemClickListener =
//                GlobalOnItemClickManagerUtils.getInstance(this);
//        globalOnItemClickListener.attachToEditText(editText);
//
//        chatAdapter = new ChatAdapter(this);
//        layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        chatList.setLayoutManager(layoutManager);
//        chatList.setAdapter(chatAdapter);
//        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                switch (newState) {
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        chatAdapter.handler.removeCallbacksAndMessages(null);
//                        chatAdapter.notifyDataSetChanged();
//                        break;
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        chatAdapter.handler.removeCallbacksAndMessages(null);
//                        mDetector.hideEmotionLayout(false);
//                        mDetector.hideSoftInput();
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
//        chatAdapter.addItemClickListener(itemClickListener);
//        LoadData();
//    }


    /**
     * 销毁参与者页面的资源
     */
    public void recycleMember() {
        //
        mTvMemberTitle = null;
        mBtnChat = null;
        mBtnInvite = null;
        listContent = null;
        memberListAdapter = null;
        //手动回收变量
        System.gc();
        mRlMeeting.setVisibility(View.VISIBLE);
        mRlMember.setVisibility(View.GONE);
    }

    /**
     * 释放聊天界面的资源
     */
//    public void recycleChat() {
//        fragments = null;
//        chatEmotionFragment = null;
//        chatFunctionFragment = null;
//        fragmentPagerAdapter = null;
//        mDetector = null;
//        chatAdapter = null;
//        layoutManager = null;
//        chatList = null;
//        System.gc();
//
//        ButterKnife.unbind(this);
//        EventBus.getDefault()
//                .removeStickyEvent(this);
//        EventBus.getDefault()
//                .unregister(this);
//        mRlMember.setVisibility(View.VISIBLE);
//        mLlChat.setVisibility(View.GONE);
//    }

    /**
     * 按钮监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             * page 0 参与者
             */
            case R.id.btn_meeting_members:
                //打开参与者页面
//                showToast(mContext, "打开参与者页面");
                initMember();
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
                    lockMeetingDBOper(0);

                } else {
                    //发送http 解锁
                    lockMeetingDBOper(1);


                }

                break;
            /**
             * 离会
             */
            case R.id.btn_meeting_leave:
                leaveMeetingAction();
                break;
            case R.id.btn_member_chat:
                //打开聊天界面
//                mRlMember.setVisibility(View.GONE);
//                mLlChat.setVisibility(View.VISIBLE);
//                initChat();
                startActivity(new Intent(mContext, ChatActivity.class));
                break;

        }

    }

    /**
     * memberListAdapter 的callback: 修改与会者的权限
     *
     * @param v
     */
    @Override
    public void click(View v) {
        int position = (Integer) v.getTag();

        if (check_in_type == 2) {
            //获取当前状态
            MeetingMemberInfo member = memberInfoList.get(position);
            String client_email = member.getClient_email();
            boolean client_is_drawable, client_is_talkable;
            client_is_drawable = member.isClient_is_drawable();
            client_is_talkable = member.isClient_is_talkable();

            if (v.getId() == R.id.img_item_member_draw) {
                if (client_is_drawable) {
                    client_is_drawable = false;
                    //修改状态
                    member.setClient_is_drawable(false);
                    //刷新联系人列表
                    memberListAdapter.setIsDrawable(position, false);
                    //修改UI
                    ImageUtil.load(mContext, R.drawable.ic_undraw, R.drawable.ic_undraw,
                            (ImageView) v);
                    if (client_email.equals(my_email)) {
                        isDrawable = false;
                        showToast(mContext, getString(R.string.forbid_draw));
                    } else {
                        //js 通知当事人
                        String call = "javascript:alterUserPermission('" + client_email + "','" +
                                false + "','" + null
                                + "')";
                        //调用js函数
                        mWvCanvas.loadUrl(call);
                    }


                } else {
                    client_is_drawable = true;
                    //修改状态
                    member.setClient_is_drawable(true);
                    //刷新联系人列表
                    memberListAdapter.setIsDrawable(position, true);
                    //修改UI
                    ImageUtil.load(mContext, R.drawable.ic_draw, R.drawable.ic_draw,
                            (ImageView) v);
                    if (client_email.equals(my_email)) {
                        isDrawable = true;
                        showToast(mContext, getString(R.string.allow_draw));
                    } else {
                        //js 通知当事人
                        String call = "javascript:alterUserPermission('" + client_email + "','" +
                                true + "','" + null
                                + "')";
                        //调用js函数
                        mWvCanvas.loadUrl(call);
                    }

                }


            } else
                if (v.getId() == R.id.img_item_member_talk) {
                    if (client_is_talkable) {
                        client_is_talkable = false;
                        //修改状态
                        member.setClient_is_drawable(false);
                        //刷新联系人列表
                        memberListAdapter.setIsTalkable(position, false);
                        // 修改UI
                        ImageUtil.load(mContext, R.drawable.ic_muting, R.drawable.ic_muting,
                                (ImageView) v);

                        if (client_email.equals(my_email)) {
                            isTalkable = false;
                            showToast(mContext, getString(R.string.forbid_talk));
                        } else {
                            //js 通知当事人
                            String call =
                                    "javascript:alterUserPermission('" + client_email + "','" +
                                            null + "','" + false
                                            + "')";
                            //调用js函数
                            mWvCanvas.loadUrl(call);
                        }


                    } else {
                        client_is_talkable = true;
                        //修改状态
                        member.setClient_is_drawable(true);
                        //刷新联系人列表
                        memberListAdapter.setIsTalkable(position, true);
                        // 修改UI
                        ImageUtil.load(mContext, R.drawable.ic_unmutinging,
                                R.drawable.ic_unmutinging,
                                (ImageView) v);

                        if (client_email.equals(my_email)) {
                            isTalkable = true;
                            showToast(mContext, getString(R.string.allow_talk));
                        } else {
                            //js 通知当事人
                            String call =
                                    "javascript:alterUserPermission('" + client_email + "','" +
                                            null + "','" + true
                                            + "')";
                            //调用js函数
                            mWvCanvas.loadUrl(call);
                        }

                    }
                }


        }


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (check_in_type == 2) {

            final String client_email = memberInfoList.get(i)
                    .getClient_email();
            final String familyName = memberInfoList.get(i)
                    .getClient_family_name();
            final String givenName = memberInfoList.get(i)
                    .getClient_given_name();

            if (!my_email.equals(client_email)) {
                //弹出框
                new CommonAlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.member_manage))
                        .setMessage("踢出" + familyName + " " + givenName)
                        .setPositiveButton(
                                mContext.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //调用js 踢人
                                        //js 通知当事人
                                        String call =
                                                "javascript:kickout('" + client_email + "')";
                                        //调用js函数
                                        mWvCanvas.loadUrl(call);

                                    }
                                })
                        .setNegativeButton(
                                mContext.getString(R.string.no),
                                null)
                        .show();


            }

        }

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
            } else
                if (mRlMember.getVisibility() == View.VISIBLE) {
                    try {
                        Thread.sleep(100);
                        recycleMember();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
//                else
//                    if (mLlChat.getVisibility() == View.VISIBLE) {
//                        try {
//                            Thread.sleep(100);
//                            recycleChat();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
                else {
                    // 监控返回键
                    leaveMeetingAction();
                }
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
     * item点击事件
     */
//    private ChatAdapter.onItemClickListener itemClickListener =
//            new ChatAdapter.onItemClickListener() {
//                @Override
//                public void onHeaderClick(int position) {
//                    Toast.makeText(MeetingActivity.this, "onHeaderClick", Toast.LENGTH_SHORT)
//                            .show();
//                }
//
//                @Override
//                public void onImageClick(View view, int position) {
//                    int location[] = new int[2];
//                    view.getLocationOnScreen(location);
//                    FullImageInfo fullImageInfo = new FullImageInfo();
//                    fullImageInfo.setLocationX(location[0]);
//                    fullImageInfo.setLocationY(location[1]);
//                    fullImageInfo.setWidth(view.getWidth());
//                    fullImageInfo.setHeight(view.getHeight());
//                    fullImageInfo.setImageUrl(messageInfos.get(position)
//                            .getImageUrl());
//                    EventBus.getDefault()
//                            .postSticky(fullImageInfo);
//                    startActivity(new Intent(mContext, FullImageActivity.class));
//                    overridePendingTransition(0, 0);
//                }
//
//                @Override
//                public void onVoiceClick(final ImageView imageView, final int position) {
//                    if (animView != null) {
//                        animView.setImageResource(res);
//                        animView = null;
//                    }
//                    switch (messageInfos.get(position)
//                            .getType()) {
//                        case 1:
//                            animationRes = R.drawable.voice_left;
//                            res = R.mipmap.icon_voice_left3;
//                            break;
//                        case 2:
//                            animationRes = R.drawable.voice_right;
//                            res = R.mipmap.icon_voice_right3;
//                            break;
//                    }
//                    animView = imageView;
//                    animView.setImageResource(animationRes);
//                    animationDrawable = (AnimationDrawable) imageView.getDrawable();
//                    animationDrawable.start();
//                    MediaManager.playSound(messageInfos.get(position)
//                            .getFilepath(), new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            animView.setImageResource(res);
//                        }
//                    });
//                }
//            };

    /**
     * 构造聊天数据
     */
//    private void LoadData() {
//        messageInfos = new ArrayList<>();
//
//        MessageInfo messageInfo = new MessageInfo();
//        messageInfo.setContent("你好，欢迎使用Rance的聊天界面框架");
//        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//        messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//        messageInfos.add(messageInfo);
//
//        MessageInfo messageInfo1 = new MessageInfo();
//        messageInfo1.setFilepath("http://www.trueme.net/bb_midi/welcome.wav");
//        messageInfo1.setVoiceTime(3000);
//        messageInfo1.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo1.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
//        messageInfo1.setHeader(
//                "http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
//        messageInfos.add(messageInfo1);
//
//        MessageInfo messageInfo2 = new MessageInfo();
//        messageInfo2.setImageUrl(
//                "http://img4.imgtn.bdimg.com/it/u=1800788429,176707229&fm=21&gp=0.jpg");
//        messageInfo2.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//        messageInfo2.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//        messageInfos.add(messageInfo2);
//
//        MessageInfo messageInfo3 = new MessageInfo();
//        messageInfo3.setContent("[微笑][色][色][色]");
//        messageInfo3.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo3.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
//        messageInfo3.setHeader(
//                "http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
//        messageInfos.add(messageInfo3);
//
//        chatAdapter.addAll(messageInfos);
//    }
//
//    /**
//     * 处理聊天消息逻辑
//     *
//     * @param messageInfo
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void MessageEventBus(final MessageInfo messageInfo) {
//        messageInfo.setHeader(
//                "http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
//        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
//        messageInfos.add(messageInfo);
//        chatAdapter.add(messageInfo);
//        chatList.scrollToPosition(chatAdapter.getCount() - 1);
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
//                chatAdapter.notifyDataSetChanged();
//            }
//        }, 2000);
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                MessageInfo message = new MessageInfo();
//                message.setContent("这是模拟消息回复");
//                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
//                message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
//                messageInfos.add(message);
//                chatAdapter.add(message);
//                chatList.scrollToPosition(chatAdapter.getCount() - 1);
//            }
//        }, 3000);
//    }

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
                            } else {
                                mRlAvatar.setVisibility(View.GONE);
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
                        if (shareType == 0) {
                            mRlAvatar.setVisibility(View.VISIBLE);
                        }
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
                beforeFinish();
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
        isDrawable = getIntent().getExtras()
                .getBoolean(post_meeting_is_drawable);
        isTalkable = getIntent().getExtras()
                .getBoolean(post_meeting_is_talkable);

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
        keyList.add(share_avatar);


        if (check_in_type == HOST_CHECK_IN) {//host
            meeting_id = getIntent().getExtras()
                    .getInt(post_meeting_id);
        }

        valueList = SharedPrefUtil.getInstance()
                .getStringDatas(keyList);

        my_email = valueList.get(1);

        if (valueList != null && meeting_url != -1L) {
            String params = "";
            if (check_in_type == HOST_CHECK_IN && meeting_id != -1) {//host

                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_user_avatar + "=" + valueList.get(4) + "&" +
                        post_meeting_id + "=" + meeting_id + "&" +
                        post_meeting_is_drawable + "=" + isDrawable + "&" +
                        post_meeting_is_talkable + "=" + isTalkable + "&" +
                        post_meeting_check_in_type + "=" + check_in_type + "&" +
                        post_meeting_url + "=" + meeting_url;

            } else {
                params = "?" +
                        post_token + "=" + valueList.get(0) + "&" +
                        post_user_email + "=" + valueList.get(1) + "&" +
                        post_user_family_name + "=" + valueList.get(2) + "&" +
                        post_user_given_name + "=" + valueList.get(3) + "&" +
                        post_user_avatar + "=" + valueList.get(4) + "&" +
                        post_meeting_is_drawable + "=" + isDrawable + "&" +
                        post_meeting_is_talkable + "=" + isTalkable + "&" +
                        post_meeting_check_in_type + "=" + check_in_type + "&" +
                        post_meeting_host_email + "=" + meeting_host_email + "&" +
                        post_meeting_url + "=" + meeting_url;
            }
            if (!NetworkUtil.isNetworkConnected(mContext)) {
                //网络连接不可用
                //弹出对话框，让用户开启网络
                NetworkUtil.setNetworkMethod(mContext);

            }
            mWvCanvas.loadUrl(URL_MEETING + params);
            mWvCanvas.setVisibility(View.VISIBLE);// 加载完之后进行设置显示，以免加载时初始化效果不好看
            mWvCanvas.addJavascriptInterface(this, "board");
        }

    }

    /**
     * 获取权限
     */
    public void requestPermission() {
        PermissionsUtil.TipInfo tip =
                new PermissionsUtil.TipInfo(null,
                        getString(R.string.should_get_to_share), null,
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
     * 构造选择分享方式的弹框
     */
    private void createShareChooseDialog() {

        new ShareChooseDialog.Builder(mContext)
                .setTitle(getString(R.string.choose_share))
                .setShareWebPageCallback(this)
                .show();

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
                                    //清空会议联系人
                                    memberInfoList.clear();
                                    //修改数据库
                                    leaveMeetingDBOper();

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
                                        //修改数据库
                                        leaveMeetingDBOper();
                                    }
                                })
                        .setNegativeButton(
                                mContext.getString(R.string.no),
                                null)
                        .show();
            }

    }

    /**
     * 离会的数据库操作
     */
    public void leaveMeetingDBOper() {
        mlodingDialog = loding(mContext, R.string.leaving);

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
                 * 2.检查页面参数合法性
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

                /**
                 * 3.发送
                 */

                OkGo.post(URL_LEAVE_MEETING)
                        .tag(this)
                        .params(post_meeting_check_in_type, check_in_type)
                        .params(post_token, token)
                        .params(post_user_email, email)
                        .params(post_meeting_url, meeting_url)
                        .params(post_meeting_id, meeting_id)

                        .execute(new JsonCallback<CommonJson>() {
                                     @Override
                                     public void onSuccess(CommonJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             /**
                                              * 跳到会议界面
                                              */
                                             mlodingDialog.cancle();
//                                                     showToast(mContext, o.getMsg());
                                             //同步离会消息
                                             syncLeaveMeeting();
                                             //离开当前界面
                                             beforeFinish();
                                             finish();

                                         } else {
                                             //提示所有错误
                                             mlodingDialog.cancle();
                                             showToast(mContext, o.getMsg());
                                             //离开当前界面
                                             beforeFinish();
                                             finish();
                                         }

                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         mlodingDialog.cancle();
                                         showToast(mContext, R.string.system_error);
                                         //离开当前界面
                                         beforeFinish();
                                         finish();
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
//                                    showToast(mContext, R.string.system_error);
                        break;
                }
            }
        }.execute();

    }

    /**
     * 锁定会议/解锁会议
     *
     * @param type
     */
    public void lockMeetingDBOper(final int type) {
        if (type == 0) {
            mlodingDialog = loding(mContext, R.string.locking);
        } else
            if (type == 1) {
                mlodingDialog = loding(mContext, R.string.unlocking);
            }


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
                 * 2.检查页面参数合法性
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

                String feature = "";
                if (type == 0) {
                    feature = "lock";
                } else
                    if (type == 1) {
                        feature = "unlock";
                    }
                /**
                 * 3.发送
                 */
                OkGo.post(URL_HOST_MEETING)
                        .tag(this)
                        .params(post_need_feature, feature)
                        .params(post_meeting_check_in_type, check_in_type)
                        .params(post_token, token)
                        .params(post_user_email, email)
                        .params(post_meeting_url, meeting_url)
                        .params(post_meeting_id, meeting_id)

                        .execute(new JsonCallback<CommonJson>() {

                                     @Override
                                     public void onSuccess(CommonJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             if (mlodingDialog != null) {
                                                 mlodingDialog.cancle();
                                             }
                                             if (type == 0) {//锁定成功
                                                 //修改UI
                                                 changeToSelected(mBtnLock, 2, "解锁");
                                                 isLock = true;

                                                 showToast(mContext, getString(R.string.lock_success));
                                             } else
                                                 if (type == 1) {
                                                     //修改UI
                                                     changeToUnSelected(mBtnLock, 2, "锁定");
                                                     isLock = false;
                                                     //解锁成功
                                                     showToast(mContext, getString(R.string.unlock_success));
                                                 }


                                         } else {
                                             //提示所有错误
                                             if (mlodingDialog != null) {
                                                 mlodingDialog.cancle();
                                             }
                                             showToast(mContext, o.getMsg());
                                         }


                                     }


                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         if (mlodingDialog != null) {
                                             mlodingDialog.cancle();
                                         }
                                         showToast(mContext, R.string.system_error);
                                     }
                                 }
                        );


                return -1;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (mlodingDialog != null) {
                    mlodingDialog.cancle();
                }
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
//                                    showToast(mContext, R.string.system_error);
                        break;
                }
            }
        }.execute();
    }

    /**
     * 初始化 所有的 参与者
     */
    @android.webkit.JavascriptInterface
    public void addMembers(final String membersInfo) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              showLog("增加所有的 参与者1");
                              showLog(membersInfo);
                              memberInfoList.clear();
                              memberInfoList.addAll(new Gson().fromJson(membersInfo, MemberListJson
                                      .class)
                                      .toMemberInfoList());
                              showLog(memberInfoList.toString());
                              showLog("增加所有的 参与者2");

                          }

                      }

        );

    }

    /**
     * 增加一个 参与者
     */
    @android.webkit.JavascriptInterface
    public void addMember(final String membersInfo) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              showLog("增加一个 参与者1");
                              showLog(membersInfo);
                              MeetingMemberInfo member = new Gson().fromJson(membersInfo,
                                      MemberJson.class)
                                      .toMemberInfo();
                              showLog(member.toString());
                              //去重
                              for (int i = 0; i < memberInfoList.size(); i++) {
                                  if (memberInfoList.get(i)
                                          .getClient_email()
                                          .equals(member.getClient_email())) {
                                      //重复的数据
                                      memberInfoList.remove(i);
                                  }
                              }
                              //增加新的
                              memberInfoList.add(member);
                              showLog(memberInfoList.toString());
                              showLog("增加一个 参与者2");

                          }

                      }

        );

    }

    /**
     * 接受 主持人 修改权限的消息
     */
    @android.webkit.JavascriptInterface
    public void alterPermission(final String is_Drawable, final String is_Talkable) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              showLog("接受 主持人 修改权限的消息");
                              boolean tmpIsDrawable, tmpIsTalkable;
                              if (!is_Drawable.equals("null")) {
                                  tmpIsDrawable = Boolean.valueOf(is_Drawable);
                                  if (tmpIsDrawable != isDrawable) {
                                      isDrawable = tmpIsDrawable;
                                      if (tmpIsDrawable) {
                                          showToast(mContext, getString(R.string.host_allow_draw));
                                      } else {

                                          showToast(mContext, getString(R.string.host_forbid_draw));
                                      }


                                  }
                              }
                              if (!is_Talkable.equals("null")) {
                                  tmpIsTalkable = Boolean.valueOf(is_Talkable);


                                  if (tmpIsTalkable != isTalkable)

                                  {
                                      isTalkable = tmpIsTalkable;
                                      if (tmpIsTalkable) {
                                          showToast(mContext, getString(R.string.host_allow_talk));
                                      } else {
                                          showToast(mContext, getString(R.string.host_forbid_talk));
                                      }
                                  }
                              }


                          }


                      }


        );

    }


    /**
     * 接受加会者的离会消息
     *
     * @param email
     * @param name
     */
    @android.webkit.JavascriptInterface
    public void memberLeave(final String email, final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //修改会议参与者
                //删除
                for (int i = 0; i < memberInfoList.size(); i++) {
                    if (memberInfoList.get(i)
                            .getClient_email()
                            .equals(email)) {
                        memberInfoList.remove(i);
                    }
                }
                /**
                 * 刷新联系人列表
                 */
                if (mRlMember.getVisibility() == View.VISIBLE) {
                    memberListAdapter.clear();
                    memberListAdapter.addAll(memberInfoList);
                    memberListAdapter.notifyDataSetChanged();
                }
                showToast(mContext, name + "离开了会议");
                showLog(memberInfoList.toString());

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
                leaveForHostLeave = true;
                //弹出框提示 主持人离会,退出
                new CommonAlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.host_left_meeting_end))
                        .setPositiveButton(
                                mContext.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //清空参与者列表
                                        memberInfoList.clear();
                                        //离会操作
                                        leaveMeetingDBOper();

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
            if (leaveForHostLeave) {
                String call =
                        "javascript:leaveForHostLeave()";

                showLog(call);
                //调用js函数
                mWvCanvas.loadUrl(call);
            } else {
                String call =
                        "javascript:syncLeaveMeeting('" + name + "')";

                showLog(call);
                //调用js函数
                mWvCanvas.loadUrl(call);
            }

        }
    }

//    /**
//     * 共享图片
//     */
//    public void sharePic() {
//        shareType = 1;
//        mRlSharePic.setVisibility(View.VISIBLE);
//
//    }


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

//                        showToast(mContext, "主持人正在共享");
                    } else {
                        mRlSharePic.setVisibility(View.GONE);//不可见
//                        showToast(mContext, "主持人正在使用白板");
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

//                        showToast(mContext, "主持人正在共享");
                    } else {
//                        showToast(mContext, "主持人正在使用白板");
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
     * 接受踢人信号
     */
    @android.webkit.JavascriptInterface
    public void kickout() {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              showToast(mContext, getString(R.string.you_kick_out));
                              //关闭页面
                              beforeFinish();
                              finish();


                          }

                      }

        );

    }


}
