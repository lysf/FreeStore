package com.snailgame.cjg.common.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.util.JumpUtil;

import java.util.List;

/**
 * Created by sunxy on 2014/10/10.
 */
public class CustomQuickReturnView extends HorizontalScrollView {

    private int width;
    private Context context;
    private LinearLayout linearLayout;
    private float startX;
    private float startY;
    private Activity activity;

    private int[] mRoute;

    public CustomQuickReturnView(Context context) {
        super(context);
    }

    public CustomQuickReturnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        width = (getResources().getDisplayMetrics().widthPixels - 3) / 4;
        initView(context);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = ev.getX();
                float deltaX = ev.getX() - startX;
                float deltaY = ev.getY() - startY;
                //处理左右滑动
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (currentX > startX
                            || (currentX < startX)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startX = 0;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initView(Context context) {
        linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        addView(linearLayout);
    }


    public void inflatHotGameData(List<ContentModel> lists, int[] route) {
        if (lists != null) {
            mRoute = route;
            LayoutInflater inflater = LayoutInflater.from(FreeStoreApp.getContext());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    width,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

            int size = lists.size();
            if (size == 0)
                return;

            for (int index = 0; index < size; index++) {
                final ContentModel contentModel = lists.get(index);
                final int position = index + 1;
                if (contentModel != null) {
                    LinearLayout innerLayout;
                    innerLayout = (LinearLayout) inflater.inflate(R.layout.home_image_width_text, null);
                    FSSimpleImageView iconImage = (FSSimpleImageView) innerLayout.findViewById(R.id.homeIcon);
                    TextView iconText = (TextView) innerLayout.findViewById(R.id.homeIconText);
                    LinearLayout rootLayout = (LinearLayout) innerLayout.findViewById(R.id.gameRootLayout);
                    iconImage.setImageUrl(contentModel.getsImageUrl());
                    iconText.setText(contentModel.getsTitle());
                    rootLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] route = mRoute.clone();
                            route[AppConstants.STATISTCS_DEPTH_FOUR] = position;
                            jumpHotGame(contentModel, route);
                        }
                    });
                    innerLayout.setLayoutParams(layoutParams);
                    linearLayout.addView(innerLayout);
                }

            }
        }
    }

    private void jumpHotGame(ContentModel contentModel, int[] route) {
        //热门游戏跳转
        JumpUtil.itemJump(context, contentModel.getsJumpUrl(), contentModel.getcSource(), contentModel, route);
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
