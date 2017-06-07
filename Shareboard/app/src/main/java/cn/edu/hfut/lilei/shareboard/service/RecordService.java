package cn.edu.hfut.lilei.shareboard.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

import cn.edu.hfut.lilei.shareboard.R;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;


public class RecordService extends Service {
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private boolean running;
    private int width = 720;
    private int height = 1080;
    private int dpi;
    private File output;


    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        serviceThread.start();
        running = false;
        mediaRecorder = new MediaRecorder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public boolean isRunning() {
        return running;
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public boolean startRecord() {
        showToast(this, getString(R.string.start_record));
        if (mediaProjection == null || running) {
            return false;
        }

        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        running = true;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean stopRecord() {
        if (!running) {
            return false;
        }
        if (output != null) {
            showToast(this, getString(R.string.save_to) + output.getAbsolutePath());
        } else {
            showToast(this, getString(R.string.record_fail));
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(output.getAbsolutePath()))));
        running = false;
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        mediaProjection.stop();


        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MeetingBoard", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null,
                null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);


        File file =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsoluteFile();//注意小米手机必须这样获得public绝对路径
        String fileName = "小喵白板";
        File appDir = new File(file, fileName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        fileName = System.currentTimeMillis() + ".mp4";
        output = new File(appDir, fileName);
        /**
         * 这里将时间作为不同照片的名称
         */
//        output = new File(MyAppUtil.getsaveDirectory(this),
////        output = new File(MyAppUtil.getsaveDirectory(this, "ScreenRecord"),
//                MP4_PATH_FOR_SCREEN_RECORD + "_" +
//                        System
//                                .currentTimeMillis
//                                        () + ".mp4");

        /**
         * 如果该文件夹已经存在，则删除它，否则创建一个
         */
        try {
            if (output.exists()) {
                output.delete();
            }
            output.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


        mediaRecorder.setOutputFile(output.getAbsolutePath());
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }
}