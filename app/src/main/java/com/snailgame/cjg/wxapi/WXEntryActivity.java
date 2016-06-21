package com.snailgame.cjg.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.receiver.GetPointsReceiver;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by sunxy on 14-3-25.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private String appId;
    private String gameName;
    private boolean isShareMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                //ComUtil.deleteFile(WechatShare.SDCARD_ROOT+WechatShare.IMAGE_NAME);
                appId = GlobalVar.getInstance().getShareAppId();
                gameName = GlobalVar.getInstance().getShareGameName();
                if (!TextUtils.isEmpty(appId) && IdentityHelper.isLogined(FreeStoreApp.getContext())) {
                    getSharePoints();
                } else {
                    finishActivity();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                finishActivity();
                break;
            default:
                result = R.string.errcode_unknown;
                finishActivity();
                break;
        }


        ToastUtils.showMsgLong(this, result);
    }

    /**
     * 发送广播去增加用户的积分点
     */
    private void getSharePoints() {
        isShareMenu = GlobalVar.getInstance().isShareMenu();
        sendBroadcast(GetPointsReceiver.newIntent(this, appId, gameName, "1", isShareMenu));
        finishActivity();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalVar.getInstance().setShareAppId("36");
        GlobalVar.getInstance().setShareGameName("");
        GlobalVar.getInstance().setShareMenu(false);

    }

    private void finishActivity() {
        finish();
    }
}