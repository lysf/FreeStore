package third.com.snail.trafficmonitor.engine.data.bean;

/**
 * Created by lic on 2014/12/08
 * 基本的进程属性
 */
public class BasicProcessBean {
//    /**
//     * 图标 暂时无用，先废弃
//     */
//    private Drawable icon;
    /**
     * 应用名称
     */
    private String applicationname;
    /**
     * 进程id
     */
    private int pid;
    /**
     * 进程名称
     */
    private String processname;
    /**
     * 进程的重要程度，可以由api获取到
     */
    private int importance;

    public BasicProcessBean() {
//        icon = null;
        applicationname = "";
        pid = 0;
        processname = "";
        importance = 0;
    }

//    public Drawable getIcon() {
//        return icon;
//    }
//
//    public void setIcon(Drawable icon) {
//        this.icon = icon;
//    }

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
