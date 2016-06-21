package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;

public class AppDetailActionBar extends RelativeLayout {
	private TextView mTitile;

	private TextView mCollectTitle;

	public TextView getmCollectTitle() {
		return mCollectTitle;
	}

	public AppDetailActionBar(Context context) {
		super(context);
		initUI();
	}

	public AppDetailActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI();
	}

	public AppDetailActionBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initUI();
	}
	
	private void initUI() {
		mTitile = (TextView) findViewById(R.id.tv_title);
        mCollectTitle = (TextView) findViewById(R.id.tv_collect_title);
	}

	public void setTitle(CharSequence title, boolean isCollect) {
		if (mTitile == null || mCollectTitle == null) {
		    initUI();
		}
        if(isCollect){
            mCollectTitle.setText(title);
        }else{
            mTitile.setText(title);
        }

	}

	public void setTitle(int resId, boolean isCollect) {
		if (mTitile == null || mCollectTitle == null) {
		    initUI();
		}
        if(isCollect){
            mCollectTitle.setText(resId);
        }else{
            mTitile.setText(resId);
        }
	}
}
