package com.snailgame.cjg.seekgame.recommend.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;
import com.snailgame.cjg.seekgame.recommend.RecommendType;
import com.snailgame.cjg.seekgame.recommend.model.RecommendInfo;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 应用推荐Banner
 */
public class RecommendHeadAdapter extends BaseAdapter implements ImpRefresh<List<RecommendInfo>> {
    private LayoutInflater inflater;
    private Activity context;
    private List<RecommendInfo> mInfos;
    private int[] mRoute;

    private final int MAX_NUM = 4;
    private boolean isGameRecommend; //是否是游戏推荐页面

    public RecommendHeadAdapter(Activity context, List<RecommendInfo> infos, boolean isGameRecommend, int[] route) {
        this.context = context;
        mInfos = infos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
        this.isGameRecommend = isGameRecommend;
        mRoute[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_BANNERAD;
    }


    @Override
    public int getCount() {
        if (mInfos != null) {
            return mInfos.size() >= MAX_NUM ? MAX_NUM : mInfos.size();
        }

        return 0;
    }

    @Override
    public RecommendInfo getItem(int position) {
        return mInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View view, ViewGroup container) {
        final int index = position;
        final RecommendInfo info = getItem(position);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.recommend_head_view, null);
            holder = new ViewHolder(view);
            holder.imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ComUtil.dpToPx(80)));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (info != null) {
            holder.imageView.setImageUrl(info.getcPicSmall());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isGameRecommend) {
                        MobclickAgent.onEvent(context, UmengAnalytics.EVENT_GAME_BANNER);
                    }
                    Intent intent = new Intent();
                    int linkParam = (int) info.getnParamId();
                    int type = Integer.parseInt(info.getcType());

                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = index + 1;
                    switch (RecommendType.valueOf(type)) {
                        case RECOMMEND_DETAIL:
                            intent = DetailActivity.newIntent(context, linkParam, route);
                            break;
                        case RECOMMEND_COLLECTION:
                            intent = CollectionActivity.newIntent(context, linkParam, route);
                            break;
                        case RECOMMEND_HTML:
                            String url = info.getcHtmlUrl();
                            int appId = IdentityHelper.getAppId();
                            String imei = ComUtil.getIMEI(view.getContext());
                            if (IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                                String uid = IdentityHelper.getUid(FreeStoreApp.getContext());
                                String iid = IdentityHelper.getIdentity(FreeStoreApp.getContext());
                                url = String.format(url
                                                + "?nAppId=%1$s&nUserId=%2$s&cIdentity=%3$s&cImei=%4$s",
                                        String.valueOf(appId), uid, iid, imei);
                            } else {
                                url = String.format(url
                                                + "?nAppId=%1$s&cImei=%2$s",
                                        String.valueOf(appId), imei);
                            }

                            intent = WebViewActivity.newIntent(context, url);
                            break;
                        default:
                            break;
                    }
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.image_view)
        FSSimpleImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void refreshData(List<RecommendInfo> infos) {
        mInfos = infos;
    }
}