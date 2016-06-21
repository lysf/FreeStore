package third.com.zhy.base.loadandretry;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;

/**
 * Created by zhy on 15/8/26.
 */
public class LoadingAndRetryLayout extends FrameLayout {
    private View mLoadingView;
    private View mErrorView;
    private View mContentView;
    private View mEmptyView;
    private LayoutInflater mInflater;

    private AnimationDrawable animationDrawable;
    private int mLoadingViewButtonId = R.id.buttonLoading;
    private int mErrorViewButtonId = R.id.buttonError;
    private int mEmptyViewButtonId = R.id.buttonEmpty;

    private int mErrorMessageViewId;
    private int mEmptyMessageViewId;
    private int mLoadingMessageViewId;


    private static final String TAG = LoadingAndRetryLayout.class.getSimpleName();


    public LoadingAndRetryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
    }


    public LoadingAndRetryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingAndRetryLayout(Context context) {
        this(context, null);
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public void showLoading() {
        if (isMainThread()) {
            showView(mLoadingView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mLoadingView);
                }
            });
        }
    }

    public void showError() {
        if (isMainThread()) {
            showView(mErrorView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mErrorView);
                }
            });
        }

    }

    public void showContent() {
        if (isMainThread()) {
            showView(mContentView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mContentView);
                }
            });
        }
    }

    public void showEmpty() {
        if (isMainThread()) {
            showView(mEmptyView);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    showView(mEmptyView);
                }
            });
        }
    }


    private void showView(View view) {

        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        if (view == null) return;
        if (view == mLoadingView) {
            mLoadingView.setVisibility(View.VISIBLE);
            if (animationDrawable != null) {
                animationDrawable.start();
            }

            if (mErrorView != null)
                mErrorView.setVisibility(View.GONE);
            if (mContentView != null)
                mContentView.setVisibility(View.GONE);
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        } else if (view == mErrorView) {
            mErrorView.setVisibility(View.VISIBLE);
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            if (mContentView != null)
                mContentView.setVisibility(View.GONE);
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        } else if (view == mContentView) {
            mContentView.setVisibility(View.VISIBLE);
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            if (mErrorView != null)
                mErrorView.setVisibility(View.GONE);
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
        } else if (view == mEmptyView) {
            mEmptyView.setVisibility(View.VISIBLE);
            if (mLoadingView != null)
                mLoadingView.setVisibility(View.GONE);
            if (mErrorView != null)
                mErrorView.setVisibility(View.GONE);
            if (mContentView != null)
                mContentView.setVisibility(View.GONE);
        }


    }

    public View setContentView(int layoutId) {
        return setContentView(mInflater.inflate(layoutId, this, false));
    }

    public View setLoadingView(int layoutId) {
        return setLoadingView(mInflater.inflate(layoutId, this, false));
    }

    public View setEmptyView(int layoutId) {
        return setEmptyView(mInflater.inflate(layoutId, this, false));
    }

    public View setRetryView(int layoutId) {
        return setRetryView(mInflater.inflate(layoutId, this, false));
    }

    public View setLoadingView(View view) {
        View loadingView = mLoadingView;
        if (loadingView != null) {
            Log.w(TAG, "you have already set a loading view and would be instead of this new one.");
        }
        removeView(loadingView);
        addView(view);
        mLoadingView = view;
        return mLoadingView;
    }

    public View setEmptyView(View view) {
        View emptyView = mEmptyView;
        if (emptyView != null) {
            Log.w(TAG, "you have already set a empty view and would be instead of this new one.");
        }
        removeView(emptyView);
        addView(view);
        mEmptyView = view;
        return mEmptyView;
    }

    public View setRetryView(View view) {
        View retryView = mErrorView;
        if (retryView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(retryView);
        addView(view);
        mErrorView = view;
        return mErrorView;

    }

    public View setContentView(View view) {
        View contentView = mContentView;
        if (contentView != null) {
            Log.w(TAG, "you have already set a retry view and would be instead of this new one.");
        }
        removeView(contentView);
        addView(view);
        mContentView = view;
        return mContentView;
    }

    public View getRetryView() {
        return mErrorView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public View getDefaultErrorView(final OnLoadingAndRetryListener listener) {
        mErrorView = mInflater.inflate(R.layout.listview_error, null);

        if (!(mErrorMessageViewId > 0)) mErrorMessageViewId = R.id.textViewMessage;
        if (mErrorViewButtonId > 0) {
            View errorViewButton = mErrorView.findViewById(mErrorViewButtonId);
            if (errorViewButton != null) {
                errorViewButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.setRetryEvent(null);
                        }
                    }
                });
                errorViewButton.setVisibility(View.VISIBLE);
            }
        } else if (mErrorViewButtonId > 0) {
            View errorViewButton = mErrorView.findViewById(mErrorViewButtonId);
            errorViewButton.setVisibility(View.GONE);
        }

        return mErrorView;
    }

    public View getDefultLoadingView() {
        mLoadingView = (ViewGroup) mInflater.inflate(R.layout.listview_loading, null);
        LinearLayout container = (LinearLayout) mLoadingView.findViewById(R.id.loadingContainer);

        ImageView loadingImage = (ImageView) mLoadingView.findViewById(R.id.imageViewLoading);
        loadingImage.setBackgroundResource(R.drawable.loading_animal);
        animationDrawable = (AnimationDrawable) loadingImage.getBackground();

        if (mLoadingViewButtonId > 0) {
            View loadingViewButton = mLoadingView.findViewById(mLoadingViewButtonId);
            loadingViewButton.setVisibility(View.GONE);
        }
        return mLoadingView;
    }

    public View getDefaultEmptyView() {
        mEmptyView = (ViewGroup) mInflater.inflate(R.layout.listview_empty, null);
        LinearLayout container = (LinearLayout) mEmptyView.findViewById(R.id.emptyContainer);

        if (!(mEmptyMessageViewId > 0)) mEmptyMessageViewId = R.id.textViewMessage;

        if (mEmptyViewButtonId > 0) {
            View emptyViewButton = mEmptyView.findViewById(mEmptyViewButtonId);
            emptyViewButton.setVisibility(View.GONE);
        }

        return mEmptyView;
    }

    public void setEmptyMessage(String emptyMessage) {
        if (mEmptyMessageViewId > 0)
            ((TextView) mEmptyView.findViewById(mEmptyMessageViewId)).setText(emptyMessage);
    }
}
