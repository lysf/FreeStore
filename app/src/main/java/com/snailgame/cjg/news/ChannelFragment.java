package com.snailgame.cjg.news;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.cjg.common.db.daoHelper.NewsChannelDaoHelper;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.news.adpter.ChannelDragAdapter;
import com.snailgame.cjg.news.adpter.ChannelOtherAdapter;
import com.snailgame.cjg.news.util.NewsChannelListSortUtil;
import com.snailgame.cjg.news.widget.ChannelDragGrid;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.FastDevFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by yanHH on 2016/4/21.
 */
public class ChannelFragment extends FastDevFragment implements AdapterView.OnItemClickListener,ChannelDragGrid.OnDragItemClickListener {
    /**
     * 最少频道个数
     */
    private static final int MIN_CHANNEL_COUNT = 3;
    @Bind(R.id.userGridView)
    ChannelDragGrid userGridView;
    @Bind(R.id.otherGridView)
    FullGridView otherGridView;

    ChannelDragAdapter userAdapter;
    ChannelOtherAdapter otherAdapter;
    List<NewsChannel> otherChannelList = new ArrayList<NewsChannel>();
    List<NewsChannel> userChannelList = new ArrayList<NewsChannel>();
    //是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
    boolean isMove = false;
//    private OnHideChannelListener hideChannelListener;
//    @OnClick(R.id.iv_channel_up)//隐藏频道栏目
//     void hideChannelFragment(){
//        if(hideChannelListener != null){
//            hideChannelListener.hideChannelListener();
//        }
//    }

    @Override
    protected void handleIntent() {
        List<NewsChannel> allChannelList = NewsChannelDaoHelper.getInstance(getActivity()).queryAllChannel();
        userChannelList.clear();
        otherChannelList.clear();
        if (allChannelList != null) {
            for (NewsChannel item : allChannelList) {
                if (item.isShow()) {
                    userChannelList.add(item);
                } else {
                    otherChannelList.add(item);
                }
            }
        }
    }

    @Override
    protected void initView() {
        userAdapter = new ChannelDragAdapter(getActivity(), NewsChannelListSortUtil.getShowChannelList(userChannelList));
        userGridView.setAdapter(userAdapter);
        otherAdapter = new ChannelOtherAdapter(getActivity(), NewsChannelListSortUtil.getOtherChannelList(otherChannelList));
        otherGridView.setAdapter(otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnDragItemClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if(position == 0) return;
                if (parent.getAdapter().getCount() <= MIN_CHANNEL_COUNT) {//频道个数小于3
                    ToastUtils.showMsg(getActivity(), getResources().getString(R.string.news_channel_min_channel_count));
                } else {
                    view.findViewById(R.id.iv_icon_del).setVisibility(View.GONE);

                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.tv_text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final NewsChannel channel = ((ChannelDragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        otherAdapter.setVisible(false);
                        //添加到最后一个
                        channel.setShow(false);
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.tv_text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final NewsChannel channel = ((ChannelOtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    channel.setShow(true);
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final NewsChannel moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof ChannelDragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(getActivity());
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(getActivity());
        iv.setImageBitmap(cache);
        return iv;
    }

    //更新频道
    public boolean isUpdateChannel() {
        ArrayList<NewsChannel> newsChannelList = new ArrayList<>();
        newsChannelList.addAll(userAdapter.getChannnelLst());
        newsChannelList.addAll(otherAdapter.getChannelLst());
        NewsChannelDaoHelper.getInstance(getActivity()).saveChannelToDb(newsChannelList);
        userChannelList = userAdapter.getChannnelLst();
        return userAdapter.isListChanged();
    }

    //重置监测频道变化的变量
    public void reSetChange() {
        if (userAdapter != null) {
            userAdapter.resetChanged();
        }
    }

    //资讯当前所在频道
    public void setCurrentChannelItem(String currentChannel) {
        userAdapter.setCurrentItem(currentChannel);
    }

//    //隐藏频道
//    public interface OnHideChannelListener{
//        void hideChannelListener();
//    }
//    public void setHideChannelListener(OnHideChannelListener hideChannelListener){
//        if(hideChannelListener != null){
//            this.hideChannelListener = hideChannelListener;
//        }
//    }


}
