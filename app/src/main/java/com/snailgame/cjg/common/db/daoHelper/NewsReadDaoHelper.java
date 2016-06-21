package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.NewsReaded;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by TAJ_C on 2016/4/19.
 */
public class NewsReadDaoHelper {

    private Dao<NewsReaded, Integer> newsReadDao;
    private volatile static NewsReadDaoHelper newsReadDaoHelper = null;
    private static final int MAX_NUM = 5;

    public static NewsReadDaoHelper getInstance(Context context) {
        if (newsReadDaoHelper == null) {
            synchronized (NewsReadDaoHelper.class) {
                if (newsReadDaoHelper == null) {
                    newsReadDaoHelper = new NewsReadDaoHelper(context);
                }
            }
        }
        return newsReadDaoHelper;
    }

    public NewsReadDaoHelper(Context context) {
        try {
            newsReadDao = FreeStoreDataHelper.getHelper(context).getNewsReadDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(NewsReaded newsReaded) {

        try {
            List<NewsReaded> newsRead = newsReadDao.queryForEq(NewsReaded.COLUM_NEWS_ID, newsReaded.getNewsID());
            if (ListUtils.isEmpty(newsRead)) {
                newsReadDao.create(newsReaded);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<NewsReaded> queryReadData() {
        List<NewsReaded> newsReadedList = null;
        try {
            newsReadedList = newsReadDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newsReadedList;
    }


    public synchronized void deleteExtraMsg() {
        List<NewsReaded> newsReadedList = queryReadData();
        if (newsReadedList != null && newsReadedList.size() > MAX_NUM) {
            try {
                newsReadDao.delete(newsReadedList.subList(0, newsReadedList.size() - MAX_NUM));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}

