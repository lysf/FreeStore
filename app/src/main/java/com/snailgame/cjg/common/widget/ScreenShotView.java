package com.snailgame.cjg.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.ImageFullScreenActivity;
import com.snailgame.cjg.util.ComUtil;

/**
 * 用于显示截图的可以左右滚动的View
 * Created by sunxy on 2015/4/17.
 */
public class ScreenShotView extends HorizontalScrollView {
    private Context mContenxt;
    private String screenShotUrls[];

    public ScreenShotView(Context context) {
        super(context);
    }

    public ScreenShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenShotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScreenShotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScreenShotView(Context context, String screenShotUrls[]) {
        if (screenShotUrls != null) {
            mContenxt = context;
            this.screenShotUrls = screenShotUrls;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            for (int i = 0; i < screenShotUrls.length; i++) {
                FSSimpleImageView imageView = new FSSimpleImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPlaceHolderImageRes(R.drawable.pic_ragle_loading);
                imageView.setImageUrl(screenShotUrls[i]);
                imageView.setOnClickListener(new StartFullScreesImages(i));
                LinearLayout.LayoutParams paramas = new LinearLayout.LayoutParams(ComUtil.dpToPx(109), ComUtil.dpToPx(178));
                paramas.setMargins(ComUtil.dpToPx(2), ComUtil.dpToPx(0), ComUtil.dpToPx(2), ComUtil.dpToPx(0));
                linearLayout.addView(imageView, paramas);
            }
            removeAllViews();
            addView(linearLayout);
        }
    }

    private class StartFullScreesImages implements View.OnClickListener {
        private int position;

        public StartFullScreesImages(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            mContenxt.startActivity(ImageFullScreenActivity.newIntent(mContenxt, position, screenShotUrls));
        }
    }

}
