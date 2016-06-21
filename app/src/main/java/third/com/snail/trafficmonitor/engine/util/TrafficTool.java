package third.com.snail.trafficmonitor.engine.util;

import android.content.Context;
import android.net.TrafficStats;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.FileTrafficObject;
import third.com.snail.trafficmonitor.engine.data.bean.FlowInfo;
import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.data.table.NetworkDao;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;

/**
 * Created by kevin on 14-9-23.
 * <p/>
 * TrafficTool
 */
public class TrafficTool {
    private static final String TAG = TrafficTool.class.getSimpleName();

    private static final String PROC_PREFIX = "/proc/uid_stat/";
    private static final String PROC_RX_ENDLESS = "/tcp_rcv";
    private static final String PROC_TX_ENDLESS = "/tcp_snd";

    private static final String BYTE_SPEED = "B/s";
    private static final String KB_SPEED = "KB/s";
    private static final String MB_SPEED = "MB/s";
    private static final String GB_SPEED = "GB/s";

    public static final String BYTE_UNIT = "B";
    public static final String KB_UNIT = "KB";
    public static final String MB_UNIT = "MB";
    public static final String GB_UNIT = "GB";

    public static final long KB_IN_BYTES = 1024;
    public static final long MB_IN_BYTES = KB_IN_BYTES * 1024;
    public static final long GB_IN_BYTES = MB_IN_BYTES * 1024;

    /**
     * 通过系统接口获取每一个应用（Uid）的接收流量，但是不精确
     *
     * @param uid
     */
    public static long getUidRxBytes(int uid) {
        long bytes = -1;
        bytes = TrafficStats.getUidRxBytes(uid);
        if (bytes == TrafficStats.UNSUPPORTED || bytes <= 0) {
            bytes = getUidRxBytesByProc(uid);
        }
        return bytes;
    }

    /**
     * 通过系统接口获取每一个应用（Uid）的发送流量，但是不精确
     *
     * @param uid
     */
    public static long getUidTxBytes(int uid) {
        long bytes = -1;
        bytes = TrafficStats.getUidTxBytes(uid);
        if (bytes == TrafficStats.UNSUPPORTED || bytes <= 0) {
            bytes = getUidTxBytesByProc(uid);
        }
        return bytes;
    }

    /**
     * 直接读取系统stats文件的流量数据，此方法和系统的统计流量方法一样，解析出的stats文件是一个表状数据。
     *
     * @return 可能为空
     */
    @Nullable
    public static SparseArray<FileTrafficObject> getFileTrafficData() {
        StringBuilder stringBuilder = new StringBuilder();
        if (CommandHelper.runCmd("cat /proc/net/xt_qtaguid/stats", stringBuilder) != 0) {
            return null;
        }
        SparseArray<FileTrafficObject> stringSparseArray = new SparseArray<>();
        FileTrafficObject fileTrafficObject;
        String stringResult = stringBuilder.toString();//返回数据为表状数据
        LogWrapper.d("stringResult", stringResult);
        // 以换行符为key分割表型字符串，分割出来的即时每一条不同的使用流量的值
        String[] sectionStrings = stringResult.split("\n");
        for (int i = 1; i < sectionStrings.length; i++) {// 去掉第一行的表字段声明
            String sectionString = sectionStrings[i];
            String[] elementStrings = sectionString.split(" ");//以空格分割，得到每个字段的值
            String acct_tag_hex = elementStrings[2];
            if (acct_tag_hex.equals("0x0")) {//如果acct_tag_hex不为0x0则舍弃，是重复数据
                fileTrafficObject = new FileTrafficObject();
                int cnt_set = Integer.parseInt(elementStrings[4]);
                String iface = elementStrings[1];
                String uid = elementStrings[3];
                long rxBytes = Long.parseLong(elementStrings[5]);
                long txBytes = Long.parseLong(elementStrings[7]);
                fileTrafficObject.setUid(uid);
                fileTrafficObject.setIface(iface);
                int key = uid.hashCode() + iface.hashCode();//以uid和iface的哈希值相加作为key
                if (stringSparseArray.get(key) == null) {
                    fileTrafficObject.setRxBytes(rxBytes);
                    fileTrafficObject.setTxBytes(txBytes);
                    if (cnt_set == 1) {//区分前台后台的字段cnt_set 1表示前台
                        fileTrafficObject.setForegroundBytes(rxBytes + txBytes);
                    } else {
                        fileTrafficObject.setBackgroundBytes(rxBytes + txBytes);
                    }
                } else {
                    FileTrafficObject existFileTrafficObject = stringSparseArray.get(key);
                    if (cnt_set == 1) {//区分前台后台的字段cnt_set 1表示前台
                        fileTrafficObject.setForegroundBytes(existFileTrafficObject.getForegroundBytes() +
                                rxBytes + txBytes);
                        fileTrafficObject.setBackgroundBytes(existFileTrafficObject.getBackgroundBytes());
                    } else {
                        fileTrafficObject.setBackgroundBytes(existFileTrafficObject.getBackgroundBytes() +
                                rxBytes + txBytes);
                        fileTrafficObject.setForegroundBytes(existFileTrafficObject.getForegroundBytes());
                    }
                    fileTrafficObject.setRxBytes(existFileTrafficObject.getRxBytes() + rxBytes);
                    fileTrafficObject.setTxBytes(existFileTrafficObject.getTxBytes() + txBytes);
                }
                stringSparseArray.put(key, fileTrafficObject);
            }
        }
        return stringSparseArray;
    }


    public static long getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    public static long getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }

    private static DecimalFormat sDf = new DecimalFormat("0.00");

    public static String getSpeed(long diff) {
        if (diff / GB_IN_BYTES >= 1) {
            return "" + sDf.format((double) diff / GB_IN_BYTES) + GB_SPEED;
        } else if (diff / MB_IN_BYTES >= 1) {
            return "" + sDf.format((double) diff / MB_IN_BYTES) + MB_SPEED;
        } else if (diff / KB_IN_BYTES >= 1) {
            return "" + sDf.format((double) diff / KB_IN_BYTES) + KB_SPEED;
        }
        return "" + diff + BYTE_SPEED;
    }

    public static String getCost(long bytes) {
        if (bytes / GB_IN_BYTES >= 1) {
            return "" + sDf.format((double) bytes / GB_IN_BYTES) + GB_UNIT;
        } else if (bytes / MB_IN_BYTES >= 1) {
            return "" + sDf.format((double) bytes / MB_IN_BYTES) + MB_UNIT;
        } else if (bytes / KB_IN_BYTES >= 1) {
            return "" + sDf.format((double) bytes / KB_IN_BYTES) + KB_UNIT;
        }
        return "" + bytes + BYTE_UNIT;
    }

    public static FlowInfo getCostLong(long bytes) {
        FlowInfo flowInfo = new FlowInfo();
        if (bytes / (1024 * 1024 * 1024) >= 1) {
            flowInfo.setBytes(sDf.format((float) bytes / GB_IN_BYTES));
            flowInfo.setBytesType(GB_UNIT);//GB
        } else if (bytes / (1024 * 1024) >= 1) {
            flowInfo.setBytes(sDf.format((float) bytes / MB_IN_BYTES));
            flowInfo.setBytesType(MB_UNIT);//MB
        } else if (bytes / 1024 >= 1) {
            flowInfo.setBytes(sDf.format((float) bytes / KB_IN_BYTES));
            flowInfo.setBytesType(KB_UNIT);//KB
        } else {
            flowInfo.setBytes(String.valueOf((float) bytes));
            flowInfo.setBytesType(BYTE_UNIT);//B
        }
        return flowInfo;
    }

    private static long getUidTxBytesByProc(int uid) {
        RandomAccessFile file = null;
        String path = PROC_PREFIX + uid + PROC_TX_ENDLESS;
        long txTraffic = -1;
        try {
            file = new RandomAccessFile(path, "r");
            txTraffic = Long.parseLong(file.readLine());
        } catch (IOException e) {
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return txTraffic;
    }

    private static long getUidRxBytesByProc(int uid) {
        RandomAccessFile file = null;
        String path = PROC_PREFIX + uid + PROC_RX_ENDLESS;
        long rxTraffic = -1;
        try {
            file = new RandomAccessFile(path, "r");
            rxTraffic = Long.parseLong(file.readLine());
        } catch (IOException e) {
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return rxTraffic;
    }

    /**
     * 通过网络的iface反射获取改Iface的上传和下载流量，iface只会区分移动和wifi，移动的一般为rmnet_data0,wifi一般为wlan0
     *
     * @param context
     * @param mobileData
     * @param wifiData
     */
    public static void getIfaceTotal(Context context, long[] mobileData, long[] wifiData) throws SQLException {
        if (mobileData == null || wifiData == null
                || mobileData.length != 2 || wifiData.length != 2) {
            throw new IllegalArgumentException("mobileData and wifiData must be long array and length is 2.");
        }
        List<Network> networkList = new NetworkDao(context).queryForAll();
        long wifiRx = -1, wifiTx = -1;
        long mobileRx = -1, mobileTx = -1;
        if (networkList != null) {
            //反射获取，可能为-1
            for (Network n : networkList) {
                if (n.getNetworkType() == Network.NetworkType.WIFI && wifiRx == -1 && wifiTx == -1) {
                    wifiRx += TrafficTool.getIfaceRxBytes(n.getIface());
                    wifiTx += TrafficTool.getIfaceTxBytes(n.getIface());
                } else {
                    if (mobileRx == -1 && mobileTx == -1) {
                        mobileRx += TrafficTool.getIfaceRxBytes(n.getIface());
                        mobileTx += TrafficTool.getIfaceTxBytes(n.getIface());
                    }
                }
            }
        }
        wifiData[0] = wifiRx;
        wifiData[1] = wifiTx;
        mobileData[0] = mobileRx;
        mobileData[1] = mobileTx;
    }

    public static long getIfaceRxBytes(String iface) {
        long result = -1;
        try {
            Method method = TrafficStats.class.getDeclaredMethod("getRxBytes", String.class);
            method.setAccessible(true);
            result = (Long) method.invoke(null, iface);
        } catch (Exception ignore) {
        }
        return result;
    }

    public static long getIfaceTxBytes(String iface) {
        long result = -1;
        try {
            Method method = TrafficStats.class.getDeclaredMethod("getTxBytes", String.class);
            method.setAccessible(true);
            result = (Long) method.invoke(null, iface);
        } catch (Exception ignore) {
        }
        return result;
    }

    public static String[] getMobileIfaces() {
        String[] result = new String[0];
        try {
            Method method = TrafficStats.class.getDeclaredMethod("getMobileIfaces");
            method.setAccessible(true);
            result = (String[]) method.invoke(null);
        } catch (NoSuchMethodException e) {
            LogWrapper.e(TAG, "TrafficStats.getMobileIfaces method not found");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LogWrapper.e(TAG, "TrafficStats.getMobileIfaces method invoke error");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LogWrapper.e(TAG, "TrafficStats.getMobileIfaces method access error");
            e.printStackTrace();
        } catch (Exception e) {
            LogWrapper.e(TAG, "TrafficStats.getMobileIfaces UNKNOWN error occurred");
            e.printStackTrace();
        }
        return result;
    }

}
