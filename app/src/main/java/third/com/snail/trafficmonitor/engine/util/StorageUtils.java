package third.com.snail.trafficmonitor.engine.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File Operations Utils
 */
public class StorageUtils {
    public static boolean isExternalStorageAvailable() {
        boolean available = Environment.getExternalStorageState()
                .equalsIgnoreCase(Environment.MEDIA_MOUNTED);
        if (available) {
            LogWrapper.d("External storage is available!!!");
        }
        return available;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageOnlyReadable() {
        String state = Environment.getExternalStorageState();
        boolean readOnly = state.equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY);
        if (readOnly) {
            LogWrapper.e("External storage is read only!");
        }
        return readOnly;
    }

    public static File getCacheFile(Context context, String filename) {
        return new File(context.getCacheDir(), filename);
    }

    public static boolean deleteCacheFile(Context context, String filename) {
        File file = new File(context.getCacheDir(), filename);
        return file.delete();
    }

    public static OutputStream writeToInternalStorage(Context context, String filename) throws FileNotFoundException {
        return context.openFileOutput(filename, Context.MODE_PRIVATE);
    }

    public static File getInternalStorageFile(Context context, String filename) {
        return new File(context.getFilesDir(), filename);
    }

    public static InputStream readFromInternalStorage(Context context, String filename) throws FileNotFoundException {
        return context.openFileInput(filename);
    }

    public static boolean deleteInternalStorageFile(Context context, String filename) {
        if (context.fileList() != null && context.fileList().length > 0) {
            for (String file : context.fileList()) {
                if (file.equals(filename)) {
                    return context.deleteFile(filename);
                }
            }
        }
        return false;
    }

    public static File getExternalStorageFile(Context context, String filename) {
        return new File(context.getExternalFilesDir(null), filename);
    }

    public static boolean deleteExternalStorageFile(Context context, String filename) {
        File file = new File(context.getExternalFilesDir(null), filename);
        return file.exists() && file.delete();
    }

    /* Auto decide internal or external to operate */
    public static File getFile(Context context, String filename) {
        if (isExternalStorageAvailable() && !isExternalStorageOnlyReadable()) {
            return getExternalStorageFile(context, filename);
        } else {
            return getInternalStorageFile(context, filename);
        }
    }

    public static boolean deleteFile(Context context, String filename) {
        if (isExternalStorageAvailable() && !isExternalStorageOnlyReadable()) {
            return deleteExternalStorageFile(context, filename);
        } else {
            return deleteInternalStorageFile(context, filename);
        }
    }

    public static void copyFile(File src, File tgt) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(src);
            output = new FileOutputStream(tgt);
            byte[] buf = new byte[1024];
            while (input.read(buf) > 0) {
                output.write(buf);
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * Add for Much Start 2015/10/15
     */
    private static final int APP_INSTALL_AUTO = 0;
    public static final int APP_INSTALL_DEVICE = 1;
    public static final int APP_INSTALL_SDCARD = 2;
    public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";

    @SuppressLint("NewApi")
    public static int getInstallPath(Context context){
        int installPathId = Settings.Global.getInt(context.getContentResolver(),
                DEFAULT_INSTALL_LOCATION, APP_INSTALL_AUTO);
        if(installPathId == APP_INSTALL_AUTO){
            if(isMounted()){
                return APP_INSTALL_SDCARD;
            }else{
                return APP_INSTALL_DEVICE;
            }
        }
        return installPathId;
    }

    /**
     * 是否已有存储卡挂载
     *
     * @return
     */
    public static boolean isMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * Add for Much End 2015/10/15
     */
}
