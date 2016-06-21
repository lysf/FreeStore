package com.snailgame.cjg.news.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.news.adpter.ChannelDragAdapter;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ToastUtils;

public class ChannelDragGrid extends GridView {
    /**
     * 频道数量的最小值
     */
    private final int MIN_CHANNEL_COUT = 3;
    /**
     * 点击时候的X位置
     */
    public int downX;
    /**
     * 点击时候的Y位置
     */
    public int downY;
    /**
     * 点击时候对应整个界面的X位置
     */
    public int windowX;
    /**
     * 点击时候对应整个界面的Y位置
     */
    public int windowY;
    /**
     * 屏幕上的X
     */
    private int win_view_x;
    /**
     * 屏幕上的Y
     */
    private int win_view_y;
    /**
     * 拖动的里x的距离
     */
    int dragOffsetX;
    /**
     * 拖动的里Y的距离
     */
    int dragOffsetY;
    /**
     * 长按时候对应postion
     */
    public int dragPosition;
    /**
     * Up后对应的ITEM的Position
     */
    private int dropPosition;
    /**
     * 开始拖动的ITEM的Position
     */
    private int startPosition;
    /**
     * item高
     */
    private int itemHeight;
    /**
     * item宽
     */
    private int itemWidth;
    /**
     * 拖动的时候对应ITEM的VIEW
     */
    private View dragImageView = null;
    /**
     * 长按的时候ITEM的VIEW
     */
    private View dragItemView = null;
    /**
     * WindowManager管理器
     */
    private WindowManager windowManager = null;
    /** */
    private WindowManager.LayoutParams windowParams = null;
    /**
     * item总量
     */
    private int itemTotalCount;
    /**
     * 一行的ITEM数量
     */
    private int nColumns = 4;
    /**
     * 行数
     */
    private int nRows;
    /**
     * 剩余部分
     */
    private int Remainder;
    /**
     * 是否在移动
     */
    private boolean isMoving = false;
    /** */
    private int holdPosition;
    /**
     * 拖动的时候放大的倍数
     */
    private double dragScale = 1.6D;
    /**
     * 震动器
     */
    private Vibrator mVibrator;
    /**
     * 每个ITEM之间的水平间距
     */
    private int mHorizontalSpacing = 15;
    /**
     * 每个ITEM之间的竖直间距
     */
    private int mVerticalSpacing = 15;
    /* 移动时候最后个动画的ID */
    private String LastAnimationID;
    private LinearLayout ll_channel_moving;
    private ImageView iv_channel_moving;
    /**
     * 判断点击还是移动
     */
    private boolean isDrag;
    /**
     * 滑动的最小距离
     */
    private int mTouchSlop;
    /**
     * item点击事件
     */
    private OnDragItemClickListener onDragItemClickListener;


    public ChannelDragGrid(Context context) {
        super(context);
        init(context);
    }

    public ChannelDragGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ChannelDragGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //将布局文件中设置的间距dip转为px
        mHorizontalSpacing = ComUtil.dpToPx(mHorizontalSpacing);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) ev.getX();
            downY = (int) ev.getY();
            windowX = (int) ev.getX();
            windowY = (int) ev.getY();
        }
        return onTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        boolean bool = true;

        // 移动时候的对应x,y位置
        bool = super.onTouchEvent(ev);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                windowX = (int) ev.getX();
                downY = (int) ev.getY();
                windowY = (int) ev.getY();
                isDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFirstMove && (Math.abs(downX - x) > mTouchSlop || Math.abs(downY - y) > mTouchSlop)) {//防止重复
                    setItemClickListener(ev);
                    isDrag = true;
                }

                if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
                    isDrag = true;
                    onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
                    if (!isMoving) {
                        OnMove(x, y);
                    }
                    if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isDrag) {
                    break;
                } else {
                    if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
                        stopDrag();
                        onDrop(x, y);
                        isFirstMove = true;
                        ((ChannelDragAdapter) getAdapter()).initHideViewPosition();
                        requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                    return true;
                }
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 在拖动的情况
     */
    private void onDrag(int x, int y, int rawx, int rawy) {
        if (dragImageView != null) {
//            windowParams.alpha = 0.6f;
//			windowParams.x = x - win_view_x + viewX;
//			windowParams.y = y +  win_view_y + viewY;
//			windowParams.x = rawx - itemWidth / 2;
//			windowParams.y = rawy - itemHeight / 2;
            windowParams.x = rawx - win_view_x;
            windowParams.y = rawy - win_view_y;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    /**
     * 在松手下放的情况
     */
    private void onDrop(int x, int y) {
        // 根据拖动到的x,y坐标获取拖动位置下方的ITEM对应的POSTION
        int tempPostion = pointToPosition(x, y);
//		if (tempPostion != AdapterView.INVALID_POSITION) {
        dropPosition = tempPostion;
        ChannelDragAdapter mChannelDragAdapter = (ChannelDragAdapter) getAdapter();
        //显示刚拖动的ITEM
        mChannelDragAdapter.setShowDropItem(true);
        //刷新适配器，让对应的ITEM显示
        mChannelDragAdapter.notifyDataSetChanged();
//		}
    }

    //判断点击位置是否在view上
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    private boolean isFirstMove = true;

    //点击拖动
    private void setItemClickListener(final MotionEvent ev) {
        {
            int x = (int) ev.getX();// 点击事件的X位置
            int y = (int) ev.getY();// 点击事件的y位置

            startPosition = pointToPosition(x, y);// 第一次点击的postion
            dragPosition = startPosition;
            if (dragPosition == 0 || dragPosition == AdapterView.INVALID_POSITION) {
                return;
            }
//            if (getAdapter().getCount() <= MIN_CHANNEL_COUT) {//小于频道数最小值，不容许操作
//                toastMinChannelCount();
//                return;
//            }

            ((ChannelDragAdapter) getAdapter()).showDelView();
            ((ChannelDragAdapter) getAdapter()).hideMovingView(dragPosition - getFirstVisiblePosition());

            ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
            TextView dragTextView = (TextView) dragViewGroup.findViewById(R.id.tv_text_item);
            ImageView delImageVIew = (ImageView) dragViewGroup.findViewById(R.id.iv_icon_del);
            dragTextView.setSelected(true);
            dragTextView.setEnabled(false);


            itemHeight = dragViewGroup.getHeight();
            itemWidth = dragViewGroup.getWidth();
            itemTotalCount = ChannelDragGrid.this.getCount();
            int row = itemTotalCount / nColumns;// 算出行数
            Remainder = (itemTotalCount % nColumns);// 算出最后一行多余的数量
            if (Remainder != 0) {
                nRows = row + 1;
            } else {
                nRows = row;
            }
            // 如果特殊的这个不等于拖动的那个,并且不等于-1
            if (dragPosition != AdapterView.INVALID_POSITION) {
                // 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
                win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
                win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
                dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
                dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
                dragTextView.setBackgroundColor(Color.WHITE);
                dragItemView = dragTextView;
                dragTextView.destroyDrawingCache();
                dragTextView.setDrawingCacheEnabled(true);
                Bitmap dragBitmap = Bitmap.createBitmap(dragTextView.getDrawingCache());
                startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                hideDropItem();
                dragViewGroup.setVisibility(View.INVISIBLE);
                isMoving = false;
                isFirstMove = false;
                requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    /**
     * 长按点击监听
     *
     * @param ev
     */
    public void setOnItemClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                int x = (int) ev.getX();// 长安事件的X位置
                int y = (int) ev.getY();// 长安事件的y位置

                startPosition = position;// 第一次点击的postion
                dragPosition = position;

                if (getAdapter().getCount() < MIN_CHANNEL_COUT) {//小于频道数最小值，不容许操作
                    toastMinChannelCount();
                    return false;
                }

                ((ChannelDragAdapter) getAdapter()).showDelView();

                ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                TextView dragTextView = (TextView) dragViewGroup.findViewById(R.id.tv_text_item);
                dragTextView.setSelected(true);
                dragTextView.setEnabled(false);
                itemHeight = dragViewGroup.getHeight();
                itemWidth = dragViewGroup.getWidth();
                itemTotalCount = ChannelDragGrid.this.getCount();
                int row = itemTotalCount / nColumns;// 算出行数
                Remainder = (itemTotalCount % nColumns);// 算出最后一行多余的数量
                if (Remainder != 0) {
                    nRows = row + 1;
                } else {
                    nRows = row;
                }
                // 如果特殊的这个不等于拖动的那个,并且不等于-1
                if (dragPosition != AdapterView.INVALID_POSITION) {
                    // 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
                    win_view_x = windowX - dragViewGroup.getLeft();//VIEW相对自己的X，半斤
                    win_view_y = windowY - dragViewGroup.getTop();//VIEW相对自己的y，半斤
                    dragOffsetX = (int) (ev.getRawX() - x);//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
                    dragOffsetY = (int) (ev.getRawY() - y);//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
                    dragItemView = dragViewGroup;
                    dragViewGroup.destroyDrawingCache();
                    dragViewGroup.setDrawingCacheEnabled(true);
                    Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
                    mVibrator.vibrate(50);//设置震动时间
                    startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                    hideDropItem();
                    dragViewGroup.setVisibility(View.INVISIBLE);
                    isMoving = false;
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                return false;
            }
        });
    }

    public void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();// 获取WINDOW界面的
        //Gravity.TOP|Gravity.LEFT;这个必须加
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        // 计算当前项Left离窗体的距离
//		windowParams.x = x - (int)((itemWidth / 2) * dragScale);
//		windowParams.y = y - (int) ((itemHeight / 2) * dragScale);
        //得到preview左上角相对于屏幕的坐标
        windowParams.x = x - win_view_x;
        windowParams.y = y - win_view_y;
//		this.windowParams.x = (x - this.win_view_x + this.viewX);//位置的x值
//		this.windowParams.y = (y - this.win_view_y + this.viewY);//位置的y值
        //设置拖拽item的宽和高
        windowParams.width = (int) (dragScale * dragBitmap.getWidth());// 放大dragScale倍，可以设置拖动后的倍数
        windowParams.height = (int) (dragScale * dragBitmap.getHeight());// 放大dragScale倍，可以设置拖动后的倍数
//        windowParams.width = ComUtil.dpToPx(100);
//        windowParams.height = ComUtil.dpToPx(50);
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.windowParams.format = PixelFormat.TRANSLUCENT;
        this.windowParams.windowAnimations = 0;
        if (ll_channel_moving == null) {
            ll_channel_moving = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_channel_moving, null);
            iv_channel_moving = (ImageView) ll_channel_moving.findViewById(R.id.iv_channel_moving);
        }
        iv_channel_moving.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
        windowManager.addView(ll_channel_moving, windowParams);
        dragImageView = ll_channel_moving;
    }

    /**
     * 停止拖动 ，释放并初始化
     */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    /**
     * 在ScrollView内，所以要进行计算高度
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 隐藏 放下 的ITEM
     */
    private void hideDropItem() {
        ((ChannelDragAdapter) getAdapter()).setShowDropItem(false);
    }

    /**
     * 获取移动动画
     */
    public Animation getMoveAnimation(float toXValue, float toYValue) {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toXValue,
                Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toYValue);// 当前位置移动到指定位置
        mTranslateAnimation.setFillAfter(true);// 设置一个动画效果执行完毕后，View对象保留在终止的位置。
        mTranslateAnimation.setDuration(300L);
        return mTranslateAnimation;
    }

    /**
     * 移动的时候触发
     */
    public void OnMove(int x, int y) {
        // 拖动的VIEW下方的POSTION
        int dPosition = pointToPosition(x, y);
        // 判断下方的POSTION是否是最开始2个不能拖动的
        if (getAdapter().getCount() > 1) {
            if ((dPosition == -1) || (dPosition == dragPosition)) {
                return;
            }
            dropPosition = dPosition;
            if (dragPosition != startPosition) {
                dragPosition = startPosition;
            }
            int movecount;
            //拖动的=开始拖的，并且 拖动的 不等于放下的
            if (dropPosition != 0 && ((dragPosition == startPosition) || (dragPosition != dropPosition))) {
                //移需要移动的动ITEM数量
                movecount = dropPosition - dragPosition;
            } else {
                //移需要移动的动ITEM数量为0
                movecount = 0;
            }
            if (movecount == 0) {
                return;
            }

            int movecount_abs = Math.abs(movecount);

            if (dPosition != dragPosition) {
                //dragGroup设置为不可见
                ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
                dragGroup.setVisibility(View.INVISIBLE);

                float to_x = 1;// 当前下方positon
                float to_y;// 当前下方右边positon
                //x_vlaue移动的距离百分比（相对于自己长度的百分比）
                float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
                //y_vlaue移动的距离百分比（相对于自己宽度的百分比）
                float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
                Log.d("x_vlaue", "x_vlaue = " + x_vlaue);
                for (int i = 0; i < movecount_abs; i++) {
                    to_x = x_vlaue;
                    to_y = y_vlaue;
                    //像左
                    if (movecount > 0) {
                        // 判断是不是同一行的
                        holdPosition = dragPosition + i + 1;
                        if (dragPosition / nColumns == holdPosition / nColumns) {
                            to_x = -x_vlaue;
                            to_y = 0;
                        } else if (holdPosition % 4 == 0) {
                            to_x = 3 * x_vlaue;
                            to_y = -y_vlaue;
                        } else {
                            to_x = -x_vlaue;
                            to_y = 0;
                        }
                    } else {
                        //向右,下移到上，右移到左
                        holdPosition = dragPosition - i - 1;
                        if (dragPosition / nColumns == holdPosition / nColumns) {
                            to_x = x_vlaue;
                            to_y = 0;
                        } else if ((holdPosition + 1) % 4 == 0) {
                            to_x = -3 * x_vlaue;
                            to_y = y_vlaue;
                        } else {
                            to_x = x_vlaue;
                            to_y = 0;
                        }
                    }
                    ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);
                    Animation moveAnimation = getMoveAnimation(to_x, to_y);
                    moveViewGroup.startAnimation(moveAnimation);
                    //如果是最后一个移动的，那么设置他的最后个动画ID为LastAnimationID
                    if (holdPosition == dropPosition) {
                        LastAnimationID = moveAnimation.toString();
                    }
                    moveAnimation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub
                            isMoving = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // TODO Auto-generated method stub
                            // 如果为最后个动画结束，那执行下面的方法
                            if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
                                ChannelDragAdapter mChannelDragAdapter = (ChannelDragAdapter) getAdapter();
                                mChannelDragAdapter.exchange(startPosition, dropPosition);
                                startPosition = dropPosition;
                                dragPosition = dropPosition;
                                isMoving = false;
                            }
                        }
                    });
                }
            }
        }
    }


    //当频道数少于3时提示
    private void toastMinChannelCount() {
        ToastUtils.showMsg(getContext(), getResources().getString(R.string.news_channel_min_channel_count));
    }

    /**
     * 拖动时拦截item点击事件
     */
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!isDrag && onDragItemClickListener != null) {
                onDragItemClickListener.onItemClick(adapterView, view, i, l);
            }
        }
    };

    public void setOnDragItemClickListener(OnDragItemClickListener onDragItemClickListener) {
        this.onDragItemClickListener = onDragItemClickListener;
    }

    public interface OnDragItemClickListener {
        void onItemClick(AdapterView<?> adapterView, View view, int i, long l);
    }
}