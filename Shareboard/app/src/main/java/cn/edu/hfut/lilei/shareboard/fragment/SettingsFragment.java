package cn.edu.hfut.lilei.shareboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.SettingsMyInfoActivity;


public class SettingsFragment extends Fragment{
	private LinearLayout mLlAccount;
	private AvatarImageView mPhoto;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_index, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		mPhoto = (AvatarImageView) view.findViewById(R.id.img_settings_photo);
		mPhoto.setTextAndColor("磊", R.color.skyblue);

		mLlAccount= (LinearLayout) view.findViewById(R.id.ll_settings_account);
		mLlAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i=new Intent();
				i.setClass(view.getContext(), SettingsMyInfoActivity.class);
				startActivity(i);
			}
		});
	}
}
