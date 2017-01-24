package cn.edu.hfut.lilei.shareboard.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;


public class ContactsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		textView.setGravity(Gravity.CENTER);
		textView.setLayoutParams(params);
		textView.setText(R.string.contacts);
		return textView;
	}
}
