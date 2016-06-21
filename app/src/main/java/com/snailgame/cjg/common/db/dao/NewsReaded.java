package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by TAJ_C on 2016/4/19.
 */
@Getter
@Setter
@DatabaseTable(tableName = "news_readed_tab")
public class NewsReaded {

    public final static String COLUMN_ID = BaseColumns._ID;

    public final static String COLUM_NEWS_ID = "news_id";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUM_NEWS_ID, unique = true)
    private String newsID;

}
