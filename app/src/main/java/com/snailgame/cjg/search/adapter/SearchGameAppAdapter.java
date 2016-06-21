package com.snailgame.cjg.search.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.event.TabChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.search.SearchFragmentAdapter;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

/**
 * 搜索 -> 游戏/应用adapter
 * Created by pancl on 2015/6/3.
 */
public class SearchGameAppAdapter extends CommonListItemAdapter {
    private boolean isShowHeader = false;
    private int resultCount;

    public SearchGameAppAdapter(Activity context, List<AppInfo> appInfos, int fragementType, int[] route) {
        super(context, appInfos, fragementType, route);
    }

    public SearchGameAppAdapter(Activity context, List<AppInfo> appInfos, int[] route) {
        super(context, appInfos, AppConstants.VALUE_SEARCH_APP_LIST, route);
    }

    public void setShowHeader(boolean isShowHeader, int resultCount) {
        this.isShowHeader = isShowHeader;
        this.resultCount = resultCount;
        super.isUseViewHolder = isShowHeader;
    }

    @Override
    public int getCount() {
        if (super.getCount() > 0) {
            return isShowHeader ? super.getCount() + 2 : super.getCount();
        } else {
            return 0;
        }
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
                    convertView = super.getView(position - 1, convertView, parent);
                }
            }
        } else {
            convertView = super.getView(position, convertView, parent);
        }
        return convertView;
    }

    protected View getHeaderView(boolean isShowHeaderDivider) {
        View header = inflater.inflate(R.layout.search_result_header, null);
        if(!isShowHeaderDivider)
            header.findViewById(R.id.header_divider).setVisibility(View.GONE);

        if (getCount() > 0) {
            TextView title = (TextView) header.findViewById(R.id.card_header_title);
//            setDrawableLeft(title, mContext.getResources().getDrawable(R.drawable.common_red_selector));

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String tabName = ResUtil.getStringArray(R.array.search_result_tabs)[SearchFragmentAdapter.TAB_GAME_APP];
            stringBuilder.append(tabName).append(String.format(mContext.getString(R.string.search_result_count), String.valueOf(resultCount)));
            stringBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.search_result_header_color)),
                    tabName.length(), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setText(stringBuilder);

            header.findViewById(R.id.headerRoot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainThreadBus.getInstance().post(new TabChangeEvent(SearchFragmentAdapter.TAB_GAME_APP));
                }
            });
        }
        return header;
    }

    private void setDrawableLeft(TextView textView, Drawable drawable) {
        drawable.setBounds(0, 0, ComUtil.dpToPx(4), ComUtil.dpToPx(18));
        textView.setCompoundDrawables(drawable, null, null, null);
    }

}
