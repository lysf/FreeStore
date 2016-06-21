package com.snailgame.cjg.news.adpter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.news.model.NewsIgnoreModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 资讯过滤UI
 * Created by TAJ_C on 2016/4/11.
 */
public class NewsIgnoreAdatper extends BaseAdapter {
    List<NewsIgnoreModel> ignoreList;

    private Context mContext;

    public NewsIgnoreAdatper(Context context, List<NewsIgnoreModel> ignoreList) {
        this.mContext = context;
        this.ignoreList = ignoreList;

        if (ignoreList != null) {
            //添加其他
            NewsIgnoreModel model = new NewsIgnoreModel();
            model.setCSource("-1");
            model.setSTagName(context.getString(R.string.news_ignore_other));
            ignoreList.add(model);

            for (NewsIgnoreModel item : ignoreList) {
                item.setSelected(false);
            }
        }

    }

    @Override
    public int getCount() {
        if (ignoreList != null) {
            return ignoreList.size();
        } else {
            return 0;
        }
    }

    @Override
    public NewsIgnoreModel getItem(int position) {
        return ignoreList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView view = new TextView(mContext);
        view.setLayoutParams(new AbsListView.LayoutParams(ComUtil.dpToPx(80), ComUtil.dpToPx(35)));
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);

        view.setTextColor(ResUtil.getColor(view.isSelected() ?
                R.color.btn_green_normal : R.color.primary_text_color));

        view.setBackgroundResource(R.drawable.btn_news_ignore_selector);

        final NewsIgnoreModel item = getItem(position);
        if (item != null) {
            view.setText(item.getSTagName());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setSelected(!view.isSelected());
                item.setSelected(view.isSelected());
                view.setTextColor(ResUtil.getColor(view.isSelected() ?
                        R.color.btn_green_normal : R.color.primary_text_color));

                if (listener != null) {
                    int total = 0;
                    for (NewsIgnoreModel item : ignoreList) {
                        if (item.isSelected()) {
                            total++;
                        }
                    }
                    listener.refreshNumView(total);
                }
            }
        });
        return view;
    }


    public List<NewsIgnoreModel> getSelectedData() {
        List<NewsIgnoreModel> bufList = new ArrayList<>();

        for (NewsIgnoreModel item : ignoreList) {
            if (item.isSelected()) {
                bufList.add(item);
            }
        }
        return bufList;
    }

    OnRefreshNumListener listener;

    public void setOnRefreshNumLister(OnRefreshNumListener listener) {
        this.listener = listener;
    }

    public interface OnRefreshNumListener {
        void refreshNumView(int num);
    }
}
