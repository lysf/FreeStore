package third.com.snail.trafficmonitor.engine.util.process;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import third.com.snail.trafficmonitor.engine.data.bean.BasicProcessBean;
import third.com.snail.trafficmonitor.engine.data.bean.ProcessBean;
import third.com.snail.trafficmonitor.engine.util.LogWrapper;
import third.com.snail.trafficmonitor.engine.util.su.CommandHelper;


/**
 * Created by lic on 2014/12/08
 * 获取进程信息和列表的工具类
 */
public class ProcessUtil {
    private Context context;
    private boolean hasRoot;
    private ActivityManager am;
    private ActivityManager.MemoryInfo memory;

    public ProcessUtil(Context context) {
        this.context = context;
        ImportantProcessUtil.initprocess(context);
        hasRoot = CommandHelper.hasRoot();
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        memory = new ActivityManager.MemoryInfo();
    }

    /**
     * 获取基本的进程列表并且排序
     *
     * @return
     */
    private List<BasicProcessBean> getprocess() {
        List<RunningAppProcessInfo> runlist = new ArrayList<>();
        List<BasicProcessBean> beanlist = new ArrayList<>();
        ActivityManager activitymanager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getApplicationContext().getPackageManager();
        //获取当前正在运行的进程
        runlist.addAll(activitymanager.getRunningAppProcesses());
        BasicProcessBean basic;
        for (int i = 0; i < runlist.size(); i++) {
            basic = new BasicProcessBean();
            for (int j = 0; j < runlist.get(i).pkgList.length; j++) {
                try {
                    String processName = runlist.get(i).processName;
                    //判断当前的进程是否比较重要，重要的话就不加入可停止的进程列表
                    if (ImportPreference.isImportant(processName)) {
                        LogWrapper.e("Important_ProcessName：" + processName + "Application_Name：" + pm.getApplicationLabel(pm.getApplicationInfo(runlist.get(i).pkgList[0],
                                PackageManager.GET_META_DATA)));
                        break;
                    }
                    if (runlist.get(i).pkgList.length == 1) {
                        basic.setApplicationname("" + pm.getApplicationLabel(pm.getApplicationInfo(runlist.get(i).pkgList[0],
                                PackageManager.GET_META_DATA)));
//                        basic.setIcon(pm.getApplicationIcon(pm.getApplicationInfo(runlist.get(i).pkgList[0],
//                                PackageManager.GET_META_DATA)));
                        basic.setPid(runlist.get(i).pid);
                        basic.setProcessname(runlist.get(i).pkgList[0]);
                        basic.setImportance(runlist.get(i).importance);
                        beanlist.add(basic);
                    } else {
                        if (runlist.get(i).pkgList[j].startsWith(runlist.get(i).processName)) {
                            basic.setApplicationname("" + pm.getApplicationLabel(pm.getApplicationInfo(runlist.get(i).pkgList[j],
                                    PackageManager.GET_META_DATA)));
//                            basic.setIcon(pm.getApplicationIcon(pm.getApplicationInfo(runlist.get(i).pkgList[j],
//                                    PackageManager.GET_META_DATA)));
                            basic.setPid(runlist.get(i).pid);
                            basic.setProcessname(runlist.get(i).pkgList[j]);
                            basic.setImportance(runlist.get(i).importance);
                            beanlist.add(basic);
                        }
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        //对进程列表的重要程度的做一个从高到低的排序
        for (int m = 0; m < beanlist.size(); m++) {
            BasicProcessBean b;
            BasicProcessBean b2;
            for (int j = 0; j < beanlist.size() - m - 1; j++) {
                if (beanlist.get(j).getImportance() < beanlist.get(j + 1).getImportance()) {
                    b = beanlist.get(j);
                    b2 = beanlist.get(j + 1);
                    beanlist.set(j, b2);
                    beanlist.set(j + 1, b);
                }
            }
        }
        return beanlist;
    }

    /**
     * 获取正在运行的进程列表
     *
     * @return
     */
    public List<ProcessBean> getRunningProcess() {
        List<BasicProcessBean> beanList;
        List<ProcessBean> processlist = new ArrayList<>();
        beanList = getprocess();
        CpuMemoryUtil cpu = new CpuMemoryUtil();
        ProcessBean p;
        for (BasicProcessBean b : beanList) {
            p = new ProcessBean();
//            p.setIcon(b.getIcon());
            //判断当前进程可不可以被杀死
            if (JudgeKill.iskill(b.getProcessname(), context)) {
                if (b.getImportance() == 300 || b.getImportance() == 400 || b.getImportance() == 500) {
                    p.setIschecked(true);
                } else {
                    p.setIschecked(false);
                }
            } else {
                p.setIschecked(false);
            }
            p.setMemory(cpu.getProcessMem(b.getPid()));
            p.setProcessname(b.getProcessname());
            p.setPid(b.getPid());
            p.setApplicationname(b.getApplicationname());
            p.setCpupercent("" + cpu.getProcessCpu(b.getPid()) / 100.0D + "%");
            processlist.add(p);
        }
        return processlist;
    }

    /**
     * 杀掉进程列表中被选中的进程
     */
    public void killProgress(List<ProcessBean> processBeans) {
        if (hasRoot) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] strings = new String[processBeans.size()];
            for (int i = 0; i < strings.length; i++) {
                if (processBeans.get(i).getIschecked()) {
                    LogWrapper.d("kill process:" + processBeans.get(i).getProcessname() + processBeans.get(i).getApplicationname());
                    strings[i] = "kill -9 " + processBeans.get(i).getPid();
                }
            }
            CommandHelper.runCmdsAsRoot(stringBuilder, strings);
        } else {
            if (Build.VERSION.SDK_INT > 7) {
                for (int i = 0; i < processBeans.size(); i++) {
                    if (processBeans.get(i).getIschecked()) {
                        LogWrapper.d("kill process:" + processBeans.get(i).getProcessname() + processBeans.get(i).getApplicationname());
                        am.killBackgroundProcesses(processBeans.get(i).getProcessname());
                    }
                }
            } else {
                for (int i = 0; i < processBeans.size(); i++) {
                    if (processBeans.get(i).getIschecked()) {
                        LogWrapper.d("kill process:" + processBeans.get(i).getProcessname() + processBeans.get(i).getApplicationname());
                        am.restartPackage(processBeans.get(i).getProcessname());
                    }
                }
            }
        }
    }

    /**
     * 获取当前系统的可用内存
     *
     * @return
     */
    public long getMemory() {
        am.getMemoryInfo(memory);
        return memory.availMem;
    }

    /**
     * 获取当前系统的总内存
     *
     * @return
     */
    public long getTotalMemory() {
        if (Build.VERSION.SDK_INT < 16) {
            return -1;
        } else {
            am.getMemoryInfo(memory);
            return memory.totalMem;
        }
    }

}
