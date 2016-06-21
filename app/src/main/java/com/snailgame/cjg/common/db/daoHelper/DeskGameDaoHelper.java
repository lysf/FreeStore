package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.DeskGame;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.cjg.downloadmanager.model.UpgradeAppListModel;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.fastdev.util.ListUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lic on 2015/7/24.
 */
public class DeskGameDaoHelper {
    /**
     * 保存升级列表到数据库
     *
     * @param itemList 需要保存的数据
     */
    public static synchronized void insertDeskGameDb(Context mContext, List<UpgradeAppListModel.ModelItem> itemList) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(mContext).getDeskGameDaoDao();
        DeskGame deskGame;
        for (UpgradeAppListModel.ModelItem item : itemList) {
            if (item.getcAppType().equals(AppConstants.VALUE_TYPE_GAME)) {
                List<DeskGame> deskGames = deskGameDao.queryForEq(DeskGame.GAME_PACKAGE, item.getcPackage());
                deskGame = new DeskGame();
                if (ListUtils.isEmpty(deskGames)) {
                    deskGame.setPackageName(item.getcPackage());
                    deskGameDao.create(deskGame);
                }
            }
        }
    }

    /**
     * 保存我的游戏列表到数据库
     *
     * @param selectedGameLists 需要保存的数据
     */
    public static synchronized void insertToDb(Context context, List<InstallGameInfo> selectedGameLists) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(context).getDeskGameDaoDao();

        DeskGame deskGame;
        for (InstallGameInfo installGameInfo : selectedGameLists) {
            List<DeskGame> deskGames = deskGameDao.queryForEq(DeskGame.GAME_PACKAGE, installGameInfo.getPackageName());
            deskGame = new DeskGame();
            if (ListUtils.isEmpty(deskGames)) {
                deskGame.setPackageName(installGameInfo.getPackageName());
                deskGameDao.create(deskGame);
            }

        }
    }

    /**
     * 插入deskGame到deskGame数据库
     *
     * @param deskGame
     */
    public static synchronized void insertToDb(Context context, DeskGame deskGame) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(context).getDeskGameDaoDao();
        List<DeskGame> deskGames = deskGameDao.queryForEq(DeskGame.GAME_PACKAGE, deskGame.getPackageName());
        if (ListUtils.isEmpty(deskGames)) {
            deskGameDao.create(deskGame);
        }
    }

    /**
     * 根据包名删除数据库相应的记录
     *
     * @param packageName 要删除数据的包名
     */
    public static synchronized void deleteFromDb(Context context, String packageName) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(context).getDeskGameDaoDao();
        DeleteBuilder<DeskGame, Integer> deleteBuilder = deskGameDao.deleteBuilder();
        deleteBuilder.where().eq(DeskGame.GAME_PACKAGE, packageName);
        PreparedDelete<DeskGame> preparedDelete = deleteBuilder.prepare();
        deskGameDao.delete(preparedDelete);
    }

    /**
     * 根据包名删除数据库相应的记录
     */
    public static synchronized List<DeskGame> queryForAll(Context context) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(context).getDeskGameDaoDao();
        List<DeskGame> deskGameList = deskGameDao.queryForAll();
        if (deskGameList == null)
            deskGameList = new ArrayList<>();
        return deskGameList;
    }

    /**
     * 修改免商店3.5.8的桌面我的游戏数据库去除重复的问题
     *
     * @param context
     */
    public static synchronized void deleteforDeskgameRepeat(Context context) throws SQLException {
        Dao<DeskGame, Integer> deskGameDao = FreeStoreDataHelper.getHelper(context).getDeskGameDaoDao();
        List<DeskGame> repeatDeskGameList = queryForAll(context);
        Map<String, Integer> map = new HashMap<>();
        List<DeskGame> deskGames = new ArrayList<>();
        DeskGame deskGame;
        for (DeskGame repeatDeskGame : repeatDeskGameList) {
            if (!map.containsKey(repeatDeskGame.getPackageName())) {
                deskGame = new DeskGame();
                deskGame.setId(repeatDeskGame.getId());
                deskGame.setPackageName(repeatDeskGame.getPackageName());
                deskGames.add(deskGame);
                map.put(repeatDeskGame.getPackageName(), repeatDeskGame.getId());
            }
        }
        deskGameDao.delete(repeatDeskGameList);
        for (DeskGame deskGameq : deskGames) {
            deskGameDao.create(deskGameq);
        }

    }


}
