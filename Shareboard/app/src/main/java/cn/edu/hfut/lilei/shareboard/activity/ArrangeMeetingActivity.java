package cn.edu.hfut.lilei.shareboard.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.MyDateTimeUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ArrangeMeetingActivity extends SwipeBackActivity {
    private Button mBtnSave;
    private LinearLayout mLlMeetingDate, mLlMeetingStartTime, mLlMeetingEndTime;
    private TextView mTvMeetingDate, mTvMeetingStartTime, mTvMeetingEndTime;
    private int year, month, day, a_pm1, hour_24_1, hour_12_1, minite1, a_pm2, hour_24_2, hour_12_2, minite2;
    private String[] am_pm = {"上午", "下午"};
    private long startMillis, endMillis;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_meeting);
        init();


    }

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

    private void updateDate() {

        Calendar then = Calendar.getInstance();
        then.set(year, month, day, 0, 0);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        if (then.before(now)) {
            //如果选择的开始日期早于当前
            mTvMeetingDate.setTextColor(getResources().getColor(R.color.my_red));
            mBtnSave.setEnabled(false);
        } else {
            mTvMeetingDate.setTextColor(getResources().getColor(R.color.my_white));
            mBtnSave.setEnabled(true);
        }

        mTvMeetingDate.setText(year + "/" + (month + 1) + "/" + day);
    }

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
        mTvMeetingStartTime.setText(am_pm[a_pm1] + MyDateTimeUtils.zeroConvert(hour_12_1) + ":" + MyDateTimeUtils.addZero(minite1));
    }

    private void updateEndTime() {
        mTvMeetingEndTime.setText(am_pm[a_pm2] + MyDateTimeUtils.zeroConvert(hour_12_2) + ":" + MyDateTimeUtils.addZero(minite2));
    }


    private void init() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow), SwipeBackLayout.EDGE_LEFT);
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
        mLlMeetingDate = (LinearLayout) findViewById(R.id.ll_arrange_meeting_date);
        mLlMeetingStartTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_start_time);
        mLlMeetingEndTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_end_time);
        mTvMeetingDate = (TextView) findViewById(R.id.tv_arrange_meeting_date);
        mTvMeetingStartTime = (TextView) findViewById(R.id.tv_arrange_meeting_start_time);
        mTvMeetingEndTime = (TextView) findViewById(R.id.tv_arrange_meeting_end_time);
        mBtnSave = (Button) findViewById(R.id.btn_arrangemeeting_save);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
//        final int a_pm = c.get(Calendar.AM_PM);
//        final int hour_24 = c.get(Calendar.HOUR_OF_DAY);
//        final int hour_12 = c.get(Calendar.HOUR);
        Calendar c1 = (Calendar) c.clone();
        c1.add(Calendar.HOUR, 1);
//        final int year1 = c1.get(Calendar.YEAR);
//        final int month1 = c1.get(Calendar.MONTH);
//        final int day1 = c1.get(Calendar.DAY_OF_MONTH);
        a_pm1 = c1.get(Calendar.AM_PM);
        hour_24_1 = c1.get(Calendar.HOUR_OF_DAY);
        hour_12_1 = c1.get(Calendar.HOUR);
        minite1 = 0;
        Calendar c2 = (Calendar) c.clone();
        c2.add(Calendar.HOUR, 2);
//        final int year2 = c2.get(Calendar.YEAR);
//        final int month2 = c2.get(Calendar.MONTH);
//        final int day2 = c2.get(Calendar.DAY_OF_MONTH);
        a_pm2 = c2.get(Calendar.AM_PM);
        hour_24_2 = c2.get(Calendar.HOUR_OF_DAY);
        hour_12_2 = c2.get(Calendar.HOUR);
        minite2 = 0;

        mTvMeetingDate.setText(year + "/" + (month + 1) + "/" + day);
        mTvMeetingStartTime.setText(am_pm[a_pm1] + " " + MyDateTimeUtils.zeroConvert(hour_12_1) + ":00");
        mTvMeetingEndTime.setText(am_pm[a_pm2] + " " + MyDateTimeUtils.zeroConvert(hour_12_2) + ":00");


        mLlMeetingDate.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View view) {

                                                  //创建DatePickerDialog对象
                                                  DatePickerDialog dpd = new DatePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                                                      @Override
                                                      public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                          year = i;
                                                          month = i1;
                                                          day = i2;
                                                          updateDate();
                                                      }
                                                  }, year, month, day);
                                                  dpd.setTitle(R.string.set_date);
                                                  dpd.show();//显示DatePickerDialog组件
                                              }
                                          }

        );

        mLlMeetingStartTime.setOnClickListener(new View.OnClickListener()

                                               {
                                                   @Override
                                                   public void onClick(View view) {
                                                       TimePickerDialog tpd = new TimePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
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
                                                       tpd.show();//显示DatePickerDialog组件
                                                   }
                                               }

        );
        mLlMeetingEndTime.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View view) {
                                                     TimePickerDialog tpd = new TimePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
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
                                                     tpd.setTitle(R.string.set_time);
                                                     tpd.show();//显示DatePickerDialog组件
                                                 }
                                             }

        );
        mBtnSave.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setClass(ArrangeMeetingActivity.this, MeetingInfoActivity.class);
                                            Bundle bundle = new Bundle();
                                            timeConvertForBundle();
                                            bundle.putLong("startMillis", startMillis);
                                            bundle.putLong("endMillis", endMillis);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    }

        );
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
