package com.snailgame.cjg.message.model;

import com.snailgame.cjg.common.db.dao.PushModel;
import com.snailgame.cjg.common.db.daoHelper.PushModelDaoHelper;
import com.snailgame.cjg.event.RefreshMsgNumEvent;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.util.MainThreadBus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by yftx on 6/26/14.
 */
public class MsgNum {
    private static MsgNum instance;

    private MsgNum() {

    }

    public static MsgNum getInstance() {
        if (instance == null) {
            instance = new MsgNum();
        }
        return instance;
    }

    public void getNums() {
        Runnable readMsgNum = new Runnable() {
            @Override
            public void run() {
                int count = readUnReadMsgNum();
                MainThreadBus.getInstance().post(new RefreshMsgNumEvent(count));
            }
        };
        new Thread(readMsgNum).start();
    }

    private synchronized int readUnReadMsgNum() {
        try {
            List<PushModel> models = PushModelDaoHelper.queryForAll(FreeStoreApp.getContext());
            if (models == null)
                return 0;
            models = PushModelDaoHelper.deleteExtraMsgs(FreeStoreApp.getContext(), models);
            Iterator<PushModel> ite = models.iterator();
            while (ite.hasNext()) {
                if (ite.next().getHasRead() == PushModel.HAS_READ) {
                    ite.remove();
                }
            }

            return models.size();
        } catch (Exception e) {
            e.printStackTrace();

        }

        return 0;
    }
}
