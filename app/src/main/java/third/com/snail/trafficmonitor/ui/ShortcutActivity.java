package third.com.snail.trafficmonitor.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.util.ComUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.EngineEnvironment;
import third.com.snail.trafficmonitor.engine.data.bean.FlowInfo;
import third.com.snail.trafficmonitor.engine.data.bean.ProcessBean;
import third.com.snail.trafficmonitor.engine.util.TrafficTool;
import third.com.snail.trafficmonitor.engine.util.process.ProcessUtil;
import third.com.snail.trafficmonitor.ui.widget.RiseNumberTextview.RiseNumberTextView;

/**
 * Created by kevin on 14/12/24.
 */
public class ShortcutActivity extends Activity {

    public static void createShortcutSafety(Context context) {
        if (!ComUtil.isShortcutExist(context, context.getString(R.string.shortcut_name))) {
            createShortcut(context);
        }
    }

    private static void createShortcut(Context context) {
        if (!EngineEnvironment.isTrafficEnable())
            return;
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.shortcut_name));
        shortcut.putExtra("duplicate", false); //防止重复创建

        Intent bring = new Intent(Intent.ACTION_VIEW);
        ComponentName name = new ComponentName(context.getPackageName(), ShortcutActivity.class.getName());
        bring.setComponent(name);

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, bring);

        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_shortcut);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        context.sendBroadcast(shortcut);
    }

    public static void removeShortcut(Context context) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

        Intent bring = new Intent(Intent.ACTION_VIEW);
        ComponentName name = new ComponentName(context.getPackageName(), ShortcutActivity.class.getName());
        bring.setComponent(name);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.shortcut_name));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, bring);
        context.sendBroadcast(shortcut);
    }

    private Handler handler = new MsgHandler(this);
    private int currentFrameIndex = 0;
    @Bind(R.id.ivMemoryClean)
    ImageView ivMemoryClean;
    private final int FRAME_ANIMATION_DURATION = 100;//帧动画时间间隔
    private int mFrameCount;

    private ProcessUtil mUtil;
    private long mPrevMemInfo;
    @Bind(R.id.tv_save_number)
    RiseNumberTextView saveNumber;
    @Bind(R.id.tv_avail)
    RiseNumberTextView availNumber;
    @Bind(R.id.tv_save_unit)
    TextView saveUnit;
    @Bind(R.id.tv_available_unit)
    TextView availUnit;
    @Bind(R.id.tv_total)
    TextView totalNumber;
    @Bind(R.id.tv_available_memory)
    TextView availableMemory;
    @Bind(R.id.layout_number)
    LinearLayout numberLayout;
    private int[] anim = new int[]{
            R.drawable.memory_clean_01,
            R.drawable.memory_clean_02,
            R.drawable.memory_clean_03,
            R.drawable.memory_clean_04,
            R.drawable.memory_clean_05,
            R.drawable.memory_clean_04,
            R.drawable.memory_clean_05,
            R.drawable.memory_clean_08,
            R.drawable.memory_clean_09,
            R.drawable.memory_clean_10,
            R.drawable.memory_clean_11,
            R.drawable.memory_clean_12,
            R.drawable.memory_clean_13,
            R.drawable.memory_clean_14,
            R.drawable.memory_clean_01

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engine_activity_memory_clean);
        ButterKnife.bind(this);
        overridePendingTransition(0, 0);
        fullScreen();
        mFrameCount = anim.length;
        showFrameAnimation();
        MobclickAgent.onEvent(this, UmengAnalytics.EVENT_CLEAR_MEMORY);
        mUtil = new ProcessUtil(this);
        /**
         * 开启新的线程来进行清理内存
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPrevMemInfo = mUtil.getMemory();
                List<ProcessBean> list = mUtil.getRunningProcess();
                mUtil.killProgress(list);
            }
        }).start();
    }

    private void fullScreen() {
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 之所以用handler实现而不是直接用帧动画
     * 是因为直接用帧动画回出现变形
     */
    private void showFrameAnimation() {
        handler.sendEmptyMessage(0);
    }

    static class MsgHandler extends Handler {
        private WeakReference<ShortcutActivity> mActivity;

        public MsgHandler(ShortcutActivity activity) {
            this.mActivity = new WeakReference<ShortcutActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ShortcutActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleAnimation(msg);
            }
        }
    }

    private void handleAnimation(Message msg) {

        ivMemoryClean.setImageResource(anim[currentFrameIndex]);
        if (currentFrameIndex >= mFrameCount - 1) {
            long mAfterMemory = mUtil.getMemory();
            long totalMemory = mUtil.getTotalMemory();
            /**
             * 可用内存有可能比杀进程之前的可用内存要小，故取绝对值
             */
            FlowInfo cleanInfo = TrafficTool.getCostLong(Math.abs(mAfterMemory - mPrevMemInfo));
            FlowInfo mAfterMemInfo = TrafficTool.getCostLong(mAfterMemory);
            numberLayout.setVisibility(View.VISIBLE);
            try {
                saveNumber.withNumber(Float.parseFloat(cleanInfo.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            saveNumber.setDuration(350);
            saveUnit.setText(cleanInfo.getBytesType());

            try {
                availNumber.withNumber(Float.parseFloat(mAfterMemInfo.getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            availNumber.setDuration(350);
            availUnit.setText(mAfterMemInfo.getBytesType());

            if (totalMemory == -1) {
                availableMemory.setVisibility(View.VISIBLE);
                totalNumber.setVisibility(View.GONE);
            } else {
                String memory = String.format("/%s",
                        TrafficTool.getCost(totalMemory));
                totalNumber.setText(memory);
            }

            saveNumber.start();
            availNumber.start();
            /**
             * 数字动画播完的回调
             */
            availNumber.setOnEnd(new RiseNumberTextView.EndListener() {
                @Override
                public void onEndFinish() {
                    if (handler != null)
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
                    else
                        finish();
                }
            });
            return;
        }
        ++currentFrameIndex;
        handler.sendEmptyMessageDelayed(0, FRAME_ANIMATION_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
