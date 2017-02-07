package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.GroupMemberInfo;

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
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_group_member, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_contacts_catalog);
            viewHolder.imgPhoto = (ImageView) view.findViewById(R.id.img_contacts_photo);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_contacts_account);
            viewHolder.tvStatus = (TextView) view.findViewById(R.id.tv_contacts_status);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.imgPhoto.setImageDrawable(this.list.get(position).getPhoto());
        viewHolder.tvStatus.setText(this.list.get(position).getStatus());
        viewHolder.tvTitle.setText(this.list.get(position).getName());

        return view;

    }

    final static class ViewHolder {
        ImageView imgPhoto;
        TextView tvLetter;
        TextView tvTitle;
        TextView tvStatus;
    }

    /**
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
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
        String sortStr = str.trim().substring(0, 1).toUpperCase();
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