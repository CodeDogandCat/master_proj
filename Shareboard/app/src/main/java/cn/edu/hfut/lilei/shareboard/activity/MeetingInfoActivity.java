package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.AppInfo;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.MyDateTimeUtils;
import cn.edu.hfut.lilei.shareboard.view.InviteChooserDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.MyDateTimeUtils.getPreString;


public class MeetingInfoActivity extends SwipeBackActivity {

    private TextView mTvMeetingDate, mTvMeetingTheme, mTvMeetingID, mTvMeetingLength;
    private int year, month, day, a_pm1, hour_24_1, hour_12_1, minite1, a_pm2, hour_24_2, hour_12_2, minite2;
    private String[] am_pm = {"上午", "下午"};
    private Button mBtnEdit, mBtnStart, mBtnAddToCalendar, mBtnAddInvite, mBtnDelete;
    private long mEventID, startMillis, endMillis, lenght;
    private String preStr = "";
    private Context mContext;
    private List<AppInfo> mlistAppInfo;
    private int queryAppInfoflag = 0;
    private ListView listContent = null;
    private View dialogView = null;//弹出窗口
    private SwipeBackLayout mSwipeBackLayout;

    // Request code for READ_CALENDAR and WRITE_CALENDAR. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_WRITE_CALENDAR = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);
        getBundle();
        init();


    }

    private void updateTvMeetingDate() {
        mTvMeetingDate.setText(preStr + am_pm[a_pm1] + MyDateTimeUtils.zeroConvert(hour_12_1) + ":" + MyDateTimeUtils.addZero(minite1));
    }

    private void getBundle() {
        Bundle bundle = this.getIntent().getExtras();
        startMillis = bundle.getLong("startMillis");
        endMillis = bundle.getLong("endMillis");

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        year = start.get(Calendar.YEAR);
        month = start.get(Calendar.MONTH);
        day = start.get(Calendar.DAY_OF_MONTH);
        a_pm1 = start.get(Calendar.AM_PM);
        hour_12_1 = start.get(Calendar.HOUR);
        minite1 = start.get(Calendar.MINUTE);

        lenght = MyDateTimeUtils.minuteLength(startMillis, endMillis);
        preStr = getPreString(startMillis);

    }

    private void init() {
        mContext = this;
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
        mTvMeetingTheme.setText("李磊的白板会议");
        mTvMeetingID.setText("123-4444-8888");
        mTvMeetingLength.setText(lenght + getResources().getString(R.string.minute));

        //弹出窗内部的listview
        LayoutInflater inflater = LayoutInflater.from(mContext);
        dialogView = inflater.inflate(R.layout.dialog_invite_chooser, null);
        listContent = (ListView) dialogView.findViewById(R.id.lv_dialog_invite_chooser);
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
                    insertEvent();
                    break;
                case R.id.btn_meeting_info_invite:
                    invite();
                    break;
                case R.id.btn_meeting_info_delete:
                    delete();
                    break;

            }
        }
    };

    private void edit() {

    }

    private void delete() {

    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public void queryAppInfo() {
        mlistAppInfo = new ArrayList<AppInfo>();
        queryAppInfoflag = 0;
        PackageManager pm = mContext.getPackageManager(); // 获得PackageManager对象

        //获取短信应用
        Uri smsToUri = Uri.parse("smsto:");
        Intent mainIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        if (pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY).size() != 0) {
            resolveInfos.add(pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY).get(0));
            queryAppInfoflag += 1;
        }
        //获取邮件应用
        Uri mailToUri = Uri.parse("mailto:");
        mainIntent = new Intent(Intent.ACTION_SENDTO, mailToUri);
        // 通过查询，获得所有ResolveInfo对象.
        if (pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY).size() != 0) {
            resolveInfos.add(pm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY).get(0));
            queryAppInfoflag += 3;
        }

        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
//            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
//                    Intent launchIntent = new Intent();
//                    launchIntent.setData(Uri.parse("mailto:"));
//                    launchIntent.putExtra(Intent.EXTRA_SUBJECT, "报到");
//                    launchIntent.putExtra(Intent.EXTRA_TEXT, "我来上班啦");
//                    launchIntent.setType("text/plain");
//                    launchIntent.putExtra("sms_body", "你好");
//
//                    launchIntent.setComponent(new ComponentName(pkgName,
//                            activityName));
                // 创建一个AppInfo对象，并赋值
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppName(MyAppUtil.getApplicationNameByPackageName(mContext, pkgName));
                appInfo.setAppIcon(icon);
//                    appInfo.setIntent(launchIntent);
                mlistAppInfo.add(appInfo); // 添加至列表中
                System.out.println(appLabel + " activityName---" + activityName
                        + " pkgName---" + pkgName);
            }
            // 创建一个AppInfo对象，用来 复制到剪贴板
            AppInfo appInfo = new AppInfo();
            appInfo.setAppLabel("");
            appInfo.setPkgName("");
            appInfo.setAppName("复制到剪贴板");
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.drawable.copy));
            appInfo.setIntent(null);
            mlistAppInfo.add(appInfo); // 添加至列表中
        }

    }

    private void invite() {

        final String subject = String.format(getResources().getString(R.string.invite_title), "李磊的白板会议");
        final String content = (String.format(getResources().getString(R.string.invite_content), "李磊", "李磊的白板会议", preStr + am_pm[a_pm1] + MyDateTimeUtils.zeroConvert(hour_12_1) + ":" + MyDateTimeUtils.addZero(minite1), "123-4444-8888", "888666"));


        final InviteChooserDialog.Builder dialog = new InviteChooserDialog.Builder(MeetingInfoActivity.this);

        dialog.setTitle(getString(R.string.choose_invite_type));
        dialog.setSubject(subject);
        dialog.setContent(content);
        dialog.setData(mlistAppInfo);
        dialog.setView(listContent);
        dialog.show();

//        listContent.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                        switch (queryAppInfoflag) {
//                            case 0:
//                                dialog.cancle();
//                                MyAppUtil.copy(content, mContext);
//                                break;
//                            case 1:
//                                switch (position) {
//                                    case 0:
//                                        dialog.cancle();
//                                        MyAppUtil.sendSMS(content, mContext);
//                                        break;
//                                    case 1:
//                                        dialog.cancle();
//                                        MyAppUtil.copy(content, mContext);
//                                        break;
//                                }
//                                break;
//                            case 3:
//                                switch (position) {
//                                    case 0:
//                                        dialog.cancle();
//                                        MyAppUtil.sendMail(subject, content, mContext);
//                                        break;
//                                    case 1:
//                                        dialog.cancle();
//                                        MyAppUtil.copy(content, mContext);
//                                        break;
//
//                                }
//                                break;
//                            case 4:
//                                switch (position) {
//                                    case 0:
//                                        dialog.cancle();
//                                        MyAppUtil.sendSMS(content, mContext);
//                                        break;
//                                    case 1:
//                                        dialog.cancle();
//                                        MyAppUtil.sendMail(subject, content, mContext);
//                                        break;
//                                    case 2:
//                                        dialog.cancle();
//                                        MyAppUtil.copy(content, mContext);
//                                        break;
//
//                                }
//                                break;
//                        }
//                    }
//                });

    }


    private void startMeeting() {

    }

    private void viewEvent() {
        showToast("view " + mEventID);
//        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mEventID);
//        Intent intent = new Intent(Intent.ACTION_VIEW)
//                .setData(uri);

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        startActivity(intent);

//                Calendar beginTime = Calendar.getInstance();
//                beginTime.set(2017, 2, 10, 7, 30);
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(2017, 2, 10, 8, 30);
//                Intent intent = new Intent(Intent.ACTION_INSERT)
//                        .setData(CalendarContract.Events.CONTENT_URI)
//                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                        .putExtra(CalendarContract.Events.TITLE, "Yoga")
//                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
//                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
//                startActivity(intent);
    }

    private void insertEvent() {
        long calID = 1;

//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2017, 1, 10, 20, 30);
//        startMillis = beginTime.getTimeInMillis();
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2017, 1, 10, 21, 30);
//        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, String.format(getResources().getString(R.string.invite_title), "李磊的白板会议"));
        values.put(CalendarContract.Events.EVENT_LOCATION, "小喵白板-加入会议");
        values.put(CalendarContract.Events.DESCRIPTION, String.format(getResources().getString(R.string.invite_describe), "李磊", "123-4444-8888", "888666"));
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "GMT+8");//时区
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        showToast("时间添加准备");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, PERMISSIONS_REQUEST_READ_WRITE_CALENDAR);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        } else {
            // Android version is lesser than 6.0 or the permission is already granted.

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            mEventID = Long.parseLong(uri.getLastPathSegment());
            ContentValues values2 = new ContentValues();
            // 提前15分钟有提醒
            values2.put(CalendarContract.Reminders.MINUTES, 15);
            values2.put(CalendarContract.Reminders.EVENT_ID, mEventID);
            values2.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values2);
            showToast("时间添加完毕" + mEventID);
            mTvMeetingDate.setText("事件ID:" + mEventID);
            viewEvent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_WRITE_CALENDAR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                insertEvent();
            } else {
                showToast("直到您允许该权限，才能添加事件至日历项");
            }
        }
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
