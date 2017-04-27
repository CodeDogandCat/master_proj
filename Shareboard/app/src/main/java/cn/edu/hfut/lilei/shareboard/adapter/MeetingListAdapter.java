package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.JsonEnity.MeetingListJson;

public class MeetingListAdapter extends BaseAdapter implements View.OnClickListener {
    private List<MeetingListJson.ServerModel> data = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private Callback mCallback;

    public MeetingListAdapter(Context context, Callback callback
    ) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.mCallback = callback;
    }

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        public void click(View v);
    }

    @Override
    public void onClick(View view) {
        mCallback.click(view);
    }

    /**
     * 组件集合，对应list.xml中的控件
     */
    public final class Holder {
        public TextView tvMeetingTheme;
        public TextView tvMeetingId;
        public Button btnStart;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public MeetingListJson.ServerModel getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.listitem_my_meeting, null);
            holder.tvMeetingId =
                    (TextView) convertView.findViewById(R.id.tv_my_meeting_id_param_item);
            holder.tvMeetingTheme =
                    (TextView) convertView.findViewById(R.id.tv_my_meeting_theme_param_item);
            holder.btnStart =
                    (Button) convertView.findViewById(R.id.btn_my_meeting_start_item);
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();

        MeetingListJson.ServerModel meeting = getItem(position);

        holder.tvMeetingTheme.setText(String.format(context.getResources()
                .getString(R.string
                        .meeting_theme_param), meeting.getMeeting_theme()));

        String tmp = String.valueOf(meeting.getMeeting_url());
        if (tmp.length() == 12) {

            tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 8) + "-" + tmp.substring(8, 12);
        }
        holder.tvMeetingId.setText(String.format(context.getResources()
                .getString(R.string
                        .meeting_id_param), tmp));

        holder.btnStart.setOnClickListener(this);
        holder.btnStart.setTag(position);
        return convertView;
    }

    public void addAll(List<MeetingListJson.ServerModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();// 数据有改变

    }

    public void clear() {
        data.clear();

    }

}
