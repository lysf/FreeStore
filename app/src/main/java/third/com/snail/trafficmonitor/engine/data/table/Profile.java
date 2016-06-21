package third.com.snail.trafficmonitor.engine.data.table;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by kevin on 14-9-23.
 * <p/>
 * Profile table
 */
@DatabaseTable(tableName = "profile")
public class Profile {
    private final String TAG = Profile.class.getSimpleName();
    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUMN_KEY = "key";
    public final static String COLUMN_VALUE = "value";
    public final static String COLUMN_KEY_TIMESTAMP = "time_stamp";
    @DatabaseField(columnName = BaseColumns._ID, generatedId = true)
    private int id;
    @DatabaseField
    private String key;
    @DatabaseField
    private String value;

    public Profile() {
    }

    public Profile(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "TAG='" + TAG + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
