package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.edu.hfut.lilei.shareboard.JsonEnity.FriendJson;
import cn.edu.hfut.lilei.shareboard.JsonEnity.FriendListJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.InviteMemberAdapter;
import cn.edu.hfut.lilei.shareboard.adapter.MessageListAdapter;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.model.FriendInfo;
import cn.edu.hfut.lilei.shareboard.utils.CharacterUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PinyinComparatorUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.SideBar;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FRIEND;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FRIEND_GET;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_message_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_to_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_new_msg_num;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class InviteActivity extends SwipeBackActivity implements SectionIndexer,
        InviteMemberAdapter.Callback {
    //控件
    private static final String TAG = "shareboard";
    //    private EasyRecyclerView mRv;
    private ListView listContent = null;
    private MessageListAdapter mAdapter;
    private List<Msg> mDatas = new ArrayList<>();
    private MsgDao msgDao;

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    private Button mBtnInvite;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private InviteMemberAdapter adapter;
    private EditText mTvSelected;
    private LodingDialog.Builder mlodingDialog;

    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    private int lastFirstVisibleItem = -1;
    private CharacterUtil characterUtil;
    private List<FriendInfo> SourceDateList = new ArrayList<>();

    private PinyinComparatorUtil pinyinComparatorUtil;
    private List<FriendListJson.ServerModel> serverdatas = new ArrayList<>();
    private TextView tvNoFriendAlert;
    private LinkedHashMap<String, FriendInfo> selected_map = new LinkedHashMap<>();
    private String meeting_pwd;
    private String meetingurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inivte_choose);
//        EventBus.getDefault()
//                .register(this);
        init();

    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        Intent i = getIntent();
        if (i != null) {
            meetingurl = i.getExtras()
                    .getString(post_meeting_url);
            showLog("################inviteactivity " + meetingurl);
            meeting_pwd = i.getExtras()
                    .getString(post_meeting_password);
        }

//        msgDao = GreenDaoManager.getInstance()
//                .getSession()
//                .getMsgDao();
        mBtnBack = (ImageView) findViewById(R.id.img_invite_goback);
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


        mTvSelected = (EditText) (findViewById(R.id.filter_edit));
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) findViewById(R.id.title_layout_no_friends);
        characterUtil = CharacterUtil.getInstance();
        tvNoFriendAlert = (TextView) findViewById(R.id.tv_no_friend);
        tvNoFriendAlert.setVisibility(View.GONE);
        mBtnInvite = (Button) findViewById(R.id.btn_invite_invite);
        mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invite();
            }
        });
        pinyinComparatorUtil = new PinyinComparatorUtil();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.lv_contacts_content);

        adapter = new InviteMemberAdapter(mContext, SourceDateList, this);
        sortListView.setAdapter(adapter);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InviteMemberAdapter.ViewHolder holder =
                        (InviteMemberAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                holder.checkBox.toggle();
                // 将CheckBox的选中状况记录下来
                boolean tmp = holder.checkBox.isChecked();
                InviteMemberAdapter.getIsSelected()
                        .put(i, tmp);

                // 调整选定条目
                if (tmp) {
                    if (!selected_map.containsKey(SourceDateList.get(i)
                            .getEmail())) {
                        selected_map.put(SourceDateList.get(i)
                                .getEmail(), SourceDateList.get(i));
//                        showToast(mContext, "选中" + SourceDateList.get(i)
//                                .getName());
                    }
                } else {
                    if (selected_map.containsKey(SourceDateList.get(i)
                            .getEmail())) {
                        //删除
                        selected_map.remove(SourceDateList.get(i)
                                .getEmail());
//                        showToast(mContext, "取消选中" + SourceDateList.get(i)
//                                .getName());
                    }
                }
                mTvSelected.setText("");
                Editable editable = mTvSelected.getText();
                Iterator iter = selected_map.entrySet()
                        .iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    FriendInfo info = (FriendInfo) entry.getValue();
//                    final SpannableString title = new SpannableString(info
//                            .getName());
//                    title.setSpan(new BackgroundColorSpan(Color.rgb(22, 234, 47)), 0,
//                            title.length(),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    editable.append(title);
                    editable.append(info.getName());
                    editable.append("  ");

                }
                mTvSelected.setText(editable);
                if (selected_map.size() > 0 && meetingurl != null && meeting_pwd != null) {
                    mBtnInvite.setVisibility(View.VISIBLE);
                } else {
                    mBtnInvite.setVisibility(View.GONE);
                }
//                for (int j = 0; j < InviteMemberAdapter.getIsSelectedList()
//                        .size(); j++) {
//                    if (InviteMemberAdapter.getIsSelected()
//                            .get(j)) {
//
//                    }
//                }
            }
        });
        getAllFriend();

    }

    public void invite() {


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
                /**
                 * 2.获取会议设置
                 */
                ArrayList<String> keyList = new ArrayList<>();
                ArrayList<String> valueList = new ArrayList<>();
                keyList.add(share_token);
                keyList.add(share_user_email);


                valueList = SharedPrefUtil.getInstance()
                        .getStringDatas(keyList);
                if (valueList == null) {
                    return -2;
                }
                Iterator iter = selected_map.entrySet()
                        .iterator();
                StringBuilder builder = new StringBuilder("");
                while (iter.hasNext()) {

                    Map.Entry entry = (Map.Entry) iter.next();
//                    FriendInfo tmp = (FriendInfo) entry.getValue();
//                    builder.append(tmp.getEmail());
                    builder.append(entry.getKey());
                    builder.append("###");

                }
                final String data = builder.toString();

                String encryptingCode;
                try {
                    String masterPassword = "L1x#tvh_";
                    encryptingCode =
                            StringUtil.encrypt_security(masterPassword,
                                    meeting_pwd);
                    showLog("encrypt_security(masterPassword,mpassword) error");
                } catch (Exception e) {
                    e.printStackTrace();
                    return -2;
                }

                OkGo.post(URL_FRIEND)
                        .tag(this)
                        .params(post_need_feature, "inviteFriend")
                        .params(post_token, valueList.get(0))
                        .params(post_user_email, valueList.get(1))
                        .params(post_to_user_email, meetingurl + "###" + encryptingCode)
                        .params(post_message_data,
                                data)

                        .execute(new JsonCallback<FriendJson>() {
                                     @Override
                                     public void onSuccess(FriendJson o,
                                                           Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {

                                             showToast(mContext, getString(R.string.invite_already_sent));
                                             mlodingDialog.cancle();


                                         } else {
                                             //提示所有错误
                                             mlodingDialog.cancle();
                                             showToast(mContext, o.getMsg());
                                         }

                                     }

                                     @Override
                                     public void onError(Call call,
                                                         Response response,
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

    public void getAllFriend() {

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
                 * 2.获取会议设置
                 */
                ArrayList<String> keyList = new ArrayList<>();
                ArrayList<String> valueList = new ArrayList<>();
                keyList.add(share_token);
                keyList.add(share_user_email);

                valueList = SharedPrefUtil.getInstance()
                        .getStringDatas(keyList);
                if (valueList == null) {
                    return -2;
                }

                /**
                 * 3.发送
                 */
                OkGo.post(URL_FRIEND_GET)
                        .tag(this)
                        .params(post_token, valueList.get(0))
                        .params(post_user_email, valueList.get(1))

                        .execute(new JsonCallback<FriendListJson>() {
                                     @Override
                                     public void onSuccess(FriendListJson o,
                                                           Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             showLog("收到联系人列表@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                                             showToast(getContext(), "收到联系人列表");
                                             //更新好友列表
                                             serverdatas.clear();
                                             serverdatas.addAll(o.getData());
                                             convertDatas();

                                         } else {
                                             //提示所有错误
//                                             showToast(getContext(), o.getMsg());
                                             adapter.clear();
                                             tvNoFriendAlert.setVisibility(
                                                     View.VISIBLE);
                                         }

                                     }

                                     @Override
                                     public void onError(Call call,
                                                         Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         //没有好友
                                         tvNoFriendAlert.setVisibility(
                                                 View.VISIBLE);
//                                         showToast(getContext(),
//                                                 R.string.system_error);
                                     }
                                 }
                        );


                return -1;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                switch (integer) {
                    case NET_DISCONNECT:
                        //弹出对话框，让用户开启网络
                        NetworkUtil.setNetworkMethod(mContext);
                        tvNoFriendAlert.setVisibility(
                                View.VISIBLE);
                        break;
                    case -1:
                        break;
                    case -2:
                        tvNoFriendAlert.setVisibility(
                                View.VISIBLE);
                        showToast(mContext, R.string.please_relogin);
                        break;
                    default:
                        break;
                }
            }
        }.execute();
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public void initListener() {
        sortListView.setOnScrollListener(null);

        if (SourceDateList.size() >= 2) {
            sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    int section = getSectionForPosition(firstVisibleItem);
                    int nextSection = getSectionForPosition(firstVisibleItem + 1);
                    int nextSecPosition = getPositionForSection(+nextSection);
                    if (firstVisibleItem != lastFirstVisibleItem) {
                        ViewGroup.MarginLayoutParams params =
                                (ViewGroup.MarginLayoutParams) titleLayout
                                        .getLayoutParams();
                        params.topMargin = 0;
                        titleLayout.setLayoutParams(params);
                        title.setText(SourceDateList.get(
                                getPositionForSection(section))
                                .getSortLetters());
//                    sideBar.choose=section;
//                    sideBar.invalidate();

                    }
                    if (nextSecPosition == firstVisibleItem + 1) {
                        View childView = view.getChildAt(0);
                        if (childView != null) {
                            int titleHeight = titleLayout.getHeight();
                            int bottom = childView.getBottom();
                            ViewGroup.MarginLayoutParams params =
                                    (ViewGroup.MarginLayoutParams) titleLayout
                                            .getLayoutParams();
                            if (bottom < titleHeight) {
                                float pushedDistance = bottom - titleHeight;
                                params.topMargin = (int) pushedDistance;
                                titleLayout.setLayoutParams(params);
                            } else {
                                if (params.topMargin != 0) {
                                    params.topMargin = 0;
                                    titleLayout.setLayoutParams(params);
                                }
                            }
                        }
                    }
                    lastFirstVisibleItem = firstVisibleItem;

                }
            });
        }

    }

    private void convertDatas() {
        SourceDateList.clear();
        for (int i = 0; i < serverdatas.size(); i++) {
            FriendInfo sortModel = new FriendInfo();
//            sortModel.setPhoto(getResources().getDrawable(R.drawable.er));
            String name = serverdatas.get(i)
                    .getFamilyName() + serverdatas.get(i)
                    .getGivenName();
            sortModel.setName(name);
            sortModel.setStatus("");
            sortModel.setEmail(serverdatas.get(i)
                    .getEmail());
            sortModel.setAvatar(serverdatas.get(i)
                    .getAvatar());


            String pinyin = characterUtil.getSelling(name);
            String sortString = pinyin.substring(0, 1)
                    .toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            SourceDateList.add(sortModel);
//            Log.i("hi", sortModel.getSortLetters() + " " + sortModel.getName());

        }
        if (SourceDateList.size() == 0) {
            adapter.clear();
            //没有好友
            tvNoFriendAlert.setVisibility(
                    View.VISIBLE);
        } else {
            //有好友
            tvNoFriendAlert.setVisibility(
                    View.GONE);
            // 根据a-z进行排序源数据
            Collections.sort(SourceDateList, pinyinComparatorUtil);
            adapter.updateListView(SourceDateList);
            initListener();

        }

    }

    /**
     */
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position)
                .getSortLetters()
                .charAt(0);
    }

    /**
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i)
                    .getSortLetters();
            char firstChar = sortStr.toUpperCase()
                    .charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Event e = new Event();
//        e.flag = 0;
        SharedPrefUtil.getInstance()
                .saveData(share_new_msg_num, 0);
//        EventBus.getDefault()
//                .postSticky(e);
//
//        EventBus.getDefault()
//                .unregister(this);
    }


    @Override
    public void click(int i, boolean isChecked) {
        if (isChecked) {
            if (!selected_map.containsKey(SourceDateList.get(i)
                    .getEmail())) {
                //添加
                selected_map.put(SourceDateList.get(i)
                        .getEmail(), SourceDateList.get(i));
                Editable tmp = mTvSelected.getText();


                //显示
                final SpannableString title = new SpannableString(SourceDateList.get(i)
                        .getName());
                title.setSpan(new BackgroundColorSpan(Color.rgb(22, 234, 47)), 0, title.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tmp.append(" ");
                tmp.append(title);
                mTvSelected.setText(tmp);

            }
            showToast(mContext, "选中" + SourceDateList.get(i)
                    .getName());
        } else {
            if (selected_map.containsKey(SourceDateList.get(i)
                    .getEmail())) {
                //删除
                selected_map.remove(SourceDateList.get(i)
                        .getEmail());

                Iterator iter = selected_map.entrySet()
                        .iterator();
                mTvSelected.setText("");
                Editable editable = mTvSelected.getText();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    System.out.println(entry.getKey() + "=" + entry.getValue());
                    FriendInfo tmp = (FriendInfo) entry.getValue();
                    final SpannableString title = new SpannableString(tmp
                            .getName());
                    title.setSpan(new BackgroundColorSpan(Color.rgb(22, 234, 47)), 0,
                            title.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editable.append(title);
                    editable.append(" ");

                }
                mTvSelected.setText(editable);


            }
            showToast(mContext, "取消选中" + SourceDateList.get(i)
                    .getName());
        }
        if (selected_map.size() > 0 && meetingurl != null && meeting_pwd != null) {
            mBtnInvite.setVisibility(View.VISIBLE);
        } else {
            mBtnInvite.setVisibility(View.GONE);
        }


    }
}
