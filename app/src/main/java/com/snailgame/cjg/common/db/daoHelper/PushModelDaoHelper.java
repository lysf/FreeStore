package com.snailgame.cjg.common.db.daoHelper;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snailgame.cjg.common.db.FreeStoreDataHelper;
import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.event.UpdateNotificationEvent;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.MainThreadBus;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lic on 2015/7/24.
 * <p>
 * pushModel表的数据库操作工具类
 */
public class PushModelDaoHelper {
    /**
     * 查询全部
     *
     * @param context
     */
    public static List<PushModel> queryForAll(Context context) {
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                    getPushDao();
            QueryBuilder<PushModel, Integer> queryBuilder = pushModelDao.queryBuilder();
            queryBuilder.orderBy(PushModel.COL_PUSH_IS_READ, true);
            queryBuilder.orderBy(PushModel.COL_PUSH_CREATE_DATE, false);
            if (IdentityHelper.getUid(context) == null || TextUtils.isEmpty(IdentityHelper.getUid(context))) {
                queryBuilder.where().eq(PushModel.COL_PUSH_USER_ID, PushModel.PUSH_MODEL_DEFAULT_USER_ID);
            } else {
                queryBuilder.where().eq(PushModel.COL_PUSH_USER_ID, PushModel.PUSH_MODEL_DEFAULT_USER_ID).or().eq(PushModel.COL_PUSH_USER_ID, IdentityHelper.getUid(context));
            }
            PreparedQuery<PushModel> preparedQuery = queryBuilder.prepare();
            return pushModelDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 按照键值对查询
     *
     * @param key
     * @param value
     */
    public static List<PushModel> queryForEq(Context context, String key, Object value) {
        try {
            return FreeStoreDataHelper.getHelper(context).
                    getPushDao().queryForEq(key, value);
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 更新 {@link PushModel}的COL_PUSH_IS_EXIST和COL_PUSH_IS_READ字段
     *
     * @param context
     * @param model
     */
    public synchronized static void updateHasRead(Context context, PushModel model) {
        if (model == null)
            return;
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                    getPushDao();
            UpdateBuilder<PushModel, Integer> updateBuilder = pushModelDao.updateBuilder();
            updateBuilder.updateColumnValue(PushModel.COL_PUSH_IS_EXIST, PushModel.IS_NOT_EXIT);
            updateBuilder.updateColumnValue(PushModel.COL_PUSH_IS_READ, PushModel.HAS_READ);
            updateBuilder.where().eq(PushModel.COL_PUSH_MESSAGE_ID, model.getMsgId());
            PreparedUpdate<PushModel> preparedUpdate = updateBuilder.prepare();
            pushModelDao.update(preparedUpdate);
            MainThreadBus.getInstance().post(new UpdateNotificationEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新 推送表中所有{@link PushModel} COL_PUSH_IS_READ字段为已读
     *
     * @param context
     */
    public synchronized static void updateAllHasRead(Context context) {
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                    getPushDao();
            UpdateBuilder<PushModel, Integer> updateBuilder = pushModelDao.updateBuilder();
            updateBuilder.updateColumnValue(PushModel.COL_PUSH_IS_READ, PushModel.HAS_READ);
            updateBuilder.where().eq(PushModel.COL_PUSH_IS_READ, PushModel.NOT_READ);
            PreparedUpdate<PushModel> preparedUpdate = updateBuilder.prepare();
            pushModelDao.update(preparedUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新 {@link PushModel}的COL_PUSH_IS_EXIST和COL_PUSH_IS_READ字段
     *
     * @param context
     * @param model
     */
    public synchronized static void updateHasReadAsId(Context context, PushModel model) {
        if (model == null)
            return;
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                    getPushDao();
            UpdateBuilder<PushModel, Integer> updateBuilder = pushModelDao.updateBuilder();
            updateBuilder.updateColumnValue(PushModel.COL_PUSH_IS_READ, PushModel.HAS_READ);
            updateBuilder.where().eq(PushModel.COLUMN_ID, model.getId());
            PreparedUpdate<PushModel> preparedUpdate = updateBuilder.prepare();
            pushModelDao.update(preparedUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新 {@link PushModel}的COL_PUSH_IS_EXIST字段
     *
     * @param context
     * @param model
     */
    public synchronized static void updateIsExit(Context context, PushModel model) {
        if (model == null)
            return;
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                    getPushDao();
            UpdateBuilder<PushModel, Integer> updateBuilder = pushModelDao.updateBuilder();
            updateBuilder.updateColumnValue(PushModel.COL_PUSH_IS_EXIST, PushModel.IS_NOT_EXIT);
            updateBuilder.where().eq(PushModel.COL_PUSH_MESSAGE_ID, model.getMsgId());
            PreparedUpdate<PushModel> preparedUpdate = updateBuilder.prepare();
            pushModelDao.update(preparedUpdate);
            MainThreadBus.getInstance().post(new UpdateNotificationEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入
     *
     * @param context
     * @param model
     */
    public synchronized static void insert(Context context, PushModel model) {
        if (model == null)
            return;
        try {
            Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).getPushDao();
            pushModelDao.create(model);
            MainThreadBus.getInstance().post(new UpdateNotificationEvent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只保留30条消息，若消息数量大于30条，则删除旧消息
     *
     * @param context
     * @param models
     */
    public synchronized static List<PushModel> deleteExtraMsgs(Context context, List<PushModel> models) {
        if (models.size() > 30) {
            try {
                Dao<PushModel, Integer> pushModelDao = FreeStoreDataHelper.getHelper(context).
                        getPushDao();
                DeleteBuilder<PushModel, Integer> deleteBuilder = pushModelDao.deleteBuilder();
                if (models.get(29).getHasRead() == PushModel.HAS_READ) {    // 未读消息小于30条时，只删除多余的已读消息
                    deleteBuilder.where().eq(PushModel.COL_PUSH_IS_READ, PushModel.HAS_READ).and().lt(PushModel.COL_PUSH_CREATE_DATE,
                            models.get(29).getCreate_date());
                } else {        // 未读消息大于30条时，删除所有的已读消息及时间久的未读消息
                    deleteBuilder.where().eq(PushModel.COL_PUSH_IS_READ, PushModel.HAS_READ).or().lt(PushModel.COL_PUSH_CREATE_DATE,
                            models.get(29).getCreate_date());
                }
                PreparedDelete<PushModel> preparedDelete = deleteBuilder.prepare();
                pushModelDao.delete(preparedDelete);
                models = models.subList(0, 30);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return models;
    }


}
