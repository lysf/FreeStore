package com.snailgame.cjg.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.snailgame.cjg.BuildConfig;
import com.snailgame.cjg.global.FreeStoreApp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 渠道号
 * Created by xixh on 2014/12/12.
 */
public class ChannelUtil {
    public static final String CHANNEL_KEY = "channel";
    public static final String UPDATE_CHANNEL = "111";      // 官网更新渠道号

    /**
     * 获取channel ID
     *
     * @return
     */
    public static String getChannelID() {
        String channelID = SharedPreferencesUtil.getInstance().getChannelID();

        if (TextUtils.isEmpty(channelID)) {
            Context context = FreeStoreApp.getContext();
            channelID = getChannelFromApk(context, CHANNEL_KEY);
            if (!TextUtils.isEmpty(channelID))
                SharedPreferencesUtil.getInstance().setChannelID(channelID);
        }
        return channelID;
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @param channelKey
     * @return
     */
    private static String getChannelFromApk(Context context, String channelKey) {
        if (BuildConfig.DEBUG)
            return "10003";

        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        String channel = "";
        if (split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }

    /**
     * 检测是否是覆盖安装
     */
    public static void coverInstallCheck() {
        String channelID = SharedPreferencesUtil.getInstance().getChannelID();
        if (TextUtils.isEmpty(channelID))
            return;

        String apkChannelId = getChannelFromApk(FreeStoreApp.getContext(), CHANNEL_KEY);
        if (TextUtils.isEmpty(apkChannelId) || channelID.equals(apkChannelId))
            return;

        SharedPreferencesUtil.getInstance().setChannelID(apkChannelId);
        if (!apkChannelId.equals(UPDATE_CHANNEL))
            SharedPreferencesUtil.getInstance().setShouldGuideShow(true);

    }
}
