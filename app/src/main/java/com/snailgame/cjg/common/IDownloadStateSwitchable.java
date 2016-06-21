package com.snailgame.cjg.common;

/**
 * 解耦下载对象的状态和后台逻辑
 * 下载对象实现可转换下载状态的接口，提供后台逻辑调用
 * Created by pancl on 2014/10/23.
 */
public interface IDownloadStateSwitchable {
    /**
     * 转换到 下载 状态
     */
    void switchToDownload();

    /**
     * 转换到 等待 状态
     */
    void switchToWaiting();

    /**
     * 转换到 等待 WIFI环境下载状态
     */
    void switchToWaitingForWifi();

    /**
     * 转换到 暂停 状态
     */
    void switchToPause();

    /**
     * 转换到 继续 状态
     */
    void switchToContinue();

    /**
     * 转换到 安装 状态
     */
    void switchToInstall();

    /**
     * 转换到 打开 状态
     */
    void switchToOpen();

    /**
     * 转换到 升级 状态
     */
    void switchToUpgrade();

    /**
     * 转换到 重载 状态
     */
    void switchToRedownload();

    /**
     * 转换到 安装中 状态
     */
    void switchToInstalling();

    /**
     * 转换到 验证中 状态
     */
    void switchToPatching();

    /**
     * 转换到 尚在准备 状态
     */
    void switchToNotReady();

    /**
     * 转换到 正在切换中 状态
     */
    void switching();

    /**
     * 转换到 状态切换完成 状态
     */
    void switched();

    /**
     * 保存状态值
     *
     * @param state 状态值
     */
    void setState(int state);

    /**
     * 保存状态值
     *
     * @param isShow 是否显示免标志
     */
    void setFlowFreeView(boolean isShow);

    /**
     * 设置 应用预约状态
     */
    void setAppointmentState();

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    int getState();
}
