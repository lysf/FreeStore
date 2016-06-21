package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.CustomQuickReturnView;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;

import java.util.List;

/**
 * 热门游戏
 * Created by sunxy on 2014/9/11.
 */
public class HotGamesAdapter extends ModuleBaseAdapter {
    private static final int VIEW_TYPE_COUNT=2;
    private Activity activity;

    public HotGamesAdapter(Activity activity, ModuleModel moduleModel, int[] route) {
        super(activity, moduleModel, route);
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                convertView = getHeaderView(true);
                break;
            case 1:
                convertView = getHotGameView();
                break;
        }
        return convertView;
    }

    public View getHotGameView() {
        View view=inflater.inflate(R.layout.home_hot_game_layout,null);
        CustomQuickReturnView customHorizontalScrollView =(CustomQuickReturnView)view.findViewById(R.id.hotGameLayout);
        customHorizontalScrollView.setActivity(activity);
        List<ContentModel>children=moduleModel.getChilds();
        customHorizontalScrollView.inflatHotGameData(children, mRoute);
        return view;
    }

}
