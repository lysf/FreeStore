package third.com.snail.trafficmonitor.engine.util.su;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import third.com.snail.trafficmonitor.engine.util.LogWrapper;

/**
 * @author kevin
 * @version 1.0
 * <p/>
 * 命令请求辅助类。默认执行命令等待结果的超时时间为30秒，切记命令请求过程不要放在主线程。
 */
public class CommandHelper {
    private static final String TAG = CommandHelper.class.getSimpleName();

    private static final int DEFAULT_TIMEOUT = 30 * 1000;

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_REQUEST_DENY = 1;
    public static final int CODE_IO_ERROR = -2;
    public static final int CODE_TIMEOUT = -3;
    public static final int CODE_UNKNOWN_ERROR = -4;

    private static String ENV;
    private static String LINE_SEPARATOR;
    private static String PATH_SEPARATOR;

    private static final String[] ENV_SET = new String[]{
            "/system/bin/sh",
            "/system/xbin/sh",
            "/system/sbin/sh"
    };

    /**
     * 需要手动执行初始化，建议在Application中调用该方法。用以初始化CommandHelper运作所需要的运行环境及相关参数。
     */
    public static void init() {
        LINE_SEPARATOR = System.getProperty("line.separator", "\n");
        PATH_SEPARATOR = System.getProperty("path.separator", ":");
        findEnvironment(ENV_SET);
    }

    private static void findEnvironment(String[] set) {
        for (String path : set) {
            File file = new File(path);
            if (file.exists()) {
                ENV = path;
                LogWrapper.d(TAG, "ENV: " + ENV);
                break;
            }
        }
    }

    /**
     * 判断系统是否root过，通过查询系统是否拥有su执行文件确认
     * @return true root过，false尚未root
     */
    public static boolean hasRoot() {
        return findCmd("su");
    }

    /**
     * 判断系统是否有iptables命令，实现原理同{@link #hasRoot()}，如果有iptables就可以
     * 顺利执行网络防火墙的任务
     * @return true有，false没有
     */
    public static boolean hasIptable() {
        return findCmd("iptables");
    }

    public static boolean findCmd(String cmdName) {
        boolean ret = false;
        StringBuilder sb = new StringBuilder();
        runCmd("echo $PATH", sb);
        String[] paths = sb.toString().split(PATH_SEPARATOR);
        for (String p : paths) {
            StringBuilder r = new StringBuilder();
            runCmd("ls " + p, r);
            String[] cmds = r.toString().split(LINE_SEPARATOR);
            for (String c : cmds) {
                if (c.equals(cmdName)) {
                    ret = true;
                    break;
                }
            }
            if (ret) {
                break;
            }
        }
        LogWrapper.d(TAG, "findCmd " + cmdName + " " + ret);
        return ret;
    }

    /**
     * 向用户申请root，一般用来第一次申请root使用，通常情况下会通过root管理软件向用户弹出提示框
     * @return {@link #CODE_SUCCESS}成功，其他失败
     */
    public static int requestRoot() {
        int code = -1;
        try {
            code = runCmdAsRoot("exit 0", null, 60 * 1000);
            LogWrapper.e(TAG, "Request root with result: " + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 以应用用户身份执行shell命令，执行结果放在result中，使用默认超时
     * @param cmd shell命令内容
     * @param result 执行结果
     * @return 命令返回值，通常{@link #CODE_SUCCESS}代表成功
     */
    public static int runCmd(String cmd, @Nullable StringBuilder result) {
        return runCmd(cmd, result, DEFAULT_TIMEOUT);
    }

    /**
     * 以应用用户身份执行shell命令，执行结果放在result中，使用timeout用户执行超时时间，以毫秒ms为单位
     * @param cmd shell命令内容
     * @param result 执行结果
     * @param timeout 超时时间，以毫秒为单位
     * @return 命令返回值，通常{@link #CODE_SUCCESS}代表成功
     */
    public static int runCmd(String cmd, @Nullable StringBuilder result, long timeout) {
        return runCmdInternal(cmd, result, timeout, false);
    }

    /**
     * 使用root身份执行shell命令，执行结果放在result中，使用默认超时。
     * 注意：如果之前没有申请过root或没有永久给应用授权root权限，执行本方法通常会通过用户所安装的root管理
     * 应用向用户弹出提示框。
     * @param cmd shell命令内容
     * @param result 执行结果
     * @return 命令返回值，通常{@link #CODE_SUCCESS}代表成功
     */
    public static int runCmdAsRoot(String cmd, @Nullable StringBuilder result) {
        return runCmdAsRoot(cmd, result, DEFAULT_TIMEOUT);
    }

    /**
     * 使用root身份执行shell命令，执行结果放在result中，使用timeout用户执行超时时间，以毫秒ms为单位
     * @param cmd shell命令内容
     * @param result 执行结果
     * @param timeout 超时时间，以毫秒为单位
     * @return 命令返回值，通常{@link #CODE_SUCCESS}代表成功
     */
    public static int runCmdAsRoot(String cmd, @Nullable StringBuilder result, long timeout) {
        return runCmdInternal(cmd, result, timeout, true);
    }

    /**
     * 以root身份执行多条命令
     * @param result 执行结果
     * @param cmds 命令集
     * @return 命令返回值，通常{@link #CODE_SUCCESS}代表成功
     */
    public static int runCmdsAsRoot(@Nullable StringBuilder result, String... cmds) {
        String cmd = "";
        for (String c : cmds) {
            cmd += (c + ";");
        }
        LogWrapper.d(TAG, "Multi cmd: " + cmd);
        return runCmdInternal(cmd, result, DEFAULT_TIMEOUT, true);
    }

    private static int runCmdInternal(String cmd, StringBuilder result, long timeout, boolean asRoot) {
        CommandExecutor executor = new CommandExecutor(cmd, result, asRoot);
        executor.start();
        try {
            if (timeout > 0) {
                executor.join(timeout);
            } else {
                executor.join();
            }

            if (executor.isAlive()) {
                executor.interrupt();
                executor.cleanup();
                executor.join(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return executor.getExitCode();
    }

    private static final class CommandExecutor extends Thread {
        private String cmd;
        private StringBuilder result;
        private int exitCode = -1;
        private Process executor;
        private boolean asRoot = false;

        public CommandExecutor(String cmd, StringBuilder result, boolean asRoot) {
            this.cmd = cmd;
            this.result = result;
            this.asRoot = asRoot;
        }

        public int getExitCode() {
            return exitCode;
        }

        @Override
        public void run() {
            try {
                OutputStreamWriter osw;
                if (asRoot) {
                    executor = Runtime.getRuntime().exec(ENV + " -c \'su\'");
                } else {
                    executor = Runtime.getRuntime().exec(ENV);
                }
                osw = new OutputStreamWriter(executor.getOutputStream());
                osw.write(cmd);
                if (!cmd.endsWith(LINE_SEPARATOR)) {
                    osw.write(LINE_SEPARATOR);
                }
                osw.flush();
                osw.write("exit" + LINE_SEPARATOR);
                osw.flush();

                char[] buff = new char[1024];
                int count;
                InputStreamReader isr = new InputStreamReader(executor.getInputStream());
                if (result != null) {
                    while ((count = isr.read(buff)) != -1) {
                        result.append(buff, 0, count);
                    }
                }

                isr = new InputStreamReader(executor.getErrorStream());
                if (result != null) {
                    while ((count = isr.read(buff)) != -1) {
                        result.append(buff, 0, count);
                    }
                }
                if (executor != null) {
                    exitCode = executor.waitFor();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (result != null) {
                    result.append(LINE_SEPARATOR).append("Timeout");
                }
                exitCode = CODE_TIMEOUT;
            } catch (IOException e) {
                e.printStackTrace();
                if (result != null) {
                    result.append(LINE_SEPARATOR).append("I/O error");
                }
                exitCode = CODE_IO_ERROR;
            } catch (Exception e) {
                e.printStackTrace();
                if (result != null) {
                    result.append(LINE_SEPARATOR).append("Exception occurred");
                }
                exitCode = CODE_UNKNOWN_ERROR;
            } finally {
                cleanup();
            }
        }

        public synchronized void cleanup() {
            if (executor != null) {
                executor.destroy();
                executor = null;
            }
        }
    }
}
