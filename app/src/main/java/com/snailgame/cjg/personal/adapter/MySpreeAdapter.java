package com.snailgame.cjg.personal.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.MySpreeModel;
import com.snailgame.cjg.personal.model.MySpreeModel.ModelItem;
import com.snailgame.cjg.util.ComUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MySpreeAdapter extends BaseAdapter {

    private List<MySpreeModel.ModelItem> listData;

    private Activity context;

    public MySpreeAdapter(Activity context, List<MySpreeModel.ModelItem> listData) {
        this.context = context;
        this.listData = listData;
        initInfoData(listData);
    }

    private List<ModelItem> initInfoData(List<ModelItem> listData) {
        for (ModelItem item : listData) {
            String info = item.getsIntro();
            try {
                JSONObject object = new JSONObject(info);
                item.setContent(object.getString("content"));
                item.setUseMethod(object.getString("useMethod"));
                item.setDeadline(object.getString("deadline"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listData;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.fg_my_goods, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            final MySpreeModel.ModelItem goods = getItem(position);
            viewHolder.appNameView.setText(goods.getsAppName());
            viewHolder.spreeName.setText(goods.getsArticleName());
            viewHolder.cdKeyView.setText(goods.getcCdkey());
            viewHolder.spreeIcon.setImageUrlAndReUse(goods.getcLogo());
            viewHolder.spreeContent.setText(goods.getContent());
            viewHolder.spreeTitle.setText(goods.getUseMethod());
            viewHolder.dead_time.setText(goods.getDeadline());

            updateVisible(viewHolder, goods);
            updateIndicator(viewHolder, goods);
            viewHolder.spreeCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goods != null && goods.getcCdkey() != null)
                        ComUtil.copyToClipBoard(context, goods.getcCdkey());
                }
            });

            viewHolder.spree_detail_layout.setTag(R.id.tag_animation, new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!goods.isShowDetail()) {
                        updateVisible(viewHolder, goods);
                        updateIndicator(viewHolder, goods);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                    goods.setShowDetail(!goods.isShowDetail());
                    if (goods.isShowDetail()) {
                        updateVisible(viewHolder, goods);
                        updateIndicator(viewHolder, goods);
                    }
                }
            });
        }
        return convertView;
    }

    private void updateIndicator(ViewHolder viewHolder, ModelItem goods) {
        if (goods.isShowDetail()) {
            viewHolder.spreeIndicator.setImageResource(R.drawable.ic_extend_up);
        } else {
            viewHolder.spreeIndicator.setImageResource(R.drawable.ic_extend_down);
        }
    }

    private void updateVisible(ViewHolder viewHolder, ModelItem goods) {
        if (goods.isShowDetail()) {
            viewHolder.spree_detail_layout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.spree_detail_layout.setVisibility(View.GONE);
        }
    }


    @Override
    public int getCount() {
        if (null != listData) {
            return listData.size();
        }
        return 0;
    }

    @Override
    public MySpreeModel.ModelItem getItem(int position) {
        if (null != listData && position < getCount()) {
            return listData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addNewData(List<MySpreeModel.ModelItem> addListData) {
        if (listData != null) {
            listData.addAll(initInfoData(addListData));
            notifyDataSetChanged();
        }
    }


    class ViewHolder {

        @Bind(R.id.tv_app_name)
        TextView appNameView;
        @Bind(R.id.tv_goods_name)
        TextView spreeName;
        @Bind(R.id.tv_cdkey)
        TextView cdKeyView;
        @Bind(R.id.goods_icon)
        FSSimpleImageView spreeIcon;
        @Bind(R.id.goods_indicator)
        ImageView spreeIndicator;
        @Bind(R.id.expandable)
        LinearLayout spree_detail_layout;
        @Bind(R.id.toggle_button)
        View spreeLayout;
        @Bind(R.id.dead_time)
        TextView dead_time;
        @Bind(R.id.good_title)
        TextView spreeTitle;
        @Bind(R.id.good_content)
        TextView spreeContent;
        @Bind(R.id.btn_copy)
        TextView spreeCopy;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
