package com.snailgame.cjg.common.db.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by yftx on 6/25/14.
 * 对应每条推送消息内容
 */
@DatabaseTable(tableName = "push_table")
public class PushModel implements Parcelable {
    public static final int HAS_READ = 1;       // 已读
    public static final int NOT_READ = 0;       // 未读
    public static final int IS_EXIT = 1;       // 存在
    public static final int IS_NOT_EXIT = 0;    // 不存在
    public static final String PUSH_MODEL_DEFAULT_USER_ID = "-1";   //无针对用户推送在数据库中的默认的uerid

    public final static String COLUMN_ID = BaseColumns._ID;
    public static final String COL_PUSH_ID = "id";
    public static final String COL_PUSH_MESSAGE_ID = "msg_id";
    public static final String COL_PUSH_TASK_ID = "task_id";
    public static final String COL_PUSH_TITLE = "title";
    public static final String COL_PUSH_CONTENT = "content";
    public static final String COL_PUSH_EXPAND_MESSAGE = "expand_message";
    public static final String COL_PUSH_CREATE_DATE = "create_date";
    public static final String COL_PUSH_IS_READ = "is_read";        // 1：已读 0：未读
    public static final String COL_PUSH_IS_EXIST = "is_exist";    // 1：存在 0：不存在 通知不存在通知栏中
    public static final String COL_PUSH_USER_ID = "user_id";
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COL_PUSH_ID)
    private int pushId;
    @DatabaseField(columnName = COL_PUSH_TITLE)
    private String title;
    @DatabaseField(columnName = COL_PUSH_CONTENT)
    private String content;
    @DatabaseField(columnName = COL_PUSH_EXPAND_MESSAGE)
    private String expandMsg;
    @DatabaseField(columnName = COL_PUSH_CREATE_DATE)
    private String create_date;
    private String icoUrl = "";
    private String iconBigUrl = "";
    @DatabaseField(columnName = COL_PUSH_IS_READ, dataType = DataType.INTEGER, defaultValue = "0")
    private int hasRead;
    // msgId、taskId为个推平台自定义事件统计使用
    @DatabaseField(columnName = COL_PUSH_MESSAGE_ID)
    private String msgId;
    @DatabaseField(columnName = COL_PUSH_TASK_ID)
    private String taskId;
    @DatabaseField(columnName = COL_PUSH_IS_EXIST, dataType = DataType.INTEGER, defaultValue = "1")
    private int isExit;
    @DatabaseField(columnName = COL_PUSH_USER_ID, defaultValue = PUSH_MODEL_DEFAULT_USER_ID)
    private String userId;//是否针对某一特定用户进行的推送，如果是则记录用户id，防止A用户看到针对B用户的数据

    public PushModel(int pushId, String title, String content, String expandMsg, String create_date, String icoUrl,
                     int hasRead, String msgId, String taskId, String iconBigUrl, int isExit, String userId) {
        this.pushId = pushId;
        this.title = title;
        this.content = content;
        this.expandMsg = expandMsg;
        this.create_date = create_date;
        this.hasRead = hasRead;
        this.icoUrl = icoUrl;
        this.msgId = msgId;
        this.taskId = taskId;
        this.iconBigUrl = iconBigUrl;
        this.isExit = isExit;
        if (TextUtils.isEmpty(userId)) {
            this.userId = PUSH_MODEL_DEFAULT_USER_ID;
        } else {
            this.userId = userId;
        }
    }

    public PushModel() {

    }
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private static String parseIcoUrl(String expandMsg) {
        return "";
    }

    public int getPushId() {
        return pushId;
    }

    public void setPushId(int pushId) {
        this.pushId = pushId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpandMsg() {
        return expandMsg;
    }

    public void setExpandMsg(String expandMsg) {
        this.expandMsg = expandMsg;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int getHasRead() {
        return hasRead;
    }

    public void setHasRead(int hasRead) {
        this.hasRead = hasRead;
    }

    public String getIcoUrl() {
        return icoUrl;
    }

    public void setIcoUrl(String icoUrl) {
        this.icoUrl = icoUrl;
    }

    public String getIconBigUrl() {
        return iconBigUrl;
    }

    public void setIconBigUrl(String iconBigUrl) {
        this.iconBigUrl = iconBigUrl;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getIsExit() {
        return isExit;
    }

    public void setIsExit(int isExit) {
        this.isExit = isExit;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pushId);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.expandMsg);
        dest.writeString(this.create_date);
        dest.writeString(this.icoUrl);
        dest.writeString(this.iconBigUrl);
        dest.writeInt(this.hasRead);
        dest.writeString(this.msgId);
        dest.writeString(this.taskId);
        dest.writeInt(this.isExit);
        dest.writeString(this.userId);
    }

    private PushModel(Parcel in) {
        this.pushId = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.expandMsg = in.readString();
        this.create_date = in.readString();
        this.icoUrl = in.readString();
        this.iconBigUrl = in.readString();
        this.hasRead = in.readInt();
        this.msgId = in.readString();
        this.taskId = in.readString();
        this.isExit = in.readInt();
        this.userId = in.readString();
    }

    public static final Creator<PushModel> CREATOR = new Creator<PushModel>() {
        public PushModel createFromParcel(Parcel source) {
            return new PushModel(source);
        }

        public PushModel[] newArray(int size) {
            return new PushModel[size];
        }
    };
}
