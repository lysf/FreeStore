package com.snailgame.cjg.common.db.dao;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by lic on 15-7-20.
 * 首页信息
 */
@DatabaseTable(tableName = "desk_game_table")
public class DeskGame {
    public final static String COLUMN_ID = BaseColumns._ID;
    public static final String GAME_PACKAGE = "game_pac";
    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int id;
    @DatabaseField(columnName = GAME_PACKAGE, canBeNull = false, unique = true)
    private String packageName;

    public int getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
