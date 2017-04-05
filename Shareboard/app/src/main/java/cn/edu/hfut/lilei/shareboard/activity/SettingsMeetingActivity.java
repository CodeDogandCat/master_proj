package cn.edu.hfut.lilei.shareboard.activity;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.listener.PermissionListener;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.PermissionsUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_add_to_calendar;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_is_talkable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_theme;


public class SettingsMeetingActivity extends SwipeBackActivity implements View.OnClickListener {
    //控件
    private LinearLayout mLlAddToCalendar, mLlIsDrawable, mLlIsTalkable;
    private Button mBtnSave;
    private SwitchButton mBtnAddToCalendar, mBtnIsDrawable, mBtnIsTalkable;
    private EditText mEtTheme, mEtPassword;
    //数据
    private String mtheme, mpassword;
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_meeting);
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        mBtnBack = (ImageView) findViewById(R.id.img_settings_meeting_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
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
        mBtnSave = (Button) findViewById(R.id.btn_settings_meeting_save);
        mBtnAddToCalendar = (SwitchButton) findViewById(R.id.btn_settings_meeting_add_to_calendar);
        mBtnIsDrawable = (SwitchButton) findViewById(R.id.btn_settings_meeting_drawable);
        mBtnIsTalkable = (SwitchButton) findViewById(R.id.btn_settings_meeting_talkable);
        mEtTheme = (EditText) findViewById(R.id.et_settings_meeting_title);
        mEtPassword = (EditText) findViewById(R.id.et_settings_meeting_meetingpassword);

        mLlIsDrawable = (LinearLayout) findViewById(R.id.ll_settings_meeting_is_drawable);
        mLlIsTalkable = (LinearLayout) findViewById(R.id.ll_settings_meeting_is_talkable);
        mLlAddToCalendar = (LinearLayout) findViewById(R.id.ll_settings_meeting_add_to_calendar);

        mBtnSave.setOnClickListener(this);
        mBtnAddToCalendar.setOnClickListener(this);
        mLlIsDrawable.setOnClickListener(this);
        mLlIsTalkable.setOnClickListener(this);
        mLlAddToCalendar.setOnClickListener(this);
        new TouchListener.Builder(mContext).setLinearLayout(mLlIsDrawable)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlIsTalkable)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlAddToCalendar)
                .create();


        initFromSharePre();


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
        mtheme = (String) SharedPrefUtil.getInstance()
                .getData(share_meeting_theme, "空");
        if (!mtheme.equals("空")) {
            mEtTheme.setText(mtheme);
        } else {
            String familyName = (String) SharedPrefUtil.getInstance()
                    .getData(share_family_name, "空");
            String givenName = (String) SharedPrefUtil.getInstance()
                    .getData(share_given_name, "空");
            if (familyName.equals("空") || givenName.equals("空")) {
                mEtTheme.setText(R.string.no_name_meeting);
            } else {
                mEtTheme.setText(familyName + givenName + getString(R.string.his_meeting));
            }
        }
        mpassword = (String) SharedPrefUtil.getInstance()
                .getData(share_meeting_password, "空");
        if (!mpassword.equals("空")) {
            mEtPassword.setText(mpassword);
        }
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

    //进行页面内容合法性检查
    private boolean checkPageContent() {
        mtheme = mEtTheme.getText()
                .toString()
                .trim();
        mpassword = mEtPassword.getText()
                .toString()
                .trim();
        if (!StringUtil.isValidTheme(mtheme)) {
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
     * 设置监听器
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_settings_meeting_add_to_calendar:
                //请求权限但不执行后续操作
                requestCalendar();
                break;
            case R.id.ll_settings_meeting_is_drawable:
                if (mBtnIsDrawable.isChecked()) {
                    mBtnIsDrawable.setChecked(false);
                } else {
                    mBtnIsDrawable.setChecked(true);

                }
                break;
            case R.id.ll_settings_meeting_is_talkable:
                if (mBtnIsTalkable.isChecked()) {
                    mBtnIsTalkable.setChecked(false);
                } else {
                    mBtnIsTalkable.setChecked(true);

                }
                break;
            //为保存按钮设置监听器
            case R.id.btn_settings_meeting_save:
                //检查密码和会议主题的格式
                if (checkPageContent()) {
                    //保存默认会议设置
                    SharedPrefUtil.getInstance()
                            .saveData(share_meeting_is_add_to_calendar,
                                    mBtnAddToCalendar.isChecked() ? 1 : 0);
                    SharedPrefUtil.getInstance()
                            .saveData(share_meeting_is_drawable,
                                    mBtnIsDrawable.isChecked() ? 1 : 0);
                    SharedPrefUtil.getInstance()
                            .saveData(share_meeting_is_talkable,
                                    mBtnIsTalkable.isChecked() ? 1 : 0);
                    SharedPrefUtil.getInstance()
                            .saveData(share_meeting_password,
                                    mpassword);
                    SharedPrefUtil.getInstance()
                            .saveData(share_meeting_theme,
                                    mtheme);
                    //返回上一页面
                    finish();
                }
                break;
            default:
                break;
        }
    }


}
