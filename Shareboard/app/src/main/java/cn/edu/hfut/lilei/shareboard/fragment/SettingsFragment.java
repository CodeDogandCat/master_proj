package cn.edu.hfut.lilei.shareboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.SettingsMyInfoActivity;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_family_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_given_name;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class SettingsFragment extends Fragment {
    //控件
    private LinearLayout mLlAccount;
    private AvatarImageView mPhoto;
    private TextView mName;
    private TextView mEmail;
    //数据
    private boolean shouldCallUpdate;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        shouldCallUpdate = false;

        View view = inflater.inflate(R.layout.fragment_settings_index, container, false);
        init(view);
        return view;
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
        mPhoto.setTextAndColor("磊", R.color.skyblue);
        ImageUtil.loadMyAvatar(mContext,
                URL_AVATAR + SharedPrefUtil.getInstance()
                        .getData(share_user_email, "未设置") + ".jpeg",
                mPhoto);
    }

    private void init(View view) {
        mContext = view.getContext();
        mName = (TextView) view.findViewById(R.id.tv_settings_name);
        mEmail = (TextView) view.findViewById(R.id.tv_settings_email);
        mPhoto = (AvatarImageView) view.findViewById(R.id.img_settings_photo);
        update();

        mLlAccount = (LinearLayout) view.findViewById(R.id.ll_settings_account);
        mLlAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(view.getContext(), SettingsMyInfoActivity.class);
                startActivity(i);
            }
        });
    }
}
