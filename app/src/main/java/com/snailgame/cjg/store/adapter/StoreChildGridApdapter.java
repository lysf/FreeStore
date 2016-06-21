package com.snailgame.cjg.store.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.store.model.StoreChildContentModel;
import com.snailgame.cjg.util.ComUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * create by lic
 * 管理页面百宝箱Adapter
 */
public class StoreChildGridApdapter extends BaseAdapter {
    private Activity activity;
    private List<StoreChildContentModel> dataList;

    public StoreChildGridApdapter(Activity activity, List<StoreChildContentModel> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public StoreChildContentModel getItem(int position) {
        if (dataList != null && position < dataList.size())
            return dataList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(
                    R.layout.fragment_store_quick_view_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ComUtil.dpToPx(28), ComUtil.dpToPx(28));
        //如果是第一行则增大图标和顶部中间的空白，这样保持每一个item的竖向间距和顶部留白相当
        if (position < 4) {
            layoutParams.topMargin = ComUtil.dpToPx(8);
        } else {
            layoutParams.topMargin = ComUtil.dpToPx(4);
        }
        viewHolder.icon.setLayoutParams(layoutParams);
        StoreChildContentModel storeChildContentModel = getItem(position);
        String title = storeChildContentModel.getsTitle();
        String url = storeChildContentModel.getsImageUrl();
        if (!TextUtils.isEmpty(storeChildContentModel.getsJumpUrl())) {
            viewHolder.linearLayout.setBackgroundResource(R.drawable.ab_btn_selector);
        } else {
            viewHolder.linearLayout.setBackgroundResource(R.color.translucent_full);
        }
        if (url != null && !TextUtils.isEmpty(url))
            viewHolder.icon.setImageUrlAndReUse(url);
        if (title != null && !TextUtils.isEmpty(title))
            viewHolder.name.setText(title);

        return convertView;
    }


    final class ViewHolder {
        @Bind(R.id.icon)
        public FSSimpleImageView icon;
        @Bind(R.id.name)
        public TextView name;
        @Bind(R.id.root_view)
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
