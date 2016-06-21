package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.ImpRefresh;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.AppNewsModel;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 资讯
 *
 * @author pancl
 */
public class AppNewsAdapter extends BaseAdapter implements ImpRefresh<AppNewsModel> {
    private AppNewsModel mAppNewsModel;
    private Activity mActivity;
    private int[] mRoute;

    public AppNewsAdapter(Activity activity, AppNewsModel appNewsModel, int[] route) {
        mAppNewsModel = appNewsModel;
        mActivity = activity;
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (mAppNewsModel == null || mAppNewsModel.getItemList() == null)
            return 0;
        return mAppNewsModel.getItemList().size();
    }

    @Override
    public AppNewsModel.ModelItem getItem(int position) {
        if (mAppNewsModel == null || mAppNewsModel.getItemList() == null)
            return null;

        if (position < mAppNewsModel.getItemList().size())
            return mAppNewsModel.getItemList().get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AppNewsModel.ModelItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mActivity, R.layout.app_news_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        fillContent(item, viewHolder, position);
        return convertView;
    }

    private void fillContent(final AppNewsModel.ModelItem item, ViewHolder viewHolder, final int position) {
        if (item == null || viewHolder == null)
            return;
        if (position == 0) {
            item.setTopMargin((int) ResUtil.getDimension(R.dimen.item_margin));
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewHolder.appNewsContainer.getLayoutParams();
        lp.topMargin = item.getTopMargin();
        viewHolder.appNewsContainer.setLayoutParams(lp);
        viewHolder.tv_title.setText(item.getsTitle());
        viewHolder.tv_date.setText(item.getdCreate());
        viewHolder.tv_content.setText(item.getsSubTitle());
        viewHolder.iv_ico.setImageUrlAndReUse(item.getsImageUrl());

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] route = mRoute.clone();
                route[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_NEWS;
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
                route[AppConstants.STATISTCS_DEPTH_NINE] = item.getiId();
                mActivity.startActivity(WebViewActivity.newIntent(mActivity,
                        item.getsHtmlUrl(), route));
            }
        });
    }

    public List<AppNewsModel.ModelItem> getListData() {
        return mAppNewsModel.getItemList();
    }

    @Override
    public void refreshData(AppNewsModel appNewsModel) {
        this.mAppNewsModel = appNewsModel;
        notifyDataSetChanged();
    }


    static class ViewHolder {
        View container;
        @Bind(R.id.app_news_container)
        RelativeLayout appNewsContainer;
        @Bind(R.id.tv_title)
        TextView tv_title;
        @Bind(R.id.tv_date)
        TextView tv_date;
        @Bind(R.id.ico)
        FSSimpleImageView iv_ico;
        @Bind(R.id.tv_content)
        TextView tv_content;


        public ViewHolder(View convertView) {
            this.container = convertView;
            ButterKnife.bind(this, convertView);
        }
    }
}
