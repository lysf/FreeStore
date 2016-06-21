package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.SharedPreferencesUtil;

/**
 * Created by taj on 14-3-17.
 */
public class FlowFreeView extends LinearLayout {
    public View img_freestate;
    private Context mContext;


    public FlowFreeView(Context context) {
        super(context);
        mContext = context;
    }

    public FlowFreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.img_freestate = findViewById(R.id.img_freestate);
    }

    private void resetFlowFreeUI() {
        img_freestate.setVisibility(View.GONE);
    }

    public void setFlowFreeUI(String flowFree) {
        resetFlowFreeUI();
        if (flowFree == null)
            return;

        if (flowFree.length() < 4)
            return;

        if (flowFree.length() > 4)
            flowFree = flowFree.substring(0, 4);

        if (IdentityHelper.isLogined(mContext) && AccountUtil.isFree()) {
            switch (flowFree) {
                case AppInfo.FREE_DOWNLOAD:
                case AppInfo.FREE_DOWNLOAD_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_free_download);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_PLAY:
                case AppInfo.FREE_PLAY_AREA:
                    //如果是免卡用户 不支持免玩
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setVisibility(View.GONE);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_play);
                        img_freestate.setVisibility(VISIBLE);
                    }
                    break;
                case AppInfo.FREE_DOWNLOAD_PLAY:
                case AppInfo.FREE_DOWNLOAD_PLAY_AREA:
                    //如果是免卡用户 免下玩 改为免下
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_download);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_download_play);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_UPGRADE:
                case AppInfo.FREE_UPGRADE_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_free_update);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_DOWNLOAD_UPGRADE:
                case AppInfo.FREE_DOWNLOAD_UPGRADE_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_free_downoad_update);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_PLAY_UPGRADE:
                case AppInfo.FREE_PLAY_UPGRADE_AREA:
                    //如果是免卡用户 免玩更 改为免更
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_update);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_play_update);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_DOWNLOAD_PLAY_UPGRADE:
                case AppInfo.FREE_DOWNLOAD_PLAY_UPGRADE_AREA:
                    //如果是免卡用户 免下玩更 改为免下更
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_downoad_update);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_free_download_play_update);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                default:
                    resetFlowFreeUI();
                    break;
            }
        }
    }

    public void setGameDetailFlowFreeUI(String flowFree) {
        resetFlowFreeUI();
        if (flowFree == null)
            return;

        if (flowFree.length() < 4)
            return;

        if (flowFree.length() > 4)
            flowFree = flowFree.substring(0, 4);

        if (IdentityHelper.isLogined(mContext) && AccountUtil.isFree()) {
            switch (flowFree) {
                case AppInfo.FREE_DOWNLOAD:
                case AppInfo.FREE_DOWNLOAD_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_detail_free_download);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_PLAY:
                case AppInfo.FREE_PLAY_AREA:
                    //如果是免卡用户 不支持免玩
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setVisibility(View.GONE);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_play);
                        img_freestate.setVisibility(VISIBLE);
                    }
                    break;
                case AppInfo.FREE_DOWNLOAD_PLAY:
                case AppInfo.FREE_DOWNLOAD_PLAY_AREA:
                    //如果是免卡用户 免下玩 改为免下
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_download);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_download_play);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_UPGRADE:
                case AppInfo.FREE_UPGRADE_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_detail_free_update);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_DOWNLOAD_UPGRADE:
                case AppInfo.FREE_DOWNLOAD_UPGRADE_AREA:
                    img_freestate.setBackgroundResource(R.drawable.ic_detail_free_downoad_update);
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_PLAY_UPGRADE:
                case AppInfo.FREE_PLAY_UPGRADE_AREA:
                    //如果是免卡用户 免玩更 改为免更
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_update);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_play_update);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                case AppInfo.FREE_DOWNLOAD_PLAY_UPGRADE:
                case AppInfo.FREE_DOWNLOAD_PLAY_UPGRADE_AREA:
                    //如果是免卡用户 免下玩更 改为免下更
                    if (SharedPreferencesUtil.getInstance().isSnailPhoneNumber()) {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_downoad_update);
                    } else {
                        img_freestate.setBackgroundResource(R.drawable.ic_detail_free_download_play_update);
                    }
                    img_freestate.setVisibility(VISIBLE);
                    break;
                default:
                    resetFlowFreeUI();
                    break;
            }
        }
    }


    public void setFlowFreeUI(AppInfo appInfo) {
        if (appInfo == null) {
            resetFlowFreeUI();
            return;
        }

        if (appInfo.getiFreeArea() == AppConstants.FREE_AREA_IN) {
            resetFlowFreeUI();
            img_freestate.setBackgroundResource(R.drawable.ic_free_download);
            img_freestate.setVisibility(VISIBLE);
        } else {
            setFlowFreeUI(appInfo.getcFlowFree());
        }

    }

    public void setGameDetailFlowFreeUI(AppInfo appInfo) {
        if (appInfo == null) {
            resetFlowFreeUI();
            return;
        }

        if (appInfo.getiFreeArea() == AppConstants.FREE_AREA_IN) {
            resetFlowFreeUI();
            img_freestate.setBackgroundResource(R.drawable.ic_detail_free_download);
            img_freestate.setVisibility(VISIBLE);
        } else {
            setGameDetailFlowFreeUI(appInfo.getcFlowFree());
        }

    }
}
