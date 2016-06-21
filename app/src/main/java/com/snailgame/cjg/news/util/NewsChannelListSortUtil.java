package com.snailgame.cjg.news.util;

import com.snailgame.cjg.common.db.dao.NewsChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanHH on 2016/5/12.
 */
public class NewsChannelListSortUtil {
    //要显示的频道列表
    public static List<NewsChannel> getShowChannelList(List<NewsChannel> showChannel) {
        List<NewsChannel> resultList = new ArrayList<>();
        resultList.add(getHotChannel());
        for (NewsChannel newsChannel : showChannel) {
            if (10013 == newsChannel.getChannelId()) {//热点频道已经存在列表中
               continue;
            }
            resultList.add(newsChannel);
        }
        return resultList;
    }

    //要添加的频道列表
    public static List<NewsChannel> getOtherChannelList(List<NewsChannel> otherChannel) {
        List<NewsChannel> resultList = new ArrayList<>();
        for (NewsChannel newsChannel : otherChannel) {
            if (10013 == newsChannel.getChannelId()) {//热点频道存在列表中
                continue;
            }
            resultList.add(newsChannel);
        }
        return resultList;
    }

    //热点频道
    private static NewsChannel getHotChannel() {
        NewsChannel hotNewsChannel = new NewsChannel();
        hotNewsChannel.setChannelId(10013);
        hotNewsChannel.setChannelName("热点");
        hotNewsChannel.setShow(true);
        return hotNewsChannel;
    }
}
