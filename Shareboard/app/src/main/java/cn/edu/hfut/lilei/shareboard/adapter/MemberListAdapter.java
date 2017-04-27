package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.model.MeetingMemberInfo;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;

public class MemberListAdapter extends BaseAdapter implements View.OnClickListener {
    private List<MeetingMemberInfo> data = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private Callback mCallback;
    private String mEmail;
    private int check_in_type;

    public MemberListAdapter(Context context, String email, int check_in_type, Callback callback
    ) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.mCallback = callback;
        this.mEmail = email;
        this.check_in_type = check_in_type;

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
        public AvatarImageView ImgAvatar;
        public TextView tvGivenName;
        public TextView tvFamilyName;
        public TextView tvDesc;
        public ImageView btnTalk, btnDraw;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public MeetingMemberInfo getItem(int position) {
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
            convertView = layoutInflater.inflate(R.layout.listitem_meeting_member, null);

            holder.ImgAvatar =
                    (AvatarImageView) convertView.findViewById(R.id.img_item_member_avatar);

            holder.tvFamilyName =
                    (TextView) convertView.findViewById(R.id.tv_item_member_family_name);
            holder.tvGivenName =
                    (TextView) convertView.findViewById(R.id.tv_item_member_given_name);
            holder.tvDesc =
                    (TextView) convertView.findViewById(R.id.tv_item_member_desc);
            holder.btnDraw =
                    (ImageView) convertView.findViewById(R.id.img_item_member_draw);
            holder.btnTalk =
                    (ImageView) convertView.findViewById(R.id.img_item_member_talk);

            //与会者看不到列表中的 权限控制按钮
            if (check_in_type == 2) {
                holder.btnDraw.setVisibility(View.VISIBLE);
                holder.btnTalk.setVisibility(View.VISIBLE);
            }


            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        //头像
        MeetingMemberInfo member = getItem(position);
        holder.ImgAvatar.setTextAndColor(member.getClient_given_name(), R.color.skyblue);
        if (!member.getClient_avatar()
                .equals("")) {
            ImageUtil.loadAvatar(context, member.getClient_avatar(), holder.ImgAvatar);
        }
        //名
        holder.tvGivenName.setText(member.getClient_given_name());
        //姓
        holder.tvFamilyName.setText(member.getClient_family_name());
        //描述
        holder.tvDesc.setText("");
        if (member.client_type.equals("1")) {
            //给 管理者看的
            holder.btnDraw.setVisibility(View.VISIBLE);
            holder.btnTalk.setVisibility(View.VISIBLE);
            if (mEmail.equals(member.getClient_email())) {
                holder.tvDesc.setText("(我)");
                //如果你是  普通加会人,不能看
                holder.btnDraw.setVisibility(View.GONE);
                holder.btnTalk.setVisibility(View.GONE);
            }

        } else
            if (member.client_type.equals("2")) {
                if (mEmail.equals(member.getClient_email())) {
                    holder.tvDesc.setText("(我,主持人)");
                } else {
                    holder.tvDesc.setText("(主持人)");
                }
                //主持人的权限不能改变,也不显示按钮
                holder.btnDraw.setVisibility(View.GONE);
                holder.btnTalk.setVisibility(View.GONE);

            }
        //画画
        if (member.isClient_is_drawable()) {
            ImageUtil.load(context, R.drawable.ic_draw, R.drawable.ic_draw, holder.btnDraw);
        } else {
            ImageUtil.load(context, R.drawable.ic_undraw, R.drawable.ic_undraw, holder.btnDraw);
        }


        //静音
        if (member.isClient_is_talkable()) {
            ImageUtil.load(context, R.drawable.ic_unmutinging, R.drawable.ic_unmutinging,
                    holder.btnTalk);
        } else {
            ImageUtil.load(context, R.drawable.ic_muting, R.drawable.ic_muting, holder.btnTalk);
        }

        //设置监听器
        if (check_in_type == 2) {
            holder.btnTalk.setOnClickListener(this);
            holder.btnTalk.setTag(position);
            holder.btnDraw.setOnClickListener(this);
            holder.btnDraw.setTag(position);
        }


        return convertView;
    }

    public void addAll(List<MeetingMemberInfo> data) {
        this.data.addAll(data);
        notifyDataSetChanged();// 数据有改变

    }

    public void clear() {
        data.clear();

    }

    public void setIsDrawable(int position, boolean type
    ) {
        data.get(position)
                .setClient_is_drawable(type);

    }

    public void setIsTalkable(int position, boolean type) {
        data.get(position)
                .setClient_is_talkable(type);

    }

}
