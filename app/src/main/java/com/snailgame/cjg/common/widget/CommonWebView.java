package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/*
 * 重写webview的
 * Created by xixh 14-08-29
 */
public class CommonWebView extends WebView {
	public CommonWebView(Context context) {
		super(context);
	}
	
	public CommonWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CommonWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 由于免用户url跳转后 身份丢失，此版本暂且不在webview中实行url跳转
//	@Override
//	public void loadUrl(String url) {
//		super.loadUrl(GlobalVar.replaceWebUrl(url));
//	}
//	
//	@Override
//	public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
//		super.loadUrl(GlobalVar.replaceWebUrl(url), additionalHttpHeaders);
//	}
}
