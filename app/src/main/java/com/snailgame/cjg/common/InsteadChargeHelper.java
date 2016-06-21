package com.snailgame.cjg.common;

import com.snailgame.cjg.detail.model.InsteadCharge;
import com.snailgame.cjg.detail.model.InsteadChargeModel;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sunxy on 2015/10/15.
 */
public class InsteadChargeHelper {
    private final static String TAG = "InsteadChargeHelper";

    /**
     * 获取代充列表
     */
    public static void getInsteadCharge() {
        FSRequestHelper.newGetRequest(JsonUrl.getJsonUrl().JSON_URL_INSTEDA_CHARGE, TAG, InsteadChargeModel.class, new IFDResponse<InsteadChargeModel>() {
            @Override
            public void onSuccess(InsteadChargeModel insteadChargeModel) {
                if (insteadChargeModel != null && insteadChargeModel.getCode() == 0) {
                    List<InsteadCharge> insteadChargeList = insteadChargeModel.getInsteadChargeList();
                    if (insteadChargeList != null) {
                        HashMap<Integer, InsteadCharge> insteadChargeArrayMap = new HashMap<>(insteadChargeList.size());
                        for (InsteadCharge insteadCharge : insteadChargeList) {
                            insteadChargeArrayMap.put(insteadCharge.getnAppId(), insteadCharge);
                        }
                        GlobalVar.getInstance().setInsteadChargeArrayMap(insteadChargeArrayMap);
                    }
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, true, true, new ExtendJsonUtil());
    }
}
