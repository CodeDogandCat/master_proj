package cn.edu.hfut.lilei.shareboard.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.model.MessageInfo;
import cn.edu.hfut.lilei.shareboard.utils.AudioRecoderUtils;
import cn.edu.hfut.lilei.shareboard.utils.FileUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.PopupWindowFactory;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.utils.Utils;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_CHAT_VOICE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_SEND_CHAT_FILE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_chat_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;

public class EmotionInputDetector {

    private static final String SHARE_PREFERENCE_NAME = "com.lilei.emotioninputdetector";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";

    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences sp;
    private View mEmotionLayout;
    private EditText mEditText;
    private TextView mVoiceText;
    private View mContentView;
    private ViewPager mViewPager;
    private View mSendButton;
    private View mAddButton;
    private Boolean isShowEmotion = false;
    private Boolean isShowAdd = false;
    private AudioRecoderUtils mAudioRecoderUtils;
    private PopupWindowFactory mVoicePop;
    private TextView mPopVoiceText;
    private static String email;
    private File chatFile;


    private EmotionInputDetector() {
    }

    public static EmotionInputDetector with(Activity activity) {
        email = (String) SharedPrefUtil.getInstance()
                .getData(share_user_email, "空");
        if (email.equals("空")) {
            return null;
        }
        EmotionInputDetector emotionInputDetector = new EmotionInputDetector();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp =
                activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    public EmotionInputDetector bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    public EmotionInputDetector bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);

                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mAddButton.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    mAddButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return this;
    }

    public EmotionInputDetector bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowAdd) {
                        mViewPager.setCurrentItem(0);
                        isShowEmotion = true;
                        isShowAdd = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowEmotion = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(0);
                    isShowEmotion = true;
                }
            }
        });
        return this;
    }

    public EmotionInputDetector bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowEmotion) {
                        mViewPager.setCurrentItem(1);
                        isShowAdd = true;
                        isShowEmotion = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowAdd = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(1);
                    isShowAdd = true;
                }
            }
        });
        return this;
    }

    public EmotionInputDetector bindToSendButton(View sendButton) {
        mSendButton = sendButton;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                MessageInfo messageInfo = new MessageInfo();
                String content = mEditText.getText()
                        .toString();
                showLog("加密前" + content);


                try {
                    String masterPassword = "L1x#tvh_";

                    String encryptingCode = StringUtil.encrypt_security(masterPassword, content);
                    String enToStr =
                            Base64.encodeToString(encryptingCode.getBytes(), Base64.NO_WRAP);
                    showLog("加密后" + enToStr);

                    messageInfo.setContent(enToStr);
                    messageInfo.setClient_email(email);

                    EventBus.getDefault()
                            .post(messageInfo);
                    mEditText.setText("");

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        return this;
    }

    public EmotionInputDetector bindToVoiceButton(View voiceButton) {
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmotionLayout(false);
                hideSoftInput();
                mVoiceText.setVisibility(
                        mVoiceText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEditText.setVisibility(
                        mVoiceText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        return this;
    }

    public EmotionInputDetector bindToVoiceText(TextView voiceText) {
        mVoiceText = voiceText;
        mVoiceText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获得x轴坐标
                int x = (int) event.getX();
                // 获得y轴坐标
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mVoicePop.showAtLocation(v, Gravity.CENTER, 0, 0);
                        mVoiceText.setText("松开结束");
                        mPopVoiceText.setText("手指上滑，取消发送");
                        mVoiceText.setTag("1");
                        mAudioRecoderUtils.startRecord(mActivity);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (wantToCancle(x, y)) {
                            mVoiceText.setText("松开结束");
                            mPopVoiceText.setText("松开手指，取消发送");
                            mVoiceText.setTag("2");
                        } else {
                            mVoiceText.setText("松开结束");
                            mPopVoiceText.setText("手指上滑，取消发送");
                            mVoiceText.setTag("1");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mVoicePop.dismiss();
                        if (mVoiceText.getTag()
                                .equals("2")) {
                            //取消录音（删除录音文件）
                            mAudioRecoderUtils.cancelRecord();
                        } else {
                            //结束录音（保存录音文件）
                            mAudioRecoderUtils.stopRecord();
                        }
                        mVoiceText.setText("按住说话");
                        mVoiceText.setTag("3");
                        mVoiceText.setVisibility(View.GONE);
                        mEditText.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        return this;
    }

    private boolean wantToCancle(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > mVoiceText.getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -50 || y > mVoiceText.getHeight() + 50) {
            return true;
        }
        return false;
    }

    public EmotionInputDetector setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public EmotionInputDetector setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        return this;
    }

    public EmotionInputDetector build() {
        mActivity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();


        //设置录音保存的路径
        String baseDir = "";
        if (FileUtil.isExternalStorageWritable()) {
            baseDir = mActivity.getExternalFilesDir("")
                    .getAbsolutePath() + "/shareboard/voice/";
            showLog("isExternalStorageWritable");
        } else {
            baseDir = mActivity.getFilesDir()
                    .getAbsolutePath() + "/shareboard/voice/";
            showLog("isExternalStorage not Writable");
        }
        mAudioRecoderUtils = new AudioRecoderUtils(baseDir);

        View view = View.inflate(mActivity, R.layout.layout_microphone, null);
        mVoicePop = new PopupWindowFactory(mActivity, view);

        //PopupWindow布局文件里面的控件
        final ImageView mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        final TextView mTextView = (TextView) view.findViewById(R.id.tv_recording_time);
        mPopVoiceText = (TextView) view.findViewById(R.id.tv_recording_text);
        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(
                new AudioRecoderUtils.OnAudioStatusUpdateListener() {

                    //录音中....db为声音分贝，time为录音时长
                    @Override
                    public void onUpdate(double db, long time) {
                        mImageView.getDrawable()
                                .setLevel((int) (3000 + 6000 * db / 100));
                        mTextView.setText(Utils.long2String(time));
                        if (time >= 1000 * 60 * 3) {
                            //结束录音（保存录音文件）
                            showToast(mActivity,
                                    mActivity.getString(R.string.max_voice_length));
                            mAudioRecoderUtils.stopRecord();
                        }


                    }

                    //录音结束，filePath为保存路径
                    @Override
                    public void onStop(long time, String filePath) {

                        //上传操作
                        sendChatVoiceFile(filePath, mTextView, time);


                    }

                    @Override
                    public void onError() {
                        mVoiceText.setVisibility(View.GONE);
                        mEditText.setVisibility(View.VISIBLE);
                    }
                });
        return this;
    }

    /**
     * 发送语音文件
     *
     * @param path
     * @param mTextView
     * @param time
     */

    public void sendChatVoiceFile(final String path, final TextView mTextView, final long time) {

        chatFile = new File(path);
        showLog("录音路径" + path);
        showLog("录音" + chatFile.canRead());
        showLog("录音" + chatFile.isFile());
        showLog("录音" + chatFile.exists());
        showLog("录音" + chatFile.length());

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                /**
                 * 1.检查网络状态并提醒
                 */
                if (!NetworkUtil.isNetworkConnected(mActivity)) {
                    //网络连接不可用
                    return NET_DISCONNECT;
                }
                /**
                 * 2.构造参数
                 */

                final String email = (String) SharedPrefUtil.getInstance()
                        .getData(share_user_email, "空");
                if (email.equals("空")) {
                    return -2;
                }
                final String token = (String) SharedPrefUtil.getInstance()
                        .getData(share_token, "空");
                if (token.equals("空")) {
                    return -2;
                }
                String meeting_url = (String) SharedPrefUtil.getInstance()
                        .getData(share_meeting_url, "");
                if (meeting_url.equals("")) {
                    return -2;
                }
                final String meeting_url_str = String.valueOf(meeting_url);

                /**
                 *3.上传
                 */

                OkGo.post(URL_SEND_CHAT_FILE)
                        .tag(this)
                        .isMultipart(true)
                        .params(post_chat_data, chatFile)
                        .params(post_need_feature, "voice")
                        .params(post_token, token)
                        .params(post_user_email, email)
                        .params(post_meeting_url, meeting_url_str)
                        .execute(new JsonCallback<CommonJson>() {
                            @Override
                            public void onSuccess(CommonJson o, Call call,
                                                  Response response) {
                                if (o.getCode() == SUCCESS) {
                                    /**
                                     * 4.上传成功,显示
                                     */
//                                    if (chatFile.exists()) {
//                                        chatFile.delete();
//                                    }
                                    mTextView.setText(Utils.long2String(0));
                                    MessageInfo messageInfo = new MessageInfo();
                                    messageInfo.setClient_email(email);
                                    messageInfo.setFilepath(URL_CHAT_VOICE + o.getMsg());
                                    messageInfo.setVoiceTime(time);
                                    EventBus.getDefault()
                                            .post(messageInfo);


                                } else {
                                    //提示所有错误
//                                    if (chatFile.exists()) {
//                                        chatFile.delete();
//                                    }
                                    showLog(o.getMsg());
                                    showToast(mActivity, mActivity.getResources()
                                            .getString(R
                                                    .string
                                                    .send_error));
                                }
                            }

                            @Override
                            public void onError(Call call, Response response,
                                                Exception e) {
                                super.onError(call, response, e);
//                                if (chatFile.exists()) {
//                                    chatFile.delete();
//                                }
                                //提示所有错误
                                showLog("系统错误");
                                showToast(mActivity, mActivity.getResources()
                                        .getString(R.string.send_error));
                            }
                        });

                return -1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
//                if (chatFile.exists()) {
//                    chatFile.delete();
//                }
                switch (integer) {
                    case NET_DISCONNECT:
                        //弹出对话框，让用户开启网络
                        NetworkUtil.setNetworkMethod(mActivity);
                        break;
                    case -1:
                        break;
                    case -2:
                        showToast(mActivity, R.string.please_relogin);
                        break;

                    default:
                        break;
                }
            }
        }.execute();


    }

    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    public boolean isEmotionLayoutShown() {
        return mEmotionLayout.isShown();
    }

    private void showEmotionLayout() {
        hideSoftInput();
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 787);
        }

        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);

    }

    public void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow()
                .getDecorView()
                .getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow()
                .getDecorView()
                .getRootView()
                .getHeight();

        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            sp.edit()
                    .putInt(SHARE_PREFERENCE_TAG, softInputHeight)
                    .apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metrics);

        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager()
                .getDefaultDisplay()
                .getRealMetrics(metrics);

        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}