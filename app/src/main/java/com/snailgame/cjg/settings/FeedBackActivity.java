package com.snailgame.cjg.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ToastUtils;

import butterknife.Bind;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 反馈问题界面
 */
public class FeedBackActivity extends SwipeBackActivity {
    static String TAG = FeedBackActivity.class.getName();
    static String APP_ID = "app_id";
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.edit_title)
    EditText editContact;

    /**
     * 推荐界面反馈问题是必须带有id
     */
    public static Intent newIntent(Context context, String appId) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        intent.putExtra(APP_ID, appId);
        return intent;
    }

    /**
     * 设置界面
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, FeedBackActivity.class);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        // ActionBar
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.feed_back);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.btn_ok)
    void submitClick() {
        String phone = editContact.getText().toString();
        String content = editContent.getText().toString();
        if (content == null || content.trim().length() == 0) {
            ToastUtils.showMsg(FeedBackActivity.this, getString(
                    R.string.feed_back_content_null));
        } else {
            if (getIntent().getStringExtra(APP_ID) == null || TextUtils.isEmpty(getIntent().getStringExtra(APP_ID))) {
                FeedbackUtil.sendAppFeedback(FeedBackActivity.this,
                        content, phone, String.valueOf(AppConstants.APP_ID), TAG);
            } else {
                FeedbackUtil.sendAppFeedback(FeedBackActivity.this,
                        content, phone, getIntent().getStringExtra(APP_ID), TAG);
            }
        }
    }
}