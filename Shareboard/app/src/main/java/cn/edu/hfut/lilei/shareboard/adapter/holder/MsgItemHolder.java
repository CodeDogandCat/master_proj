package cn.edu.hfut.lilei.shareboard.adapter.holder;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MessageListAdapter;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;


public class MsgItemHolder extends BaseViewHolder<Msg> {

    private MessageListAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private TextView tvTitle, tvName, tvStatus, tvTime;
    private Button btnAccept, btnReject, btnEnterMeeting;
    private AvatarImageView avatar;
    private MsgDao msgDao;

    public MsgItemHolder(ViewGroup parent,
                         MessageListAdapter.onItemClickListener onItemClickListener,
                         Handler handler) {
        super(parent, R.layout.item_msg_swipe);
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
        avatar = (AvatarImageView) itemView.findViewById(R.id.ivAvatar);
        tvName = (TextView) itemView.findViewById(R.id.tv_name);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
        tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        btnAccept = (Button) itemView.findViewById(R.id.btn_accept);
        btnReject = (Button) itemView.findViewById(R.id.btn_reject);
        btnEnterMeeting = (Button) itemView.findViewById(R.id.btn_enter_meeting);

        msgDao = GreenDaoManager.getInstance()
                .getSession()
                .getMsgDao();

    }

    @Override
    public void setData(Msg data) {

        final Msg msg = data;
        String name = "";
        tvName.setText(msg.getTitle());
        tvTitle.setText(msg.getContent());
        avatar.setTextAndColor(name,
                R.color.firebrick);

        if (data.getMsgTime() != 0) {
            tvTime.setText(DateTimeUtil.getChatDateTime(data.getMsgTime()));
        }
        if (!TextUtils.isEmpty(msg.getAvatar())) {
            ImageUtil.load(getContext(), msg.getAvatar(), avatar);
        }
        /**
         * 判断feature 和 status
         *  waitAddFriend
         *  requestAddFriend
         *  inviteAddMeeting
         */
        //先隐藏所有的按钮
        btnAccept.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        btnEnterMeeting.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);

        String feature = "";
        feature = msg.getFeature();
        int status = -1;
        status = msg.getStatus();


        switch (feature) {
            case "waitAddFriend":
                switch (status) {
                    case 0://等待验证
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.wait_check);
                        break;
                    case 1://已成为好友
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.already_friends);
                        break;
                    case 2://好友申请已被对方拒绝
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.friend_request_rejected);
                        break;
                }
                break;
            case "requestAddFriend":

                switch (status) {
                    case 0://初始态,接受/拒绝按钮
                        btnAccept.setVisibility(View.VISIBLE);
                        btnReject.setVisibility(View.VISIBLE);
//                        btnAccept.setOnClickListener(this);
//                        btnReject.setOnClickListener(this);
                        break;
                    case 1://点击了接受,已成为好友
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.already_friends);
                        break;
                    case 2://点击了拒绝,已拒绝
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.already_reject);
                        break;
                }
                break;
            case "inviteAddMeeting":
                switch (status) {
                    case 0://初始态,加会按钮
                        btnEnterMeeting.setVisibility(View.VISIBLE);
//                        btnEnterMeeting.setOnClickListener(this);
                        break;
                    case 1://点击过加会(无论是否成功加会),显示失效
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.invalid);
                        break;
                }
                break;


        }

    }


}
