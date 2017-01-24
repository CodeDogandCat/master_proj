package cn.edu.hfut.lilei.shareboard.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MeetingIndexAdapter;

public class MeetingFragment extends Fragment{

	private ListView listView;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_meetingindex , container, false);
		listView = (ListView)view.findViewById(R.id.lv_meeting);
		List<Map<String, Object>> list=getData();
		listView.setAdapter(new MeetingIndexAdapter(getActivity(), list));
		return view;
	}

	public List<Map<String, Object>> getData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
//		for (int i = 0; i < 10; i++) {
//			Map<String, Object> map=new HashMap<String, Object>();
//			map.put("image", R.mipmap.ic_launcher);
//			map.put("title", "这是一个标题"+i);
//			list.add(map);
//		}

		//加入会议
		Map<String, Object> joinmeeting_map=new HashMap<String, Object>();
		joinmeeting_map.put("image", R.drawable.ic_joinmeeting);
		joinmeeting_map.put("title", getString(R.string.join_meeting));
		list.add(joinmeeting_map);
		//主持或安排会议
		Map<String, Object> hostmeeting_map=new HashMap<String, Object>();
		hostmeeting_map.put("image", R.drawable.ic_hostmeeting);
		hostmeeting_map.put("title", getString(R.string.host_or_arrange_meeting));
		list.add(hostmeeting_map);
		//系统消息
		Map<String, Object> systemmsg_map=new HashMap<String, Object>();
		systemmsg_map.put("image", R.drawable.ic_systemmsg);
		systemmsg_map.put("title", getString(R.string.system_msg));
		list.add(systemmsg_map);
		return list;
	}
}
