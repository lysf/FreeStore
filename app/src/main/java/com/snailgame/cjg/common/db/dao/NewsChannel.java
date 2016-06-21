package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/18.
 */
@Getter
@Setter
@DatabaseTable(tableName = "news_channel_tab")
public class NewsChannel {

    public final static String COLUMN_ID = BaseColumns._ID;
    public final static String COLUM_CHANNEL_ID = "channel_id";
    public final static String COLUM_CHANNEL_NAME = "channel_name";
    public final static String COLUM_CHANNEL_IS_SHOW = "channel_is_show";
    public final static String COLUM_CHANNEL_INDEX = "channel_index";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUM_CHANNEL_ID, unique = true)
    private int channelId;

    @DatabaseField(columnName = COLUM_CHANNEL_NAME)
    private String channelName;

    @DatabaseField(columnName = COLUM_CHANNEL_IS_SHOW)
    private boolean isShow;

    @DatabaseField(columnName = COLUM_CHANNEL_INDEX)
    private int index;
}
