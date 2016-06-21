package com.snailgame.cjg.member;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.member.model.MemberLayoutChildContentModel;
import com.snailgame.cjg.member.model.MemberLayoutChildModel;
import com.snailgame.cjg.member.model.MemberLayoutModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.store.GoodsListActivity;
import com.snailgame.cjg.store.GoodsListFragment;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 酷玩专购
 * Created by TAJ_C on 2015/12/24.
 */
public class MemberKuwanSpecialBuyFragment extends Fragment {
    private static final String TAG = MemberKuwanSpecialBuyFragment.class.getSimpleName();
    View contentView;


    public static Fragment getInstance() {
        MemberKuwanSpecialBuyFragment fragment = new MemberKuwanSpecialBuyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.general_purpose_spec_buy_view, container, false);
        loadMemberJson(JsonUrl.getJsonUrl().JSON_URL_MEMBER_KUWAN_BUY);
        return contentView;
    }

    /**
     * 获取会员信息和快速入口信息
     *
     * @param url
     */
    private void loadMemberJson(String url) {
        FSRequestHelper.newGetRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {

                if (!TextUtils.isEmpty(result)) {
                    MemberLayoutModel memberLayoutModel = ExtendJsonUtil.parseHomeJsonToModel(result, MemberLayoutModel.class);
                    if (memberLayoutModel != null && memberLayoutModel.getList() != null) {
                        List<MemberLayoutChildModel> moduleModelList = memberLayoutModel.getList();
                        for (MemberLayoutChildModel memberLayoutChildModel : moduleModelList) {
                            String templateId = memberLayoutChildModel.getcTemplateId();
                            if (memberLayoutChildModel.getChilds() != null) {
                                switch (templateId) {
                                    case MemberLayoutChildModel.TYPE_COOL_PLAY:
                                        setModelTitleView(memberLayoutChildModel);
                                        setAdImage(memberLayoutChildModel.getChilds(), true);
                                        break;
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, true);
    }


    /**
     * 设置模块标题
     *
     * @param memberLayoutChildModel
     */
    private void setModelTitleView(final MemberLayoutChildModel memberLayoutChildModel) {
        TextView titleTextView = ButterKnife.findById(contentView, R.id.title);
        View titleView = ButterKnife.findById(contentView, R.id.title_view);
        titleTextView.setText(Html.fromHtml(memberLayoutChildModel.getsTitle()));
        final String templateId = memberLayoutChildModel.getcTemplateId();
        if (memberLayoutChildModel.getsPinText() != null) {
            TextView seeMoreView = ButterKnife.findById(contentView, R.id.see_more);
            FSSimpleImageView FSSimpleImageView = ButterKnife.findById(contentView, R.id.image_arrow);
            seeMoreView.setText(memberLayoutChildModel.getsPinText());
            FSSimpleImageView.setImageUrlAndReUse(memberLayoutChildModel.getsPinIcon());
            if (memberLayoutChildModel.getsPinUrl() != null && !TextUtils.isEmpty(memberLayoutChildModel.getsPinUrl())) {
                titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (templateId.equals(MemberLayoutChildModel.TYPE_COOL_PLAY)) {
                            startActivity(GoodsListActivity.newIntent(getActivity(),
                                    Html.fromHtml(memberLayoutChildModel.getsTitle()).toString(), GoodsListFragment.TYPE_GOODS_SPECIAL));
                        }
                    }
                });
            }
        }
    }


    /**
     * 设置广告位图片和点击事件
     *
     * @param lists         广告内容的list
     * @param isContainFour 是否包含第四个广告位
     */
    private void setAdImage(final ArrayList<MemberLayoutChildContentModel> lists, boolean isContainFour) {
        FSSimpleImageView imageVertical = ButterKnife.findById(contentView, R.id.image_vertical);
        FSSimpleImageView imageHorizonal1 = ButterKnife.findById(contentView, R.id.image_horizonal1);
        FSSimpleImageView imageHorizonal2 = ButterKnife.findById(contentView, R.id.image_horizonal2);
        FSSimpleImageView imageHorizonal3 = null;
        if (isContainFour) {
            imageHorizonal3 = ButterKnife.findById(contentView, R.id.image_horizonal3);
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
                        startActivity(WebViewActivity.newIntent(getActivity(), jumpUrl));
                }
            });
        }
    }

}
