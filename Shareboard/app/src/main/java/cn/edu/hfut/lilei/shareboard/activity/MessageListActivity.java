package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.MessageListAdapter;
import cn.edu.hfut.lilei.shareboard.greendao.entity.Msg;
import cn.edu.hfut.lilei.shareboard.greendao.gen.MsgDao;
import cn.edu.hfut.lilei.shareboard.utils.GreenDaoManager;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;


public class MessageListActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener {
    //控件
    private static final String TAG = "shareboard";
    //    private EasyRecyclerView mRv;
    private ListView listContent = null;
    private MessageListAdapter mAdapter;
    private List<Msg> mDatas = new ArrayList<>();
    private MsgDao msgDao;

    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
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
//        mDatas = qb.list();

        mAdapter = new MessageListAdapter(mContext);
        mAdapter.addAll(qb.list());
        listContent.setAdapter(mAdapter);
        listContent.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Msg tmp = mAdapter.getItem(i);
        showToast(mContext, "删除");
        //pop menu 删除
        mAdapter.remove(i);
        msgDao.deleteByKey(tmp.getId());


    }

}