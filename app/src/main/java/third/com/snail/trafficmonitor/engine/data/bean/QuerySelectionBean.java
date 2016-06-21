package third.com.snail.trafficmonitor.engine.data.bean;

/**
 * Created by lc on 2015/1/7.
 * 数据库查询条件实体类，(仅对于 “key” + “运算符” + “value”)这种条件有效
 */
public class QuerySelectionBean {

    /**
     * 查询条件的key
     */
    private String key;
    /**
     * 查询的运算符如 "=" ">" "<" ">=" "<="
     */
    private String operator;
    /**
     * 查询条件的value
     */
    private String value;

    public QuerySelectionBean(String key, String operator, String value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
