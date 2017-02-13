package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.AppInfo;

public class ApplicationInfoAdapter extends BaseAdapter {

    private List<AppInfo> mlistAppInfo = null;

    LayoutInflater infater = null;

    public ApplicationInfoAdapter(Context context, List<AppInfo> apps) {
        infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mlistAppInfo = apps;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        System.out.println("size" + mlistAppInfo.size());
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup arg2) {
        System.out.println("getView at " + position);
        View view = null;
        ViewHolder holder = null;
        if (convertview == null || convertview.getTag() == null) {
            view = infater.inflate(R.layout.listitem_invite_chooser, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertview;
            holder = (ViewHolder) convertview.getTag();
        }
        AppInfo appInfo = (AppInfo) getItem(position);
        holder.appIcon.setImageDrawable(appInfo.getAppIcon());
//        holder.tvAppLabel.setText(appInfo.getAppLabel());
        holder.tvAppName.setText(appInfo.getAppName());
        return view;
    }

    class ViewHolder {
        ImageView appIcon;
        //        TextView tvAppLabel;
        TextView tvAppName;

        public ViewHolder(View view) {
            this.appIcon = (ImageView) view.findViewById(R.id.image);
//            this.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
            this.tvAppName = (TextView) view.findViewById(R.id.title);
        }
    }
}
