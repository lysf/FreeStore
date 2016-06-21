package com.snailgame.cjg.store;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.AutoScrollView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.StoreChildGridApdapter;
import com.snailgame.cjg.store.adapter.StoreFragmentBannerAdapter;
import com.snailgame.cjg.store.adapter.StoreFragmentRechargeAdapter;
import com.snailgame.cjg.store.model.StoreChildContentModel;
import com.snailgame.cjg.store.model.StoreChildModel;
import com.snailgame.cjg.store.model.StoreModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.commonsware.cwac.merge.MergeAdapter;

/**
 * Created by lic on 2015/11/4.
 * <p/>
 * 商店界面
 */
public class StoreFragment extends AbsBaseFragment {
    static String TAG = StoreFragment.class.getName();
    private static final String KEY_JSON = "key_json";
    private static final String KEY_LISTVIEW_POSITION = "scroll_position";
    @Bind(R.id.store_list)
    LoadMoreListView loadMoreListView;
    @Bind(R.id.tv_stop)
    TextView stopTextView;
    @Bind(R.id.stop_container)
    RelativeLayout stopContainer;
    private MergeAdapter mergeAdapter;
    private AutoScrollView autoScrollView;
    private String resultJson;
    private int listviewPosition;
    private boolean isResotreData;

    private static final String TYPE_GOODS = "2";//商品分类
    private static final String TYPE_VIRTUAL_RECHARGE = "3";//虚拟代充
    private static final String TYPE_POINT_SHOPS = "4";//积分商城
    private static final String TYPE_COOL_PLAY = "6";//酷玩专购
    private static final String TYPE_DISCOUNT = "7";//优惠折扣

    /**
     * 重复点击tab返回顶部
     * @param event
     */
    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_COOL_PLAY&& loadMoreListView != null) {
            loadMoreListView.setSelection(0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mergeAdapter = new MergeAdapter();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_store;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        onViewCreated();
    }

    private void onViewCreated() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) loadMoreListView.getLayoutParams();
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight(), 0, 0);
        loadMoreListView.setLayoutParams(layoutParams);
        loadMoreListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        loadMoreListView.enableLoadingMore(true);

    }

    @Override
    protected void loadData() {
        boolean isOpen = PersistentVar.getInstance().getSystemConfig().isCoolPlayLock();
        if (!isOpen) {
            stopContainer.setVisibility(View.VISIBLE);
            stopTextView.setText(getResources().getString(R.string.function_not_work));
        } else {
            loadStoreJson(JsonUrl.getJsonUrl().JSON_URL_STORE);
        }
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {
        showLoading();
        resultJson = savedInstanceState.getString(KEY_JSON);
        listviewPosition = savedInstanceState.getInt(KEY_LISTVIEW_POSITION);
        if (resultJson != null) {
            restoreInBackground();
        }

    }

    private void restoreInBackground() {
        isResotreData = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleResult(resultJson);
            }
        }).start();
    }

    @Override
    protected void saveData(Bundle outState) {
        if (resultJson != null) {
            outState.putBoolean(KEY_SAVE, true);
            outState.putString(KEY_JSON, resultJson);
            outState.putInt(KEY_LISTVIEW_POSITION, loadMoreListView.getFirstVisiblePosition());

        }
    }

    // 获取商城数据
    private void loadStoreJson(String url) {
        showBindLoading();
        FSRequestHelper.newGetRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null && !TextUtils.isEmpty(result)) {
                    resultJson = result;
                    handleResult(resultJson);
                } else {
                    showBindEmpty();
                }
            }

            @Override
            public void onNetWorkError() {
                showBindError();
            }

            @Override
            public void onServerError() {
                showBindError();
            }
        }, true);
    }

    /**
     * 数据获取中
     */
    private void showBindLoading() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoading();
            }
        });
    }

    /**
     * 数据获取失败
     */
    private void showBindError() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showError();
            }
        });
    }

    /**
     * 数据获取为空
     */
    private void showBindEmpty() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showEmpty();
            }
        });
    }

    /**
     * 解析返回数据
     *
     * @param resultStr resultStr
     */
    private void handleResult(String resultStr) {
        StoreModel storeModel = ExtendJsonUtil.parseHomeJsonToModel(resultStr, StoreModel.class);
        if (storeModel != null && storeModel.getList() != null) {
            List<StoreChildModel> moduleModelList = storeModel.getList();
            showUI(moduleModelList);
        }
    }


    /**
     * 展现界面
     */
    private void showUI(final List<StoreChildModel> moduleModelList) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String templateId;
                for (StoreChildModel storeChildModel : moduleModelList) {
                    if (storeChildModel != null && storeChildModel.getChilds() != null && storeChildModel.getcTemplateId() != null) {
                        templateId = storeChildModel.getcTemplateId();
                        //类型:1-广告位;2-快速入口；3-弹窗;4-内容栏 5-快速入口代充值
                        switch (templateId) {
                            case StoreChildModel.TYPE_BANNER:
                                showBannerHeader(storeChildModel.getChilds());
                                break;
                            case StoreChildModel.TYPE_QUICK_ENTER:
                                showQuickReturnView(storeChildModel.getChilds());
                                break;
                            case StoreChildModel.TYPE_SECKILL:
                                showSeckillViewshowSeckillView(storeChildModel);
                                break;
                            case StoreChildModel.TYPE_ONE_BANNER:
                                showOneAdView(storeChildModel);
                                break;
                            case StoreChildModel.TYPE_MORE_AD:
                                if (storeChildModel.getcHeadlineShow().equals(StoreChildModel.TYPE_SHOW_MODEL_TITLE)) {
                                    showMoreAdView(storeChildModel);
                                } else {
                                    showPhonePartsView(storeChildModel);
                                }
                                break;
                            case StoreChildModel.TYPE_RECHARGE:
                                showRechargeView(storeChildModel);
                                break;
                            case StoreChildModel.TYPE_PHONE_COMM:
                                showPhoneCommunicationView(storeChildModel);
                                break;
                            case StoreChildModel.TYPE_COOL_PLAY:
                                initSpecBuyView(storeChildModel);
                                break;
                            case StoreChildModel.TYPE_DISCOUNT:
                                initPanicBuyView(storeChildModel);
                                break;
                        }
                    }
                }
                if (getActivity() != null) {
                    loadMoreListView.onNoMoreData();
                    loadMoreListView.setAdapter(mergeAdapter);
                }

                if (isResotreData) {
                    isResotreData = false;
                    loadMoreListView.setSelection(listviewPosition);
                }

            }
        });
    }

    /**
     * 模板与模板之间带阴影的分割线
     *
     * @return
     */
    private View getTemplateDivider() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_modul_divider, loadMoreListView, false);
        return view;
    }


    /**
     * 首页幻灯片
     */
    private void showBannerHeader(List<StoreChildContentModel> lists) {
        if (getActivity() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_store_banner, loadMoreListView, false);
            autoScrollView = ButterKnife.findById(view, R.id.autoScrollView);
            autoScrollView.startAutoScroll();
            StoreFragmentBannerAdapter storeFragmentBannerAdapter = new StoreFragmentBannerAdapter(getActivity(), lists, mRoute);
            autoScrollView.setAdapter(storeFragmentBannerAdapter);
            loadMoreListView.addHeaderView(view);
        }
    }

    /**
     * 快速返回View
     *
     * @param lists lists
     */
    private void showQuickReturnView(final List<StoreChildContentModel> lists) {
        if (getActivity() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_store_quick_view, loadMoreListView, false);
            FullGridView fullGridView = ButterKnife.findById(view, R.id.grid_view);
            fullGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StoreChildContentModel item = lists.get(position);
                    if (item != null && item.getcSource() != null) {
                        switch (item.getcSource()) {
                            case TYPE_GOODS:
                                try {
                                    JSONObject jsonObject = new JSONObject(item.getsExtend());
                                    String gcId = jsonObject.optString("p1");
                                    getActivity().startActivity(GoodsListActivity.newIntent(getActivity(), item.getsTitle(), gcId));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TYPE_VIRTUAL_RECHARGE:
                                getActivity().startActivity(VirRechargeActivity.newIntent(getActivity()));
                                break;
                            case TYPE_POINT_SHOPS:
                                getActivity().startActivity(PointStoreActivity.newIntent(getActivity()));
                                break;
                            default:
                                break;
                        }
                    }

                }
            });
            fullGridView.setAdapter(new StoreChildGridApdapter(getActivity(), lists));
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 秒杀模块
     *
     * @param storeChildModel
     */
    private void showSeckillViewshowSeckillView(StoreChildModel storeChildModel) {
        final ArrayList<StoreChildContentModel> lists = storeChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.general_purpose_store_three_ad_view, loadMoreListView, false);
            setAdImage(view, lists, false);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 广告模块(单张banner)
     *
     * @param storeChildModel
     */
    private void showOneAdView(StoreChildModel storeChildModel) {
        final ArrayList<StoreChildContentModel> lists = storeChildModel.getChilds();
        if (getActivity() != null && !ListUtils.isEmpty(lists)) {
            FSSimpleImageView imageView = new FSSimpleImageView(getActivity());
            imageView.setControllerOverlayAndPlaceHolder();
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ComUtil.dpToPx(70));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(params);
            imageView.setImageUrlAndReUse(lists.get(0).getsImageUrl());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(WebViewActivity.newIntent(getActivity(), lists.get(0).getsJumpUrl()));
                }
            });
            mergeAdapter.addView(imageView);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 广告模块（多张banner）
     *
     * @param storeChildModel
     */
    private void showMoreAdView(StoreChildModel storeChildModel) {
        final ArrayList<StoreChildContentModel> lists = storeChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.general_purpose_store_four_ad_view, loadMoreListView, false);
            setAdImage(view, lists, true);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }


    /**
     * 手机通讯
     *
     * @param storeChildModel
     */
    private void showPhoneCommunicationView(StoreChildModel storeChildModel) {
        ArrayList<StoreChildContentModel> lists = storeChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_store_phone_comm_view, loadMoreListView, false);
            setModelTitleView(view, storeChildModel);
            setAdImage(view, lists, true);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 手机配件
     *
     * @param storeChildModel
     */
    private void showPhonePartsView(StoreChildModel storeChildModel) {
        ArrayList<StoreChildContentModel> lists = storeChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_store_phone_parts_view, loadMoreListView, false);
            setModelTitleView(view, storeChildModel);
            setAdImage(view, lists, false);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 钜惠代充
     *
     * @param storeChildModel
     */
    private void showRechargeView(StoreChildModel storeChildModel) {
        if (getActivity() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_store_recharge_view, loadMoreListView, false);
            setModelTitleView(view, storeChildModel);
            AutoScrollView autoScrollView = ButterKnife.findById(view, R.id.autoScrollView);
            autoScrollView.setIsAutoScroll(false);
            StoreFragmentRechargeAdapter storeFragmentRechargerAdapter = new StoreFragmentRechargeAdapter(getActivity(), storeChildModel.getChilds(), mRoute);
            autoScrollView.setAdapter(storeFragmentRechargerAdapter);
            if (storeChildModel.getChilds().size() < 4) {
                autoScrollView.hideCircleIndicator();
            }
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 酷玩专购
     *
     * @param storeChildModel
     */
    private void initSpecBuyView(StoreChildModel storeChildModel) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.general_purpose_spec_buy_view, loadMoreListView, false);
        setModelTitleView(view, storeChildModel);
        setAdImage(view, storeChildModel.getChilds(), true);
        mergeAdapter.addView(view);
        mergeAdapter.addView(getTemplateDivider());
    }

    /**
     * 会员抢购
     *
     * @param storeChildModel
     */
    private void initPanicBuyView(StoreChildModel storeChildModel) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.general_purpose_panic_buy_view, loadMoreListView, false);
        setModelTitleView(view, storeChildModel);
        setAdImage(view, storeChildModel.getChilds(), false);
        mergeAdapter.addView(view);
        mergeAdapter.addView(getTemplateDivider());
    }


    /**
     * 设置模块标题
     *
     * @param view
     * @param storeChildModel
     */
    private void setModelTitleView(View view, @NonNull final StoreChildModel storeChildModel) {
        TextView titleTextView = ButterKnife.findById(view, R.id.title);
        View titleView = ButterKnife.findById(view, R.id.title_view);
        TextView seeMoreView = ButterKnife.findById(view, R.id.see_more);
        FSSimpleImageView FSSimpleImageView = ButterKnife.findById(view, R.id.image_arrow);
        if (storeChildModel.getsTitle() != null)
            titleTextView.setText(Html.fromHtml(storeChildModel.getsTitle()));
        if (storeChildModel.getsPinText() != null)
            seeMoreView.setText(storeChildModel.getsPinText());
        if (storeChildModel.getsPinIcon() != null)
            FSSimpleImageView.setImageUrlAndReUse(storeChildModel.getsPinIcon());
        if (storeChildModel.getcSource() != null && !TextUtils.isEmpty(storeChildModel.getcSource())) {
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (storeChildModel.getcSource()) {
                        case TYPE_GOODS:
                            try {
                                JSONObject jsonObject = new JSONObject(storeChildModel.getsExtend());
                                String gcId = jsonObject.optString("p1");
                                getActivity().startActivity(GoodsListActivity.newIntent(getActivity(), Html.fromHtml(storeChildModel.getsTitle()).toString(), gcId));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case TYPE_VIRTUAL_RECHARGE:
                            getActivity().startActivity(VirRechargeActivity.newIntent(getActivity()));
                            break;
                        case TYPE_POINT_SHOPS:
                            getActivity().startActivity(PointStoreActivity.newIntent(getActivity()));
                        case TYPE_COOL_PLAY:
                            getActivity().startActivity(GoodsListActivity.newIntent(getActivity(),
                                    Html.fromHtml(storeChildModel.getsTitle()).toString(), GoodsListFragment.TYPE_GOODS_SPECIAL));
                        case TYPE_DISCOUNT:
                            getActivity().startActivity(GoodsListActivity.newIntent(getActivity(),
                                    Html.fromHtml(storeChildModel.getsTitle()).toString(), GoodsListFragment.TYPE_GOODS_DISCOUNT));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 设置广告位图片和点击事件
     *
     * @param view
     * @param lists         广告内容的list
     * @param isContainFour 是否包含第四个广告位
     */
    private void setAdImage(View view, final ArrayList<StoreChildContentModel> lists, boolean isContainFour) {
        FSSimpleImageView imageVertical = ButterKnife.findById(view, R.id.image_vertical);
        FSSimpleImageView imageHorizonal1 = ButterKnife.findById(view, R.id.image_horizonal1);
        FSSimpleImageView imageHorizonal2 = ButterKnife.findById(view, R.id.image_horizonal2);
        FSSimpleImageView imageHorizonal3 = null;
        if (isContainFour) {
            imageHorizonal3 = ButterKnife.findById(view, R.id.image_horizonal3);
        }
        for (int i = 0; i < lists.size(); i++) {
            if (i == 0) {
                setImageClick(imageVertical, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 1) {
                setImageClick(imageHorizonal1, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 2) {
                setImageClick(imageHorizonal2, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else {
                if (isContainFour) {
                    setImageClick(imageHorizonal3, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
                }
            }
        }
    }


    /**
     * 设置图片点击
     *
     * @param image   显示的imageView
     * @param url     图片显示url
     * @param jumpUrl 点击跳转url
     */
    private void setImageClick(FSSimpleImageView image, String url, final String jumpUrl) {
        image.setImageUrlAndReUse(url);
        if (jumpUrl != null && !TextUtils.isEmpty(jumpUrl)) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (jumpUrl != null && !TextUtils.isEmpty(jumpUrl))
                        startWebView(jumpUrl);
                }
            });
        }
    }


    /**
     * 跳转网页
     */

    private void startWebView(String url) {
        getActivity().startActivity(WebViewActivity.newIntent(getActivity(), url));
    }

    @Override
    protected LoadMoreListView getListView() {
        return loadMoreListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_HOME);
        startAutoScroll();
        MainThreadBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_HOME);
        stopAutoScroll();
        MainThreadBus.getInstance().unregister(this);
    }

    /**
     * banner开始自动滚动
     */
    private void startAutoScroll() {
        if (autoScrollView != null) {
            autoScrollView.startAutoScroll();
        }
    }

    /**
     * banner停止自动滚动
     */
    private void stopAutoScroll() {
        if (autoScrollView != null) {
            autoScrollView.stopAutoScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }

}
