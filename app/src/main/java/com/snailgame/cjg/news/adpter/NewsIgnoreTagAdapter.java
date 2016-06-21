package com.snailgame.cjg.news.adpter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.news.model.NewsIgnoreModel;
import com.snailgame.cjg.news.widget.flowlayout.FlowLayout;
import com.snailgame.cjg.news.widget.flowlayout.TagAdapter;
import com.snailgame.cjg.news.widget.flowlayout.TagFlowLayout;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by yanHH on 2016/4/18.
 */
public class NewsIgnoreTagAdapter extends TagAdapter implements TagFlowLayout.OnTagClickListener,TagFlowLayout.OnSelectListener{
    private Context context;
    private TagFlowLayout tagFlowLayout;
    private List<NewsIgnoreModel> datas;

    public NewsIgnoreTagAdapter(Context context,List<NewsIgnoreModel> datas,TagFlowLayout tagFlowLayout) {
        super(datas);
        this.datas = datas;
        this.context = context;
        this.tagFlowLayout = tagFlowLayout;
        if(tagFlowLayout != null){
            tagFlowLayout.setOnTagClickListener(this);
            tagFlowLayout.setOnSelectListener(this);
        }
    }
    @Override
    public View getView(FlowLayout parent, int position, Object o) {
        final TextView view = new TextView(context);
        view.setTag(R.id.tag_key,position);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = ComUtil.dpToPx(4);
        layoutParams.rightMargin = ComUtil.dpToPx(4);
        layoutParams.bottomMargin = ComUtil.dpToPx(18);
        view.setLayoutParams(layoutParams);
//        view.setMinWidth(ComUtil.dpToPx(60));
        view.setMinHeight(ComUtil.dpToPx(26));
        view.setPadding(ComUtil.dpToPx(14),0,ComUtil.dpToPx(14),0);
        view.setTextSize(14);
        view.setGravity(Gravity.CENTER);

        view.setTextColor(ResUtil.getColor(view.isSelected() ?
                R.color.btn_green_normal : R.color.primary_text_color));

        view.setBackgroundResource(R.drawable.btn_news_ignore_selector);

        final NewsIgnoreModel item = (NewsIgnoreModel) o;
        if (item != null) {
            view.setText(item.getSTagName());
        }
        return view;
    }

    @Override
    public void onSelected(Set<Integer> selectPosSet) {
        listener.refreshNumView(selectPosSet.size());
    }

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
            view.setSelected(!view.isSelected());
            ((TextView)view).setTextColor(ResUtil.getColor(view.isSelected() ?
                    R.color.btn_green_normal : R.color.primary_text_color));
        return true;
    }

    //最终的选择结果
    public List<NewsIgnoreModel> getSelectedData() {
        List<NewsIgnoreModel> bufList = new ArrayList<>();
        for(int index : tagFlowLayout.getSelectedList()){
            bufList.add(datas.get(index));
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
