package cn.edu.hfut.lilei.shareboard.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.model.FullImageInfo;
import cn.edu.hfut.lilei.shareboard.utils.DownLoadImageService;
import cn.edu.hfut.lilei.shareboard.utils.ImageDownLoadCallBack;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;

public class FullImageActivity extends Activity {

    @Bind(R.id.full_image)
    ImageView fullImage;
    @Bind(R.id.tv_image_size)
    TextView imageSize;
    @Bind(R.id.full_lay)
    RelativeLayout fullLay;
    private int mLeft;
    private int mTop;
    private float mScaleX;
    private float mScaleY;
    private Drawable mBackground;
    private Handler handler;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full_image);
        mContext = this;
        ButterKnife.bind(this);
        EventBus.getDefault()
                .register(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) //在ui线程执行
    public void onDataSynEvent(final FullImageInfo fullImageInfo) {

        //显示图片大小
        Glide.with(this)
                .load(fullImageInfo.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        int size = ImageUtil.getSize(resource);
                        double showSize = size;
                        if (size < 1024) {
                            imageSize.setText(showSize + " B");

                        } else
                            if (size < 1024 * 1024) {
                                BigDecimal bg = new BigDecimal(size / 1024);
                                showSize = bg.setScale(1, BigDecimal.ROUND_HALF_UP)
                                        .doubleValue();
                                imageSize.setText(showSize + " KB");
                            } else {
                                BigDecimal bg = new BigDecimal(size / 1024 / 1024);
                                showSize = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
                                        .doubleValue();
                                imageSize.setText(showSize + " MB");
                            }
                    }
                });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    showToast(mContext, "图片已保存到本地相册");
                } else {
                    showToast(mContext, "图片保存到本地失败");
                }
            }
        };

        //设置长按监听器
        fullImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showToast(mContext, "保存图片到本地...");
                onDownLoad(fullImageInfo.getImageUrl());
                return false;
            }
        });
        //显示图片
        final int left = fullImageInfo.getLocationX();
        final int top = fullImageInfo.getLocationY();
        final int width = fullImageInfo.getWidth();
        final int height = fullImageInfo.getHeight();
        mBackground = new ColorDrawable(getResources().getColor(R.color.my_black));
        fullLay.setBackground(mBackground);
        fullImage.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        fullImage.getViewTreeObserver()
                                .removeOnPreDrawListener(this);
                        int location[] = new int[2];
                        fullImage.getLocationOnScreen(location);
                        mLeft = left - location[0];
                        mTop = top - location[1];
                        mScaleX = width * 1.0f / fullImage.getWidth();
                        mScaleY = height * 1.0f / fullImage.getHeight();
                        activityEnterAnim();
                        return true;
                    }
                });
//        Glide.with(this).load(fullImageInfo.getImageUrl()).into(fullImage);
        ImageUtil.loadIntoUseFitWidth(this, fullImageInfo.getImageUrl(),
                R.drawable.bg_rectangle_white,
                fullImage);
    }

    /**
     * 启动图片下载线程
     */
    private void onDownLoad(String url) {
        DownLoadImageService service = new DownLoadImageService(mContext,
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {

                    }

                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
                        Message message = new Message();
                        message.what = 100;
                        handler.sendMessageDelayed(message, 0);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessageDelayed(message, 100);
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

    private void activityEnterAnim() {
        fullImage.setPivotX(0);
        fullImage.setPivotY(0);
        fullImage.setScaleX(mScaleX);
        fullImage.setScaleY(mScaleY);
        fullImage.setTranslationX(mLeft);
        fullImage.setTranslationY(mTop);
        fullImage.animate()
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .
                        setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void activityExitAnim(Runnable runnable) {
        fullImage.setPivotX(0);
        fullImage.setPivotY(0);
        fullImage.animate()
                .scaleX(mScaleX)
                .scaleY(mScaleY)
                .translationX(mLeft)
                .translationY(mTop)
                .
                        withEndAction(runnable)
                .
                        setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 255, 0);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    @Override
    public void onBackPressed() {
        activityExitAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @OnClick(R.id.full_image)
    public void onClick() {
        activityExitAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault()
                .unregister(this);
        super.onDestroy();
    }

}
