package com.zbar.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.detail.DetailActivity;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.model.JumpInfo;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * 描述: 扫描界面
 */
public class CaptureActivity extends SwipeBackActivity implements Callback {

    public final static int SCAN_REQUEST_CODE = 1;
    public final static String SCAN_RESULT = "scan_result";
    private final static int INIT_UI = 1;
    private final static int CAMERA_ERROR = 2;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;

    @Bind(R.id.capture_containter)
    RelativeLayout mContainer;
    @Bind(R.id.capture_crop_layout)
    RelativeLayout mCropLayout;
    @Bind(R.id.capture_scan_line)
    ImageView mQrLineView;
    @Bind(R.id.top_mask)
    ImageView topMask;
    @Bind(R.id.bottom_mask)
    ImageView bottomMask;
    @Bind(R.id.left_mask)
    ImageView leftMask;
    @Bind(R.id.right_mask)
    ImageView rightMask;
    @Bind(R.id.capture_preview)
    SurfaceView surfaceView;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    private Handler uiHandler = new MsgHandler(this);

    static class MsgHandler extends Handler {
        private WeakReference<CaptureActivity> mActivity;

        public MsgHandler(CaptureActivity activity) {
            this.mActivity = new WeakReference<CaptureActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            CaptureActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleUI(message);
            }

        }
    }

    private void handleUI(Message message) {
        switch (message.what) {
            case INIT_UI:
                initAnimation();
                initUI();
                break;
            case CAMERA_ERROR:
                ToastUtils.showMsg(CaptureActivity.this, getString(R.string.camer_error));
                finish();
                break;
        }
    }

    public static Intent newIntent(Context context, boolean isOutSideIn) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.putExtra(KEY_IS_OUTSIDE_IN, isOutSideIn);
        return intent;
    }

    @Override
    protected void handleIntent() {
        if (getIntent() != null)
            // add for outside in
            isOutSideIn = getIntent().getBooleanExtra(KEY_IS_OUTSIDE_IN, false);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_capture;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.scanner);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private void initAnimation() {
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, -0.97f, TranslateAnimation.RELATIVE_TO_SELF, 0.97f);
        mAnimation.setDuration(3000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
    }

    private void initUI() {
        topMask.setBackgroundResource(R.drawable.scannner_mask);
        bottomMask.setBackgroundResource(R.drawable.scannner_mask);
        leftMask.setBackgroundResource(R.drawable.scannner_mask);
        rightMask.setBackgroundResource(R.drawable.scannner_mask);
        mQrLineView.setVisibility(View.VISIBLE);
        surfaceView.setBackgroundResource(android.R.color.transparent);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 扫描完成后回调，如果是关于应用详情的url（类似http://api.app1.snail.com/appSpace/0/0/132/132588/132588.html）必须做分割，然后在本地跳转
     *
     * @param result 扫描结果
     */
    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        if (result.startsWith("http")) {
            if (result.contains("/appSpace/")) {
                String[] spilt = result.split("appSpace");
                if (spilt[1] != null && !TextUtils.isEmpty(spilt[1])) {
                    String[] split1 = spilt[1].split("/");
                    if (split1[4] != null && TextUtils.isDigitsOnly(split1[4]) && split1[5].contains(split1[4])) {
                        String appId = split1[4];
                        startActivity(DetailActivity.newIntent(CaptureActivity.this, Integer.parseInt(appId), createRoute()));
                        finish();
                        return;
                    }
                }
            } else if (result.contains("open=freestoresdk")) {
                // 使用SDK网页打开
                startActivity(LoginSDKUtil.newIntentForWebUpload(CaptureActivity.this, result));
                finish();
                return;
            } else if (result.contains("open=freestore")) {
                // 使用网页打开
                startActivity(WebViewActivity.newIntent(CaptureActivity.this, result));
                finish();
                return;
            } else {
                try {
                    // 打开内部功能
                    Uri uri = Uri.parse(result);
                    if (uri != null) {
                        String params = uri.getQueryParameter("snailgame");
                        if (!TextUtils.isEmpty(params)) {
                            String[] temp;
                            if (params.contains("|")) {
                                temp = params.split("[|]");
                            } else {
                                temp = new String[]{params};
                            }

                            if (temp != null && temp.length > 0) {
                                boolean format = false;
                                JumpInfo info = new JumpInfo();
                                info.setType(Integer.parseInt(temp[0]));
                                switch (info.getType()) {
                                    case JumpUtil.DETAIL_PAGE_TYPE:
                                    case JumpUtil.BIND_PHONE_PAGE_TYPE:
                                    case JumpUtil.GAME_SPREE_TYPE:
                                        if (temp.length > 1) {
                                            info.setPageId(temp[1]);
                                            format = true;
                                        }
                                        break;
                                    case JumpUtil.COLLECTION_PAGE_TYPE:
                                    case JumpUtil.COLLECTION_FREE_AREA_TYPE:
                                        if (temp.length > 2) {
                                            info.setPageId(temp[1]);
                                            info.setPageTitle(temp[2]);
                                            format = true;
                                        }
                                        break;
                                    case JumpUtil.WEB_PAGE_ONE_TYPE:
                                    case JumpUtil.WEB_PAGE_ACTIVITY_TYPE:
                                    case JumpUtil.WEB_PAGE_TWO_TYPE:
                                    case JumpUtil.OPEN_URL_IN_SDK:
                                    case JumpUtil.OPEN_URL_IN_SYSTEM_BROWSER:
                                        if (temp.length > 1) {
                                            info.setUrl(temp[1]);
                                            format = true;
                                        }
                                        break;
                                    default:
                                        format = true;
                                        break;

                                }

                                if (format) {
                                    JumpUtil.JumpActivity(CaptureActivity.this, info, createRoute());
                                    finish();
                                    return;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
            ComUtil.openExternalBrowser(this, result);
        } else {
            Intent intent = new Intent();
            intent.putExtra(SCAN_RESULT, result);
            setResult(RESULT_OK, intent);
        }
        finish();
        //如果扫描失败则重新初始化
//        handler.sendEmptyMessage(R.id.restart_preview);
    }

    private void initCamera(final SurfaceHolder surfaceHolder) {
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
        //由于开启camera比较慢，所以将初始化工作放入线程。
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CameraManager.get().openDriver(surfaceHolder);
                    Point point = CameraManager.get().getCameraResolution();
                    int width = point.y;
                    int height = point.x;

                    int x = mCropLayout.getLeft() * width / mContainer.getWidth();
                    int y = mCropLayout.getTop() * height / mContainer.getHeight();

                    int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
                    int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

                    setX(x);
                    setY(y);
                    setCropWidth(cropWidth);
                    setCropHeight(cropHeight);
                    handler.init();
                    uiHandler.sendEmptyMessageDelayed(INIT_UI, 150);
                } catch (IOException ioe) {
                    uiHandler.sendEmptyMessage(CAMERA_ERROR);
                    return;
                } catch (RuntimeException e) {
                    uiHandler.sendEmptyMessage(CAMERA_ERROR);
                    return;
                }
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }


    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 网页
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_WEB,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }


}