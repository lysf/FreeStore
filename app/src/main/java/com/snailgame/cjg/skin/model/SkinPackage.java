package com.snailgame.cjg.skin.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pancl on 2015/4/13.
 */
public class SkinPackage implements Serializable {
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    int iSkinVersion;
    String cSize;
    String sPkgName;
    String cApkUrl;
    String cMd5Code;
    String iVersionCode;
    String cVersionName;

    String sStartTime;
    String sEndTime;

    Date startDate;
    Date endDate;

    /**
     * 皮肤起止时间格式化
     * @param time
     * @return
     */
    public static Date getSkinPackageDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getiSkinVersion() {
        return iSkinVersion;
    }

    public String getcSize() {
        return cSize;
    }

    public String getcMd5Code() {
        return cMd5Code;
    }

    public String getcApkUrl() {
        return cApkUrl;
    }

    public String getsPkgName() {
        return sPkgName;
    }

    public String getiVersionCode() {
        return iVersionCode;
    }

    public String getcVersionName() {
        return cVersionName;
    }

    public String getsStartTime() {
        return sStartTime;
    }


    public String getsEndTime() {
        return sEndTime;
    }

    public Date getsStartDate() {
        if (startDate == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            try {
                startDate = simpleDateFormat.parse(sStartTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return startDate;
    }

    public Date getsEndDate() {
        if (endDate == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            endDate = null;
            try {
                endDate = simpleDateFormat.parse(sEndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return endDate;
    }

    @JSONField(name = "iSkinVersion")
    public void setiSkinVersion(int iSkinVersion) {
        this.iSkinVersion = iSkinVersion;
    }

    @JSONField(name = "cSize")
    public void setcSize(String cSize) {
        this.cSize = cSize;
    }

    @JSONField(name = "cMd5Code")
    public void setcMd5Code(String cMd5Code) {
        this.cMd5Code = cMd5Code;
    }

    @JSONField(name = "cApkUrl")
    public void setcApkUrl(String cApkUrl) {
        this.cApkUrl = cApkUrl;
    }

    @JSONField(name = "sPkgName")
    public void setsPkgName(String sPkgName) {
        this.sPkgName = sPkgName;
    }

    @JSONField(name = "iVersionCode")
    public void setiVersionCode(String iVersionCode) {
        this.iVersionCode = iVersionCode;
    }

    @JSONField(name = "cVersionName")
    public void setcVersionName(String cVersionName) {
        this.cVersionName = cVersionName;
    }

    @JSONField(name = "sStartTime")
    public void setsStartTime(String sStartTime) {
        this.sStartTime = sStartTime;
    }

    @JSONField(name = "sEndTime")
    public void setsEndTime(String sEndTime) {
        this.sEndTime = sEndTime;
    }
}
