package com.snailgame.cjg.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;

import butterknife.Bind;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AboutActivity extends SwipeBackActivity implements OnClickListener {
    @Bind(R.id.tv_version)
    TextView mVersion;
    @Bind(R.id.about_title)
    TextView tvTitle;
    @Bind(R.id.about_protocol)
    TextView tvProtocol;

    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        // ActionBar
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), R.string.about_action_bar_title);
        String versionName = ComUtil.getVersionNameByPackage(getPackageName(), this);
        mVersion.setText(getString(R.string.about_version, versionName));

        tvTitle.setText(Html.fromHtml(getString(R.string.about_title)));

        tvProtocol.setOnClickListener(this);
        tvProtocol.setText(Html.fromHtml(getString(R.string.about_protocol)));
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_protocol:
                startActivity(WebViewActivity.newIntent(AboutActivity.this,
                        getString(R.string.about_url)));
                break;

            default:
                break;
        }

    }
}
