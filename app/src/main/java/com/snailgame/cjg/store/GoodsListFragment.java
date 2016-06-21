package com.snailgame.cjg.store;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.AbsBaseFragment;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.event.MemberChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.member.MemberCenterActivity;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.adapter.GoodsListAdapter;
import com.snailgame.cjg.store.model.GoodsInfo;
import com.snailgame.cjg.store.model.GoodsListModel;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 商品列表
 * Created by TAJ_C on 2015/11/20.
 */
public class GoodsListFragment extends AbsBaseFragment implements LoadMoreListView.OnLoadMoreListener, AdapterView.OnItemClickListener {


    private static final String TAG = GoodsListFragment.class.getSimpleName();
    @Bind(R.id.content)
    LoadMoreListView mGoodsListView;

    @Bind(R.id.iv_show_style)
    ImageView mShowStyleView;

    @Bind(R.id.tv_sort_price)
    TextView sortPriceView;

    @Bind(R.id.tv_unlogin_hint)
    TextView hintView;
    @Bind(R.id.tv_default_sort)
    TextView defaultSortView;
    @Bind(R.id.unlogin_hint_container)
    View unLoginHintContainer;

    private List<GoodsInfo> goodsList;

    private GoodsListAdapter adapter;

    private boolean isShowSmallStyle = false;
    private View mHeaderView;

    private String goodsId;

    //是否按默认排序  key=0时，排序为 默认排序，key=3时，按价格排序
    private boolean isShowOrderByDefault = true;
    //价格排序 0表示价格升序，1表示价格降序
    private int order = 1;
    private int curPage = 1;

    private static final int PAGE_COUNT = 10;
    private static final String STYLE_SHOW_BIG = "home_body_list1col";
    private static final String STYLE_SHOW_SMALL = "home_body_list2col";

    public static final String TYPE_GOODS_SPECIAL = "-1";
    public static final String TYPE_GOODS_DISCOUNT = "-2";

    public static Fragment getInstance(String gcId) {
        GoodsListFragment fragment = new GoodsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.GOODS_LIST_ID, gcId);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Subscribe
    public void onMemberChange(MemberChangeEvent event) {
        setHintView();
        resetAndReloadData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoadinUserVisibile = false;
        MainThreadBus.getInstance().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_list;
    }

    @Override
    protected LoadMoreListView getListView() {
        return mGoodsListView;
    }

    @Override
    protected void handleIntent() {
        goodsId = getArguments().getString(AppConstants.GOODS_LIST_ID);
    }

    @Override
    protected void initView() {
        mHeaderView = new FrameLayout(getActivity());
        mHeaderView.setLayoutParams(new AbsListView.LayoutParams(1, isShowSmallStyle ? ComUtil.dpToPx(8) : 0));
        mGoodsListView.addHeaderView(mHeaderView);

        adapter = new GoodsListAdapter(getActivity(), isShowSmallStyle, goodsList);
        mGoodsListView.setAdapter(adapter);
        mGoodsListView.enableLoadingMore(true);
        mGoodsListView.setLoadingListener(this);
        mGoodsListView.setOnItemClickListener(this);

        setHintView();
        showLoading();
    }

    private void setHintView() {
        if (false == IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            hintView.setText(R.string.member_unlogin_hint);
            unLoginHintContainer.setVisibility(View.VISIBLE);
        } else if (false == AccountUtil.isMember()) {
            hintView.setText(R.string.member_unmember_hint);
            unLoginHintContainer.setVisibility(View.VISIBLE);
        } else {
            unLoginHintContainer.setVisibility(View.GONE);
        }
    }


    @Override
    protected void loadData() {

        String url = JsonUrl.getJsonUrl().JSON_URL_STORE_GOODS_LIST +
                "&gc_id=" + goodsId +
                "&key=" + (isShowOrderByDefault ? "0" : "3") +  //key=0时，排序为 默认排序，key=3时，按价格排序
                "&order=" + order +
                "&curPage=" + curPage +
                "&page=" + PAGE_COUNT +
                "&list_type=" + (isShowSmallStyle ? STYLE_SHOW_SMALL : STYLE_SHOW_BIG);

        if (goodsId.equals(TYPE_GOODS_SPECIAL)) {
            url += "&special=1";
        } else if (goodsId.equals(TYPE_GOODS_DISCOUNT)) {
            url += "&is_discount=1";
        }

        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            url += "&nUserId=" + IdentityHelper.getUid(FreeStoreApp.getContext()) + "&cIdentity=" + IdentityHelper.getIdentity(FreeStoreApp.getContext());
        }

        FSRequestHelper.newGetRequest(url, TAG, GoodsListModel.class, new IFDResponse<GoodsListModel>() {
            @Override
            public void onSuccess(GoodsListModel result) {
                resetRefreshUi();
                if (result != null && result.getCode() == 200) {
                    if (result.getItem() != null && !ListUtils.isEmpty(result.getItem().getGoodsList())) {
                        if (goodsList == null) {
                            goodsList = new ArrayList<GoodsInfo>();
                        }
                        goodsList.addAll(result.getItem().getGoodsList());

                        adapter.refreshData(goodsList);
                        curPage++;


                        if (curPage > result.getPageTotal()) {
                            noMoreData();
                        }
                    }
                }

                if (ListUtils.isEmpty(goodsList)) {
                    showEmpty();
                }
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, false);
    }

    @Override
    protected void restoreData(Bundle savedInstanceState) {

    }

    @Override
    protected void saveData(Bundle outState) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        MainThreadBus.getInstance().unregister(this);
    }

    @OnClick(R.id.tv_default_sort)
    public void orderByDefault() {
        if (isShowOrderByDefault == false) {
            isShowOrderByDefault = true;
            resetAndReloadData();
        }
        defaultSortView.setTextColor(ResUtil.getColor(R.color.red));
        sortPriceView.setTextColor(ResUtil.getColor(R.color.general_text_color));
        sortPriceView.setCompoundDrawables(null, null, getDrawableLeft(R.drawable.ic_goods_order_default), null);
    }


    @OnClick(R.id.tv_sort_price)
    public void orderByPrice() {
        isShowOrderByDefault = false;
        defaultSortView.setTextColor(ResUtil.getColor(R.color.general_text_color));
        sortPriceView.setTextColor(ResUtil.getColor(R.color.red));
        order = (order == 0 ? 1 : 0);
        sortPriceView.setCompoundDrawables(null, null,
                getDrawableLeft(order == 0 ? R.drawable.ic_goods_order_up : R.drawable.ic_goods_oreder_down), null);
        resetAndReloadData();
    }


    @OnClick(R.id.iv_show_style)
    public void changeShowStyle() {
        isShowSmallStyle = !isShowSmallStyle;

        mShowStyleView.setImageDrawable(getResources().
                getDrawable(isShowSmallStyle ? R.drawable.ic_goods_show_small : R.drawable.ic_goods_show_large));
        mGoodsListView.setDividerHeight(isShowSmallStyle ? 0 : ComUtil.dpToPx(8));
        mHeaderView.setLayoutParams(new AbsListView.LayoutParams(1, isShowSmallStyle ? ComUtil.dpToPx(8) : 0));
        adapter = new GoodsListAdapter(getActivity(), isShowSmallStyle, goodsList);
        mGoodsListView.setAdapter(adapter);
    }

    private Drawable getDrawableLeft(int resourceId) {
        Drawable drawable = getResources().getDrawable(resourceId);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
    }


    /**
     * 重置并加载数据
     */
    private void resetAndReloadData() {
        curPage = 1;
        if (goodsList != null) {
            goodsList.clear();
        }
        showLoading();
        loadData();
    }


    @Override
    public void onLoadMore() {
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoodsInfo item = (GoodsInfo) mGoodsListView.getItemAtPosition(position);
        if (item != null) {
            startActivity(WebViewActivity.newIntent(getActivity(), item.getGoodsUrl()));
        }
    }

    @OnClick(R.id.iv_unlogin_hint_close)
    public void hidUnloginHintView() {
        unLoginHintContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_unlogin_hint)
    public void loginIn() {
        if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
            startActivity(MemberCenterActivity.newIntent(getActivity()));
        } else {
            AccountUtil.userLogin(getActivity());
        }
    }
}
