package cn.edu.hfut.lilei.shareboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.SettingsAboutActivity;
import cn.edu.hfut.lilei.shareboard.activity.SettingsMeetingActivity;
import cn.edu.hfut.lilei.shareboard.activity.SettingsMyInfoActivity;
import cn.edu.hfut.lilei.shareboard.listener.TouchListener;
import cn.edu.hfut.lilei.shareboard.model.ContextEvent;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class SettingsFragment extends Fragment {
    //控件
    private LinearLayout mLlAccount, mLlMeetingSetting, mLlAbout;
    private AvatarImageView mPhoto;
    private TextView mName, mTvMeeting, mTvAbout;
    private ImageView next1, next2, next3;
    private TextView mEmail;
    //数据
    private boolean shouldCallUpdate;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        shouldCallUpdate = false;
        EventBus.getDefault()
                .register(this);
        View view = inflater.inflate(R.layout.fragment_settings_index, container, false);
        init(view);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void listenContext(final ContextEvent event) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldCallUpdate) {
            update();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldCallUpdate = true;
    }

    private void update() {
        /**
         * 重新读取缓存，更新姓名,邮箱
         */
        mName.setText((String) SharedPrefUtil.getInstance()
                .getData(share_family_name,
                        "未设置") + (String) SharedPrefUtil.getInstance()
                .getData(share_given_name,
                        "未设置"));
        mEmail.setText((String) SharedPrefUtil.getInstance()
                .getData(share_user_email,
                        "未设置"));
        mPhoto.setTextAndColor((String) SharedPrefUtil.getInstance()
                .getData(share_given_name,
                        "未设置"), R.color.skyblue);
        ImageUtil.loadMyAvatar(mContext, mPhoto);
    }

    private void init(View view) {

        mContext = view.getContext();
        mName = (TextView) view.findViewById(R.id.tv_settings_name);
        mPhoto = (AvatarImageView) view.findViewById(R.id.img_settings_photo);
        mEmail = (TextView) view.findViewById(R.id.tv_settings_email);

        mTvMeeting = (TextView) view.findViewById(R.id.tv_settings_meeting);
        mTvAbout = (TextView) view.findViewById(R.id.tv_settings_about);
        next1 = (ImageView) view.findViewById(R.id.img_settings_next1);
        next2 = (ImageView) view.findViewById(R.id.img_settings_next2);
        next3 = (ImageView) view.findViewById(R.id.img_settings_next3);

        update();

        mLlAccount = (LinearLayout) view.findViewById(R.id.ll_settings_account);
        mLlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(view.getContext(), SettingsMyInfoActivity.class);
                ContextEvent e = new ContextEvent();
                e.context = getContext();
                e.from = "main";
                e.to = "settings_my_info";
                EventBus.getDefault()
                        .postSticky(e);
                startActivity(i);
            }
        });
        mLlMeetingSetting = (LinearLayout) view.findViewById(R.id.ll_settings_meetingsetting);
        mLlMeetingSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(view.getContext(), SettingsMeetingActivity.class);
                startActivity(i);
            }
        });
        mLlAbout = (LinearLayout) view.findViewById(R.id.ll_settings_about);
        mLlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(view.getContext(), SettingsAboutActivity.class);
                startActivity(i);
            }
        });
        new TouchListener.Builder(mContext).setLinearLayout(mLlAccount)
                .setTextView1(mName)
                .setTextView2(mEmail)
                .setImageView(next1)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlMeetingSetting)
                .setTextView1(mTvMeeting)
                .setImageView(next2)
                .create();
        new TouchListener.Builder(mContext).setLinearLayout(mLlAbout)
                .setTextView1(mTvAbout)
                .setImageView(next3)
                .create();

    }
}
