package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 优质服务
 * Created by TAJ_C on 2015/12/22.
 */
public class MemberQualityServiceAdapter extends BaseAdapter {

    private Context context;
    private List<String> picUrls = new ArrayList<>();

    public MemberQualityServiceAdapter(Context context, String config) {
        this.context = context;
        try {
            picUrls.add(JSON.parseObject(config).getString("img1"));
            picUrls.add(JSON.parseObject(config).getString("img2"));
            picUrls.add(JSON.parseObject(config).getString("img3"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        if (picUrls == null) {
            return 0;
        } else {
            return picUrls.size();
        }
    }

    @Override
    public String getItem(int position) {
        return picUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member_quality_service, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.picView.setImageUrlAndReUse(getItem(position));
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.siv_pic_view)
        FSSimpleImageView picView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
