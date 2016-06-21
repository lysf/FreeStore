package com.snailgame.cjg.communication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snailgame.cjg.MainActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.AutoScrollView;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.common.widget.FullGridView;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.communication.adapter.CommunicationChildGridApdapter;
import com.snailgame.cjg.communication.adapter.CommunicationFragmentBannerAdapter;
import com.snailgame.cjg.communication.model.CommunicationChildContentModel;
import com.snailgame.cjg.communication.model.CommunicationChildModel;
import com.snailgame.cjg.communication.model.CommunicationModel;
import com.snailgame.cjg.event.TabClickedEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.commonsware.cwac.merge.MergeAdapter;

/**
 * Created by lic on 2015/1/20.
 * <p>
 * 通讯标签界面
 */
public class CommunicationFragment extends AbsBaseFragment {
    static String TAG = CommunicationFragment.class.getName();
    private static final String KEY_JSON = "key_json";
    private static final String KEY_LISTVIEW_POSITION = "scroll_position";
    @Bind(R.id.communication_list)
    LoadMoreListView loadMoreListView;
    private MergeAdapter mergeAdapter;
    private AutoScrollView autoScrollView;
    private String resultJson;
    private int listviewPosition;
    private boolean isRestoreData;

    /**
     * 重复点击tab返回顶部
     * @param event
     */
    @Subscribe
    public void scrollTop(TabClickedEvent event) {
        if (event.getTabPosition() == MainActivity.TAB_COMMUNICATION&& loadMoreListView != null) {
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
        return R.layout.fragment_communication;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) loadMoreListView.getLayoutParams();
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height) + ComUtil.getStatesBarHeight(), 0, 0);
        loadMoreListView.setLayoutParams(layoutParams);
        loadMoreListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        loadMoreListView.enableLoadingMore(true);
    }

    @Override
    protected void loadData() {
        loadStoreJson(JsonUrl.getJsonUrl().JSON_URL_COMMUNICATION);
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
        isRestoreData = true;
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

    /**
     * 获取通讯数据
     */
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
                ArrayMap<String, String> map = new ArrayMap<String, String>();

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
        CommunicationModel communicationModel = ExtendJsonUtil.parseHomeJsonToModel(resultStr, CommunicationModel.class);
        if (communicationModel != null && communicationModel.getList() != null) {
            List<CommunicationChildModel> moduleModelList = communicationModel.getList();
            showUI(moduleModelList);
        } else {
            showBindEmpty();
        }
    }


    /**
     * 展现界面
     */
    private void showUI(final List<CommunicationChildModel> moduleModelList) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String templateId;
                                            for (CommunicationChildModel communicationChildModel : moduleModelList) {
                                                if (communicationChildModel != null && communicationChildModel.getChilds() != null && communicationChildModel.getcTemplateId() != null) {
                                                    templateId = communicationChildModel.getcTemplateId();
                                                    //类型:1-广告位;2-快速入口；3-三竖栏广告位;4-热门免卡 5-热门资费包
                                                    switch (templateId) {
                                                        case CommunicationChildModel.TYPE_BANNER:
                                                            showBannerHeader(communicationChildModel.getChilds());
                                                            break;
                                                        case CommunicationChildModel.TYPE_QUICK_ENTER:
                                                            showQuickReturnView(communicationChildModel.getChilds());
                                                            break;
                                                        case CommunicationChildModel.TYPE_THREE_VERTICAL_AD:
                                                            showThreeVerticalAdView(communicationChildModel);
                                                            break;
                                                        case CommunicationChildModel.TYPE_HOT_CARD_:
                                                            showHotFreeCardView(communicationChildModel);
                                                            break;
                                                        case CommunicationChildModel.TYPE_HOT_TARIFF_PACKAGE:
                                                            showHotTariffPackageView(communicationChildModel);
                                                            break;
                                                    }
                                                }
                                            }
                                            if (getActivity() != null) {
                                                loadMoreListView.onNoMoreData();
                                                loadMoreListView.setAdapter(mergeAdapter);
                                            }

                                            if (isRestoreData) {
                                                isRestoreData = false;
                                                loadMoreListView.setSelection(listviewPosition);
                                            }

                                        }
                                    }

        );
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
    private void showBannerHeader(List<CommunicationChildContentModel> lists) {
        if (getActivity() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_communication_banner, loadMoreListView, false);
            autoScrollView = ButterKnife.findById(view, R.id.autoScrollView);
            autoScrollView.startAutoScroll();
            CommunicationFragmentBannerAdapter communicationFragmentBannerAdapter =
                    new CommunicationFragmentBannerAdapter(getActivity(), lists, mRoute);
            autoScrollView.setAdapter(communicationFragmentBannerAdapter);
            loadMoreListView.addHeaderView(view);
        }
    }

    /**
     * 快速返回View
     *
     * @param lists lists
     */
    private void showQuickReturnView(final List<CommunicationChildContentModel> lists) {
        if (getActivity() != null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_communication_quick_view, loadMoreListView, false);
            FullGridView fullGridView = ButterKnife.findById(view, R.id.grid_view);
            fullGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CommunicationChildContentModel item = lists.get(position);
                    if (!TextUtils.isEmpty(item.getsJumpUrl())) {
                        startWebView(item.getsJumpUrl());
                    }
                }
            });
            fullGridView.setAdapter(new CommunicationChildGridApdapter(getActivity(), lists));
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 三竖栏广告位
     *
     * @param communicationChildModel
     */
    private void showThreeVerticalAdView(CommunicationChildModel communicationChildModel) {
        ArrayList<CommunicationChildContentModel> lists = communicationChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).
                    inflate(R.layout.fragment_communication_three_vertical_ad_view, loadMoreListView, false);
            setAdImage(view, lists, false);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 热门免卡
     *
     * @param communicationChildModel
     */
    private void showHotFreeCardView(CommunicationChildModel communicationChildModel) {
        ArrayList<CommunicationChildContentModel> lists = communicationChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).
                    inflate(R.layout.fragment_communication_hot_free_card_view, loadMoreListView, false);
            setModelTitleView(view, communicationChildModel);
            setAdImage(view, lists, false);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }

    /**
     * 热门资费包
     *
     * @param communicationChildModel
     */
    private void showHotTariffPackageView(CommunicationChildModel communicationChildModel) {
        ArrayList<CommunicationChildContentModel> lists = communicationChildModel.getChilds();
        if (getActivity() != null && lists != null) {
            View view = LayoutInflater.from(getActivity()).
                    inflate(R.layout.fragment_communication_hot_tariff_package_view, loadMoreListView, false);
            setModelTitleView(view, communicationChildModel);
            setAdImage(view, lists);
            mergeAdapter.addView(view);
            mergeAdapter.addView(getTemplateDivider());
        }
    }


    /**
     * 设置模块标题
     *
     * @param view
     * @param communicationChildModel
     */
    private void setModelTitleView(View view, @NonNull final CommunicationChildModel communicationChildModel) {
        TextView titleTextView = ButterKnife.findById(view, R.id.title);
        View titleView = ButterKnife.findById(view, R.id.title_view);
        TextView seeMoreView = ButterKnife.findById(view, R.id.see_more);
        FSSimpleImageView FSSimpleImageView = ButterKnife.findById(view, R.id.image_arrow);
        if (communicationChildModel.getsTitle() != null)
            titleTextView.setText(Html.fromHtml(communicationChildModel.getsTitle()));
        if (communicationChildModel.getsPinText() != null)
            seeMoreView.setText(communicationChildModel.getsPinText());
        if (communicationChildModel.getsPinIcon() != null)
            FSSimpleImageView.setImageUrlAndReUse(communicationChildModel.getsPinIcon());
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(communicationChildModel.getsPinUrl())) {
                    startWebView(communicationChildModel.getsPinUrl());
                }
            }
        });
    }

    /**
     * 设置广告位图片和点击事件
     *
     * @param view
     * @param lists         广告内容的list
     * @param isContainFour 是否包含第四个广告位
     */
    private void setAdImage(View view, final ArrayList<CommunicationChildContentModel> lists, boolean isContainFour) {
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
     * 设置热门资费的图片显示和点击事件
     *
     * @param view
     * @param lists 广告内容的list
     */
    private void setAdImage(View view, final ArrayList<CommunicationChildContentModel> lists) {
        FSSimpleImageView imageHorizonal1 = ButterKnife.findById(view, R.id.image_horizonal1);
        FSSimpleImageView imageHorizonal2 = ButterKnife.findById(view, R.id.image_horizonal2);
        FSSimpleImageView imageHorizonal3 = ButterKnife.findById(view, R.id.image_horizonal3);
        FSSimpleImageView imageHorizonal4 = ButterKnife.findById(view, R.id.image_horizonal4);
        FSSimpleImageView imageHorizonal5 = ButterKnife.findById(view, R.id.image_horizonal5);
        FSSimpleImageView imageHorizonal6 = ButterKnife.findById(view, R.id.image_horizonal6);

        for (int i = 0; i < lists.size(); i++) {
            if (i == 0) {
                setImageClick(imageHorizonal1, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 1) {
                setImageClick(imageHorizonal2, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 2) {
                setImageClick(imageHorizonal3, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 3) {
                setImageClick(imageHorizonal4, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 4) {
                setImageClick(imageHorizonal5, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
            } else if (i == 5) {
                setImageClick(imageHorizonal6, lists.get(i).getsImageUrl(), lists.get(i).getsJumpUrl());
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
