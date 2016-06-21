package com.snailgame.cjg.common.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.ZoomableDraweeView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.UIUtil;

import java.util.ArrayList;

import butterknife.Bind;
import third.viewpagerindicator.CirclePageIndicator;

public class ImageFullScreenActivity extends BaseFSActivity {

    @Bind(R.id.viewPagerImage)
    ViewPager viewPagerImage;
    @Bind(R.id.circlePageIndicator)
    CirclePageIndicator mCirclePageIndicator;

    public static Intent newIntent(Context context, int position, String[] mPicUrls) {
        Intent intent = new Intent(context, ImageFullScreenActivity.class);
        intent.putExtra(AppConstants.IMAGE_INDEX, position);
        intent.putExtra(AppConstants.IMAGE_URLS, mPicUrls);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        setWindowFullScreen();

        int index = getIntent().getIntExtra(AppConstants.IMAGE_INDEX, 0);
        String[] picUrl = getIntent().getStringArrayExtra(AppConstants.IMAGE_URLS);

        ArrayList<View> pageViews = new ArrayList<View>();
        for (String aPicUrl : picUrl) {
            ZoomableDraweeView imageView = new ZoomableDraweeView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPlaceHolderImageRes(R.drawable.pic_ragle_loading);
            imageView.setImageUrl(aPicUrl);
            imageView.setOnTouchListener(new ImageTouchEvent());
            pageViews.add(imageView);
        }

        viewPagerImage.setAdapter(new ViewPagerAdapter(pageViews));
        mCirclePageIndicator.setViewPager(viewPagerImage, pageViews.size());
//		viewPagerImage.setOnPageChangeListener(new ViewPagerChangeListener(layoutDots, pageViews.size(), this));
        UIUtil.changeViewPagerTabWithOutAnimation(viewPagerImage, index);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.image_full_screen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private class ImageTouchEvent extends SimpleOnGestureListener implements OnTouchListener {
        private GestureDetector detector;

        public ImageTouchEvent() {
            detector = new GestureDetector(ImageFullScreenActivity.this, this);
        }

        public boolean onTouch(View v, MotionEvent event) {
            return detector.onTouchEvent(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            ImageFullScreenActivity.this.finish();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private ArrayList<View> pageViews;

        public ViewPagerAdapter(ArrayList<View> pageViews) {
            this.pageViews = pageViews;
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    private class ViewPagerChangeListener implements OnPageChangeListener {
        private ImageView[] imageViews;
        private int normalDrawableId;
        private int selectDrawableId;

        public ViewPagerChangeListener(LinearLayout layoutDotHolder, int pageSize, Context context) {
            selectDrawableId = R.drawable.select_white_dot;
            normalDrawableId = R.drawable.normal_white_dot;
            imageViews = new ImageView[pageSize];
            layoutDotHolder.removeAllViews();
            for (int i = 0; i < pageSize; i++) {
                imageViews[i] = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(45, 45);
                params.leftMargin = 10;
                imageViews[i].setLayoutParams(params);

                if (i == 0) {
                    imageViews[i].setBackgroundResource(selectDrawableId);
                } else {
                    imageViews[i].setBackgroundResource(normalDrawableId);
                }

                layoutDotHolder.addView(imageViews[i]);
            }
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageSelected(int position) {
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position].setBackgroundResource(selectDrawableId);

                if (position != i) {
                    imageViews[i].setBackgroundResource(normalDrawableId);
                }
            }
        }
    }

    /**
     * 禁止点击菜单键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
