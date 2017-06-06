package cn.edu.hfut.lilei.shareboard.activity;

import android.annotation.TargetApi;
import android.app.Activity;
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

import cn.edu.hfut.lilei.shareboard.JsonEnity.MeetingJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.string.familyName;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.HOST_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.MEETING_REQUEST_CODE;
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
    private int year, month, day, a_pm1, hour_24_1, hour_12_1, minite1, a_pm2, hour_24_2, hour_12_2,
            minite2;
    private long startMillis, endMillis, eventId = -1;
    private String meeting_url;
    private String title, description, mpassword, mfamilyName, mgivenName;
    private String[] am_pm = {"上午", "下午"};
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_host_meeting);
        init();


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_hostmeeting_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

                mlodingDialog = loding(mContext, R.string.entering);
                MyAppUtil.changeBtnDisable(mBtnStartMeeting);

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
                        if (mtheme.equals("空")) {

                            mfamilyName = (String) SharedPrefUtil.getInstance()
                                    .getData(share_family_name, "空");
                            mgivenName = (String) SharedPrefUtil.getInstance()
                                    .getData(share_given_name, "空");
                            if (mfamilyName.equals("空") || mgivenName.equals("空")) {
                                mtheme = getString(R.string.no_name_meeting);
                            } else {
                                mtheme = familyName + mgivenName + getString(R.string.his_meeting);
                            }
                        }
                        startMillis = DateTimeUtil.millisNow();
                        endMillis = DateTimeUtil.millisSecondInHours(1);
                        description = "";
                        showLog("theme: " + mtheme);
                        showLog("description: " + description);

                        String encryptingCode;
                        try {
                            String masterPassword = "L1x#tvh_";
                            encryptingCode =
                                    StringUtil.encrypt_security(masterPassword, valueList.get(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                            showLog("encrypt_security(masterPassword,valueList.get(2)) error");
                            return -2;
                        }
                        showLog("加密后" + encryptingCode);

                        /**
                         * 3.发送会议用户默认设置
                         */

                        final ArrayList<String> finalValueList = valueList;
                        OkGo.post(URL_HOST_MEETING)
                                .tag(this)
                                .params(post_need_feature, "add")
                                .params(post_token, valueList.get(0))
                                .params(post_user_email, valueList.get(1))
//                                .params(post_meeting_password, StringUtil.getMD5(valueList.get(2)))
//                                .params(post_meeting_password, valueList.get(2))
                                .params(post_meeting_password, encryptingCode)
                                .params(post_meeting_theme, mtheme)
                                .params(post_meeting_is_drawable, valueList2.get(1))
                                .params(post_meeting_is_talkable, valueList2.get(0))
                                .params(post_meeting_is_add_to_calendar, 0)//立即召开会议,不要添加日历提醒
                                .params(post_meeting_start_time, startMillis)
                                .params(post_meeting_end_time, endMillis)
                                .params(post_meeting_event_id, -1)//日历事件ID
                                .params(post_meeting_desc, "")//会议描述
                                .params(post_is_enter_meeting, true)
                                .execute(new JsonCallback<MeetingJson>() {
                                             @Override
                                             public void onSuccess(MeetingJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {

                                                     /**
                                                      * 跳到会议界面
                                                      */

//                                                     showToast(mContext, o.getMsg());
                                                     Intent intent = new Intent();
                                                     intent.setClass(ArrangeOrHostMeetingActivity.this,
                                                             MeetingActivity.class);
                                                     Bundle b = new Bundle();
                                                     b.putInt(post_meeting_check_in_type, HOST_CHECK_IN);
                                                     b.putInt(post_meeting_id, o.getData()
                                                             .getMeeting_id());
                                                     b.putBoolean(post_meeting_is_drawable,
                                                             valueList2.get(1) == 1);
                                                     b.putBoolean(post_meeting_is_talkable,
                                                             valueList2.get(0) == 1);


                                                     b.putString(post_meeting_url, o.getData()
                                                             .getMeeting_url());
                                                     showLog("@@@@@@@@@@@meeting url" + o.getData()
                                                             .getMeeting_url());
                                                     b.putString(post_meeting_password,
                                                             finalValueList.get(2));

                                                     intent.putExtras(b);
                                                     startActivity(intent);
//                                                     startActivityForResult(intent, MEETING_REQUEST_CODE);
                                                     mlodingDialog.cancle();
                                                     finish();


                                                 } else {
                                                     //提示所有错误
                                                     mlodingDialog.cancle();
//                                                     showToast(mContext, o.getMsg());
                                                     MyAppUtil.changeBtnClickable
                                                             (mBtnStartMeeting, R.drawable.btn_yellow);
                                                 }

                                             }

                                             @Override
                                             public void onError(Call call, Response response,
                                                                 Exception e) {
                                                 super.onError(call, response, e);
                                                 mlodingDialog.cancle();
//                                                 showToast(mContext, R.string.system_error);
                                                 mlodingDialog.cancle();
                                                 MyAppUtil.changeBtnClickable(mBtnStartMeeting,
                                                         R.drawable.btn_yellow);
                                             }
                                         }
                                );


                        return -1;

                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        mlodingDialog.cancle();
                        MyAppUtil.changeBtnClickable(mBtnStartMeeting, R.drawable.btn_yellow);
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

    public String getDescribe() {
        return String.format(getResources().getString(
                R.string.invite_content),
                mfamilyName + mgivenName,
                title,
                DateTimeUtil.getPreString(
                        startMillis) +
                        am_pm[a_pm1] +
                        DateTimeUtil.zeroConvert
                                (hour_12_1) +
                        ":" +
                        DateTimeUtil.addZero(
                                minite1),
                String.valueOf(meeting_url),
                mpassword);
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
            case MEETING_REQUEST_CODE:
                MyAppUtil.changeBtnClickable
                        (mBtnStartMeeting, R.drawable.btn_yellow);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);


    }


}
