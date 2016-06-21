package com.snailgame.cjg.common.share.weixin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;

import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * Created by sunxy on 14-3-19.
 */
public class WechatShareActivity extends BaseFSActivity {


    // IWXAPI 是第三方app和微信通信的openapi接口
    private boolean wechatCircle=false;
    private IWXAPI api;
    private ProgressDialog progressDialog;

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.share);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.WECHAT_APP_ID);
        wechatCircle=getIntent().getBooleanExtra(AppConstants.WXTIMELINE,false);
        String url=getIntent().getStringExtra(AppConstants.SHARE_URL);
        if(url!=null)
            sendToWeixin(url);


    }

    private void sendToWeixin(String text) {
         progressDialog=ProgressDialog.show(WechatShareActivity.this, null, ResUtil.getString(R.string.tv_loading));

        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.title = "Will be ignored";
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = wechatCircle?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;

        // 调用api接口发送数据到微信
        if(api.sendReq(req))
            progressDialog.cancel();
        finish();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(progressDialog!=null)
                progressDialog.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }
}