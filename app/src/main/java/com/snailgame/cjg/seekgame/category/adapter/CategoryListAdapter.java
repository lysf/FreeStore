package com.snailgame.cjg.seekgame.category.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.seekgame.category.model.CategoryListModel.ModelItem;
import com.snailgame.cjg.seekgame.recommend.AppListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 该类为分类列表子项目的通用适配器
 */
public class CategoryListAdapter extends BaseAdapter
        implements ImpRefresh<List<ModelItem>> {
    private LayoutInflater infater;
    private Context iContext;

    private int[] mRoute;
    /**
     * 数据源
     */
    private List<ModelItem> infos = new ArrayList<ModelItem>();

    public CategoryListAdapter(Context context, List<ModelItem> appInfos, int[] route) {
        iContext = context;
        infos = appInfos;
        infater = LayoutInflater.from(context);
        mRoute = route;
        // 分类列表
        mRoute[AppConstants.STATISTCS_DEPTH_THREE] = AppConstants.STATISTCS_THIRD_LIST;
    }

    @Override
    public int getCount() {
        if (infos == null) {
            return 0;
        }
        return infos.size() % 2 == 0 ? infos.size() / 2 : infos.size() / 2 + 1;
    }

    @Override
    public ModelItem getItem(int position) {
        if (infos == null || infos.size() <= position) {
            return null;
        }
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = infater.inflate(R.layout.app_sort_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ModelItem sortLeft = getItem(2 * position);
        if (holder.sortItemLeft != null && sortLeft != null) {
            holder.sortTitleLeft.setText(sortLeft.getsCategoryName());
            holder.sortDesLeft.setText(sortLeft.getsCategoryDesc());
            holder.sortLogoLeft.setImageUrlAndReUse(sortLeft.getcPicUrl());
            holder.sortItemLeft.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = 2 * position + 1;
                    if (sortLeft.getcCategoryType() == AppConstants.CATEGORY_CAT) {
                        iContext.startActivity(AppListActivity.newIntent(iContext, sortLeft.getiCategoryId(), sortLeft.getsCategoryName(), false, route));
                    } else {
                        iContext.startActivity(AppListActivity.newIntent(iContext, AppConstants.VALUE_CATEGORY_TAG, sortLeft.getiCategoryId(), sortLeft.getsCategoryName(), false, route));
                    }
                }
            });
        }

        if (infos.size() > (2 * position + 1)) {
            final ModelItem sortRight = getItem(2 * position + 1);
            if (holder.sortItemRight != null && sortRight != null) {
                holder.sortTitleRight.setText(sortRight.getsCategoryName());
                holder.sortDesRight.setText(sortRight.getsCategoryDesc());
                holder.sortLogoRight.setImageUrlAndReUse(sortRight.getcPicUrl());
                if (holder.sortItemRight != null)
                    holder.sortItemRight.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int[] route = mRoute.clone();
                            route[AppConstants.STATISTCS_DEPTH_FOUR] = 2 * position + 2;
                            if (sortRight.getcCategoryType() == AppConstants.CATEGORY_CAT) {
                                iContext.startActivity(AppListActivity.newIntent(iContext, sortRight.getiCategoryId(), sortRight.getsCategoryName(), false, route));
                            } else {
                                iContext.startActivity(AppListActivity.newIntent(iContext, AppConstants.VALUE_CATEGORY_TAG, sortRight.getiCategoryId(), sortRight.getsCategoryName(), false, route));
                            }
                        }
                    });
                holder.sortItemRight.setVisibility(View.VISIBLE);
            }
        } else {
            holder.sortItemRight.setVisibility(View.INVISIBLE);
        }


        return view;
    }

    @Override
    public void refreshData(List<ModelItem> gameCollections) {
        infos = gameCollections;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.sort_item_left)
        View sortItemLeft;
        @Bind(R.id.sort_item_image_left)
        FSSimpleImageView sortLogoLeft;
        @Bind(R.id.sort_item_title_left)
        TextView sortTitleLeft;
        @Bind(R.id.sort_item_desc_left)
        TextView sortDesLeft;
        @Bind(R.id.sort_item_right)
        View sortItemRight;
        @Bind(R.id.sort_item_image_right)
        FSSimpleImageView sortLogoRight;
        @Bind(R.id.sort_item_desc_right)
        TextView sortDesRight;
        @Bind(R.id.sort_item_title_right)
        TextView sortTitleRight;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
