package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> implements View.OnClickListener {
    protected Context mContext;
    protected List<Msg> mDatas;
    protected LayoutInflater mInflater;

    public MsgAdapter(Context mContext, List<Msg> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    public List<Msg> getDatas() {
        return mDatas;
    }

    public MsgAdapter setDatas(List<Msg> datas) {
        mDatas = datas;
        return this;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_msg_swipe, parent, false));
    }


    @Override
    public void onBindViewHolder(final MsgAdapter.ViewHolder holder, final int position) {
        final Msg msg = mDatas.get(position);
        String name = "";
        if (!TextUtils.isEmpty(msg.getFamilyName()) && !TextUtils.isEmpty(msg.getGivenName())) {
            name = msg.getFamilyName() + " " + msg.getGivenName();
        }
        holder.tvName.setText(name);
        holder.tvTitle.setText(msg.getTitle());
        holder.avatar.setTextAndColor(name,
                R.color.firebrick);
        if (!TextUtils.isEmpty(msg.getAvatar())) {
            ImageUtil.load(mContext, msg.getAvatar(), holder.avatar);
        }
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
                        holder.btnAccept.setOnClickListener(this);
                        holder.btnReject.setOnClickListener(this);
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
                        holder.btnEnterMeeting.setOnClickListener(this);
                        break;
                    case 1://点击过加会(无论是否成功加会),显示失效
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText(R.string.invalid);
                        break;
                }
                break;


        }
        holder.itemView.findViewById(R.id.btnDel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SwipeMenuLayout) holder.itemView).quickClose();
                        mDatas.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });


    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                //发送请求,修改本地db
                break;
            case R.id.btn_reject:
                //发送请求,修改本地db
                break;
            case R.id.btn_enter_meeting:
                //获取content的加会信息

                //加会
                break;


        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvName, tvStatus;
        Button btnAccept, btnReject, btnEnterMeeting;
        AvatarImageView avatar;


        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (AvatarImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
            btnReject = (Button) itemView.findViewById(R.id.btn_reject);
            btnEnterMeeting = (Button) itemView.findViewById(R.id.btn_enter_meeting);
        }
    }
}
