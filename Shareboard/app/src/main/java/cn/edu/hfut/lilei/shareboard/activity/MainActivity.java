package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.TabPageAdapter;
import cn.edu.hfut.lilei.shareboard.fragment.ContactsFragment;
import cn.edu.hfut.lilei.shareboard.fragment.MeetingFragment;
import cn.edu.hfut.lilei.shareboard.fragment.SettingsFragment;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.view.customdialog.CustomAlertDialog;


public class MainActivity extends FragmentActivity implements
        OnPageChangeListener, OnCheckedChangeListener {
    //控件
    private LinearLayout mLlActionbarRight;
    private LinearLayout.LayoutParams mlp;
    private RelativeLayout mRlActionbar;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private TextView mTvTitle;
    private ImageView mImgAddContact;

    //数据
    //按钮的没选中显示的图标
    private int[] unselectedIconIds = {R.drawable.ic_white_04,
            R.drawable.ic_white_39, R.drawable.ic_white_48
    };
    //按钮的选中显示的图标
    private int[] selectedIconIds = {R.drawable.ic_yellow_04,
            R.drawable.ic_yellow_39, R.drawable.ic_yellow_48
    };
    private int[] pageTitles = {R.string.meeting, R.string.contacts, R.string.settings};
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private boolean hasAddContactIcon;
    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        selectPage(0); // 默认选中首页
    }

    protected void init() {
        mContext = this;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
        }
        Fragment meetingFragment = new MeetingFragment();
        Fragment contactsFragment = new ContactsFragment();
        Fragment settingsFragment = new SettingsFragment();
        fragments.add(meetingFragment);
        fragments.add(contactsFragment);
        fragments.add(settingsFragment);


        mRlActionbar = (RelativeLayout) findViewById(R.id.rl_main_actionbar);
        mLlActionbarRight = (LinearLayout) findViewById(R.id.ll_main_actionbar_right);
        mTvTitle = (TextView) findViewById(R.id.tv_main_title);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mRadioGroup.setOnCheckedChangeListener(this);
        TabPageAdapter tabPageAdapter = new TabPageAdapter(
                getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(tabPageAdapter);
        mViewPager.setOnPageChangeListener(this);


        //联系人页面actionbar右侧的添加联系人图标
        mImgAddContact = new ImageView(this);
        ImageUtil.load(mContext, R.drawable.ic_black_add, R.drawable.ic_black_add, mImgAddContact);
        mlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        hasAddContactIcon = false;//标志着当前页面没有添加联系人图标

    }


    /**
     * 选择某页
     *
     * @param position 页面的位置
     */
    private void selectPage(int position) {
        //公共部分
        // 将所有的tab的icon变成灰色的
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            Drawable gray = getResources().getDrawable(unselectedIconIds[i]);
            gray.setBounds(0, 0, 130,
                    130);

            RadioButton child = (RadioButton) mRadioGroup.getChildAt(i);
            child.setCompoundDrawables(null, gray, null, null);
            child.setTextColor(getResources().getColor(
                    R.color.my_white));
        }
        //切换页面标题
        mTvTitle.setText(pageTitles[position]);
        // 切换页面
        mViewPager.setCurrentItem(position, false);
        // 改变图标
        Drawable yellow = getResources().getDrawable(selectedIconIds[position]);
        yellow.setBounds(0, 0, 130,
                130);
        RadioButton select = (RadioButton) mRadioGroup.getChildAt(position);
        select.setCompoundDrawables(null, yellow, null, null);
        select.setTextColor(getResources().getColor(
                R.color.my_yellow));

        //联系人页面特定
        if (position == 1) {

            if (hasAddContactIcon == false) {
                mImgAddContact.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new CustomAlertDialog.Builder(MainActivity.this)
                                        .setTitle(getString(R.string.add_friend))
                                        .setMessage(getString(R.string.inputemail))
                                        .setPositiveButton(
                                                getString(R.string.confirm),
                                                null)
                                        .show();
                            }
                        }
                );
                mLlActionbarRight.addView(mImgAddContact, mlp);
                hasAddContactIcon = true;
            }


        } else {
            if (hasAddContactIcon) {
                mLlActionbarRight.removeView(mImgAddContact);
                hasAddContactIcon = false;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_main_meeting:
                selectPage(0);
                break;
            case R.id.btn_main_contacts:
                selectPage(1);
                break;
            case R.id.btn_main_settings:
                selectPage(2);
                break;
        }
    }

}
