package third.com.snail.trafficmonitor.engine.util.firewall;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * @author kevin
 * @version 1.0
 * <p/>
 * 网络控制iptables命令辅助类。用以针对应用组装对应的iptables命令。
 */
public class IptableCommand {
    public static final int ADD = 0x01;
    public static final int DEL = 0x02;

    /**
     * {@link #ADD}代表添加网络控制策略
     * {@link #DEL}代表删除网络控制策略
     */
    @IntDef({ADD, DEL})
    public @interface OPERATION {
    }

    public static final int OUTPUT = 0x03;
    public static final int INPUT = 0x04;

    /**
     * {@link #OUTPUT}代表限制应用自本地发出网络数据包
     * {@link #INPUT}代表限制应用发出数据包到本地
     */
    @IntDef({OUTPUT, INPUT})
    public @interface DIRECTION {
    }

    private String policyBody;
    private String chainBody;

    /**
     * 返回iptables命令的Policy策略部分
     * @return Policy cmd string
     */
    public String getPolicyBody() {
        return policyBody;
    }

    /**
     * 设置iptables命令的Policy策略部分内容
     * @param policyBody 策略部分内容
     */
    public void setPolicyBody(String policyBody) {
        this.policyBody = policyBody;
    }

    /**
     * 获取iptables命令的Chain规则部分
     * @return Chain cmd string
     */
    public String getChainBody() {
        return chainBody;
    }

    /**
     * 设置iptables命令的Chain规则部分
     * @param chainBody 规则部分内容
     */
    public void setChainBody(String chainBody) {
        this.chainBody = chainBody;
    }

    /**
     * 获取iptables整条命令
     * @return iptables命令
     */
    public String getCmd() {
        return policyBody + " " + chainBody;
    }

    /**
     * iptables命令构建类
     */
    public static class Builder {
        private int operation = -1;
        private int uid = -2;
        private int direction = -1;
        private String iface;

        /**
         * 构建新的iptables构造器，指定处理方式
         * @param operation {@link #ADD#DEL}
         */
        public Builder(@OPERATION int operation) {
            this.operation = operation;
            this.direction = OUTPUT;
        }

        /**
         * 构建新的iptables构造器，指定处理方式和限制方向
         * @param operation {@link #ADD#DEL}
         * @param direction {@link #OUTPUT#INPUT}
         */
        public Builder(@OPERATION int operation, @DIRECTION int direction) {
            this.operation = operation;
            this.direction = direction;
        }

        /**
         * 指定iptables命令所限制的uid
         * @param uid uid
         * @return iptables的Builder
         */
        public Builder uid(int uid) {
            this.uid = uid;
            return this;
        }

        /**
         * 指定iptables命令所限制的网络接口(WIFI or MOBILE)
         * @param iface 网络接口的iface，通过相关类获取
         * @return iptables的Builder
         */
        public Builder iface(String iface) {
            this.iface = iface;
            return this;
        }

        // POLICY BODY        | CHAIN BODY
        // iptables -A OUTPUT   -o wlan+ -m owner --uid-owner 10022 -j REJECT
        // iptables -D OUTPUT   -o wlan+ -m owner --uid-owner 10022 -j REJECT
        // 相同的chain body 才能支持Delete操作
        /**
         * 根据所收集到的必要参数构建出一个完整的IptableCommand类
         * @return IptableCommand class
         */
        @Nullable
        public IptableCommand build() {
            // -1 有可能是WIFI和Mobile统计成员
            if (uid == -1) return null;
            if (operation == -1 || uid == -2 || direction == -1
                    || iface == null || TextUtils.isEmpty(iface)) {
                throw new IllegalArgumentException("Lack of parameters, plz check your builder again. [" + this.toString() + "]");
            }
            IptableCommand command = new IptableCommand();
            String policy = "iptables -%1$s %2$s";
            policy = String.format(policy, operation == ADD ? "A" : "D",
                    direction == OUTPUT ? "OUTPUT" : "INPUT");
            command.setPolicyBody(policy);
            String chain = "-o %1$s -m owner --uid-owner %2$d -j REJECT";
            chain = String.format(chain, iface, uid);
            command.setChainBody(chain);
            return command;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "operation=" + operation +
                    ", uid=" + uid +
                    ", direction=" + direction +
                    ", iface='" + iface + '\'' +
                    '}';
        }
    }
}
