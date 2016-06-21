package com.snailgame.cjg.detail.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.snailgame.cjg.BaseFSActivity;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.NetworkUtils;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

public class VideoPlayActivity extends BaseFSActivity implements MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener
        , SurfaceHolder.Callback, View.OnClickListener, MediaPlayer.OnErrorListener {
    private static final long ANIMATION_DURATION = 400;
    private static final long DELAY_TIME = 3 * 1000;
    private static final int HIDE_MESSAGE_WHAT = 0;
    private static final String KEY_CURRENT_SEEK_POSITION = "current_seek_position";
    private static final String KEY_SCREEN_LOCKED = "screen_locked";
    private int videoWidth;
    private int videoHeight;
    public MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView;
    @Bind(R.id.skbProgress)
    SeekBar skbProgress;
    @Bind(R.id.progressBarWait)
    ProgressBar progressBarWait;
    @Bind(R.id.progressContainer)
    LinearLayout progressContainer;
    @Bind(R.id.textViewPlayed)
    TextView timePlayed;
    @Bind(R.id.textViewLength)
    TextView timeTotal;
    @Bind(R.id.pause_or_stop)
    ImageView pauseOrStop;
    @Bind(R.id.bottom_divider)
    View divider;
    private Timer mTimer = new Timer();
    private String video_url;
    private boolean buttonsShow;
    private View actionbarView;
    private int progressContainerTranslateHeight;
    private int dividerTranslateHeight;
    private boolean mediaPrepared;
    private long pauseTime;
    private boolean isScreenRotate = false;

    //onDestroy removeCallBacks 防止内存泄露
    Handler handleProgress = new HandleProgressMsgHandler(this);

    Handler showOrHideHandler = new ShowOrHideMsgHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        surfaceView.setOnClickListener(this);
        pauseOrStop.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mTimer.schedule(mTimerTask, 0, 1000);
        progressContainerTranslateHeight = ResUtil.getDimensionPixelSize(R.dimen.dimen_48dp);
        dividerTranslateHeight = ResUtil.getDimensionPixelSize(R.dimen.dimen_49dp);
        hideButtons();
        showNoWifiTips();
    }

    private void showNoWifiTips() {
        if (!NetworkUtils.isWifiEnabled(this))
            ToastUtils.showMsg(this, R.string.no_wifi_tips);
    }

    @Override
    protected void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            video_url = intent.getStringExtra(AppConstants.VIDEO_URL);
            actionbarView = ActionBarUtils.makeDetailActionbarStyle(this, intent.getStringExtra(AppConstants.COMPANY_NAME), true, false, false);
            actionbarView.findViewById(R.id.actionBarLayoutHolder).setBackgroundResource(R.drawable.ic_video_actionbar_bg);
            TextView title = (TextView) actionbarView.findViewById(R.id.tv_collect_title);
            title.setTextColor(ResUtil.getColor(R.color.white));
            ComUtil.setDrawableLeft(title, R.drawable.ic_back_normal);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_play;
    }

    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };



    private void handleVideoProgress() {
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (duration > 0) {
            long pos = skbProgress.getMax() * position / duration;
            skbProgress.setProgress((int) pos);
        }
    }




    private void showOrHideButton() {
        if (buttonsShow)
            hideButtons();
        else {
            showButtons();
        }
    }


    public void playUrl(String videoUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();//prepare之后自动播放
        } catch (IllegalArgumentException e) {
            showToast(getString(R.string.video_play_error));
            e.printStackTrace();
        } catch (IllegalStateException e) {
            showToast(getString(R.string.video_play_error));
            e.printStackTrace();
        } catch (IOException e) {
            showToast(getString(R.string.video_play_error));
            e.printStackTrace();
        }
    }

    private void showToast(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(VideoPlayActivity.this, string, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    public void stop() {
        if (mTimerTask != null)
            mTimerTask.cancel();
        if (mTimer != null)
            mTimer.cancel();
        if (handleProgress != null)
            handleProgress.removeCallbacks(null);
        if (showOrHideHandler != null)
            showOrHideHandler.removeCallbacks(null);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            playUrl(video_url);
        } catch (Exception e) {
            showToast(getString(R.string.video_play_error));
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }


    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer mediaPlayer) {
        progressBarWait.setVisibility(View.GONE);
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            mediaPlayer.start();
            if (isScreenRotate) {
                pause();
                mediaPlayer.seekTo((int) pauseTime);
                skbProgress.setProgress((int) pauseTime / 1000);
                isScreenRotate = false;
            }
        }
        int duration = mediaPlayer.getDuration() / 1000; // duration in seconds
        skbProgress.setMax(duration);
        timeTotal.setText(ComUtil.durationInSecondsToString(duration));
        // progressContainer.setVisibility(View.VISIBLE);
        mediaPrepared = true;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        finish();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
    }


    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        progressBarWait.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surfaceView:
                if (mediaPrepared) {
                    if (buttonsShow) {
                        hideButtons();
                    } else {
                        showButtons();
                        showOrHideHandler.removeMessages(HIDE_MESSAGE_WHAT);
                        showOrHideHandler.sendEmptyMessageDelayed(HIDE_MESSAGE_WHAT, DELAY_TIME);
                    }
                }

                break;
            case R.id.pause_or_stop:
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    mediaPlayer.start();
                    pauseOrStop.setImageResource(R.drawable.ic_play_video_resume);
                }
                break;
        }
    }

    private void pause() {
        if (!isScreenRotate)
            pauseTime = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        pauseOrStop.setImageResource(R.drawable.ic_play_video_pause);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (mediaPlayer != null)
                timePlayed.setText(ComUtil.durationInSecondsToString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showOrHideHandler.removeMessages(HIDE_MESSAGE_WHAT);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                progressBarWait.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                showOrHideHandler.sendEmptyMessageDelayed(HIDE_MESSAGE_WHAT, DELAY_TIME);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_CURRENT_SEEK_POSITION, pauseTime);
        outState.putBoolean(KEY_SCREEN_LOCKED, true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pauseTime = savedInstanceState.getLong(KEY_CURRENT_SEEK_POSITION);
        isScreenRotate = savedInstanceState.getBoolean(KEY_SCREEN_LOCKED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || mediaPlayer.isLooping()))
            pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }


    private void showButtons() {
        if (!animIsRunning) {
            getSupportActionBar().show();
            bottomInAnimation();
            pauseOrStop.setVisibility(View.VISIBLE);
            buttonsShow = true;
            if (mediaPlayer != null && mediaPlayer.isPlaying())
                pauseOrStop.setImageResource(R.drawable.ic_play_video_resume);
            else {
                pauseOrStop.setImageResource(R.drawable.ic_play_video_pause);
            }
        }
    }

    private void hideButtons() {
        if (!animIsRunning) {
            getSupportActionBar().hide();
            bottomOutAnimation();
            pauseOrStop.setVisibility(View.GONE);
            buttonsShow = false;
        }
    }

    private boolean animIsRunning = false;

    private void bottomInAnimation() {

        animIsRunning = true;
        ViewPropertyAnimator.animate(divider).translationYBy(-dividerTranslateHeight).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animIsRunning = false;
            }
        }).setDuration(ANIMATION_DURATION).start();
        ViewPropertyAnimator.animate(progressContainer).translationYBy(-progressContainerTranslateHeight).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animIsRunning = false;
            }
        }).setDuration(ANIMATION_DURATION).start();

    }

    private void bottomOutAnimation() {

        animIsRunning = true;
        ViewPropertyAnimator.animate(divider).translationYBy(dividerTranslateHeight).setDuration(ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animIsRunning = false;
            }
        }).start();
        ViewPropertyAnimator.animate(progressContainer).translationYBy(progressContainerTranslateHeight).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animIsRunning = false;
            }
        }).setDuration(ANIMATION_DURATION).start();
    }

    static class HandleProgressMsgHandler extends Handler {
        private WeakReference<VideoPlayActivity> mActivity;

        public HandleProgressMsgHandler(VideoPlayActivity activity) {
            this.mActivity = new WeakReference<VideoPlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleVideoProgress();
            }
        }
    }


    static class ShowOrHideMsgHandler extends Handler {
        private WeakReference<VideoPlayActivity> mActivity;

        public ShowOrHideMsgHandler(VideoPlayActivity activity) {
            this.mActivity = new WeakReference<VideoPlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayActivity activity = mActivity.get();
            if (activity != null) {
                activity.showOrHideButton();
            }
        }
    }
}