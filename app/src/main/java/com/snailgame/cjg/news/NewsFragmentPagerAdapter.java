package com.snailgame.cjg.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.snailgame.cjg.common.db.dao.NewsChannel;
import com.snailgame.cjg.news.adpter.MapFragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by TAJ_C on 2016/4/12.
 */
public class NewsFragmentPagerAdapter extends MapFragmentStatePagerAdapter {
    private List<NewsChannel> mChannelList;
    private Context mContext;

    public NewsFragmentPagerAdapter(Context context, FragmentManager fm, List<NewsChannel> mChannelList) {
        super(fm);
        this.mContext = context;
        this.mChannelList = mChannelList;
    }

    @Override
    public int getCount() {
        if (mChannelList != null) {
            return mChannelList.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mChannelList != null && mChannelList.get(position) != null) {
            return mChannelList.get(position).getChannelName();
        } else {
            return "";
        }
    }

    public void refreshData(List<NewsChannel> newsChannelList) {
        this.mChannelList = newsChannelList;
        notifyDataSetChanged();
    }

    public int getTargetTitlePosition(String title) {
        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getChannelName().equals(title)) {
                return i;
            }
        }
        return 0;
    }


    @Override
    public String getFragmentTag(int position) {
        return position >= mChannelList.size() || mChannelList == null || mChannelList.get(position) == null  ? null : mChannelList.get(position).getChannelName();
    }

    @Override
    public Fragment getItem(int position) {
        if (mChannelList != null && mChannelList.get(position) != null) {
            return NewsFragment.getInstance(mChannelList.get(position).getChannelId());
        } else {
            return null;
        }
    }
}
