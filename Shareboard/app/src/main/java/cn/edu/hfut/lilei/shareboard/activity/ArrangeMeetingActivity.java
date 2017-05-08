package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.PostRequest;

import java.util.ArrayList;
import java.util.Calendar;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.JsonEnity.MeetingJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
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
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_HOST_MEETING;
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
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_add_to_calendar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_theme;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class ArrangeMeetingActivity extends SwipeBackActivity implements View.OnClickListener {
    //控件
    private LinearLayout mLlMeetingDate, mLlMeetingStartTime, mLlMeetingEndTime, mLlAddToCalendar,
            mLlIsDrawable, mLlIsTalkable;
    private Button mBtnSave;
    private TextView mTvMeetingDate, mTvMeetingStartTime, mTvMeetingEndTime;
    private SwitchButton mBtnAddToCalendar, mBtnIsDrawable, mBtnIsTalkable;
    private EditText mEtTitle, mEtPassword;
    private LodingDialog.Builder mlodingDialog;
    //数据
    private int year, month, day, a_pm1, hour_24_1, hour_12_1, minite1, a_pm2, hour_24_2, hour_12_2,
            minite2;
    private long startMillis, endMillis, eventId = -1, meeting_url;
    private String title, description, mpassword, mfamilyName, mgivenName;
    private String feature;
    private Boolean isTalkable, isDrawable, addToCalendar;

    private int is_talkable, is_drawable, is_add_to_calendar, meeting_id;
    private String[] am_pm = {"上午", "下午"};
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_meeting);
        init();


    }

    /**
     * 获取权限
     */
    private void requestCalendar() {
        PermissionsUtil.TipInfo tip =
                new PermissionsUtil.TipInfo(null,
                        getString(R.string.should_get_this_for_insert_event_to_calendar), null,
                        null);

        if (PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            if (mBtnAddToCalendar.isChecked()) {
                mBtnAddToCalendar.setChecked(false);
            } else {
                mBtnAddToCalendar.setChecked(true);

            }
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    if (mBtnAddToCalendar.isChecked()) {
                        mBtnAddToCalendar.setChecked(false);
                    } else {
                        mBtnAddToCalendar.setChecked(true);

                    }
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    //没有授予权限，此按钮不可选
                    mBtnAddToCalendar.setChecked(false);
                }
            }, new String[]{Manifest.permission.WRITE_CALENDAR}, true, tip);
        }
    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_arrange_meeting_goback);
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
        mLlMeetingDate = (LinearLayout) findViewById(R.id.ll_arrange_meeting_date);
        mLlMeetingStartTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_start_time);
        mLlMeetingEndTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_end_time);
        mLlIsDrawable = (LinearLayout) findViewById(R.id.ll_arrange_meeting_is_drawable);
        mLlIsTalkable = (LinearLayout) findViewById(R.id.ll_arrange_meeting_is_talkable);
        mLlAddToCalendar = (LinearLayout) findViewById(R.id.ll_arrange_meeting_add_to_calendar);

        mTvMeetingDate = (TextView) findViewById(R.id.tv_arrange_meeting_date);
        mTvMeetingStartTime = (TextView) findViewById(R.id.tv_arrange_meeting_start_time);
        mTvMeetingEndTime = (TextView) findViewById(R.id.tv_arrange_meeting_end_time);
        mBtnSave = (Button) findViewById(R.id.btn_arrange_meeting_save);

        mBtnAddToCalendar = (SwitchButton) findViewById(R.id.btn_arrange_meeting_add_to_calendar);
        mBtnIsDrawable = (SwitchButton) findViewById(R.id.btn_arrange_meeting_able_to_draw);
        mBtnIsTalkable = (SwitchButton) findViewById(R.id.btn_arrange_meeting_able_to_talk);

        mEtTitle = (EditText) findViewById(R.id.et_arrange_meeting_title);
        mEtPassword = (EditText) findViewById(R.id.et_arrange_meeting_meetingpassword);
        mLlMeetingDate.setOnClickListener(this);
        mLlMeetingStartTime.setOnClickListener(this);
        mLlMeetingEndTime.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnAddToCalendar.setOnClickListener(this);

        mLlIsDrawable.setOnClickListener(this);
        mLlIsTalkable.setOnClickListener(this);
        mLlAddToCalendar.setOnClickListener(this);

        new TouchListener.Builder(mContext).setLinearLayout(mLlMeetingDate)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlMeetingStartTime)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlMeetingEndTime)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlIsDrawable)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlIsTalkable)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlAddToCalendar)
                .create();

        getBundle();


    }

    /**
     * 设置默认的当前日期，会议开始时间，会议结束时间的初始值
     */
    public void setDefaultDateTime() {
        //计算日期和时间
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //计算一个小时之后的时间
        Calendar c1 = (Calendar) c.clone();
        c1.add(Calendar.HOUR, 1);
        a_pm1 = c1.get(Calendar.AM_PM);
        hour_24_1 = c1.get(Calendar.HOUR_OF_DAY);
        hour_12_1 = c1.get(Calendar.HOUR);
        minite1 = 0;

        //计算2小时之后的时间
        Calendar c2 = (Calendar) c.clone();
        c2.add(Calendar.HOUR, 2);
        a_pm2 = c2.get(Calendar.AM_PM);
        hour_24_2 = c2.get(Calendar.HOUR_OF_DAY);
        hour_12_2 = c2.get(Calendar.HOUR);
        minite2 = 0;

        //设置当前日期，会议开始时间，会议结束时间的初始值
        mTvMeetingDate.setText(year + "/" + (month + 1) + "/" + day);
        mTvMeetingStartTime.setText(
                am_pm[a_pm1] + " " + DateTimeUtil.zeroConvert(hour_12_1) + ":00");
        mTvMeetingEndTime.setText(
                am_pm[a_pm2] + " " + DateTimeUtil.zeroConvert(hour_12_2) + ":00");
    }

    /**
     * 获取bundle参数,判断后续操作
     */
    private void getBundle() {
        Bundle bundle = this.getIntent()
                .getExtras();
        feature = bundle.getString(post_need_feature);
        //新增会议安排
        if (feature.equals("add")) {
            //加载本地存储
            initFromSharePre();
            setDefaultDateTime();
        } else
            //修改会议安排
            if (feature.equals("edit")) {
                //加载bundle传过来的参数
                startMillis = bundle.getLong("startMillis");
                endMillis = bundle.getLong("endMillis");
                eventId = bundle.getLong("eventId");
                title = bundle.getString("tvMeetingTheme");
                meeting_url = bundle.getLong(post_meeting_url);
                mpassword = bundle.getString("password");
                meeting_id = bundle.getInt(post_meeting_id);
                isDrawable = bundle.getBoolean("isDrawable");
                isTalkable = bundle.getBoolean("isTalkable");
                addToCalendar = bundle.getBoolean("addToCalendar");
                //加载页面参数的值
                initFromBundle();


            }

    }

    /**
     * 加载页面传递的参数
     */
    public void initFromBundle() {
        mEtTitle.setText(title);
        mEtPassword.setText(mpassword);
        mBtnAddToCalendar.setCheckedImmediately(addToCalendar);
        mBtnIsDrawable.setCheckedImmediately(isDrawable);
        mBtnIsTalkable.setCheckedImmediately(isTalkable);

        //设置 时间参数
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        year = start.get(Calendar.YEAR);
        month = start.get(Calendar.MONTH);
        day = start.get(Calendar.DAY_OF_MONTH);
        a_pm1 = start.get(Calendar.AM_PM);
        hour_24_1 = start.get(Calendar.HOUR_OF_DAY);
        hour_12_1 = start.get(Calendar.HOUR);
        minite1 = 0;

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endMillis);
        a_pm2 = end.get(Calendar.AM_PM);
        hour_24_2 = end.get(Calendar.HOUR_OF_DAY);
        hour_12_2 = end.get(Calendar.HOUR);
        minite2 = 0;

        //更新时间参数 到 界面
        updateDate();
        updateStartTime();
        updateEndTime();


    }

    /**
     * 加载本地存储
     */
    public void initFromSharePre() {
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<Integer> valueList = new ArrayList<>();

        keyList.add(share_meeting_is_add_to_calendar);
        keyList.add(share_meeting_is_drawable);
        keyList.add(share_meeting_is_talkable);
        valueList = SharedPrefUtil.getInstance()
                .getIntegerDatas(keyList);
        if (valueList != null) {

            mBtnAddToCalendar.setCheckedImmediately((valueList.get(0) == 1));
            mBtnIsDrawable.setCheckedImmediately((valueList.get(1) == 1));
            mBtnIsTalkable.setCheckedImmediately((valueList.get(2) == 1));
        }
        title = (String) SharedPrefUtil.getInstance()
                .getData(share_meeting_theme, "空");
        if (!title.equals("空")) {
            mEtTitle.setText(title);
        } else {
            String familyName = (String) SharedPrefUtil.getInstance()
                    .getData(share_family_name, "空");
            String givenName = (String) SharedPrefUtil.getInstance()
                    .getData(share_given_name, "空");
            if (familyName.equals("空") || givenName.equals("空")) {
                mEtTitle.setText(R.string.no_name_meeting);
            } else {
                mEtTitle.setText(familyName + givenName + getString(R.string.his_meeting));
            }
        }
        mpassword = (String) SharedPrefUtil.getInstance()
                .getData(share_meeting_password, "空");
        if (!mpassword.equals("空")) {
            mEtPassword.setText(mpassword);
        }
    }

    /**
     * 页面内容合法性检查
     *
     * @return
     */
    private boolean checkPageContent() {
        title = mEtTitle.getText()
                .toString()
                .trim();
        mpassword = mEtPassword.getText()
                .toString()
                .trim();
        if (!StringUtil.isValidTheme(title)) {
            MyAppUtil.showToast(mContext, R.string.can_not_recognize_meeting_theme);
            return false;
        }
        if (!StringUtil.isValidMeetingPassword(mpassword)) {
            MyAppUtil.showToast(mContext, R.string.can_not_recognize_meeting_password);
            return false;
        }
        return true;

    }


    /**
     * 把时间数据转化成必要格式的参数用来传递
     */
    private void timeConvertForBundle() {
        Calendar start = Calendar.getInstance();
        start.set(year, month, day);
        start.set(Calendar.HOUR_OF_DAY, hour_24_1);
        start.set(Calendar.MINUTE, minite1);
        startMillis = start.getTimeInMillis();
        if (hour_24_2 < hour_24_1) {

            //结束时间看起来比开始时间早,则假定是第二天结束
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.add(Calendar.HOUR, 24);//第二天

        }

        start.set(Calendar.HOUR_OF_DAY, hour_24_2);
        start.set(Calendar.MINUTE, minite2);
        endMillis = start.getTimeInMillis();


    }


    /**
     * 构造 日历事件描述
     *
     * @return String
     */
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
     * 设置监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_arrange_meeting_add_to_calendar:
                //请求权限但不执行后续操作
                requestCalendar();
                break;
            case R.id.ll_arrange_meeting_is_drawable:
                if (mBtnIsDrawable.isChecked()) {
                    mBtnIsDrawable.setChecked(false);
                } else {
                    mBtnIsDrawable.setChecked(true);

                }
                break;
            case R.id.ll_arrange_meeting_is_talkable:
                if (mBtnIsTalkable.isChecked()) {
                    mBtnIsTalkable.setChecked(false);
                } else {
                    mBtnIsTalkable.setChecked(true);

                }
                break;
            //为会议开始日期设置监听器
            case R.id.ll_arrange_meeting_date:
                //创建DatePickerDialog对象
                DatePickerDialog dpd = new DatePickerDialog(ArrangeMeetingActivity.this,
                        AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        year = i;
                        month = i1;
                        day = i2;
                        updateDate();
                    }
                }, year, month, day);
                dpd.setTitle(R.string.set_date);
                //显示DatePickerDialog组件
                dpd.show();
                break;


            //为会议开始时间设置监听器
            case R.id.ll_arrange_meeting_start_time:
                TimePickerDialog tpd = new TimePickerDialog(ArrangeMeetingActivity.this,
                        AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if (i < 12) {
                            a_pm1 = 0;
                            hour_12_1 = i;
                            hour_24_1 = i;
                            minite1 = i1;
                        } else {
                            a_pm1 = 1;
                            hour_12_1 = i - 12;
                            hour_24_1 = i;
                            minite1 = i1;
                        }
                        updateStartTime();
                    }
                }, hour_24_1, minite1, false);
                tpd.setTitle(R.string.set_time);
                //显示DatePickerDialog组件
                tpd.show();
                break;


            //为会议结束时间设置监听器
            case R.id.ll_arrange_meeting_end_time:
                TimePickerDialog tpd1 = new TimePickerDialog(ArrangeMeetingActivity.this,
                        AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if (i < 12) {
                            a_pm2 = 0;
                            hour_12_2 = i;
                            hour_24_2 = i;
                            minite2 = i1;
                        } else {
                            a_pm2 = 1;
                            hour_12_2 = i - 12;
                            hour_24_2 = i;
                            minite2 = i1;
                        }
                        updateEndTime();
                    }
                }, hour_24_2, minite2, false);
                tpd1.setTitle(R.string.set_time);
                tpd1.show();//显示DatePickerDialog组件
                break;


            //为保存按钮设置监听器
            case R.id.btn_arrange_meeting_save:
                if (checkPageContent()) {

                    mlodingDialog = loding(mContext, R.string.arranging);

                    timeConvertForBundle();

                    is_talkable = mBtnIsTalkable.isChecked() ? 1 : 0;
                    is_drawable = mBtnIsDrawable.isChecked() ? 1 : 0;
                    is_add_to_calendar = mBtnAddToCalendar.isChecked() ? 1 : 0;

                    if (feature.equals("add") && mBtnAddToCalendar.isChecked()) {
                        //插入日历事件提醒
                        eventId =
                                MyAppUtil.insertCalendarEvent(
                                        mContext,
                                        startMillis, endMillis,
                                        title,
                                        null,
                                        description, null);
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
                             * 2.获取会议设置
                             */
                            ArrayList<String> keyList = new ArrayList<>();
                            ArrayList<String> valueList = new ArrayList<>();
                            keyList.add(share_token);
                            keyList.add(share_user_email);
                            keyList.add(share_family_name);
                            keyList.add(share_given_name);

                            valueList = SharedPrefUtil.getInstance()
                                    .getStringDatas(keyList);
                            if (valueList == null) {
                                return -2;
                            }
                            mfamilyName = valueList.get(2);
                            mgivenName = valueList.get(3);
                            String encryptingCode;
                            try {
                                String masterPassword = "L1x#tvh_";
                                encryptingCode =
                                        StringUtil.encrypt_security(masterPassword,
                                                mpassword);
                                showLog("encrypt_security(masterPassword,mpassword) error");
                            } catch (Exception e) {
                                e.printStackTrace();
                                return -2;
                            }
                            showLog("加密后" + encryptingCode);
                            /**
                             * 3.发送
                             */
                            description = getDescribe();
                            PostRequest tmp = OkGo.post(URL_HOST_MEETING)
                                    .tag(this)
                                    .params(post_need_feature, feature)
                                    .params(post_token, valueList.get(0))
                                    .params(post_user_email, valueList.get(1))
                                    .params(post_meeting_password,
                                            encryptingCode)
//                                    .params(post_meeting_password,
//                                            StringUtil.getMD5(mpassword))
                                    .params(post_meeting_theme, title)
                                    .params(post_meeting_is_talkable, is_talkable)
                                    .params(post_meeting_is_drawable, is_drawable)
                                    .params(post_meeting_is_add_to_calendar, is_add_to_calendar)
                                    .params(post_meeting_start_time, startMillis)
                                    .params(post_meeting_end_time, endMillis)
                                    .params(post_meeting_desc, "");

                            if (feature.equals("edit")) {
                                tmp.params(post_meeting_id, meeting_id);
                                tmp.params(post_meeting_event_id, eventId);
                                tmp.execute(new JsonCallback<CommonJson>() {
                                                @Override
                                                public void onSuccess(CommonJson o, Call call,
                                                                      Response response) {
                                                    if (o.getCode() == SUCCESS) {


                                                        if (addToCalendar) {
                                                            //更新日历提醒事件 mEventID
                                                            MyAppUtil.updateCalendarEvent
                                                                    (mContext, eventId,
                                                                            startMillis,
                                                                            endMillis,
                                                                            title,
                                                                            description);
                                                        }

                                                        /**
                                                         * 跳到会议信息界面
                                                         */
                                                        jumpToNextPage();

                                                    } else {
                                                        //提示所有错误
                                                        mlodingDialog.cancle();
//                                                        showToast(mContext, o.getMsg());
                                                    }


                                                }


                                                @Override
                                                public void onError(Call call, Response response,
                                                                    Exception e) {
                                                    super.onError(call, response, e);
                                                    mlodingDialog.cancle();
//                                                    showToast(mContext, R.string.system_error);
                                                }
                                            }
                                );

                            } else
                                if (feature.equals("add")) {
                                    tmp.params(post_meeting_event_id, eventId);
                                    tmp.execute(new JsonCallback<MeetingJson>() {
                                                    @Override
                                                    public void onSuccess(MeetingJson o, Call call,
                                                                          Response response) {
                                                        if (o.getCode() == SUCCESS) {
                                                            /**
                                                             * 获取  meeting_id, meeting_url
                                                             */
                                                            meeting_id = o.getData()
                                                                    .getMeeting_id();
                                                            meeting_url = o.getData()
                                                                    .getMeeting_url();
                                                            description = getDescribe();


                                                            /**
                                                             * 跳到会议信息界面
                                                             */
                                                            jumpToNextPage();

                                                        } else {
                                                            //删除之前添加的日历事件
                                                            if (eventId != -1) {
                                                                MyAppUtil.delCalendarEvent(
                                                                        mContext, eventId);
                                                                eventId = -1;
                                                            }
                                                            //提示所有错误
                                                            mlodingDialog.cancle();
//                                                            showToast(mContext, o.getMsg());
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(Call call, Response response,
                                                                        Exception e) {
                                                        super.onError(call, response, e);
                                                        //删除之前添加的日历事件
                                                        if (eventId != -1) {
                                                            MyAppUtil.delCalendarEvent(
                                                                    mContext, eventId);
                                                            eventId = -1;
                                                        }
                                                        mlodingDialog.cancle();
//                                                        showToast(mContext, R.string.system_error);
                                                    }
                                                }
                                    );
                                }


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

        }

    }

    /**
     * 跳转到会议信息页面
     */
    public void jumpToNextPage() {
        Intent intent = new Intent();
        intent.setClass(ArrangeMeetingActivity.this,
                MeetingInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(post_meeting_id, meeting_id);
        showLog("################ArrangeMeetingactivity " + meeting_url);
        bundle.putLong(post_meeting_url,
                meeting_url);
        bundle.putLong("startMillis", startMillis);
        bundle.putLong("endMillis", endMillis);
        bundle.putLong("eventId", eventId);
        bundle.putString("tvMeetingTheme", title);
        bundle.putString("description", description);

        bundle.putString("password", mpassword);

        bundle.putBoolean("isDrawable",
                mBtnIsDrawable.isChecked());
        bundle.putBoolean("isTalkable",
                mBtnIsTalkable.isChecked());
        bundle.putBoolean("addToCalendar",
                mBtnAddToCalendar.isChecked());

        intent.putExtras(bundle);
        mlodingDialog.cancle();
        startActivity(intent);
        finish();
    }

    /**
     * 更新开会日期
     */
    private void updateDate() {

        Calendar then = Calendar.getInstance();
        then.set(year, month, day, 0, 0);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        if (then.before(now)) {
            //如果选择的开始日期早于当前，字体置为红色，表示警告
            //同时保存按钮不可用
            mTvMeetingDate.setTextColor(getResources().getColor(R.color.my_red));
            mBtnSave.setEnabled(false);
        } else {
            mTvMeetingDate.setTextColor(getResources().getColor(R.color.my_white));
            //恢复保存按钮为可用
            mBtnSave.setEnabled(true);
        }

        mTvMeetingDate.setText(year + "/" + (month + 1) + "/" + day);
    }

    /**
     * 更新会议开始时间
     */
    private void updateStartTime() {

        Calendar then = Calendar.getInstance();
        then.set(year, month, day, hour_24_1, minite1);
        Calendar now = Calendar.getInstance();
        if (then.before(now)) {
            //如果选择的开始日期早于当前
            mTvMeetingStartTime.setTextColor(getResources().getColor(R.color.my_red));
            mBtnSave.setEnabled(false);
        } else {
            mTvMeetingStartTime.setTextColor(getResources().getColor(R.color.my_white));
            mBtnSave.setEnabled(true);
        }
        mTvMeetingStartTime.setText(am_pm[a_pm1] + DateTimeUtil.zeroConvert(hour_12_1) + ":" +
                DateTimeUtil.addZero(minite1));
    }

    /**
     * 更新会议结束时间
     */
    private void updateEndTime() {
        mTvMeetingEndTime.setText(am_pm[a_pm2] + DateTimeUtil.zeroConvert(hour_12_2) + ":" +
                DateTimeUtil.addZero(minite2));
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
