package com.snailgame.cjg.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.snailgame.cjg.R;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by sunxy on 14-3-26.
 */
public class WechatShare {
    public static final String IMAGE_SHARE_DIR = FileUtil.SD_IMAGE_PATH;
    public static final String IMAGE_NAME = "free_stroe_share.jpeg";
    public static final String GAME_IMAGE_NAME = "free_stroe_share_game.jpeg";
    public static final String ACTIVITY_IMAGE_NAME = "free_stroe_share_activity.jpeg";
    private static final int DESCRIPTION_LENGTH_LIMIT = 1024;

    private static final int THUMB_SIZE = 150;


    public static void share(Context context, String text, boolean timeline) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.WECHAT_APP_ID);

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
        req.scene = timeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        // 调用api接口发送数据到微信
        api.sendReq(req);

    }

    public static void shareWeb(Context context, String title, String url, String des, Bitmap bitmap, boolean timeline) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.WECHAT_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        if (TextUtils.isEmpty(des))
            msg.description = "";
        else if (des.length() > DESCRIPTION_LENGTH_LIMIT)
            msg.description = des.substring(0, DESCRIPTION_LENGTH_LIMIT - 5) + "...";
        else
            msg.description = des;

        Bitmap thumb;
        if (bitmap != null)
            thumb = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        else {
            thumb = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
        }
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = timeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);


    }

//    public static void shareImage(Context context) {
//        // 通过WXAPIFactory工厂，获取IWXAPI的实例
//        IWXAPI api = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID, false);
//        // 将该app注册到微信
//        api.registerApp(Constants.WECHAT_APP_ID);
//
//
//        File file = ComUtil.getShareImageFile(IMAGE_NAME);
//
//        if (!file.exists()) {
//            ToastUtils.showMsgLong(context, context.getResources().getString(R.string.no_image_to_share));
//            return;
//        }
//
//        WXImageObject imgObj = new WXImageObject();
//        imgObj.setImagePath(file.getAbsolutePath());
//
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = imgObj;
//        msg.description = SharedPreferencesUtil.getInstance().getShareDes();
//        if (TextUtils.isEmpty(msg.description))
//            msg.description = context.getResources().getString(R.string.share_pic);
//
//        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
//        if (bmp == null) {
//            ToastUtils.showMsgLong(context, context.getResources().getString(R.string.decode_bmp_fail));
//            return;
//        }
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();
//        msg.thumbData = bmpToByteArray(thumbBmp, true);
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("img");
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        api.sendReq(req);
//
//    }


//    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
//       /*if (needRecycle) {
//            bmp.recycle();
//        }*/
//
//        byte[] result = output.toByteArray();
//        try {
//            output.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


}
