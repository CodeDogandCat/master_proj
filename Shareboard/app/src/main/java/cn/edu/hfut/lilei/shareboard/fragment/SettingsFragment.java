package cn.edu.hfut.lilei.shareboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.activity.SettingsMyInfoActivity;


public class SettingsFragment extends Fragment{
	private LinearLayout mLlAccount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings_index, container, false);
		initViews(view);
		return view;
	}

	private void initViews(View view) {
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
