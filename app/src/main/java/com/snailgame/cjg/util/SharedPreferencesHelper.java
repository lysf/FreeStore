package com.snailgame.cjg.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;

public class SharedPreferencesHelper {
	private static SharedPreferencesHelper sharedHelper;
	private String xmlFileName;
	private SharedPreferences sharePre;
	private Editor editor;

	public static SharedPreferencesHelper getInstance() {
		if (sharedHelper == null) {
			synchronized (SharedPreferencesHelper.class) {
				if (sharedHelper == null) {
					sharedHelper = new SharedPreferencesHelper();
				}
			}
		}
		return sharedHelper;
	}

	public static SharedPreferencesHelper getInstance(String xmlFileName) {
		if (sharedHelper == null) {
			synchronized (SharedPreferencesHelper.class) {
				if (sharedHelper == null) {
					sharedHelper = new SharedPreferencesHelper(xmlFileName);
				}
			}
		}
		return sharedHelper;
	}

	private SharedPreferencesHelper() {
		xmlFileName = AppConstants.DEFAULT_SHARED_NAME;
		initParams();
	}

	private SharedPreferencesHelper(String fileName) {
		xmlFileName = fileName;
		initParams();
	}

	private void initParams() {
		Context context = FreeStoreApp.getContext();
		sharePre = context.getSharedPreferences(xmlFileName, Context.MODE_PRIVATE);
		editor = sharePre.edit();
	}

	public String getValue(String key, String defValue) {
		return sharePre.getString(key, defValue);
	}

	public int getValue(String key, int defValue) {
		return sharePre.getInt(key, defValue);
	}

	public Boolean getValue(String key, Boolean defValue) {
		return sharePre.getBoolean(key, defValue);
	}

	public long getValue(String key, long defValue) {
		return sharePre.getLong(key, defValue);
	}

	public void putValue(String key, String defValue) {
		editor.putString(key, defValue);
	}

	public void putValue(String key, int defValue) {
		editor.putInt(key, defValue);
	}

	public void putValue(String key, Boolean defValue) {
		editor.putBoolean(key, defValue);
	}

	public void putValue(String key, long defValue) {
		editor.putLong(key, defValue);
	}

	public void removeItem(String key) {
		editor.remove(key);
	}

	public void clearAllData() {
		editor.clear();
	}

	public void applyValue() {
		editor.apply();
	}

	public void commitValue() {
		editor.commit();
	}

}
