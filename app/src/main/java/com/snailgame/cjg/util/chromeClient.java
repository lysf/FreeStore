package com.snailgame.cjg.util;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class chromeClient extends WebChromeClient {
    ProgressBar mProgressBar;

    TextView titleView;

    public chromeClient(ProgressBar ProgressBar, TextView titleView) {
        this.mProgressBar = ProgressBar;
        this.titleView = titleView;
    }

    public chromeClient(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress < 100 && mProgressBar.getVisibility() == ProgressBar.GONE) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
        mProgressBar.setProgress(newProgress);
        if (newProgress == mProgressBar.getMax()) {
            mProgressBar.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (titleView != null) {
            titleView.setText(title);
        }
    }
}
