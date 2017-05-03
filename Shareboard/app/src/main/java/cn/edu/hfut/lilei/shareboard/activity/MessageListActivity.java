package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MessageListAdapter;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.model.Event;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_new_msg_num;


public class MessageListActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener {
    //控件
    private static final String TAG = "shareboard";
    //    private EasyRecyclerView mRv;
    private ListView listContent = null;
    private MessageListAdapter mAdapter;
    private List<Msg> mDatas = new ArrayList<>();
    //悬浮菜单
    private PopupWindow mMenuPop;
    private int rotate = 0;
    private int rotation = 225;
    private boolean rotateDirection = true;
    private int PopWidth;
    private int PopHeight;
    private MsgDao msgDao;

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        EventBus.getDefault()
                .register(this);
        init();


    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;


        msgDao = GreenDaoManager.getInstance()
                .getSession()
                .getMsgDao();
        mBtnBack = (ImageView) findViewById(R.id.img_message_list_goback);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        //右滑返回
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
            }
        });

        listContent = (ListView) findViewById(R.id.lv_message_list);


        QueryBuilder<Msg> qb = msgDao.queryBuilder();
        qb.orderDesc(MsgDao.Properties.Id);
        mDatas = qb.list();

        mAdapter = new MessageListAdapter(mContext);
        mAdapter.addAll(mDatas);
        listContent.setAdapter(mAdapter);
        listContent.setOnItemClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDatas.size() == 0) {
                    showToast(mContext, getString(R.string.no_msgs));
                }
            }
        }, 800);

        SharedPrefUtil.getInstance()
                .saveData(share_new_msg_num, 0);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        showLevitateMenu(i, view);
    }

    private void showLevitateMenu(int position, View view) {

        //创建popwindow
        if (getPopMenu(position)) {
            if (mMenuPop != null) {

                //获取ImageView控件在手机屏幕的位置
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];

                //贴在右边
                mMenuPop.showAtLocation(view, Gravity.NO_GRAVITY,
                        view.getWidth() / 2,
                        y + view.getHeight() / 2 - PopHeight / 2);

            }

        }


    }

    /**
     * 初始化popWindow
     */
    private void initMenuPop(final int position) {
        // 获取自定义布局文件pop.xml的视图
        View view = View.inflate(mContext, R.layout.item_pop_levitate_menu, null);

        final TextView tv_enter = (TextView) view.findViewById(R.id.tv_enter);
        final TextView tv_line = (TextView) view.findViewById(R.id.tv_line);
        final TextView tv_record = (TextView) view.findViewById(R.id.tv_record);

        tv_enter.setText(R.string.delete);
        tv_enter.setVisibility(View.VISIBLE);
        tv_line.setVisibility(View.GONE);
        tv_record.setVisibility(View.GONE);

        setPopWindow(view);

        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Msg tmp = mAdapter.getItem(position);
//                showToast(mContext, "删除");
                //pop menu 删除
                mAdapter.remove(position);
                msgDao.deleteByKey(tmp.getId());
                mMenuPop.dismiss();
            }
        });
    }

    private Boolean getPopMenu(int position) {


        if (mMenuPop != null && mMenuPop.isShowing()) {
            mMenuPop.dismiss();
            mMenuPop = null;
            return false;
        } else {
            //初始化popupWindow弹窗
            initMenuPop(position);
            return true;
        }
    }


    public void setPopWindow(View view) {

        //测量view的宽高，由于popupwindow没有测量的方法，只能测量内部view的宽高
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        PopWidth = view.getMeasuredWidth();
        PopHeight = view.getMeasuredHeight();

        //下面这两个必须有！！
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        // PopupWindow(布局，宽度，高度) 注意，此处宽高应为-2也就是wrap_content
        mMenuPop = new PopupWindow(view, -2, -2, true);

        // 重写onKeyListener,按返回键消失
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    mMenuPop.dismiss();
                    mMenuPop = null;
                    return true;
                }
                return false;
            }
        });

        //点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMenuPop != null && mMenuPop.isShowing()) {
                    mMenuPop.dismiss();
                    mMenuPop = null;
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Event e = new Event();
        e.flag = 0;
        SharedPrefUtil.getInstance()
                .saveData(share_new_msg_num, 0);
        EventBus.getDefault()
                .postSticky(e);

        EventBus.getDefault()
                .unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void updateUnreadMsgNum(Event e) {
        if (e.flag == 1) {
            QueryBuilder<Msg> qb = msgDao.queryBuilder();
            qb.orderDesc(MsgDao.Properties.Id);
            mDatas = qb.list();
            mAdapter.clear();
            mAdapter.addAll(mDatas);
        }

    }

}
