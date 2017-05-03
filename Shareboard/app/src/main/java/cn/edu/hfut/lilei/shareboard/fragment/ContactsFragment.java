package cn.edu.hfut.lilei.shareboard.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.JsonEnity.FriendJson;
import cn.edu.hfut.lilei.shareboard.JsonEnity.FriendListJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.SortGroupMemberAdapter;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.listener.FragmentListener;
import cn.edu.hfut.lilei.shareboard.model.FriendInfo;
import cn.edu.hfut.lilei.shareboard.utils.CharacterUtil;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PinyinComparatorUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.ClearEditText;
import cn.edu.hfut.lilei.shareboard.widget.SideBar;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.CommonAlertDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.R.string.contacts;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FRIEND;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_FRIEND_GET;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_message_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_to_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;


public class ContactsFragment extends android.support.v4.app.Fragment implements SectionIndexer {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private ClearEditText mClearEditText;
    private LodingDialog.Builder mlodingDialog;

    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    /**
     */
    private int lastFirstVisibleItem = -1;
    /**
     */
    private CharacterUtil characterUtil;
    private List<FriendInfo> SourceDateList;

    /**
     */
    private PinyinComparatorUtil pinyinComparatorUtil;
    private View view;
    private FragmentListener listener;
    private List<FriendListJson.ServerModel> serverdatas = new ArrayList<>();


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (FragmentListener) activity;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        Log.i("main","$1");
        view = inflater.inflate(R.layout.fragment_contacts_index, container, false);
//        Log.i("main","$2");
        init();
//        Log.i("main","$3");
        return view;
    }

    private void init() {

        titleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        title = (TextView) view.findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) view.findViewById(R.id.title_layout_no_friends);
        characterUtil = CharacterUtil.getInstance();

        pinyinComparatorUtil = new PinyinComparatorUtil();

        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) view.findViewById(R.id.lv_contacts_content);
        sortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteFriend((FriendInfo) adapter.getItem(i));
                return false;
            }
        });


        getAllFriend();

//        SourceDateList = filledData(getResources().getStringArray(R.array.account));
        SourceDateList = convertDatas();


        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparatorUtil);
        adapter = new SortGroupMemberAdapter(view.getContext(), SourceDateList);
        sortListView.setAdapter(adapter);
        sortListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int section = getSectionForPosition(firstVisibleItem);
                int nextSection = getSectionForPosition(firstVisibleItem + 1);
                int nextSecPosition = getPositionForSection(+nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(SourceDateList.get(
                            getPositionForSection(section))
                            .getSortLetters());
//                    sideBar.choose=section;
//                    sideBar.invalidate();

                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;

            }
        });
        mClearEditText = (ClearEditText) (view.findViewById(R.id.filter_edit));


        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                titleLayout.setVisibility(View.GONE);
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private List<FriendInfo> convertDatas() {
        List<FriendInfo> mSortList = new ArrayList<FriendInfo>();

        for (int i = 0; i < contacts.length; i++) {
            FriendInfo sortModel = new FriendInfo();
//            sortModel.setPhoto(getResources().getDrawable(R.drawable.er));
            sortModel.setName(contacts[i]);
            sortModel.setStatus("空闲");
            String pinyin = characterUtil.getSelling(contacts[i]);
            String sortString = pinyin.substring(0, 1)
                    .toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
//            Log.i("hi", sortModel.getSortLetters() + " " + sortModel.getName());

        }
        return mSortList;

    }


    /**
     * @param contacts
     * @return
     */
    private List<FriendInfo> filledData(String[] contacts) {
        List<FriendInfo> mSortList = new ArrayList<FriendInfo>();

        for (int i = 0; i < contacts.length; i++) {
            FriendInfo sortModel = new FriendInfo();
//            sortModel.setPhoto(getResources().getDrawable(R.drawable.er));
            sortModel.setName(contacts[i]);
            sortModel.setStatus("空闲");
            String pinyin = characterUtil.getSelling(contacts[i]);
            String sortString = pinyin.substring(0, 1)
                    .toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
//            Log.i("hi", sortModel.getSortLetters() + " " + sortModel.getName());

        }
        return mSortList;

    }

    /**
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<FriendInfo> filterDateList = new ArrayList<FriendInfo>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (FriendInfo sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterUtil.getSelling(name)
                        .startsWith(
                                filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, pinyinComparatorUtil);
        adapter.updateListView(filterDateList);
        if (filterDateList.size() == 0) {
            tvNofriends.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     */
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position)
                .getSortLetters()
                .charAt(0);
    }

    /**
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i)
                    .getSortLetters();
            char firstChar = sortStr.toUpperCase()
                    .charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }


    public void getAllFriend() {

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                /**
                 * 1.检查网络状态并提醒
                 */
                if (!NetworkUtil.isNetworkConnected(getContext())) {
                    //网络连接不可用
                    return NET_DISCONNECT;
                }
                /**
                 * 2.获取会议设置
                 */
                ArrayList<String> keyList = new ArrayList<>();
                ArrayList<String> valueList = new ArrayList<>();
                keyList.add(share_token);
                keyList.add(share_user_email);

                valueList = SharedPrefUtil.getInstance()
                        .getStringDatas(keyList);
                if (valueList == null) {
                    return -2;
                }

                /**
                 * 3.发送
                 */
                OkGo.post(URL_FRIEND_GET)
                        .tag(this)
                        .params(post_token, valueList.get(0))
                        .params(post_user_email, valueList.get(1))

                        .execute(new JsonCallback<FriendListJson>() {
                                     @Override
                                     public void onSuccess(FriendListJson o,
                                                           Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {

                                             showToast(getContext(), "请求已发送");
                                             //更新好友列表


                                         } else {
                                             //提示所有错误
                                             showToast(getContext(), o.getMsg());
                                         }

                                     }

                                     @Override
                                     public void onError(Call call,
                                                         Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         showToast(getContext(),
                                                 R.string.system_error);
                                     }
                                 }
                        );


                return -1;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                switch (integer) {
                    case NET_DISCONNECT:
                        //弹出对话框，让用户开启网络
                        NetworkUtil.setNetworkMethod(getContext());
                        break;
                    case -1:
                        break;
                    case -2:
                        showToast(getContext(), R.string.please_relogin);
                        break;
                    default:
                        break;
                }
            }
        }.execute();
    }


    /**
     * 删除好友
     */
    private void deleteFriend(final FriendInfo contact) {
        final SpannableString title = new SpannableString(contact.getName());
        title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        title.setSpan(new RelativeSizeSpan(0.8f), tmp.indexOf("\n")
//                , tmp.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        new CommonAlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_contact_confirm))
                .setMessage(title)
                .setPositiveButton(
                        getActivity().getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /**
                                 * 删除数据库记录
                                 */
                                mlodingDialog = loding(getActivity(), R.string.deleting);

                                //1.检验
                                final String toEmail = contact.getEmail();


                                String myEmail = (String) SharedPrefUtil.getInstance()
                                        .getData(
                                                share_user_email, "");
                                if (myEmail.equals("")) {
                                    showToast(getContext(),
                                            getContext().getString(R.string.please_relogin));
                                    return;
                                }

                                final String tag = myEmail + DateTimeUtil.millisNow();// 全局标记

                                //2.发送请求
                                mlodingDialog = loding(getContext(), R.string.sending);
                                new AsyncTask<Void, Void, Integer>() {

                                    @Override
                                    protected Integer doInBackground(Void... voids) {
                                        /**
                                         * 1.检查网络状态并提醒
                                         */
                                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                                            //网络连接不可用
                                            return NET_DISCONNECT;
                                        }
                                        /**
                                         * 2.获取会议设置
                                         */
                                        ArrayList<String> keyList = new ArrayList<>();
                                        ArrayList<String> valueList = new ArrayList<>();
                                        keyList.add(share_token);
                                        keyList.add(share_user_email);

                                        valueList = SharedPrefUtil.getInstance()
                                                .getStringDatas(keyList);
                                        if (valueList == null) {
                                            return -2;
                                        }

                                        /**
                                         * 3.发送
                                         */
                                        OkGo.post(URL_FRIEND)
                                                .tag(this)
                                                .params(post_need_feature, "deleteFriend")
                                                .params(post_token, valueList.get(0))
                                                .params(post_user_email, valueList.get(1))
                                                .params(post_to_user_email, toEmail)
                                                .params(post_message_data,
                                                        tag)

                                                .execute(new JsonCallback<FriendJson>() {
                                                             @Override
                                                             public void onSuccess(FriendJson o,
                                                                                   Call call,
                                                                                   Response response) {
                                                                 if (o.getCode() == SUCCESS) {

                                                                     showToast(getContext(), "请求已发送");
                                                                     //更新好友列表


                                                                     mlodingDialog.cancle();

                                                                 } else {
                                                                     //提示所有错误
                                                                     mlodingDialog.cancle();
                                                                     showToast(getContext(), o.getMsg());
                                                                 }

                                                             }

                                                             @Override
                                                             public void onError(Call call,
                                                                                 Response response,
                                                                                 Exception e) {
                                                                 super.onError(call, response, e);
                                                                 mlodingDialog.cancle();
                                                                 showToast(getContext(),
                                                                         R.string.system_error);
                                                             }
                                                         }
                                                );


                                        return -1;

                                    }

                                    @Override
                                    protected void onPostExecute(Integer integer) {
                                        super.onPostExecute(integer);
                                        mlodingDialog.cancle();
                                        switch (integer) {
                                            case NET_DISCONNECT:
                                                //弹出对话框，让用户开启网络
                                                NetworkUtil.setNetworkMethod(getContext());
                                                break;
                                            case -1:
                                                break;
                                            case -2:
                                                showToast(getContext(), R.string.please_relogin);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }.execute();
                            }
                        })
                .setNegativeButton(
                        getActivity().getString(R.string.cancel),
                        null)
                .show();


    }

    public void update() {

    }

}
