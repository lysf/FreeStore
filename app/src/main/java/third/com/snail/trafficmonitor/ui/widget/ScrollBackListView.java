package third.com.snail.trafficmonitor.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.snailgame.cjg.R;

/**
 * Created by kevin on 14/12/30.
 */
public class ScrollBackListView extends ListView {
    private final String TAG = ScrollBackListView.class.getSimpleName();

    public ScrollBackListView(Context context) {
        this(context, null);
    }

    public ScrollBackListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollBackListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFooterView(context);
    }

    private ImageView mFooterImage;
    private View mFillEmptyView;
    private Scroller mScroll;

    private float mLastY;
    private boolean mResetPending = false;

    private void initFooterView(Context context) {
        mScroll = new Scroller(context, new DecelerateInterpolator(1.4f));
        mFillEmptyView = new View(context);
        mFillEmptyView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        addFooterView(mFillEmptyView);
        View v = LayoutInflater.from(context).inflate(R.layout.engine_item_load_end_layout, null);
        mFooterImage = (ImageView) v.findViewById(R.id.footer_img);
        addFooterView(v);

        mFooterImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mFooterImage.getLayoutParams();
                lp.bottomMargin = -mFooterImage.getMeasuredHeight();
                mFooterImage.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    public void setEmptyHeight(int height) {
        AbsListView.LayoutParams lp = (LayoutParams) mFillEmptyView.getLayoutParams();
        lp.height = height;
        mFillEmptyView.setLayoutParams(lp);
    }

    public void resetEmptyHeight() {
        setEmptyHeight(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getRawY();
                float deltaY = mLastY - currentY;

                if (getLastVisiblePosition() == getAdapter().getCount() - 1
                        && (getBottomMargin() > 0 || deltaY > 0)) {
                    updateFooterHeight(deltaY);
                }
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mResetPending)
                    resetFooterHeight();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void showHideFooter(boolean show) {
        if (show) {
            mFooterImage.setVisibility(VISIBLE);
        } else {
            mFooterImage.setVisibility(INVISIBLE);
        }
    }

    private int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mFooterImage.getLayoutParams();
        return lp.bottomMargin;
    }

    private void setBottomMargin(int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mFooterImage.getLayoutParams();
        lp.bottomMargin = height;
        mFooterImage.setLayoutParams(lp);
    }

    private void updateFooterHeight(float delta) {
        mResetPending = true;
        int height = getBottomMargin() + (int) (delta / 1.8);
        setBottomMargin(height);
    }

    private void resetFooterHeight() {
        mResetPending = false;
        int bottomMargin = getBottomMargin();
        int footerHeight = mFooterImage.getHeight();
        if (footerHeight > 0) {
            mScroll.startScroll(0, bottomMargin, 0, -footerHeight - bottomMargin, 400);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroll.computeScrollOffset()) {
            //松手回弹效果
            setBottomMargin(mScroll.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }
}
