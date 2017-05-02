package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;


public class MessageListAdapter extends BaseAdapter {
    protected Context mContext;
    private MsgDao msgDao;
    private List<Msg> data = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public MessageListAdapter(Context context
    ) {
        this.mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        msgDao = GreenDaoManager.getInstance()
                .getSession()
                .getMsgDao();
    }


    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Msg getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        if (data.size() > position && position >= 0) {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Msg> data) {
        this.data.addAll(data);
        notifyDataSetChanged();// 数据有改变

    }

    public void clear() {
        data.clear();

    }

    public final class Holder {
        public TextView tvTitle, tvName, tvStatus, tvTime;
        public Button btnAccept, btnReject, btnEnterMeeting;
        public AvatarImageView avatar;
    }


    @Override
    public View getView(final int position, View itemView, ViewGroup parent) {
        MessageListAdapter.Holder holder = null;
        if (itemView == null) {
            holder = new Holder();
            //获得组件，实例化组件
            itemView = layoutInflater.inflate(R.layout.item_msg_swipe, null);


            holder.avatar = (AvatarImageView) itemView.findViewById(R.id.ivAvatar);
            holder.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            holder.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            holder.tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            holder.tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            holder.btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
            holder.btnReject = (Button) itemView.findViewById(R.id.btn_reject);
            holder.btnEnterMeeting = (Button) itemView.findViewById(R.id.btn_enter_meeting);


            itemView.setTag(holder);
        }
        holder = (MessageListAdapter.Holder) itemView.getTag();
        final Msg msg = getItem(position);

        holder.tvName.setText(msg.getTitle());
        holder.tvTitle.setText(msg.getContent());
        holder.avatar.setTextAndColorSeed(msg.getTitle(),
                msg.getTitle());

        if (msg.getMsgTime() != 0) {
            holder.tvTime.setText(DateTimeUtil.getChatDateTime(msg.getMsgTime()));
        }
        if (!TextUtils.isEmpty(msg.getAvatar())) {
            ImageUtil.load(mContext, msg.getAvatar(), holder.avatar);
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更改status 为 1
                msg.setStatus(1);
                msgDao.update(msg);
                notifyDataSetChanged();

                //发送消息

            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更改status 为 2
                msg.setStatus(2);
                msgDao.update(msg);
                notifyDataSetChanged();

                //发送消息
            }
        });
        holder.btnEnterMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更改status 为 1
                msg.setStatus(1);
                msgDao.update(msg);
                notifyDataSetChanged();

                //发送消息
            }
        });


        /**
         * 判断feature 和 status
         *  waitAddFriend
         *  requestAddFriend
         *  inviteAddMeeting
         */
        //先隐藏所有的按钮
        holder.btnAccept.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnEnterMeeting.setVisibility(View.GONE);
        holder.tvStatus.setVisibility(View.GONE);

        String feature = "";
        feature = msg.getFeature();
        int status = -1;
        status = msg.getStatus();


        switch (feature) {
            case "waitAddFriend":
                switch (status) {
                    case 0://等待验证
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.wait_check);
                        break;
                    case 1://已成为好友
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.already_friends);
                        break;
                    case 2://好友申请已被对方拒绝
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.friend_request_rejected);
                        break;
                }
                break;
            case "requestAddFriend":

                switch (status) {
                    case 0://初始态,接受/拒绝按钮
                        holder.btnAccept.setVisibility(View.VISIBLE);
                        holder.btnReject.setVisibility(View.VISIBLE);
//                        btnAccept.setOnClickListener(this);
//                        btnReject.setOnClickListener(this);
                        break;
                    case 1://点击了接受,已成为好友
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.already_friends);
                        break;
                    case 2://点击了拒绝,已拒绝
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.already_reject);
                        break;
                }
                break;
            case "inviteAddMeeting":
                switch (status) {
                    case 0://初始态,加会按钮
                        holder.btnEnterMeeting.setVisibility(View.VISIBLE);
//                        btnEnterMeeting.setOnClickListener(this);
                        break;
                    case 1://点击过加会(无论是否成功加会),显示失效
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.invalid);
                        break;
                }
                break;


        }

        return itemView;
    }


    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        public void click(View v);
    }

}
