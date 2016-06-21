package com.snailgame.cjg.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import com.snailgame.fastdev.util.ResUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * 类说明：  apk 签名信息获取工具类
 * 
 * @author 	Cundong
 * @date 	2013-9-6
 * @version 1.0
 */
public class SignUtils {

	/**
	 * 获取未安装Apk的签名
	 *
     * @param context
	 * @param apkPath
	 * @return
	 */
	public static String getUnInstalledApkSignature(Context context, String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";

		try {
			Class clazz = Class.forName(PATH_PackageParser);
			Object packageParser = getParserObject(clazz);

			Object packag = getPackage(context, clazz, packageParser, apkPath);

			Method collectCertificatesMethod = clazz.getMethod("collectCertificates",
                    Class.forName(PATH_PackageParser + "$Package"), int.class);
			collectCertificatesMethod.invoke(packageParser, packag, PackageManager.GET_SIGNATURES);
			Signature mSignatures[] = (Signature[]) packag.getClass().getField("mSignatures").get(packag);

			Signature apkSignature = mSignatures.length > 0 ? mSignatures[0] : null;

			if (apkSignature != null) {
				return apkSignature.toCharsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Object getParserObject(Class clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
				clazz.getConstructor().newInstance() :
				clazz.getConstructor(String.class).newInstance("");
	}

	private static Object getPackage(Context c, Class clazz, Object instance, String path) throws Exception {
		Object pkg;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Method method = clazz.getMethod("parsePackage", File.class, int.class);
			pkg = method.invoke(instance, new File(path), 0x0004);
		} else {
			Method method = clazz.getMethod("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);
			pkg = method.invoke(instance, new File(path), null, ResUtil.getDisplayMetrics(), 0x0004);
		}
		return pkg;
	}

	/**
	 * 获取已安装apk签名
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getInstalledApkSignature(Context context,
			String packageName) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm
				.getInstalledPackages(PackageManager.GET_SIGNATURES);

		Iterator<PackageInfo> iter = apps.iterator();
		while (iter.hasNext()) {
			PackageInfo packageinfo = iter.next();
			String thisName = packageinfo.packageName;
			if (thisName.equals(packageName)) {
				return packageinfo.signatures[0].toCharsString();
			}
		}

		return null;
	}
}