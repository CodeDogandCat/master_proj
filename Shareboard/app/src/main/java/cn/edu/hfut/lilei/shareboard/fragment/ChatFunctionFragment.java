package cn.edu.hfut.lilei.shareboard.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.base.BaseFragment;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.model.MessageInfo;
import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.utils.FileUtil;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_CHAT_IMG;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_SEND_CHAT_FILE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_chat_data;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_need_feature;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;

public class ChatFunctionFragment extends BaseFragment {
    private View rootView;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private File output;
    private Uri imageUri;
    private Context mContext;
    private File srcFile, targetFile;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_chat_function, container, false);
            ButterKnife.bind(this, rootView);
        }
        mContext = rootView.getContext();
        return rootView;
    }

    @OnClick({R.id.chat_function_photo, R.id.chat_function_photograph})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_function_photograph:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    takePhoto();
                }
                break;
            case R.id.chat_function_photo:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    choosePhoto();
                }
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        /**
         * 最后一个参数是文件夹的名称
         */

        String baseDir = "";
        if (FileUtil.isExternalStorageWritable()) {
            baseDir = mActivity.getExternalFilesDir("")
                    .getAbsolutePath() + "/shareboard/";
        } else {
            baseDir = mActivity.getFilesDir()
                    .getAbsolutePath() + "/shareboard/";
        }

        File file = new File(baseDir,
                "image");//拍照后保存的路径

        if (!file.exists()) {
            file.mkdir();
        }
        /**
         * 这里将时间作为不同照片的名称
         */
        output = new File(file, System.currentTimeMillis() + ".jpeg");

        /**
         * 如果该文件已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 隐式打开拍照的Activity，并且传入CROP_PHOTO常量作为拍照结束后回调的标志
         */
        imageUri = Uri.fromFile(output);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CROP_PHOTO);

    }

    /**
     * 从相册选取图片
     */
    private void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    public void onActivityResult(int req, int res, final Intent data) {
        switch (req) {
            case CROP_PHOTO:
                if (res == Activity.RESULT_OK) {
                    sendChatImageFile(
                            ImageUtil.getImageAbsolutePath19(mContext, imageUri));
                } else {
                    showLog("失败");
                }


                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        //上传操作
                        sendChatImageFile(
                                ImageUtil.getImageAbsolutePath19(mContext, uri));

                    } catch (Exception e) {
                        e.printStackTrace();
                        showLog(e.getMessage());
                    }
                } else {
                    showLog("失败");
                }

                break;

            default:
                break;
        }

    }

    /**
     * 发送聊天 图片
     *
     * @param path
     */
    public void sendChatImageFile(final String path) {
        showLog("图片路径" + path);

        srcFile = new File(path);
        targetFile = new File(path.substring(0, path.lastIndexOf(".")
        ) + "compressed.jpeg");


        ImageUtil.compressImage(srcFile, targetFile, null, false);


        if (targetFile.length() > 1024 * 1024 * 6)// 6M  照片最大限制
        {
            showToast(mContext, getString(R.string.image_too_large));
        } else {


            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... voids) {
                    /**
                     * 1.检查网络状态并提醒
                     */
                    if (!NetworkUtil.isNetworkConnected(mContext)) {
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
                    long meeting_url = (long) SharedPrefUtil.getInstance()
                            .getData(share_meeting_url, -1L);
                    if (meeting_url == -1) {
                        return -2;
                    }
                    final String meeting_url_str = String.valueOf(meeting_url);

                    /**
                     *3.上传
                     */


                    OkGo.post(URL_SEND_CHAT_FILE)
                            .tag(this)
                            .isMultipart(true)
                            .params(post_need_feature, "image")
                            .params(post_token, token)
                            .params(post_user_email, email)
                            .params(post_meeting_url, meeting_url_str)
                            .params(post_chat_data, targetFile)
                            .execute(new JsonCallback<CommonJson>() {
                                @Override
                                public void onSuccess(CommonJson o, Call call,
                                                      Response response) {
                                    if (o.getCode() == SUCCESS) {
                                        /**
                                         * 4.上传成功,显示
                                         */
                                        targetFile.delete();
                                        MessageInfo messageInfo = new MessageInfo();
                                        messageInfo.setImageUrl(URL_CHAT_IMG + o.getMsg());
                                        messageInfo.setClient_email(email);
                                        EventBus.getDefault()
                                                .post(messageInfo);


                                    } else {
                                        //提示所有错误
                                        targetFile.delete();
                                        showLog(o.getMsg());
                                        showToast(mContext,
                                                getString(R.string.send_error));
                                    }
                                }

                                @Override
                                public void onError(Call call, Response response,
                                                    Exception e) {
                                    super.onError(call, response, e);
                                    targetFile.delete();
                                    //提示所有错误
                                    showLog("系统错误");
                                    showToast(mContext, getString(R.string.send_error));
                                }
                            });

                    return -1;
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    super.onPostExecute(integer);
                    targetFile.delete();
                    switch (integer) {
                        case NET_DISCONNECT:
                            //弹出对话框，让用户开启网络
                            NetworkUtil.setNetworkMethod(mContext);
                            break;
                        case -1:
                            break;
                        case -2:
                            showToast(mContext, R.string.please_relogin);
                            break;

                        default:
                            break;
                    }
                }
            }.execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                toastShow("请同意系统权限后继续");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
