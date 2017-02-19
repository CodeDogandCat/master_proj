package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.AppInfo;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil.getPreString;


public class MeetingInfoActivity extends SwipeBackActivity {
    //控件
    private View dialogView = null;//弹出窗口
    private TextView mTvMeetingDate, mTvMeetingTheme, mTvMeetingID, mTvMeetingLength;
    private Button mBtnEdit, mBtnStart, mBtnAddToCalendar, mBtnAddInvite, mBtnDelete;
    private ListView listContent = null;
    //数据
    private int year, month, day, a_pm1, hour_12_1, minite1;
    private int queryAppInfoflag = -1;
    private long mEventID = -1, startMillis, endMillis, lenght;
    private String title, description, mid, mpassword;
    private String preStr = "";
    private String[] am_pm = {"上午", "下午"};
    private List<AppInfo> mlistAppInfo;
    //上下文参数
    private Context mContext;


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
        title = bundle.getString("title");
        description = bundle.getString("description");
        mid = bundle.getString("mid");
        mpassword = bundle.getString("mpassword");

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
        mTvMeetingID.setText(mid);
        mTvMeetingLength.setText(lenght + getResources().getString(R.string.minute));

        //邀请方式弹出窗内部的listview
        LayoutInflater inflater = LayoutInflater.from(mContext);
        dialogView = inflater.inflate(R.layout.dialog_invite_chooser, null);
        listContent = (ListView) dialogView.findViewById(R.id.lv_dialog_invite_chooser);


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

    private void edit() {

    }

    private void delete() {

    }

    private void startMeeting() {

    }


}
