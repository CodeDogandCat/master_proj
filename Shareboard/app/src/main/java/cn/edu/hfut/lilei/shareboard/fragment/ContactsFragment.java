package cn.edu.hfut.lilei.shareboard.fragment;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.SortGroupMemberAdapter;
import cn.edu.hfut.lilei.shareboard.enity.GroupMemberInfo;
import cn.edu.hfut.lilei.shareboard.utils.CharacterUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PinyinComparatorUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.widget.ClearEditText;
import cn.edu.hfut.lilei.shareboard.widget.SideBar;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.CommonAlertDialog;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
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
    private List<GroupMemberInfo> SourceDateList;

    /**
     */
    private PinyinComparatorUtil pinyinComparatorUtil;
    private View view;


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
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


//                Toast.makeText(
//                        getActivity().getApplication(),
//                        ((GroupMemberInfo) adapter.getItem(position)).getName(),
//                        Toast.LENGTH_SHORT)
//                        .show();
                deleteFriend((GroupMemberInfo) adapter.getItem(position));


            }
        });

        SourceDateList = filledData(getResources().getStringArray(R.array.account));
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

    /**
     * @param contacts
     * @return
     */
    private List<GroupMemberInfo> filledData(String[] contacts) {
        List<GroupMemberInfo> mSortList = new ArrayList<GroupMemberInfo>();

        for (int i = 0; i < contacts.length; i++) {
            GroupMemberInfo sortModel = new GroupMemberInfo();
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
        List<GroupMemberInfo> filterDateList = new ArrayList<GroupMemberInfo>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (GroupMemberInfo sortModel : SourceDateList) {
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

    /**
     * 删除好友
     */
    private void deleteFriend(GroupMemberInfo contact) {
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
                                         * 2.获取参数
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

//                                        OkGo.post(URL_HOST_MEETING)
//                                                .tag(this)
//                                                .params(post_need_feature, "delete")
//                                                .params(post_token, valueList.get(0))
//                                                .params(post_user_email, valueList.get(1))
//                                                .params(post_meeting_id, meeting_id)
//                                                .execute(new JsonCallback<CommonJson>() {
//                                                             @Override
//                                                             public void onSuccess(CommonJson o, Call call,
//                                                                                   Response response) {
//                                                                 if (o.getCode() == SUCCESS) {
//
//                                                                     /**
//                                                                      * 删除指定的日历提醒时间
//                                                                      */
//
//                                                                     MyAppUtil.delCalendarEvent(mContext,
//                                                                             mEventID);
//
//                                                                     /**
//                                                                      * 跳到界面
//                                                                      */
//
//                                                                     Intent intent = new Intent();
//                                                                     intent.setClass(mContext,
//                                                                             ArrangeOrHostMeetingActivity.class);
//                                                                     mlodingDialog.cancle();
//                                                                     mContext.startActivity(intent);
//                                                                     clickableAllBtn();
//                                                                     ((Activity) mContext).finish();
//
//                                                                 } else {
//                                                                     //提示所有错误
//                                                                     mlodingDialog.cancle();
//                                                                     showToast(mContext, o.getMsg());
//                                                                 }
//
//                                                             }
//
//                                                             @Override
//                                                             public void onError(Call call,
//                                                                                 Response response,
//                                                                                 Exception e) {
//                                                                 super.onError(call, response, e);
//                                                                 mlodingDialog.cancle();
//                                                                 showToast(mContext, R.string.system_error);
//                                                             }
//                                                         }
//                                                );


                                        return -1;

                                    }

                                    @Override
                                    protected void onPostExecute(Integer integer) {
                                        super.onPostExecute(integer);
                                        mlodingDialog.cancle();
                                        switch (integer) {
                                            case NET_DISCONNECT:
                                                //弹出对话框，让用户开启网络
                                                NetworkUtil.setNetworkMethod(getActivity());
                                                break;
                                            case -1:
                                                break;
                                            case -2:
                                                showToast(getActivity(), R.string.please_relogin);
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

}
