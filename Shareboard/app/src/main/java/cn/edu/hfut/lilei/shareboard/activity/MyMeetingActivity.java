package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MeetingListAdapter;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.JsonEnity.MeetingListJson;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import cn.edu.hfut.lilei.shareboard.widget.refreshandload.SwipeRefreshLayout;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.string.meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.HOST_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_ENTER_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_HOST_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_page;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class MyMeetingActivity extends SwipeBackActivity implements SwipeRefreshLayout
        .OnRefreshListener, SwipeRefreshLayout.OnLoadListener, AdapterView.OnItemClickListener,
        MeetingListAdapter.Callback {
    //控件
    private Button mBtnFresh;
    private ListView listContent = null;
    private LodingDialog.Builder mlodingDialog;
    private SwipeRefreshLayout mSwipeLayout;
    //数据
    List<MeetingListJson.ServerModel> data = new ArrayList<>();
    private MeetingListAdapter adapter;
    private int page;
    private int totalPage;
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_meeting);
        getBundle();
        init();


    }


    /**
     * 获取页面参数
     */
    private void getBundle() {

    }

    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_my_meeting_goback);
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

        //下拉刷新,上拉加载
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setOnLoadListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeLayout.setLoadNoFull(false);

        page = 0;
        totalPage = 0;
        listContent = (ListView) findViewById(R.id.lv_my_meeting);
        adapter = new MeetingListAdapter(mContext, this);
        listContent.setAdapter(adapter);
        listContent.setOnItemClickListener(this);


        mBtnFresh = (Button) findViewById(R.id.btn_my_meeting_refresh);
        mBtnFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeLayout.startRefresh();
            }
        });
        //刷新
        mSwipeLayout.startRefresh();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //刷新
        mSwipeLayout.startRefresh();
    }

    /**
     *
     */
    private void loadMeetingList(final int type) {
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
                 * 2.获取本地参数
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

                OkGo.post(URL_HOST_MEETING)
                        .tag(this)
                        .params(post_need_feature, "get")
                        .params(post_token, valueList.get(0))
                        .params(post_user_email, valueList.get(1))
                        .params(post_meeting_page, page)
                        .params(post_meeting_id, meeting_id)
                        .execute(new JsonCallback<MeetingListJson>() {
                                     @Override
                                     public void onSuccess(MeetingListJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             if (type == 0) {//getTotalMeeting
                                                 showLog("---------------length" + o.getData()
                                                         .size());
                                                 data.addAll(o.getData());
                                                 adapter.clear();
                                                 adapter.addAll(data);
                                             } else
                                                 if (type == 1) {//getAdditionalMeeting

                                                     data.addAll(o.getData());//增加新的部分
                                                     adapter.addAll(o.getData());//增加新的部分
                                                 }

                                         } else

                                         {
                                             // 否则提示没有了
                                             showToast(mContext, getString(R.string.nothing_else));
                                         }
                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         showToast(mContext, R.string.system_error);
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

    /**
     * 获取会议总页数
     */
    private void getTotalPage() {
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
                 * 2.获取本地参数
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

                OkGo.post(URL_HOST_MEETING)
                        .tag(this)
                        .params(post_need_feature, "getPages")
                        .params(post_token, valueList.get(0))
                        .params(post_user_email, valueList.get(1))
                        .execute(new JsonCallback<CommonJson>() {
                                     @Override
                                     public void onSuccess(CommonJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {

                                             totalPage = o.getCode();
                                             // 和page比较，如果page+1<=totalpage,就加载下一页，这个时候不要清空timeline
                                             if (page + 1 <= totalPage) {

                                                 loadMeetingList(1);//loadAddition

                                             } else {
                                                 // 否则提示没有了
                                                 showToast(mContext, getString(R.string.nothing_else));

                                             }


                                         } else {
                                             // 否则提示没有了
                                             showToast(mContext, getString(R.string.nothing_else));
                                         }


                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         showToast(mContext, R.string.system_error);
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


    @Override
    public void onLoad() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mSwipeLayout.setLoading(false);
                ++page;
                getTotalPage();

            }
        }, 200);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                page = 0;
                data.clear();
                adapter.clear();
                loadMeetingList(0);

            }
        }, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        showLog("点到了我");
        /**
         * 跳转到 meetingInfo页面
         */
        MeetingListJson.ServerModel meeting = adapter.getItem(i);
        Intent intent = new Intent(mContext, MeetingInfoActivity.class);
//        System.out.println("点击了" + i);
        Bundle bundle = new Bundle();
        bundle.putLong("startMillis", meeting.getMeeting_start_time());
        bundle.putLong("endMillis", meeting.getMeeting_end_time());
        bundle.putLong("eventId", meeting.getEvent_id());
        bundle.putString("tvMeetingTheme", meeting.getMeeting_theme());
        bundle.putString("description", meeting.getMeeting_desc());
        bundle.putLong(post_meeting_url, meeting.getMeeting_url());
        bundle.putString("password", meeting.getMeeting_password());
        bundle.putInt(post_meeting_id, meeting.getMeeting_id());
        bundle.putBoolean("isDrawable", meeting.getMeeting_is_drawable() == 1);
        bundle.putBoolean("isTalkable", meeting.getMeeting_is_talkable() == 1);
        bundle.putBoolean("addToCalendar", meeting.getMeeting_is_add_to_calendar() == 1);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    @Override
    public void click(View v) {
        /**
         * 进入会议
         */
        final long meeting_url = data.get((Integer) v.getTag())
                .getMeeting_url();
        final int meeting_id = data.get((Integer) v.getTag())
                .getMeeting_id();
        mlodingDialog = loding(mContext, R.string.entering);

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

                OkGo.post(URL_ENTER_MEETING)
                        .tag(this)
                        .params(post_meeting_check_in_type, HOST_CHECK_IN)
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
                                             Intent intent = new Intent();
                                             intent.setClass(mContext,
                                                     MeetingActivity.class);
                                             Bundle b = new Bundle();
                                             b.putInt(post_meeting_check_in_type, HOST_CHECK_IN);
                                             b.putInt(post_meeting_id, meeting_id);
                                             b.putLong(post_meeting_url, meeting_url);
                                             intent.putExtras(b);
                                             startActivity(intent);

                                         } else {
                                             //提示所有错误
                                             mlodingDialog.cancle();
                                             showToast(mContext, o.getMsg());
                                         }

                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         mlodingDialog.cancle();
                                         showToast(mContext, R.string.system_error);
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
