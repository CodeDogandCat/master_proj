package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.model.FriendInfo;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_AVATAR;

public class InviteMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List<FriendInfo> list = null;
    private Context mContext;
    private Callback mCallback;
    private static HashMap<Integer, Boolean> isSelected;
    private static List<Integer> isSelected_list;

    public InviteMemberAdapter(Context mContext, List<FriendInfo> list, Callback callback
    ) {
        this.mContext = mContext;
        this.list = list;
        this.mCallback = callback;
        isSelected = new HashMap<Integer, Boolean>();
        isSelected_list = new ArrayList<>();
    }

    public interface Callback {

        public void click(int i, boolean isChecked);
    }


    /**
     * @param list
     */
    public void updateListView(List<FriendInfo> list) {
        this.list = list;
        notifyDataSetChanged();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {

        if (list.size() > position && position >= 0) {
            list.remove(position);
        }
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
        final FriendInfo mContent = list.get(position);
        if (view == null) {
            //创建 viewholder
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.listitem_group_member_invite, null);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_contacts_catalog);
            viewHolder.imgPhoto = (AvatarImageView) view.findViewById(R.id.img_contacts_photo);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_contacts_account);
            viewHolder.tvEmail = (TextView) view.findViewById(R.id.tv_contacts_email);
            viewHolder.tvStatus = (TextView) view.findViewById(R.id.tv_contacts_status);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.check_box);
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
        viewHolder.imgPhoto.setTextAndColorSeed(
                StringUtil.substring(name, length - 2, length, "", ""),
                name);


        String url = null;
        url = URL_AVATAR + this.list.get(position)
                .getAvatar();
//        if (position % 2 == 0) {
//            url = "http://img1.skqkw.cn:888/2014/12/06/14t/erha2fghuww-129662.png";
//        } else {
//            url = "http://img1.skqkw.cn:888/2014/12/06/14t/lileighuww-129662.png";
//        }


        ImageUtil.loadAvatar(mContext, url, viewHolder.imgPhoto);

        /**
         * 2.加载其他用户的状态(暂时忽略)
         */
        viewHolder.tvStatus.setText("");
        /**
         * 3.加载邮箱
         */
        viewHolder.tvTitle.setText(name);
        viewHolder.tvEmail.setText(mContent.getEmail());
        viewHolder.checkBox.setChecked(getIsSelected().get(position));

//        //复选框
//        viewHolder.checkBox.setOnCheckedChangeListener(
//                new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        isSelected.put(position, b);
//                        mCallback.click(position, b);
//                    }
//                });

        return view;

    }

    public static List<Integer> getIsSelectedList() {
        return InviteMemberAdapter.isSelected_list;
    }

    public static void addToIsSelectedList(Integer position) {
        if (!InviteMemberAdapter.isSelected_list.contains(position)) {
            InviteMemberAdapter.isSelected_list.add(position);
        }
    }

    public static void removeFromIsSelectedList(Integer position) {
        if (InviteMemberAdapter.isSelected_list.contains(position)) {
            InviteMemberAdapter.isSelected_list.remove(position);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        InviteMemberAdapter.isSelected = isSelected;
    }

    public final static class ViewHolder {
        public AvatarImageView imgPhoto;
        public TextView tvLetter;
        public TextView tvTitle;
        public TextView tvEmail;
        public TextView tvStatus;
        public CheckBox checkBox;
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