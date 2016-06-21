package third.com.snail.trafficmonitor.engine.data.bean;

import android.content.Context;

import com.snailgame.cjg.R;

/**
 * Created by kevin on 14-10-10.
 * <p/>
 * link to system/core/include/private/android_filesystem_config.h
 */
public class SystemInsideUID {
    public enum InsideUid {
        AID_ROOT(0),  /* traditional unix root user */
        AID_SYSTEM(1000),  /* system server */
        AID_RADIO(1001),  /* telephony subsystem, RIL */
        AID_BLUETOOTH(1002),  /* bluetooth subsystem */
        AID_GRAPHICS(1003),  /* graphics devices */
        AID_INPUT(1004),  /* input devices */
        AID_AUDIO(1005),  /* audio devices */
        AID_CAMERA(1006),  /* camera devices */
        AID_LOG(1007),  /* log devices */
        AID_COMPASS(1008),  /* compass device */
        AID_MOUNT(1009),  /* mountd socket */
        AID_WIFI(1010),  /* wifi subsystem */
        AID_ADB(1011),  /* android debug bridge (adbd) */
        AID_INSTALL(1012),  /* group for installing packages */
        AID_MEDIA(1013),  /* mediaserver process */
        AID_DHCP(1014),  /* dhcp client */
        AID_SDCARD_RW(1015),  /* external storage write access */
        AID_VPN(1016),  /* vpn system */
        AID_KEYSTORE(1017),  /* keystore subsystem */
        AID_USB(1018),  /* USB devices */
        AID_DRM(1019),  /* DRM server */
        AID_MDNSR(1020),  /* MulticastDNSResponder (service discovery) */
        AID_GPS(1021),  /* GPS daemon */
        AID_UNUSED1(1022),  /* deprecated, DO NOT USE */
        AID_MEDIA_RW(1023),  /* internal media storage write access */
        AID_MTP(1024),  /* MTP USB driver access */
        AID_UNUSED2(1025),  /* deprecated, DO NOT USE */
        AID_DRMRPC(1026),  /* group for drm rpc */
        AID_NFC(1027),  /* nfc subsystem */
        AID_SDCARD_R(1028),  /* external storage read access */
        AID_CLAT(1029),  /* clat part of nat464 */
        AID_LOOP_RADIO(1030),  /* loop radio devices */
        AID_MEDIA_DRM(1031),  /* MediaDrm plugins */
        AID_PACKAGE_INFO(1032),  /* access to installed package details */
        AID_SDCARD_PICS(1033),  /* external storage photos access */
        AID_SDCARD_AV(1034),  /* external storage audio/video access */
        AID_SDCARD_ALL(1035),  /* access all users external storage */
        AID_LOGD(1036),  /* log daemon */
        AID_SHARED_RELRO(1037),  /* creator of shared GNU RELRO files */
        AID_SHELL(2000),  /* adb and debug shell user */
        AID_CACHE(2001),  /* cache access */
        AID_DIAG(2002),  /* access to diagnostic resources */
        AID_NET_BT_ADMIN(3001),  /* bluetooth: create any socket */
        AID_NET_BT(3002),  /* bluetooth: create sco, rfcomm or l2cap sockets */
        AID_INET(3003),  /* can create AF_INET and AF_INET6 sockets */
        AID_NET_RAW(3004),  /* can create raw INET sockets */
        AID_NET_ADMIN(3005),  /* can configure interfaces and routing tables. */
        AID_NET_BW_STATS(3006),  /* read bandwidth statistics */
        AID_NET_BW_ACCT(3007),  /* change bandwidth statistics accounting */
        AID_NET_BT_STACK(3008),  /* bluetooth: access config files */
        AID_EVERYBODY(9997),  /* shared between all apps in the same profile */
        AID_MISC(9998),  /* access to misc storage */
        AID_NOBODY(9999);

        int uid = -1;
        public static void init() {}

        InsideUid(int uid) {
            this.uid = uid;
        }

        public int value() {
            return uid;
        }

        public String valueOf(Context context) {
            switch (uid) {
                case 0:
                    return context.getString(R.string.user_root_0);
                case 1000:
                    return context.getString(R.string.user_system_1000);
                case 1013:
                    return context.getString(R.string.user_media_1013);
            }
            return context.getString(R.string.user_unknown);
        }

        public String getFakePkgName() {
            switch (uid) {
                case 0:
                    return "root";
                case 1000:
                    return "android";
                case 1013:
                    return "media_server";
            }
            return "Unknown";
        }
    }
}
