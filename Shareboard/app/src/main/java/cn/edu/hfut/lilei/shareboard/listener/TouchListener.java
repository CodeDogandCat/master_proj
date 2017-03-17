package cn.edu.hfut.lilei.shareboard.listener;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;

public class TouchListener {

    public static class Builder {
        private Context mContext;
        private TextView textView1 = null;
        private TextView textView2 = null;
        private ImageView imageView = null;
        private LinearLayout linearLayout = null;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTextView1(TextView textView1) {
            this.textView1 = textView1;
            return this;
        }

        public Builder setTextView2(TextView textView2) {
            this.textView2 = textView2;
            return this;
        }

        public Builder setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
            return this;
        }

        public TouchListener create() {

            linearLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        if (textView1 != null) {
                            textView1.setTextColor(mContext.getResources()
                                    .getColor(R.color.my_black));
                        }
                        if (textView2 != null) {
                            textView2.setTextColor(mContext.getResources()
                                    .getColor(R.color.my_black));
                        }
                        if (imageView != null) {
                            ImageUtil.load(mContext, R.drawable.ic_black_rightarrow, R
                                    .drawable.ic_black_rightarrow, imageView);
                        }
                        view.setBackgroundColor(mContext.getResources()
                                .getColor(R.color.my_yellow));

                    } else {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            if (textView1 != null) {
                                textView1.setTextColor(mContext.getResources()
                                        .getColor(R.color.my_yellow));
                            }
                            if (textView2 != null) {
                                textView2.setTextColor(mContext.getResources()
                                        .getColor(R.color.my_yellow));
                            }
                            if (imageView != null) {
                                ImageUtil.load(mContext, R.drawable.ic_yellow_rightarrow, R
                                        .drawable.ic_yellow_rightarrow, imageView);
                            }
                            view.setBackgroundColor(mContext.getResources()
                                    .getColor(R.color.my_barblack));

                        }
                    }
                    return false;
                }
            });
            return new TouchListener();
        }

    }


}
