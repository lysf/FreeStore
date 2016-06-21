package third.com.snail.trafficmonitor.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.snailgame.cjg.R;

import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * Created by kevin on 14/11/20.
 */
public class LoadingRecyclerView extends FrameLayout {
    private final String TAG = LoadingRecyclerView.class.getSimpleName();

    protected View mLoading;
    protected RecyclerView mRecyclerView;
    protected TextView mEmptyView;
    protected TextView mHintText;

    public LoadingRecyclerView(Context context) {
        this(context, null);
    }

    public LoadingRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LogWrapper.e(TAG, "initialize");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.engine_loading_recycler_view, this, true);

        mLoading = rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_tv);
        mHintText = (TextView) rootView.findViewById(R.id.tv_hint);
        mLoading.setVisibility(INVISIBLE);
        mRecyclerView.setVisibility(INVISIBLE);
        mEmptyView.setVisibility(INVISIBLE);
    }

    public boolean isLoading() {
        return mLoading.getVisibility() == VISIBLE;
    }

    public void showLoading() {
        mLoading.setVisibility(VISIBLE);
        mRecyclerView.setVisibility(INVISIBLE);
    }

    public void finishLoading() {
        ViewPropertyAnimator.animate(mLoading).alpha(0f).setDuration(getContext()
                .getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                        mLoading.setVisibility(INVISIBLE);
                        ViewHelper.setAlpha(mLoading, 1f);
                        mRecyclerView.setVisibility(VISIBLE);
                    }
                }).start();
    }

    public void showHint(CharSequence sequence) {
        mHintText.setVisibility(VISIBLE);
        mHintText.setText(sequence);
    }

    public void hideHint() {
        mHintText.setVisibility(GONE);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public RecyclerView.ViewHolder findViewHolderByPosition(int position) {
        return mRecyclerView.findViewHolderForPosition(position);
    }

    public void setHasFixedSize(boolean fixed) {
        mRecyclerView.setHasFixedSize(fixed);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        if (adapter != null) {
            mRecyclerView.getAdapter().registerAdapterDataObserver(mObserver);
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(GONE);
            }
        }
    };

    @Override
    public void setOverScrollMode(int overScrollMode) {
        super.setOverScrollMode(overScrollMode);
        if (mRecyclerView != null) {
            mRecyclerView.setOverScrollMode(overScrollMode);
        }
    }
}
