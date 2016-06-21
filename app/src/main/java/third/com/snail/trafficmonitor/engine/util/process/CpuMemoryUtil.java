package third.com.snail.trafficmonitor.engine.util.process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lic on 2014/12/08
 * 计算cpu占有率
 */
public class CpuMemoryUtil {
    private double startupTime;

    public CpuMemoryUtil() {
        getstarttime();
    }

    /**
     * 获取内存占用率
     *
     * @return
     */
    public static long getmemorypercent() {
        long l1 = 40000;
        try {
            InputStream in = Runtime.getRuntime().exec("cat proc/meminfo").getInputStream();
            StringBuilder builder = new StringBuilder();
            byte[] b = new byte[1024];
            while (in.read(b) != -1) {
                builder.append(new String(b));
            }
            String[] info = builder.toString().split("[\n]+");
            l1 = Long.parseLong(info[0].trim().split("[ ]+")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l1;
    }

    /**
     * 通过pid获取cpu占用率
     *
     * @return
     */
    public int getProcessCpu(int paramInt) {
        int i = 0;
        try {
            String str = "/proc/" + paramInt + "/stat";
            FileReader localFileReader = new FileReader(str);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 1024);
            String[] arrayOfString = localBufferedReader.readLine().trim().split("\\s+");
            double d1 = this.startupTime;
            double d2 = Long.parseLong(arrayOfString[21]) * 10L;
            double d3 = d1 - d2;
            long l1 = Long.parseLong(arrayOfString[13]);
            long l2 = Long.parseLong(arrayOfString[14]);
            long l3 = l1 + l2;
            long l4 = Long.parseLong(arrayOfString[15]);
            long l5 = l3 + l4;
            long l6 = Long.parseLong(arrayOfString[16]);
            long l7 = (l5 + l6) * 10L;
            i = (int) (100L * l7 * 100L / d3);
            localBufferedReader.close();
            localFileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 获取cpu信息
     *
     * @return
     */
    public String getCpu() {
        ProcessBuilder cmd;
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result = result + new String(re);
                return result.toString();
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 通过pid获取当前进程占用内存
     *
     * @return
     */
    public long getProcessMem(int paramInt) {
        long i = 0;
        try {
            String str = "/proc/" + paramInt + "/statm";
            FileReader localFileReader = new FileReader(str);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 1024);
            i = Long.parseLong(localBufferedReader.readLine().split(
                    "\\s+")[5]);
            localFileReader.close();
            localBufferedReader.close();

        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    /**
     * 获取系统启动到现在的时间
     */
    public void getstarttime() {
        try {
            FileReader localFileReader = new FileReader("/proc/uptime");
            double d1 = Double.parseDouble(new BufferedReader(localFileReader, 1024).readLine().split("\\s+")[0]) * 1000.0D;
            startupTime = d1;
            localFileReader.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
