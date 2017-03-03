package cn.edu.hfut.lilei.shareboard.utils;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import cn.edu.hfut.lilei.shareboard.R;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.ALBUM_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CAMERA_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.CROP_REQUEST_CODE;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.IMG_PATH_FOR_CAMERA;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_avatar;

public class ImageUtil {

    /**
     * 检查传入的url或者数组中第一个url是不是为空
     */
    @SuppressWarnings("uncheck")
    public static boolean isUrlsEmpty(String... urls) {
        return urls == null || urls.length == 0 || urls[0] == null || urls[0].trim()
                .isEmpty();
    }

    @SuppressWarnings("uncheck")
    public static String checkAndHandleUrl(String... urls) {
        if (isUrlsEmpty(urls)) {
            return "empty_url";
        }
        return urls[0];
    }

    /**
     * 加载网络图片
     *
     * @param context
     * @param url
     * @param target
     */
    public static void load(@NonNull Context context, String url,
                            @NonNull ImageView target) {
        Picasso.with(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)//没有大图
                .placeholder(R.drawable.imageholder)
                .error(R.drawable.imageholder)
                .into(target);
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param drawableid
     * @param target
     */
    public static void load(@NonNull Context context, @NonNull int drawableid,
                            @NonNull ImageView target) {
        Picasso.with(context)
                .load(drawableid)
                .config(Bitmap.Config.RGB_565)//没有大图
                .placeholder(R.drawable.imageholder)
                .error(R.drawable.imageholder)
                .into(target);
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param drawableid
     * @param target
     */
    public static void loadWithHolder(@NonNull Context context, @NonNull int drawableid, @NonNull
            int drawableid2, @NonNull ImageView target) {
        Picasso.with(context)
                .load(drawableid)
                .config(Bitmap.Config.RGB_565)//没有大图
                .placeholder(drawableid2)
                .into(target);
    }

    /**
     * 加载用户头像
     *
     * @param context
     * @param url
     * @param target
     */
    public static void loadAvatar(@NonNull Context context, String url,
                                  @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }

    public static void loadAvatar(@NonNull Context context, @NonNull int drawableid,
                                  @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(drawableid)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }

    public static void loadAvatar(@NonNull Context context, @NonNull File imgfile,
                                  @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(imgfile)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }

    public static void loadAvatar(@NonNull Context context, @NonNull Uri imgfile,
                                  @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(imgfile)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     */
    public static String getImageAbsolutePath19(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else
                if (isDownloadsDocument(imageUri)) {
                    String id = DocumentsContract.getDocumentId(imageUri);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else
                    if (isMediaDocument(imageUri)) {
                        String docId = DocumentsContract.getDocumentId(imageUri);
                        String[] split = docId.split(":");
                        String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else
                            if ("video".equals(type)) {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            } else
                                if ("audio".equals(type)) {
                                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                }
                        String selection = MediaStore.Images.Media._ID + "=?";
                        String[] selectionArgs = new String[]{split[1]};
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
        }

        // MediaStore (and general)
        if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else
            if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                return imageUri.getPath();
            }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 删除一条图片
     */
    public static void deleteImageUri(Context context, Uri uri) {
        context.getContentResolver()
                .delete(uri, null, null);
    }

    /**
     * 用第三方应用app打开图片
     */
    public static void openImageByOtherApp(Context context, Uri imageUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "image/*");
        context.startActivity(intent);
    }

    /**
     * 打开相机拍照
     *
     * @param activity
     */
    public static void startCamera(Activity activity) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                getExternalStorageDirectory(), IMG_PATH_FOR_CAMERA)));
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }

    /**
     * 打开相册选择图片
     *
     * @param activity
     */
    public static void startGallery(Activity activity) {


        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);

    }

    /**
     * 开始裁剪
     *
     * @param uri
     */
    public static void startCrop(Activity activity, Uri uri, Uri cropUri, int width, int
            height) {

        //调用Android系统自带的一个图片剪裁页面
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        activity.startActivityForResult(intent, CROP_REQUEST_CODE);

    }


    public static void loadMyAvatar(@NonNull Context context, String url,
                                    @NonNull ImageView target) {

        /**
         * 1.检查安卓本地用户图片，如果有，显示
         */
        String uriStr = (String) SharedPrefUtil.getInstance()
                .getData(share_avatar, "空");
        if (!uriStr.equals("空")) {
            showLog("查到的图片路径" + uriStr);
            Picasso.with(context)
                    .load(Uri.parse(uriStr))
                    .config(Bitmap.Config.RGB_565)//没有大图
                    .into(target);
        }

        //还没有加载成功
        if (target.getDrawable() == null) {
            /**
             * 2.从网络加载显示
             */
            //从网络加载
            Picasso.with(context)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)//没有大图
                    .into(target);
        }


    }
}
