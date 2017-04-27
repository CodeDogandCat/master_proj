package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.model.GroupMemberInfo;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List<GroupMemberInfo> list = null;
    private Context mContext;

    public SortGroupMemberAdapter(Context mContext, List<GroupMemberInfo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * @param list
     */
    public void updateListView(List<GroupMemberInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final GroupMemberInfo mContent = list.get(position);
        if (view == null) {
            //创建 viewholder
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.listitem_group_member, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_contacts_catalog);
            viewHolder.imgPhoto = (AvatarImageView) view.findViewById(R.id.img_contacts_photo);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_contacts_account);
            viewHolder.tvStatus = (TextView) view.findViewById(R.id.tv_contacts_status);
            view.setTag(viewHolder);
        } else {
            //取出已有的viewholder
            viewHolder = (ViewHolder) view.getTag();
        }
        //获取排序字母所在的位置
        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        /**
         * 1.加载头像
         */
        String name = this.list.get(position)
                .getName();
        int length = StringUtil.length(name);
        viewHolder.imgPhoto.setTextAndColor(StringUtil.substring(name, length - 2, length, "", ""),
                R.color.mediumaquamarine);
        String url;
        if (position % 2 == 0) {
            url = "http://img1.skqkw.cn:888/2014/12/06/14t/erha2fghuww-129662.png";
        } else {
            url = "http://img1.skqkw.cn:888/2014/12/06/14t/lileighuww-129662.png";
        }

        ImageUtil.loadAvatar(mContext, url, viewHolder.imgPhoto);

        /**
         * 2.加载其他用户的状态
         */
        viewHolder.tvStatus.setText(this.list.get(position)
                .getStatus());
        /**
         * 3.加载邮箱
         */
        viewHolder.tvTitle.setText(name);

        return view;

    }

    final static class ViewHolder {
        AvatarImageView imgPhoto;
        TextView tvLetter;
        TextView tvTitle;
        TextView tvStatus;
    }

    /**
     */
    public int getSectionForPosition(int position) {
        return list.get(position)
                .getSortLetters()
                .charAt(0);
    }

    /**
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i)
                    .getSortLetters();
            char firstChar = sortStr.toUpperCase()
                    .charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim()
                .substring(0, 1)
                .toUpperCase();
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}