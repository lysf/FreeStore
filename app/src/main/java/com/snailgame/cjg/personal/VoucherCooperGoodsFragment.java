package com.snailgame.cjg.personal;

import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.adapter.VoucherGamelistAdapter;
import com.snailgame.cjg.personal.model.MyVoucherGoodsCooperModel;
import com.snailgame.cjg.personal.model.MyVoucherGoodsModel;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人中心 -> 我的代金券 -> 商品代金券详情(合作游戏) fragment
 * Created by pancl on 2015/6/23.
 */
public class VoucherCooperGoodsFragment extends FixedSupportV4BugFragment implements VoucherCooperActivity.IHeaderSpace {

    static final String TAG = VoucherCooperGoodsFragment.class.getName();
    private MyVoucherGoodsModel.ModelItem item;
    private AbsListView.OnScrollListener onScrollListener;

    @Bind(R.id.gv_game_list)
    GridView gv_game_list;

    /**
     * @param item 商品代金券项
     * @return
     */
    public static VoucherCooperGoodsFragment create(MyVoucherGoodsModel.ModelItem item) {
        VoucherCooperGoodsFragment fragment = new VoucherCooperGoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.KEY_VOUCHER, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            item = bundle.getParcelable(AppConstants.KEY_VOUCHER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voucher_gamelist_fragment, null);
        ButterKnife.bind(this, view);

        gv_game_list.setOnScrollListener(onScrollListener);

        loadData();
        return view;
    }

    /**
     * 获取 商品代金券列表 数据
     */
    private void loadData() {
        if (item != null) {
            String url = JsonUrl.getJsonUrl().JSON_URL_KU_WAN_VOUCHER_COOPER + "&voucher_id=" + item.getiVoucherId();
            if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                url += "&nUserId=" + IdentityHelper.getUid(FreeStoreApp.getContext()) + "&cIdentity=" + IdentityHelper.getIdentity(FreeStoreApp.getContext());
            }
            FSRequestHelper.newGetRequest(url, TAG,
                    MyVoucherGoodsCooperModel.class, new IFDResponse<MyVoucherGoodsCooperModel>() {
                        @Override
                        public void onSuccess(MyVoucherGoodsCooperModel model) {
                            if (model == null || ListUtils.isEmpty(model.getItemList())) {
                                return;
                            }
                            VoucherGamelistAdapter voucherGamelistAdapter = new VoucherGamelistAdapter(getActivity(), model.getItemList());
                            gv_game_list.setAdapter(voucherGamelistAdapter);
                        }

                        @Override
                        public void onNetWorkError() {
                        }

                        @Override
                        public void onServerError() {
                        }
                    }, false);
        }
    }

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void setHeaderSpace(int headerHeight) {
        gv_game_list.setPadding(0, headerHeight, 0, 0);
        gv_game_list.setClipToPadding(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
    }
}
