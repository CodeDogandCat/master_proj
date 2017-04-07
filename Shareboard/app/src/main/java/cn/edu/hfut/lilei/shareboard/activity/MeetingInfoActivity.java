package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.enity.AppInfo;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.AddContactDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil.getPreString;
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
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class MeetingInfoActivity extends SwipeBackActivity {
    //控件
    private View dialogView = null;//弹出窗口
    private TextView mTvMeetingDate, mTvMeetingTheme, mTvMeetingID, mTvMeetingLength;
    private Button mBtnEdit, mBtnStart, mBtnAddToCalendar, mBtnAddInvite, mBtnDelete;
    private ListView listContent = null;
    private LodingDialog.Builder mlodingDialog;
    //数据
    private int year, month, day, a_pm1, hour_12_1, minite1, meeting_id;
    private int queryAppInfoflag = -1;
    private long mEventID = -1, startMillis, endMillis, lenght, meeting_url;
    private String title, description, mpassword, mid;
    private Boolean isTalkable, isDrawable, addToCalendar;
    private String preStr = "";
    private String[] am_pm = {"上午", "下午"};
    private List<AppInfo> mlistAppInfo;
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);
        getBundle();
        init();


    }


    /**
     * 获取页面参数
     */
    private void getBundle() {
        Bundle bundle = this.getIntent()
                .getExtras();
        startMillis = bundle.getLong("startMillis");
        endMillis = bundle.getLong("endMillis");
        mEventID = bundle.getLong("eventId");
        title = bundle.getString("tvMeetingTheme");
        description = bundle.getString("description");
        meeting_url = bundle.getLong(post_meeting_url);
        mpassword = bundle.getString("password");
        meeting_id = bundle.getInt(post_meeting_id);
        isDrawable = bundle.getBoolean("isDrawable");
        isTalkable = bundle.getBoolean("isTalkable");
        addToCalendar = bundle.getBoolean("addToCalendar");

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        year = start.get(Calendar.YEAR);
        month = start.get(Calendar.MONTH);
        day = start.get(Calendar.DAY_OF_MONTH);
        a_pm1 = start.get(Calendar.AM_PM);
        hour_12_1 = start.get(Calendar.HOUR);
        minite1 = start.get(Calendar.MINUTE);

        lenght = DateTimeUtil.minuteLength(startMillis, endMillis);
        preStr = getPreString(startMillis);

    }

    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_meeting_info_goback);
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

        //获得控件
        mBtnEdit = (Button) findViewById(R.id.btn_meeting_info_edit);
        mBtnStart = (Button) findViewById(R.id.btn_meeting_info_start);
        mBtnAddToCalendar = (Button) findViewById(R.id.btn_meeting_info_add_to_calendar);
        mBtnAddInvite = (Button) findViewById(R.id.btn_meeting_info_invite);
        mBtnDelete = (Button) findViewById(R.id.btn_meeting_info_delete);
        mBtnEdit.setOnClickListener(listener);
        mBtnStart.setOnClickListener(listener);
        mBtnAddToCalendar.setOnClickListener(listener);
        mBtnAddInvite.setOnClickListener(listener);
        mBtnDelete.setOnClickListener(listener);

        mTvMeetingDate = (TextView) findViewById(R.id.tv_meeting_info_date);
        mTvMeetingTheme = (TextView) findViewById(R.id.tv_meeting_info_theme);
        mTvMeetingID = (TextView) findViewById(R.id.tv_meeting_info_meeting_id);
        mTvMeetingLength = (TextView) findViewById(R.id.tv_meeting_info_length);
        updateTvMeetingDate();

        mTvMeetingTheme.setText(title);
        String tmp = String.valueOf(meeting_url);
        if (tmp.length() == 12) {

            mid = tmp.substring(0, 4) + "-" + tmp.substring(4, 8) + "-" + tmp.substring(8, 12);
        } else {
            mid = tmp;
        }
        mTvMeetingID.setText(mid);
        mTvMeetingLength.setText(lenght + getResources().getString(R.string.minute));

        //邀请方式弹出窗内部的listview
        LayoutInflater inflater = LayoutInflater.from(mContext);
        dialogView = inflater.inflate(R.layout.dialog_invite_chooser, null);
        listContent = (ListView) dialogView.findViewById(R.id.lv_dialog_invite_chooser);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                //延迟弹出邀请方式选择框
                MyAppUtil.invite(mContext, String.format(getResources().getString(R.string
                        .invite_title), title), description, mlistAppInfo);
            }
        }.execute();


    }

    public void disableAllBtn() {
        MyAppUtil.changeBtnDisable(mBtnStart);

        MyAppUtil.changeBtnDisable(mBtnDelete);
        MyAppUtil.changeBtnDisable(mBtnAddInvite);
        MyAppUtil.changeBtnDisable(mBtnAddToCalendar);
        mBtnEdit.setClickable(false);
    }

    public void clickableAllBtn() {
        MyAppUtil.changeBtnClickable(mBtnStart, R.drawable.btn_yellow);

        MyAppUtil.changeBtnClickable(mBtnDelete, R.drawable.btn_black);
        MyAppUtil.changeBtnClickable(mBtnAddInvite, R.drawable.btn_black);
        MyAppUtil.changeBtnClickable(mBtnAddToCalendar, R.drawable.btn_black);
        mBtnEdit.setClickable(true);
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_meeting_info_edit:
                    edit();
                    break;
                case R.id.btn_meeting_info_start:
                    startMeeting();
                    break;
                case R.id.btn_meeting_info_add_to_calendar:
                    requestCalendar();
                    break;
                case R.id.btn_meeting_info_invite:
                    MyAppUtil.invite(mContext, String.format(getResources().getString(R.string
                            .invite_title), title), description, mlistAppInfo);
                    break;
                case R.id.btn_meeting_info_delete:
                    delete();
                    break;

            }
        }
    };

    /**
     * 获取权限
     */
    private void requestCalendar() {
        showLog("eventid" + mEventID);
        PermissionsUtil.TipInfo tip =
                new PermissionsUtil.TipInfo(null,
                        getString(R.string.should_get_this_for_insert_event_to_calendar), null,
                        null);

        if (PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            if (mEventID == -1) {
                //插入日历事件提醒
                mEventID = MyAppUtil.insertCalendarEvent(mContext, startMillis, endMillis, title,
                        null,
                        description, null);
                MyAppUtil.viewCalendarEvent(mContext, startMillis);

            } else {
                //已经自动添加到了日历，当前只需要查看
                MyAppUtil.viewCalendarEvent(mContext, startMillis);
            }

        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    if (mEventID == -1) {
                        //插入日历事件提醒
                        mEventID = MyAppUtil.insertCalendarEvent(mContext, startMillis, endMillis,
                                title,
                                null,
                                description, null);
                        MyAppUtil.viewCalendarEvent(mContext, startMillis);
                    } else {
                        //已经自动添加到了日历，当前只需要查看
                        MyAppUtil.viewCalendarEvent(mContext, startMillis);
                    }
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                }
            }, new String[]{Manifest.permission.WRITE_CALENDAR}, true, tip);
        }
    }


    /**
     * 更新开会时间
     */
    private void updateTvMeetingDate() {
        mTvMeetingDate.setText(
                preStr + am_pm[a_pm1] + DateTimeUtil.zeroConvert(hour_12_1) + ":" +
                        DateTimeUtil.addZero(minite1));
    }

    /**
     * 编辑会议
     */
    private void edit() {
        Intent intent = new Intent();
        intent.setClass(mContext,
                ArrangeMeetingActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(post_need_feature, "edit");
        bundle.putInt(post_meeting_id, meeting_id);
        bundle.putInt(post_meeting_id, meeting_id);
        bundle.putLong(post_meeting_url,
                meeting_url);
        bundle.putLong("startMillis", startMillis);
        bundle.putLong("endMillis", endMillis);
        bundle.putLong("eventId", mEventID);
        bundle.putString("tvMeetingTheme", title);
        bundle.putString("password", mpassword);
        bundle.putBoolean("isDrawable",
                isDrawable);
        bundle.putBoolean("isTalkable",
                isTalkable);
        bundle.putBoolean("addToCalendar",
                addToCalendar);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 删除会议
     */
    private void delete() {

        new AddContactDialog.Builder(MeetingInfoActivity.this)
                .setTitle(getString(R.string.delete_meeting_confirm))
                .setPositiveButton(
                        MeetingInfoActivity.this.getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /**
                                 * 删除数据库记录
                                 */
                                mlodingDialog = loding(mContext, R.string.deleting);
                                disableAllBtn();

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
                                         * 2.获取参数
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
                                                .params(post_need_feature, "delete")
                                                .params(post_token, valueList.get(0))
                                                .params(post_user_email, valueList.get(1))
                                                .params(post_meeting_id, meeting_id)
                                                .execute(new JsonCallback<CommonJson>() {
                                                             @Override
                                                             public void onSuccess(CommonJson o, Call call,
                                                                                   Response response) {
                                                                 if (o.getCode() == SUCCESS) {

                                                                     /**
                                                                      * 删除指定的日历提醒时间
                                                                      */

                                                                     MyAppUtil.delCalendarEvent(mContext,
                                                                             mEventID);

                                                                     /**
                                                                      * 跳到界面
                                                                      */

                                                                     Intent intent = new Intent();
                                                                     intent.setClass(mContext,
                                                                             ArrangeOrHostMeetingActivity.class);
                                                                     mlodingDialog.cancle();
                                                                     mContext.startActivity(intent);
                                                                     clickableAllBtn();
                                                                     ((Activity) mContext).finish();

                                                                 } else {
                                                                     //提示所有错误
                                                                     mlodingDialog.cancle();
                                                                     clickableAllBtn();
                                                                     showToast(mContext, o.getMsg());
                                                                 }

                                                             }

                                                             @Override
                                                             public void onError(Call call,
                                                                                 Response response,
                                                                                 Exception e) {
                                                                 super.onError(call, response, e);
                                                                 mlodingDialog.cancle();
                                                                 clickableAllBtn();
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
                                        clickableAllBtn();
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
                        })
                .setNegativeButton(
                        MeetingInfoActivity.this.getString(R.string.cancel),
                        null)
                .show();


    }


    /**
     * 进入会议
     */
    private void startMeeting() {
        mlodingDialog = loding(mContext, R.string.entering);
        disableAllBtn();


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
                                             b.putBoolean(post_meeting_is_drawable, isDrawable);
                                             b.putBoolean(post_meeting_is_talkable, isTalkable);
                                             intent.putExtras(b);
                                             startActivity(intent);
                                             mlodingDialog.cancle();
                                             finish();

                                         } else {
                                             //提示所有错误
                                             showToast(mContext, o.getMsg());
                                             mlodingDialog.cancle();
                                             clickableAllBtn();
                                         }

                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         mlodingDialog.cancle();
                                         showToast(mContext, R.string.system_error);
                                         clickableAllBtn();
                                     }
                                 }
                        );


                return -1;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mlodingDialog.cancle();
                clickableAllBtn();
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


}
