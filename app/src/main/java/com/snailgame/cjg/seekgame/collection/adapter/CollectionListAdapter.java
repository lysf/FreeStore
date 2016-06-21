package com.snailgame.cjg.seekgame.collection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.seekgame.collection.CollectionActivity;
import com.snailgame.cjg.seekgame.collection.model.CollectionListModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 合集列表
 * Created by chenping1 on 2014/4/9.
 */
public class CollectionListAdapter extends BaseAdapter
        implements ImpRefresh<List<CollectionListModel.ModelItem>> {
    private List<CollectionListModel.ModelItem> mCollectionInfoList;
    private LayoutInflater infater;
    private Context context;
    private int[] mRoute;

    public CollectionListAdapter(Context context,
                                 List<CollectionListModel.ModelItem> collectionInfoList, int[] route) {
        mCollectionInfoList = collectionInfoList;
        infater = LayoutInflater.from(FreeStoreApp.getContext());
        this.context = context;
        mRoute = route;
        mRoute[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
    }

    @Override
    public int getCount() {
        if (mCollectionInfoList == null)
            return 0;
        return mCollectionInfoList.size();
    }

    @Override
    public CollectionListModel.ModelItem getItem(int i) {
        if (mCollectionInfoList == null)
            return null;

        if (i < mCollectionInfoList.size())
            return mCollectionInfoList.get(i);
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CollectionListModel.ModelItem info = getItem(position);

        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = infater.inflate(R.layout.app_pic_list, parent, false);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if (info != null) {
                holder.picView.setImageUrlAndReUse(info.getcPicUrl());

                holder.picView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int[] route = mRoute.clone();
                        route[AppConstants.STATISTCS_DEPTH_FOUR] = position + 1;
                        context.startActivity(CollectionActivity.newIntent(context, info.getiCollectionId(), route));
                    }
                });
            }
        }
        return view;
    }

    @Override
    public void refreshData(List<CollectionListModel.ModelItem> collectionInfoList) {
        mCollectionInfoList = collectionInfoList;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.app_pic_item)
        FSSimpleImageView picView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
