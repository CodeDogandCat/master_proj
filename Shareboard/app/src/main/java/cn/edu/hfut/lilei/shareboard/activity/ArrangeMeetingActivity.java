package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.edu.hfut.lilei.shareboard.R;


public class ArrangeMeetingActivity extends Activity {
    private Button mBtnSave;
    private LinearLayout mLlMeetingDate, mLlMeetingStartTime, mLlMeetingEndTime;
    private TextView mTvMeetingDate, mTvMeetingStartTime, mTvMeetingEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_meeting);
        init();


    }

    private void init() {
        mLlMeetingDate = (LinearLayout) findViewById(R.id.ll_arrange_meeting_date);
        mLlMeetingStartTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_start_time);
        mLlMeetingEndTime = (LinearLayout) findViewById(R.id.ll_arrange_meeting_end_time);
        mTvMeetingDate = (TextView) findViewById(R.id.tv_arrange_meeting_date);
        mTvMeetingStartTime = (TextView) findViewById(R.id.tv_arrange_meeting_start_time);
        mTvMeetingEndTime = (TextView) findViewById(R.id.tv_arrange_meeting_end_time);
        mBtnSave = (Button) findViewById(R.id.btn_arrangemeeting_save);
        String[] am_pm = {"上午", "下午"};
        Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int a_pm = c.get(Calendar.AM_PM);
        final int hour_24 = c.get(Calendar.HOUR_OF_DAY);
        final int hour_12 = c.get(Calendar.HOUR);
        Calendar c1 = (Calendar) c.clone();
        c1.add(Calendar.HOUR, 1);
        final int year1 = c1.get(Calendar.YEAR);
        final int month1 = c1.get(Calendar.MONTH);
        final int day1 = c1.get(Calendar.DAY_OF_MONTH);
        final int a_pm1 = c1.get(Calendar.AM_PM);
        final int hour_24_1 = c1.get(Calendar.HOUR_OF_DAY);
        final int hour_12_1 = c1.get(Calendar.HOUR);
        Calendar c2 = (Calendar) c.clone();
        c2.add(Calendar.HOUR, 2);
        final int year2 = c2.get(Calendar.YEAR);
        final int month2 = c2.get(Calendar.MONTH);
        final int day2 = c2.get(Calendar.DAY_OF_MONTH);
        final int a_pm2 = c2.get(Calendar.AM_PM);
        final int hour_24_2 = c2.get(Calendar.HOUR_OF_DAY);
        final int hour_12_2 = c2.get(Calendar.HOUR);

        mTvMeetingDate.setText(year + "/" + (month+1) + "/" + day);
        mTvMeetingStartTime.setText(am_pm[a_pm1] + " " + hour_12_1 + ":00");
        mTvMeetingEndTime.setText(am_pm[a_pm2] + " " + hour_12_2 + ":00");


        mLlMeetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //创建DatePickerDialog对象
                DatePickerDialog dpd = new DatePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, null, year, month, day);
                dpd.setTitle(R.string.set_date);
                dpd.show();//显示DatePickerDialog组件
            }
        });

        mLlMeetingStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, null, hour_24_1, 0, false);
                tpd.setTitle(R.string.set_time);
                tpd.show();//显示DatePickerDialog组件
            }
        });
        mLlMeetingEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(ArrangeMeetingActivity.this, AlertDialog.THEME_HOLO_DARK, null, hour_24_2, 0, false);
                tpd.setTitle(R.string.set_time);
                tpd.show();//显示DatePickerDialog组件
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("保存");
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
