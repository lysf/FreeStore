package com.snailgame.cjg.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by yftx
 * on 2/22/14.
 */
public class ToastUtils {
    /**
     * @param context
     * @param msg     需要提示的toast文字
     */
    public static void showMsg(Context context, String msg) {
        Toast toast = createToast(context, msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * @param context
     * @param msg     需要提示的toast文字
     */
    public static void showMsgLong(Context context, String msg) {
        Toast toast = createToast(context, msg);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * @param context
     * @param msg     需要提示的toast文字
     */
    public static void showMsg(Context context, String msg, int gravity) {
        Toast toast = createToast(context, msg);
        toast.setGravity(gravity, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * @param context
     * @param id         string中配置的文字id
     * @param formatArgs id中配置的字符串对应的参数
     */
    public static void showMsg(Context context, int id, Object... formatArgs) {
        String msg = ResUtil.getString(id, formatArgs);
        Toast toast = createToast(context, msg);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * @param context
     * @param id         string中配置的文字id
     * @param formatArgs id中配置的字符串对应的参数
     */
    public static void showMsgLong(Context context, int id, Object... formatArgs) {
        String msg = ResUtil.getString(id, formatArgs);
        Toast toast = createToast(context, msg);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * @param context
     * @param msg     Toast显示文字
     */
    public static Toast createToast(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.common_toast_layout,
                null);
        TextView contentTv = (TextView) layout.findViewById(R.id.text_content);
        contentTv.setText(msg);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, ResUtil.getDimensionPixelSize(R.dimen.dimen_30dp));
        toast.setView(layout);
        return toast;
    }

}
