package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;

/**
 * Created by sunxy on 14-8-22.
 */
public class CustomLoadingView extends RelativeLayout implements View.OnClickListener {
    private View view;
    private ImageView imageView;
    private TextView textView;
    private Button btnError;

    private AnimationDrawable animationDrawable;
    public CustomLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.custom_loading_view, null);
        imageView = (ImageView) view.findViewById(R.id.loadingGif);
        textView = (TextView) view.findViewById(R.id.loadingText);
        btnError = (Button) view.findViewById(R.id.buttonError);
        btnError.setOnClickListener(this);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(view, layoutParams);
    }

    public void showLoading() {
        setVisibility(VISIBLE);
        stopAnimation();
        imageView.setBackgroundResource(R.drawable.loading_animal);
        animationDrawable=(AnimationDrawable)imageView.getBackground();
        animationDrawable.start();

        textView.setText(FreeStoreApp.getContext().getString(R.string.please_wait));
        btnError.setVisibility(GONE);
    }

    private void stopAnimation() {
        if(animationDrawable!=null&&animationDrawable.isRunning()) {
           animationDrawable.stop();
        }
    }

    public void hide() {
        stopAnimation();
        setVisibility(GONE);
    }

    public void showError() {
        setVisibility(View.VISIBLE);
        stopAnimation();
        imageView.setBackgroundResource(R.drawable.no_network);
        textView.setText(FreeStoreApp.getContext().getString(R.string.bad_network));
        btnError.setVisibility(retryLoadListener != null ? VISIBLE : GONE);
    }

    private RetryLoadListener retryLoadListener;

    @Override
    public void onClick(View v) {
        if (retryLoadListener != null) {
            retryLoadListener.retryLoad();
        }
    }

    public interface RetryLoadListener {
        void retryLoad();
    }

    public void setOnRetryLoad(RetryLoadListener listener) {
        this.retryLoadListener = listener;
    }
}
