package com.snailgame.cjg.search.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.event.TabChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.search.SearchFragmentAdapter;
import com.snailgame.cjg.search.model.SearchKuwanModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 搜索 -> 酷玩adapter
 *
 * @author pancl
 */
public class SearchKuwanAdapter extends BaseAdapter {
    private List<SearchKuwanModel.ModelItem> mModelItems;
    private Activity mActivity;
    protected LayoutInflater inflater;
    private int[] mRoute;
    private boolean isShowHeader;
    private int resultCount;

    public SearchKuwanAdapter(Activity activity, List<SearchKuwanModel.ModelItem> modelItems, int[] route) {
        mModelItems = modelItems;
        mActivity = activity;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute=route;
    }

    @Override
    public int getCount() {
        if (mModelItems == null)
            return 0;
        return isShowHeader ? mModelItems.size() + 2 : mModelItems.size();
    }

    @Override
    public SearchKuwanModel.ModelItem getItem(int position) {
        if (mModelItems == null)
            return null;

        if (position < mModelItems.size())
            return mModelItems.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isShowHeader) {
            if (position == 0) {
                convertView = getHeaderView(false);
            } else {
                if (position == getCount() - 1) {
                    convertView = inflater.inflate(R.layout.home_modul_divider, null);
                } else {
                    convertView = getItemView(position - 1, convertView,parent);
                }
            }
        } else {
            convertView = getItemView(position, convertView,parent);
        }
        return convertView;
    }

    private View getItemView(int position, View convertView, ViewGroup parent) {
        final SearchKuwanModel.ModelItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView != null && !isShowHeader) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate( R.layout.activity_app_search_kuwan_item, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        fillContent(item, viewHolder, position);
        return convertView;
    }

    private void fillContent(final SearchKuwanModel.ModelItem item, ViewHolder viewHolder, final int position) {
        if (item == null || viewHolder == null)
            return;
        if (isShowHeader) {
            viewHolder.rl_container.setBackgroundResource(R.drawable.list_item_selector);
            viewHolder.divider.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_title.setText(item.getsGoodsName());
        viewHolder.tv_price.setText(mActivity.getString(R.string.yuan_char) + String.valueOf(item.getiGoodsPrice()));
        viewHolder.tv_pre_price.setText(mActivity.getString(R.string.yuan_char) + String.valueOf(item.getiMarketPrice()));
        viewHolder.tv_pre_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.iv_ico.setImageUrlAndReUse(item.getsGoodsImageUrl());

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] route = mRoute.clone();
                route[AppConstants.STATISTCS_DEPTH_FIVE] = AppConstants.STATISTCS_FIFTH_NEWS;
                route[AppConstants.STATISTCS_DEPTH_SEVEN] = position + 1;
                route[AppConstants.STATISTCS_DEPTH_NINE] = item.getiGoodsId();
                mActivity.startActivity(WebViewActivity.newIntent(mActivity,
                        item.getsGoodsPurchaseUrl(), route));
            }
        });
    }

    public void refreshData(List<SearchKuwanModel.ModelItem> modelItems) {
        this.mModelItems = modelItems;
        notifyDataSetChanged();
    }

    public void addData(List<SearchKuwanModel.ModelItem> modelItems) {
        if (this.mModelItems == null) {
            mModelItems = new ArrayList<>();
        }

        this.mModelItems.addAll(modelItems);
        notifyDataSetChanged();
    }

    public void refreshRoute(int[] route) {
        this.mRoute = route;
        notifyDataSetChanged();
    }

    public void setShowHeader(boolean isShowHeader, int resultCount) {
        this.isShowHeader = isShowHeader;
        this.resultCount = resultCount;
    }

    protected View getHeaderView(boolean isShowHeaderDivider) {
        View header = inflater.inflate(R.layout.search_result_header, null);
        if(!isShowHeaderDivider)
            header.findViewById(R.id.header_divider).setVisibility(View.GONE);

        if (mModelItems != null) {
            TextView title = (TextView) header.findViewById(R.id.card_header_title);
//            setDrawableLeft(title, mActivity.getResources().getDrawable(R.drawable.common_red_selector));

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String tabName = ResUtil.getStringArray(R.array.search_result_tabs)[SearchFragmentAdapter.TAB_KUWAN];
            stringBuilder.append(tabName).append(String.format(mActivity.getString(R.string.search_result_count), String.valueOf(resultCount)));
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.search_result_header_color)),
                    tabName.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(stringBuilder);

            header.findViewById(R.id.headerRoot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainThreadBus.getInstance().post(new TabChangeEvent(SearchFragmentAdapter.TAB_KUWAN));
                }
            });
        }
        return header;
    }

    private void setDrawableLeft(TextView textView, Drawable drawable) {
        drawable.setBounds(0, 0, ComUtil.dpToPx(4), ComUtil.dpToPx(18));
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    static class ViewHolder {
        View container;
        @Bind(R.id.app_news_container)
        RelativeLayout rl_container;
        @Bind(R.id.app_news_title)
        TextView tv_title;
        @Bind(R.id.app_news_price)
        TextView tv_price;
        @Bind(R.id.app_news_pre_price)
        TextView tv_pre_price;
        @Bind(R.id.app_news_icon)
        FSSimpleImageView iv_ico;
        @Bind(R.id.home_divider)
        View divider;


        public ViewHolder(View convertView) {
            this.container = convertView;
            ButterKnife.bind(this, convertView);
        }
    }
}
