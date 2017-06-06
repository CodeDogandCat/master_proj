package cn.edu.hfut.lilei.shareboard.utils;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cn.edu.hfut.lilei.shareboard.R;

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
    public static void startCamera(Activity activity, String baseDir) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(baseDir, IMG_PATH_FOR_CAMERA)));
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


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                result = result.replaceAll("/", "_a");
                result = result.replaceAll("\\+", "_b");
                result = result.replaceAll("=", "_c");
                result = result.replaceAll("\\\\", "_d");
//                result = URLEncoder.encode(result, "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        base64Data = base64Data.replaceAll("_a", "/");
        base64Data = base64Data.replaceAll("_b", "+");
        base64Data = base64Data.replaceAll("_c", "=");
        base64Data = base64Data.replaceAll("_d", "\\");

//        try {
//            base64Data = URLDecoder.decode(base64Data, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void loadMyAvatar(@NonNull Context context,
                                    @NonNull ImageView target) {

        /**
         * 检查缓存的url，如果有，显示
         */
        String url = (String) SharedPrefUtil.getInstance()
                .getData(share_avatar, "空");
        if (!url.equals("空")) {

            Picasso.with(context)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)//没有大图
                    .into(target);
        }

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
    public static void load(@NonNull Context context, @NonNull int drawableid, @Nullable
            Integer holder, @NonNull ImageView target) {
        if (holder == null) {
            holder = R.drawable.imageholder;
        }
        Picasso.with(context)
                .load(drawableid)
                .config(Bitmap.Config.RGB_565)//没有大图
                .placeholder(holder)
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

    public static void loadAvatar(@NonNull Context context, @NonNull String url,
                                  @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(url)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }

    public static void loadAvatarNoCache(@NonNull Context context, @NonNull Uri imgfile,
                                         @NonNull ImageView target) {
        //不要error和placeHolder ,为了显示默认的文字头像
        Picasso.with(context)
                .load(imgfile)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .config(Bitmap.Config.RGB_565)//没有大图
                .into(target);
    }


    /**
     * 传入一个bitmap，根据传入比例进行大小缩放
     *
     * @param bitmap
     * @param widthRatio  宽度比例，缩小就比1小，放大就比1大
     * @param heightRatio
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float widthRatio, float heightRatio) {
        Matrix matrix = new Matrix();
        matrix.postScale(widthRatio, heightRatio);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }


    /**
     * 传入图片路径，根据图片进行压缩，仅压缩大小，不压缩质量
     *
     * @param oriFile    源文件
     * @param targetFile 这个和 stream传一个就行
     * @param ifDel      是否需要在压缩完毕后删除原图
     */
    public static void compressImage(File oriFile, File targetFile, OutputStream stream,
                                     boolean ifDel) {
        if (oriFile == null) return;
        Log.i("shareboard", "源图片为" + oriFile);
        Log.i("shareboard", "目标地址为" + targetFile);
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true; // 不读取像素数组到内存中，仅读取图片的信息，非常重要
            BitmapFactory.decodeFile(oriFile.getAbsolutePath(), opts);//读取文件信息，存放到Options对象中
            // 从Options中获取图片的分辨率
            int imageHeight = opts.outHeight;
            int imageWidth = opts.outWidth;
            int longEdge = Math.max(imageHeight, imageWidth);//取出宽高中的长边
            int pixelCount = (imageWidth * imageHeight) >> 20;//看看这张照片有多少百万像素
            Log.i("shareboard",
                    "图片宽为" + imageWidth + "图片高为" + imageHeight + "图片像素数为" + pixelCount + "百万像素");

            long size = oriFile.length();
            Log.i("shareboard", "f.length 图片大小为" + (size) + " B");
            //走到这一步的时候，内存里还没有bitmap
            Bitmap bitmap = null;
            if (pixelCount > 4) {//如果超过了4百万像素，那么就首先对大小进行压缩
                float compressRatio = longEdge / 1280f;
                int compressRatioInt = Math.round(compressRatio);
                if (compressRatioInt % 2 != 0 && compressRatioInt != 1)
                    compressRatioInt++;//如果是奇数的话，就给弄成偶数
                Log.i("shareboard", "长宽压缩比是" + compressRatio + " 偶数化后" + compressRatioInt);
                //尺寸压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                //目标出来的大小1024*1024 1百万像素，100k左右
                options.inSampleSize = Math.round(
                        compressRatioInt);//注意，此处必须是偶数，根据计算好的比例进行压缩,如果长边没有超过1280*1.5，就不去压缩,否则就压缩成原来的一半
                options.inJustDecodeBounds = false;//在decode file的时候，不仅读取图片的信息，还把像素数组到内存
                options.inPreferredConfig =
                        Bitmap.Config.RGB_565;//每个像素占四位，即R=5，G=6，B=5，没有透明度，那么一个像素点占5+6+5=16位
                //现在开始将bitmap放入内存
                bitmap = BitmapFactory.decodeFile(oriFile.getAbsolutePath(),
                        options);//根据压缩比取出大小已经压缩好的bitmap
                //此处会打印出存入内存的bitmap大小
            } else {//如果是长图或者长边短于1920的图，那么只进行质量压缩
                // 现在开始将bitmap放入内存
                bitmap = BitmapFactory.decodeFile(oriFile.getAbsolutePath());
                //此处会打印出bitmap在内存中占得大小
            }
            if (targetFile != null) compressMethodAndSave(bitmap, targetFile);
            if (stream != null) compressBitmapToStream(bitmap, stream);
            if (ifDel) oriFile.delete();//是否要删除源文件
            System.gc();
        } catch (Exception e) {
            Log.d("shareboard", "" + e.getMessage()
                    .toString());
        }
    }

    /**
     * 获取一个bitmap在内存中所占的大小
     *
     * @param image
     * @return
     */
    private static int getSize(Bitmap image) {
        int size = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            size = image.getAllocationByteCount();
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
                size = image.getByteCount();
            } else {
                size = image.getRowBytes() * image.getHeight();
            }
        return size;
    }

    /**
     * 根据传来的bitmap的大小计算一个质量压缩率，并且保存到指定路径中去，只压缩质量，不压缩大小
     *
     * @param image
     * @param targetFile
     */
    public static void compressMethodAndSave(Bitmap image, File targetFile) {
        try {
            OutputStream stream = new FileOutputStream(targetFile);
            int size = compressBitmapToStream(image, stream);
            if (size == 0) return;
            long afterSize = targetFile.length();
            Log.i("shareboard",
                    "压缩完后图片大小" + (afterSize >> 10) + "KB 压缩率:::" + afterSize * 100 / size + "%");
        } catch (Exception e) {
            Log.i("shareboard", "压缩图片出现异常", e);
        }
    }

    public static int compressBitmapToStream(Bitmap image, OutputStream stream) {
        if (image == null || stream == null) return 0;
        try {
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            int size = getSize(image);
            Log.i("shareboard",
                    "存入内寸的bitmap大小是" + (size >> 10) + " KB 宽度是" + image.getWidth() + " 高度是" +
                            image.getHeight());
            int quality = getQuality(size);//根据图像的大小得到合适的有损压缩质量
            Log.i("shareboard", "目前适用的有损压缩率是" + quality);
            long startTime = System.currentTimeMillis();
            image.compress(format, quality, stream);//压缩文件并且输出
            if (image != null) {
                image.recycle();//此处把bitmap从内存中移除
                image = null;
            }
            Log.i("shareboard", "压缩图片并且存储的耗时" + (System.currentTimeMillis() - startTime));
            return size;
        } catch (Exception e) {
            Log.i("shareboard", "压缩图片出现异常", e);
        }
        return 0;
    }

    /**
     * 根据图像的大小得到合适的有损压缩质量，因为此时传入的bitmap大小已经比较合适了，靠近1000*1000，所以根据其内存大小进行质量压缩
     *
     * @param size
     * @return
     */
    private static int getQuality(int size) {
        int mb = size >> 20;//除以100万，也就是m
        int kb = size >> 10;
        Log.i("shareboard", "准备按照图像大小计算压缩质量，大小是" + kb + "KB,兆数是" + mb);
        if (mb > 70) {
            return 17;
        } else
            if (mb > 50) {
                return 20;
            } else
                if (mb > 40) {
                    return 25;
                } else
                    if (mb > 20) {
                        return 40;
                    } else
                        if (mb > 10) {
                            return 60;
                        } else
                            if (mb > 3) {//目标压缩大小 100k，这里可根据实际情况来判断
                                return 60;
                            } else
                                if (mb >= 2) {
                                    return 60;
                                } else
                                    if (kb > 1500) {
                                        return 70;
                                    } else
                                        if (kb > 1000) {
                                            return 80;
                                        } else
                                            if (kb > 500) {
                                                return 85;
                                            } else
                                                if (kb > 100) {
                                                    return 90;
                                                } else {
                                                    return 100;
                                                }
    }


    public static void loadIntoUseFitWidth(Context context, final String imageUrl, int errorImageId,
                                           final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model,
                                               Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() -
                                imageView.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height =
                                vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        return false;
                    }
                })
                .placeholder(errorImageId)
                .error(errorImageId)
                .into(imageView);
    }


}
