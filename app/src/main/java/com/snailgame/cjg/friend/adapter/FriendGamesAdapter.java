package com.snailgame.cjg.friend.adapter;

import android.app.Activity;

import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.global.AppConstants;

import java.util.List;

/**
 * Created by TAJ_C on 2016/5/12.
 */
public class FriendGamesAdapter extends CommonListItemAdapter {

    public FriendGamesAdapter(Activity context, List<AppInfo> appInfos, int fragementType, int[] route) {
        super(context, appInfos, fragementType, route);
    }

    public FriendGamesAdapter(Activity context, List<AppInfo> appInfos, int[] route) {
        super(context, appInfos, AppConstants.VALUE_FRIEND_GAMES, route);
    }



}
