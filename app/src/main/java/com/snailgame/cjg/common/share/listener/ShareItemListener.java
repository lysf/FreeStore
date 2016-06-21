package com.snailgame.cjg.common.share.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.PersistentVar;
import com.snailgame.cjg.common.share.sina.SinaShareActivity;
import com.snailgame.cjg.common.widget.ShareDialog;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.wxapi.WechatShare;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by sunxy on 14-5-26.
 */
public class ShareItemListener implements AdapterView.OnItemClickListener {
    private Context context;
    private Bitmap mSharedIcon;
    private String shareTitle, shareUrl, content;
    private ShareDialog shareDialog;
    private String iconUrl;
    private String gameId;
    private String appName;
    private LoadShareImageTask loadShareImageTask;
    //是否是从应用详情页面分享应用
    private ShareType shareType;

    private boolean isWechatShare;

    private enum ShareType {
        INVITED, NORMAL_WEB, DETAIL_SHARE
    }

    /**
     * 邀请分享
     */
    public ShareItemListener(Context context, ShareDialog shareDialog) {
        this.context = context;
        this.shareTitle = PersistentVar.getInstance().getSystemConfig().getShareTitle();
        this.shareUrl = PersistentVar.getInstance().getSystemConfig().getShareUrl();
        this.content = context.getString(R.string.invite_friend_content);
        this.shareDialog = shareDialog;
        this.shareType = ShareType.INVITED;
    }


    /**
     * 普通网页分享
     */
    public ShareItemListener(Context context, String shareTitle, String content, String shareUrl, Bitmap shareIcon, ShareDialog shareDialog, String iconUrl) {
        this.context = context;
        this.mSharedIcon = shareIcon;
        this.shareTitle = shareTitle;
        this.shareUrl = shareUrl;
        this.content = content;
        this.iconUrl = iconUrl;
        this.shareDialog = shareDialog;
        this.shareType = ShareType.NORMAL_WEB;
    }


    /**
     * 应用详情分享应用
     */
    public ShareItemListener(Context context, String shareTitle, String content, String shareUrl, Bitmap shareIcon, ShareDialog shareDialog, String iconUrl, String gameId, String appName) {
        this.context = context;
        this.mSharedIcon = shareIcon;
        this.shareTitle = shareTitle;
        this.shareUrl = shareUrl;
        this.content = content;
        this.shareDialog = shareDialog;
        this.iconUrl = iconUrl;
        this.gameId = gameId;
        this.appName = appName;
        this.shareType = ShareType.DETAIL_SHARE;
    }

    public LoadShareImageTask getLoadImageTask() {
        return loadShareImageTask;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case ShareDialog.MICRO_MSG:
                //分享的图片如果没有就重新下载
                isWechatShare = true;
                if (shareType == ShareType.INVITED) {
                    wechatShare(true, null);
                } else {
                    if (mSharedIcon != null && !mSharedIcon.isRecycled()) {
                        wechatShare(true, mSharedIcon);
                    } else {
                        if (loadShareImageTask != null)
                            loadShareImageTask.cancel(true);
                        loadShareImageTask = new LoadShareImageTask(true);
                        if (!TextUtils.isEmpty(iconUrl))
                            new LoadShareImageTask(true).execute(iconUrl);
                    }
                }
                break;
            case ShareDialog.WECHAT_FRIEND:
                isWechatShare = true;
                if (shareType == ShareType.INVITED) {
                    wechatShare(false, null);
                } else {
                    if (mSharedIcon != null && !mSharedIcon.isRecycled()) {
                        wechatShare(false, mSharedIcon);
                    } else {
                        if (loadShareImageTask != null)
                            loadShareImageTask.cancel(true);
                        loadShareImageTask = new LoadShareImageTask(false);
                        if (!TextUtils.isEmpty(iconUrl))
                            new LoadShareImageTask(false).execute(iconUrl);
                    }
                }
                break;
            case ShareDialog.WEIBO:
                isWechatShare = false;
                if (shareType == ShareType.INVITED) {
                    weiboShare();
                } else {
                    if (mSharedIcon != null && !mSharedIcon.isRecycled()) {
                        weiboShare();
                    } else {
                        if (loadShareImageTask != null)
                            loadShareImageTask.cancel(true);
                        loadShareImageTask = new LoadShareImageTask(false);
                        if (!TextUtils.isEmpty(iconUrl))
                            new LoadShareImageTask(false).execute(iconUrl);
                    }
                }
                break;
            case ShareDialog.MORE:
                if (shareType == ShareType.INVITED) {
                    ComUtil.showIntentChoser(context, content + " " + shareUrl);
                } else {
                    ComUtil.showIntentChoser(context, shareTitle + " " + shareUrl);
                }
                break;

            default:
                break;
        }
        if (shareDialog != null)
            shareDialog.dismiss();
    }

    private void wechatShare(boolean timeline, Bitmap bitmap) {
        if (context == null) {
            return;
        }
        if (WXAPIFactory.createWXAPI(context, com.snailgame.cjg.wxapi.Constants.WECHAT_APP_ID, false).isWXAppInstalled()) {
            if (shareType == ShareType.DETAIL_SHARE) {
                GlobalVar.getInstance().setShareAppId(gameId);
                GlobalVar.getInstance().setShareGameName(appName);
            } else if (shareType == ShareType.INVITED) {
                GlobalVar.getInstance().setShareMenu(true);
            }
            WechatShare.shareWeb(context, shareTitle, shareUrl, content, bitmap, timeline);
        } else {
            ToastUtils.showMsgLong(context, R.string.no_weixin);
        }
    }

    private void weiboShare() {
        if (context == null) {
            return;
        }
        if (shareType == ShareType.DETAIL_SHARE) {
            context.startActivity(SinaShareActivity.newIntent(context, shareUrl, gameId, appName, mSharedIcon));
        } else if (shareType == ShareType.NORMAL_WEB) {
            context.startActivity(SinaShareActivity.newIntent(context, shareUrl, shareTitle, mSharedIcon));
        } else {
            context.startActivity(SinaShareActivity.newIntent(context, shareUrl));
        }
    }


    public class LoadShareImageTask extends AsyncTask<String, Void, Bitmap> {
        private boolean timeline;

        public LoadShareImageTask(boolean timeline) {
            this.timeline = timeline;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ToastUtils.showMsgLong(context, R.string.loading_game_icon);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                mSharedIcon = BitmapFactory.decodeStream(new URL(params[0]).openStream());
                mSharedIcon = ComUtil.addWhiteBackground(mSharedIcon);
                if (shareType == ShareType.DETAIL_SHARE) {
                    BitmapUtil.savePicNoCompress(mSharedIcon, ComUtil.getShareImageFile(WechatShare.GAME_IMAGE_NAME).getAbsolutePath());
                } else {
                    BitmapUtil.savePicNoCompress(mSharedIcon, ComUtil.getShareImageFile(WechatShare.ACTIVITY_IMAGE_NAME).getAbsolutePath());
                }
            } catch (IOException e) {
                LogUtils.e(e.getMessage());
            }
            if (mSharedIcon == null) {
                mSharedIcon = BitmapFactory.decodeResource(ResUtil.getResources(), R.drawable.notification);
                mSharedIcon = ComUtil.addWhiteBackground(mSharedIcon);
            }
            return mSharedIcon;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null) {
                ToastUtils.showMsgLong(context, R.string.loading_share_image_fail);
                return;
            }
            if (context == null) {
                ToastUtils.showMsgLong(context, R.string.loading_share_image_fail);
                return;
            }
            if (isWechatShare) {
                wechatShare(timeline, result);
            } else {
                weiboShare();
            }
        }
    }
}
