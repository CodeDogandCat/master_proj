package cn.edu.hfut.lilei.shareboard.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.models.MeetingJson;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.view.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.HOST_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_HOST_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_is_enter_meeting;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_desc;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_end_time;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_event_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_id;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_add_to_calendar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_start_time;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_theme;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_theme;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class ArrangeOrHostMeetingActivity extends SwipeBackActivity {
    //控件
    private LinearLayout mLlArrangeMeeting, mLlMyMeeting;
    private EditText mEtEmail;
    private Button mBtnStartMeeting;
    private ImageView next1, next2;
    private TextView myMeeting, arrangeMeeting;
    private LodingDialog.Builder mlodingDialog;
    //数据

    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_host_meeting);
        init();


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
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
        mLlMyMeeting = (LinearLayout) findViewById(R.id.ll_arrange_host_my_meeting);
        mLlArrangeMeeting = (LinearLayout) findViewById(R.id.ll_arrange_host_meeting);
        mBtnStartMeeting = (Button) findViewById(R.id.btn_arrange_host_join_meeting);
        next1 = (ImageView) findViewById(R.id.img_arrange_host_next1);
        next2 = (ImageView) findViewById(R.id.img_arrange_host_next2);
        myMeeting = (TextView) findViewById(R.id.tv_arrange_host_my_meeting);
        arrangeMeeting = (TextView) findViewById(R.id.tv_arrange_host_arrange_meeting);

        new TouchListener.Builder(mContext).setLinearLayout(mLlMyMeeting)
                .setTextView1(myMeeting)
                .setImageView(next1)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlArrangeMeeting)
                .setTextView1(arrangeMeeting)
                .setImageView(next2)
                .create();


        mLlMyMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ArrangeOrHostMeetingActivity.this, MyMeetingActivity.class);
                startActivity(intent);
            }
        });
        mLlArrangeMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ArrangeOrHostMeetingActivity.this, ArrangeMeetingActivity.class);
                Bundle b = new Bundle();
                b.putString(post_need_feature, "add");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        mBtnStartMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mlodingDialog = loding(mContext, R.string.arranging);

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
                         * 2.加载默认会议设置
                         */
                        ArrayList<String> keyList = new ArrayList<>();
                        ArrayList<String> valueList = new ArrayList<>();
                        keyList.add(share_token);
                        keyList.add(share_user_email);
                        keyList.add(share_meeting_password);

                        valueList = SharedPrefUtil.getInstance()
                                .getStringDatas(keyList);
                        if (valueList == null) {
                            return -2;
                        }
                        ArrayList<String> keyList2 = new ArrayList<>();

                        keyList2.add(share_meeting_is_talkable);
                        keyList2.add(share_meeting_is_drawable);
//                        keyList2.add(share_meeting_is_add_to_calendar);

                        final ArrayList<Integer> valueList2 = SharedPrefUtil.getInstance()
                                .getIntegerDatas(keyList2);

                        if (valueList2 == null) {
                            return -2;
                        }
                        String mtheme = (String) SharedPrefUtil.getInstance()
                                .getData(share_meeting_theme, "空");
                        if (!mtheme.equals("空")) {

                            String familyName = (String) SharedPrefUtil.getInstance()
                                    .getData(share_family_name, "空");
                            String givenName = (String) SharedPrefUtil.getInstance()
                                    .getData(share_given_name, "空");
                            if (familyName.equals("空") || givenName.equals("空")) {
                                mtheme = getString(R.string.no_name_meeting);
                            } else {
                                mtheme = familyName + givenName + getString(R.string.his_meeting);
                            }
                        }

                        /**
                         * 3.发送会议用户默认设置
                         */

                        OkGo.post(URL_HOST_MEETING)
                                .tag(this)
                                .params(post_need_feature, "add")
                                .params(post_token, valueList.get(0))
                                .params(post_user_email, valueList.get(1))
                                .params(post_meeting_password, StringUtil.getMD5(valueList.get(2)))
                                .params(post_meeting_theme, mtheme)
                                .params(post_meeting_is_drawable, valueList2.get(1))
                                .params(post_meeting_is_talkable, valueList2.get(0))
                                .params(post_meeting_is_add_to_calendar, 0)//立即召开会议,不要添加日历提醒
                                .params(post_meeting_start_time, DateTimeUtil.millisNow())
                                .params(post_meeting_end_time, DateTimeUtil.millisSecondInHours(1))
                                .params(post_meeting_event_id, -1)//日历事件ID
                                .params(post_meeting_desc, "")//会议描述
                                .params(post_is_enter_meeting, true)
                                .execute(new JsonCallback<MeetingJson>() {
                                             @Override
                                             public void onSuccess(MeetingJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {

                                                     /**
                                                      * 跳到登录界面
                                                      */
                                                     mlodingDialog.cancle();
//                                                     showToast(mContext, o.getMsg());
                                                     Intent intent = new Intent();
                                                     intent.setClass(ArrangeOrHostMeetingActivity.this,
                                                             MeetingActivity.class);
                                                     Bundle b = new Bundle();
                                                     b.putInt(post_meeting_check_in_type, HOST_CHECK_IN);
                                                     b.putInt(post_meeting_id, o.getData()
                                                             .getMeeting_id());
                                                     b.putLong(post_meeting_url, o.getData()
                                                             .getMeeting_url());
                                                     b.putBoolean(post_meeting_is_drawable,
                                                             valueList2.get(1) == 1);
                                                     b.putBoolean(post_meeting_is_talkable,
                                                             valueList2.get(0) == 1);
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
//                                    showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();

//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);


    }


}
