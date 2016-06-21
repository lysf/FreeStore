package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.UserMobileModel;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ListUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页Banner广告图片
 * Created by sunxy on 2014/10/10.
 */
public class ImagePageAdapter extends RecyclingPagerAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<ContentModel> mInfos;
    private int[] mRoute;
    private UserMobileModel.ModelItem mUserMobileModel;


    public ImagePageAdapter(Context context, List<ContentModel> infos, UserMobileModel.ModelItem userMobileModel, int[] route) {
        this(context, infos, route);
        this.mUserMobileModel = userMobileModel;
        if (mUserMobileModel != null && mInfos != null) {
            mInfos.add(0, new ContentModel());
        }
    }

    public ImagePageAdapter(Context context, List<ContentModel> infos, int[] route) {
        this.context = context;
        mInfos = infos;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (!ListUtils.isEmpty(mInfos))
            return Integer.MAX_VALUE;
        return 0;
    }


    @Override
    public int getCircleCount() {
        if (mInfos != null)
            return mInfos.size();
        return 0;
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return position % mInfos.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        final int index = getPosition(position);
        final ContentModel info = mInfos.size() == 0 ? null : mInfos.get(index);


        if (mUserMobileModel != null && index == 0) {
            MobileViewHolder holder;
            view = inflater.inflate(R.layout.item_home_user_mobile, container, false);
            holder = new MobileViewHolder(view);
            if (mUserMobileModel.getStatus() == UserMobileModel.MobileState.SUCCESS) {
                holder.mobileFlowView.setText(mUserMobileModel.getFlowRemainSize());
                holder.mobileMoneyView.setText(mUserMobileModel.getBalance());
                holder.mobileTimeView.setText(mUserMobileModel.getVoiceRemainSize());
                holder.uerLocalPhoneView.setText(mUserMobileModel.getCity() + SharedPreferencesUtil.getInstance().getNickName());
            } else {
                holder.mobileFlowView.setText(R.string.home_mobile_hor_line);
                holder.mobileMoneyView.setText(R.string.home_mobile_hor_line);
                holder.mobileTimeView.setText(R.string.home_mobile_hor_line);
                holder.uerLocalPhoneView.setText(SharedPreferencesUtil.getInstance().getNickName());
                switch (mUserMobileModel.getStatus()) {
                    case UserMobileModel.MobileState.LOADING:
                        holder.mobileHintView.setText(R.string.home_mobile_loading);
                        break;
                    case UserMobileModel.MobileState.FAILED:
                        holder.mobileHintView.setText(R.string.home_mobile_failed);
                        break;
                    case UserMobileModel.MobileState.MAINTENANCE:
                        holder.mobileHintView.setText(R.string.home_mobile_maintenance);
                        break;
                    default:
                        break;
                }
            }


        } else {
            PicViewHolder holder;
            if (view == null || view.getTag() == null) {
                holder = new PicViewHolder();
                view = inflater.inflate(R.layout.home_banner_view, null);
                holder.imageView = (FSSimpleImageView) view.findViewById(R.id.image_view);
                holder.imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setTag(holder);
            } else {
                holder = (PicViewHolder) view.getTag();
            }

            if (info != null) {
                holder.imageView.setImageUrl(info.getsImageUrl());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int[] route = mRoute.clone();
                        route[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_BANNERAD;
                        route[AppConstants.STATISTCS_DEPTH_FOUR] = index + 1;
                        JumpUtil.itemJump(context, info.getsJumpUrl(), info.getcSource(), info, route);
                    }
                });
            }
        }


        return view;
    }

    public void refreshUserMobileView(UserMobileModel.ModelItem userMobileModel) {
        this.mUserMobileModel = userMobileModel;

        notifyDataSetChanged();
    }

    private static class PicViewHolder {
        FSSimpleImageView imageView;
    }


    class MobileViewHolder {
        @Bind(R.id.tv_user_local_phone)
        TextView uerLocalPhoneView;
        @Bind(R.id.tv_mobile_money)
        TextView mobileMoneyView;
        @Bind(R.id.tv_mobile_flow)
        TextView mobileFlowView;
        @Bind(R.id.tv_mobile_time)
        TextView mobileTimeView;
        @Bind(R.id.tv_mobile_hint)
        TextView mobileHintView;

        MobileViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}