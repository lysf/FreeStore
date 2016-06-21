package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.NewsChannel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAJ_C on 2016/4/18.
 */
public class NewsChannelDaoHelper {

    private Dao<NewsChannel, Integer> newsChannelDao;
    private volatile static NewsChannelDaoHelper newsChannelDaoHelper = null;

    public static NewsChannelDaoHelper getInstance(Context context) {
        if (newsChannelDaoHelper == null) {
            synchronized (NewsChannelDaoHelper.class) {
                if (newsChannelDaoHelper == null) {
                    newsChannelDaoHelper = new NewsChannelDaoHelper(context);
                }
            }
        }
        return newsChannelDaoHelper;
    }

    public NewsChannelDaoHelper(Context context) {

        try {
            newsChannelDao = FreeStoreDataHelper.getHelper(context).getNewsChannelDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询所有频道
     *
     * @return
     * @throws SQLException
     */
    public synchronized List<NewsChannel> queryAllChannel() {
        List<NewsChannel> newsChannelList = null;
        try {
            newsChannelList = newsChannelDao.queryForAll();
            if (newsChannelList == null) {
                newsChannelList = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsChannelList;
    }

    /**
     * 查询显示的频道
     *
     * @return
     * @throws SQLException
     */
    public synchronized List<NewsChannel> queryShowChannel() {
        List<NewsChannel> newsChannelList = null;
        try {
            newsChannelList = newsChannelDao.queryForEq(NewsChannel.COLUM_CHANNEL_IS_SHOW, true);
            if (newsChannelList == null) {
                newsChannelList = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newsChannelList;
    }

    /**
     * 保存数据
     *
     * @param channelList
     * @throws SQLException
     */
    public synchronized void saveChannelToDb(List<NewsChannel> channelList) {
        try {
            newsChannelDao.delete(newsChannelDao.queryForAll());
            for (NewsChannel item : channelList) {
                newsChannelDao.create(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
