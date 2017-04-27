package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.holder.MsgItemHolder;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;


public class MessageListAdapter extends RecyclerArrayAdapter<Msg> {
    protected Context mContext;
    private onItemClickListener onItemClickListener;
    public Handler handler;
    private MsgDao msgDao;


    public interface onItemClickListener {

//        void onImageClick(View view, int position);
//
//        void onVoiceClick(ImageView imageView, int position);

    }

    public void addItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getViewType(int position) {
        return getAllData().get(position)
                .getType();
    }

    @Override
    public void OnBindViewHolder(final BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
//        holder.setIsRecyclable(false);

        final Msg tmp = getAllData().get(holder.getAdapterPosition());

        holder.itemView.findViewById(R.id.btnDel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SwipeMenuLayout) holder.itemView).quickClose();

//                        final MessageListActivity.MsgDelEvent event =
//                                new MessageListActivity.MsgDelEvent();
//                        event.setPosition(holder.getAdapterPosition());
//                        EventBus.getDefault()
//                                .postSticky(event);

                        //从数据库删除
                        msgDao.deleteByKey(tmp.getId());
                        remove(holder.getAdapterPosition());
                        notifyDataSetChanged();


                    }
                });
        holder.itemView.findViewById(R.id.btn_accept)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //更改status 为 1
                        tmp.setStatus(1);
                        msgDao.update(tmp);
                        //发送消息

                        remove(holder.getAdapterPosition());
                        notifyDataSetChanged();




                    }
                });
        holder.itemView.findViewById(R.id.btn_reject)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //更改status 为 2


                        //从数据库删除
                        msgDao.deleteByKey(getAllData().get(holder.getAdapterPosition())
                                .getId());
                        remove(holder.getAdapterPosition());
                        notifyDataSetChanged();


                    }
                });
        holder.itemView.findViewById(R.id.btn_enter_meeting)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //更改status 为 1

                        //从数据库删除
                        msgDao.deleteByKey(getAllData().get(holder.getAdapterPosition())
                                .getId());
                        remove(holder.getAdapterPosition());
                        notifyDataSetChanged();


                    }
                });

    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case 0:
                viewHolder = new MsgItemHolder(parent, onItemClickListener, handler);
                break;
        }
        return viewHolder;
    }

    public MessageListAdapter(Context context) {
        super(context);
        handler = new Handler();
        mContext = context;
        msgDao = GreenDaoManager.getInstance()
                .getSession()
                .getMsgDao();
//        EventBus.getDefault()
//                .register(this);
    }

}
