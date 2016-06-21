package third.com.snail.trafficmonitor.engine.util.process;

import android.content.Context;
import android.content.pm.PackageManager;

import com.snailgame.cjg.global.GlobalVar;


/**
 * Created by lic on 2014/12/08
 * 判断当前的进程是否能直接被杀死
 */
public class JudgeKill {
    //允许一个程序告诉appWidget服务需要访问小插件的数据库，只有非常少的应用才用到此权限
    private static final String PERMISSION_IDGET = "android.permission.BIND_APPWIDGET";
    //请求系统管理员接收者receiver，只有系统才能使用
    private static final String PERMISSION_SYSTEM = "android.permission.BIND_DEVICE_ADMIN";

    public static boolean iskill(String processname, Context c) {
        boolean flag = true;
        PackageManager pm = c.getApplicationContext().getPackageManager();
        String packageName = null;
        //判断自启动状态是启用还是禁用
        try {
            packageName = pm.getApplicationInfo(processname, PackageManager.GET_DISABLED_COMPONENTS).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            flag = false;//找不到则自启动状态为启用，不能杀掉该进程
            e.printStackTrace();
        }

        //判断程序是不是appwidget和是不是系统应用
        if (pm.checkPermission(PERMISSION_IDGET, packageName) == PackageManager.PERMISSION_GRANTED
                || pm.checkPermission(PERMISSION_SYSTEM, packageName) == PackageManager.PERMISSION_GRANTED
                || GlobalVar.getInstance().getListAppWidgetValue().contains(processname)) {
            flag = false;
        }
        return flag;
    }
}
