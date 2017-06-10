package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.EasyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.ChatAdapter;
import cn.edu.hfut.lilei.shareboard.adapter.CommonFragmentPagerAdapter;
import cn.edu.hfut.lilei.shareboard.fragment.ChatEmotionFragment;
import cn.edu.hfut.lilei.shareboard.fragment.ChatFunctionFragment;
import cn.edu.hfut.lilei.shareboard.model.ChatActivityInitInfo;
import cn.edu.hfut.lilei.shareboard.model.FullImageInfo;
import cn.edu.hfut.lilei.shareboard.model.MessageFromOtherInfo;
import cn.edu.hfut.lilei.shareboard.model.MessageInfo;
import cn.edu.hfut.lilei.shareboard.model.MessageSuccessInfo;
import cn.edu.hfut.lilei.shareboard.model.TalkPermissionChange;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.GlobalOnItemClickManagerUtils;
import cn.edu.hfut.lilei.shareboard.utils.MediaManager;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SettingUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.EmotionInputDetector;
import cn.edu.hfut.lilei.shareboard.widget.NoScrollViewPager;
import cn.edu.hfut.lilei.shareboard.widget.StateButton;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;

public class ChatActivity extends AppCompatActivity {

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
    @Bind(R.id.reply_bar)
    LinearLayout replyBar;

    public static ChatActivity instance = null;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;
    private MessageInfo messageInfo;
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private LodingDialog.Builder lodingDialog;
    private ImageView animView;
    private String my_email;
    private Context mContext;
    private Timer timer;
    private TimerTask task;
    private String name, familyName, GivenName, avatar;
    private boolean istalkable;
    private int check_in_type = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instance = this;
        ButterKnife.bind(this);
        EventBus.getDefault()
                .register(this);
        lodingDialog = MyAppUtil.loding(this, R.string.init);
        my_email = getIntent().getExtras()
                .getString(post_user_email);
        istalkable = getIntent().getExtras()
                .getBoolean(post_meeting_is_talkable);
        check_in_type = getIntent().getExtras()
                .getInt(post_meeting_check_in_type);
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        keyList.add(share_family_name);
        keyList.add(share_given_name);
        keyList.add(share_avatar);


        valueList = SharedPrefUtil.getInstance()
                .getStringDatas(keyList);
        familyName = valueList.get(0);
        GivenName = valueList.get(1);
        avatar = valueList.get(2);
        name = familyName + " " + GivenName;

        mContext = this;
        initWidget();
    }

    private void initWidget() {

        task = new TimerTask() {
            @Override
            public void run() {
                if (!NetworkUtil.isNetworkConnected(mContext)) {
                    //网络连接不可用
                    NetworkUtil.setNetworkMethod(mContext);
                }
            }
        };

        timer = new Timer();
        // 参数：
        // 0，延时0秒后执行。
        // 2000，每隔2秒执行1次task。
        timer.schedule(task, 0, 2000);

        //不可说的 情况
        if (check_in_type == 1) {
            if (!istalkable) {
                replyBar.setVisibility(View.GONE);
            }
        }


        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();
        if (mDetector == null) {
            showToast(this, R.string.please_relogin);
            finish();
        }

        GlobalOnItemClickManagerUtils globalOnItemClickListener =
                GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        //发布init状态
        ChatActivityInitInfo status = new ChatActivityInitInfo();
        status.setType(1);
        EventBus.getDefault()
                .post(status);


    }


    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener =
            new ChatAdapter.onItemClickListener() {
                @Override
                public void onHeaderClick(int position) {
                    Toast.makeText(ChatActivity.this, "onHeaderClick", Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onImageClick(View view, int position) {
                    int location[] = new int[2];
                    view.getLocationOnScreen(location);
                    FullImageInfo fullImageInfo = new FullImageInfo();
                    fullImageInfo.setLocationX(location[0]);
                    fullImageInfo.setLocationY(location[1]);
                    fullImageInfo.setWidth(view.getWidth());
                    fullImageInfo.setHeight(view.getHeight());
                    fullImageInfo.setImageUrl(messageInfos.get(position)
                            .getImageUrl());
                    EventBus.getDefault()
                            .postSticky(fullImageInfo);
                    startActivity(new Intent(ChatActivity.this, FullImageActivity.class));
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onVoiceClick(final ImageView imageView, final int position) {
                    if (animView != null) {
                        animView.setImageResource(res);
                        animView = null;
                    }
                    switch (messageInfos.get(position)
                            .getType()) {
                        case 1:
                            animationRes = R.drawable.voice_left;
                            res = R.mipmap.icon_voice_left3;
                            break;
                        case 2:
                            animationRes = R.drawable.voice_right;
                            res = R.mipmap.icon_voice_right3;
                            break;
                    }
                    animView = imageView;
                    animView.setImageResource(animationRes);
                    animationDrawable = (AnimationDrawable) imageView.getDrawable();
                    animationDrawable.start();
                    MediaManager.playSound(messageInfos.get(position)
                            .getFilepath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            animView.setImageResource(res);
                        }
                    });
                }
            };

    /**
     * 接受 可说 权限改变的 消息
     *
     * @param info
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenTalkPermissionChange(final TalkPermissionChange info) {
        if (check_in_type == 1) {
            if (info.istalkable() != istalkable) {//发生了改变
                istalkable = info.istalkable();
                if (info.istalkable()) {

                    replyBar.setVisibility(View.VISIBLE);
                } else {
                    replyBar.setVisibility(View.GONE);
                }


            }
        }


    }


    /**
     * 接受初始化的 消息
     *
     * @param initList
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenInitMsg(final List<MessageInfo> initList) {
        messageInfos = new ArrayList<>();
        messageInfos.addAll(initList);
        chatAdapter.addAll(messageInfos);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);
        lodingDialog.cancle();

    }

    /**
     * 接受他人的 消息
     *
     * @param Info
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenOtherMsg(final MessageFromOtherInfo Info) {

        messageInfo = Info.toMessageInfo();

        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);


    }

    /**
     * 更新自己发送的 消息的状态 为成功发送
     * <p>
     * 情况1: 没有关闭页面,那么 原来消息在 listview中的位置不会变,仍然是  IndexOfAdapter
     * <p>
     * 情况2: 关闭的页面, 但是会议页面 接收到了 success 消息,那么只能先保存在 meeting 页面的list中,
     * 下次再打开页面的时候, 原来消息在 listview中的位置 就是之前在list 中的位置,状态早已经修
     * 改为成功啦,不必担心
     * <p>
     * 情况3: 关闭页面,再打开页面,之后 原来消息的 success消息 传回来啦,当然也是先保存在 meeting页面
     * 的list中,但是 是通过 eventbus 通知当前页面 更新listview的,这时候 原来消息在 listview
     * 中的位置 就是之前在list 中的位置, 需要用 IndexOfList 来更新
     * <p>
     * 为了保证不会更新错  ,还要在更新前 比较msgid
     *
     * @param Info
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeMyMsgStatus(final MessageSuccessInfo Info) {
        boolean flag = false;

        /**
         * 情况1
         */
        //原来消息在 listview中的位置
        int position = Info.getIndexOfAdapter();
        if (position < chatAdapter.getCount() && position >= 0) {
            MessageInfo tmp = chatAdapter.getItem(position);
            //判断是否一致
            if (tmp.getMsgId()
                    .equals(Info.getMsgId())) {
                tmp.setSendState(SettingUtil.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();

                //界面更新完了,在更新list,始终保持一致,不在判断msgid ,有问题直接覆盖,始终朝adapter看齐
                messageInfos.get(position)
                        .setSendState(SettingUtil.CHAT_ITEM_SEND_SUCCESS);
                flag = true;


            }
        }
        /**
         * 情况3
         */
        if (!flag) {
            //原来消息在 list 中的位置
            int position2 = Info.getIndexOfList();
            if (position2 < chatAdapter.getCount() && position2 >= 0) {
                MessageInfo tmp = chatAdapter.getItem(position2);
                //判断是否一致
                if (tmp.getMsgId()
                        .equals(Info.getMsgId())) {
                    tmp.setSendState(SettingUtil.CHAT_ITEM_SEND_SUCCESS);
                    chatAdapter.notifyDataSetChanged();

                    //界面更新完了,在更新list,始终保持一致,不在判断msgid ,有问题直接覆盖,始终朝adapter看齐
                    messageInfos.get(position2)
                            .setSendState(SettingUtil.CHAT_ITEM_SEND_SUCCESS);


                }
            }
        }


    }

    /**
     * 模拟消息发出 的状态(转圈...)
     *
     * @param messageInfo
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMsgLocal(final MessageInfo messageInfo) {

        if (!NetworkUtil.isNetworkConnected(mContext)) {
            //网络连接不可用
            NetworkUtil.setNetworkMethod(mContext);
        }
        if (messageInfo.getSendState() != SettingUtil.CHAT_ITEM_SEND_ERROR) {

            messageInfo.setHeader(avatar);
            messageInfo.setType(SettingUtil.CHAT_ITEM_TYPE_RIGHT);
            messageInfo.setSendState(SettingUtil.CHAT_ITEM_SENDING);
            long now = DateTimeUtil.millisNow();
            messageInfo.setTime(now + "");
            messageInfo.setMsgId(my_email + now);
            messageInfo.setFamilyName(familyName);
            messageInfo.setGivenyName(GivenName);
            messageInfo.setClient_email(my_email);


            messageInfos.add(messageInfo);
            chatAdapter.add(messageInfo);

            chatList.scrollToPosition(chatAdapter.getCount() - 1);

            //发给 meeting页面
            EventBus.getDefault()
                    .post(messageInfo.toMessageFromMeInfo(chatAdapter.getCount() - 1, -1));
        }


    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault()
                .removeStickyEvent(this);
        EventBus.getDefault()
                .unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!istalkable) {
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra(post_meeting_is_talkable, istalkable);
                //设置返回数据
                ChatActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                ChatActivity.this.finish();
            } else
                if (mDetector.isSoftInputShown()) {

                    mDetector.hideSoftInput();

                } else
                    if (mDetector.isEmotionLayoutShown()) {

                        mDetector.hideEmotionLayout(false);

                    } else {
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        intent.putExtra(post_meeting_is_talkable, istalkable);
                        //设置返回数据
                        ChatActivity.this.setResult(RESULT_OK, intent);
                        //关闭Activity
                        ChatActivity.this.finish();
                    }


            return false;
        }

        return super.

                onKeyDown(keyCode, event);
    }
}

