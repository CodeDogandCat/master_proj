package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.Calendar;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.Config;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtils;
import cn.edu.hfut.lilei.shareboard.utils.MyDateTimeUtils;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ArrangeMeetingActivity extends SwipeBackActivity implements View.OnClickListener {
    //控件
    private LinearLayout mLlMeetingDate, mLlMeetingStartTime, mLlMeetingEndTime;
    private Button mBtnSave;
    private TextView mTvMeetingDate, mTvMeetingStartTime, mTvMeetingEndTime;
    private SwitchButton mBtnAddToCalendar;
    private EditText mEtTitle;
    //数据
    private int year, month, day, a_pm1, hour_24_1, hour_12_1, minite1, a_pm2, hour_24_2, hour_12_2,
            minite2;
    private long startMillis, endMillis, eventId = -1;
    private String title, description, mid, mpassword;
    private String[] am_pm = {"上午", "下午"};
    //上下文参数
    private Context mContext;

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
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {

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
        //右滑返回
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow),
                SwipeBackLayout.EDGE_LEFT);
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
        mTvMeetingDate = (TextView) findViewById(R.id.tv_arrange_meeting_date);
        mTvMeetingStartTime = (TextView) findViewById(R.id.tv_arrange_meeting_start_time);
        mTvMeetingEndTime = (TextView) findViewById(R.id.tv_arrange_meeting_end_time);
        mBtnSave = (Button) findViewById(R.id.btn_arrange_meeting_save);
        mBtnAddToCalendar = (SwitchButton) findViewById(R.id.btn_arrange_meeting_add_to_calendar);
        mEtTitle = (EditText) findViewById(R.id.et_arrange_meeting_title);
        mLlMeetingDate.setOnClickListener(this);
        mLlMeetingStartTime.setOnClickListener(this);
        mLlMeetingEndTime.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnAddToCalendar.setOnClickListener(this);

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
                am_pm[a_pm1] + " " + MyDateTimeUtils.zeroConvert(hour_12_1) + ":00");
        mTvMeetingEndTime.setText(
                am_pm[a_pm2] + " " + MyDateTimeUtils.zeroConvert(hour_12_2) + ":00");
    }

    //进行页面内容合法性检查
    private boolean checkPageContent() {
        return true;
    }

    /**
     * 设置监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_arrange_meeting_add_to_calendar:
                //请求权限但不执行后续操作
                requestCalendar();
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
                    timeConvertForBundle();
                    if (mBtnAddToCalendar.isChecked()) {
                        //插入日历事件提醒
                        eventId =
                                MyAppUtils.insertCalendarEvent(mContext, startMillis, endMillis,
                                        title,
                                        null,
                                        description, null);
                    }

                    //保存到数据库

                    //保存到参数
                    Intent intent = new Intent();
                    intent.setClass(ArrangeMeetingActivity.this, MeetingInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("startMillis", startMillis);
                    bundle.putLong("endMillis", endMillis);
                    bundle.putLong("eventId", eventId);
                    bundle.putString("title", title);
                    bundle.putString("description", description);
                    bundle.putString("mid", mid);
                    bundle.putString("mpassword", mpassword);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Log.i(Config.TAG, "CCCCCCCCCCCC");
                }

                break;
            default:
                break;
        }
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
        title = mEtTitle.getText()
                .toString()
                .trim();
        mid = "123-4444-8888";
        mpassword = "888666";
        description =
                String.format(getResources().getString(R.string.invite_content), "李磊",
                        "李磊的白板会议",
                        MyDateTimeUtils.getPreString(startMillis) + am_pm[a_pm1] +
                                MyDateTimeUtils.zeroConvert
                                        (hour_12_1) +
                                ":" +
                                MyDateTimeUtils.addZero(minite1), mid, mpassword);

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
        mTvMeetingStartTime.setText(am_pm[a_pm1] + MyDateTimeUtils.zeroConvert(hour_12_1) + ":" +
                MyDateTimeUtils.addZero(minite1));
    }

    /**
     * 更新会议结束时间
     */
    private void updateEndTime() {
        mTvMeetingEndTime.setText(am_pm[a_pm2] + MyDateTimeUtils.zeroConvert(hour_12_2) + ":" +
                MyDateTimeUtils.addZero(minite2));
    }


}
