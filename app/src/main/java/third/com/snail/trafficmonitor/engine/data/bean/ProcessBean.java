package third.com.snail.trafficmonitor.engine.data.bean;

/**
 * Created by lic on 2014/12/08
 * 进程的实体类
 */
public class ProcessBean {
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
     * cpu占比
     */
    private String cpupercent;
    /**
     * 占用内存
     */
    private long memory;
    /**
     * 是否被选中
     */
    private Boolean ischecked;
    /**
     * 是否受保护
     */
    private Boolean protect;

    public ProcessBean() {
//        icon = null;
        processname = "";
        cpupercent = "";
        memory = 0;
        ischecked = false;
        pid = 0;
        applicationname = "";
        protect = false;
    }

    public Boolean getProtect() {
        return protect;
    }

    public void setProtect(Boolean protect) {
        this.protect = protect;
    }

//    public Drawable getIcon() {
//        return icon;
//    }
//
//    public void setIcon(Drawable icon) {
//        this.icon = icon;
//    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname;
    }

    public String getCpupercent() {
        return cpupercent;
    }

    public void setCpupercent(String cpupercent) {
        this.cpupercent = cpupercent;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public Boolean getIschecked() {
        return ischecked;
    }

    public void setIschecked(Boolean ischecked) {
        this.ischecked = ischecked;
    }

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
}
