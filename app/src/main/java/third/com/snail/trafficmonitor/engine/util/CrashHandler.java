package third.com.snail.trafficmonitor.engine.util;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

	private final String TAG = "CrashHandler";
	
	private static CrashHandler instance;
	
	/** 系统默认的UncaughtException处理类 */ 
	private UncaughtExceptionHandler mDefaultHandler;
	private Context context;
	private UncatchExceptionListener listener;


	private CrashHandler(Application app) {
		context = app.getApplicationContext();
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	public static CrashHandler getInstance(Application app) {
		if (instance == null) {
			synchronized (CrashHandler.class) {
				instance = new CrashHandler(app);
			}
		}
		return instance;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep一会后结束程序
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
			    System.exit(10);
            }
		}

	}

	/** 
	* 自定义错误处理,收集错误信息 
	* 发送错误报告等操作均在此完成. 
	* 开发者可以根据自己的情况来自定义异常处理逻辑 
	* @param ex
	* @return true:如果处理了该异常信息;否则返回false 
	*/ 
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		
		final Throwable e = ex;
		final String exInfo = crashInfo(ex);
		Log.d(TAG, exInfo);
		
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				if (listener != null) {
					listener.uncatchException(e, exInfo);
				}
				Looper.loop();
			}

		}.start();
		return true;
	}
	
	/** 
	* 保存错误信息到文件中 
	* @param ex 
	* @return 
	*/ 
	private String crashInfo(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		
		return result;
	} 

	public void setListener(UncatchExceptionListener listener) {
		this.listener = listener;
	}

	public interface UncatchExceptionListener {
		
		void uncatchException(Throwable ex, String exInfo);
	}
}
