package com.snailgame.cjg.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.snailgame.cjg.common.widget.PopupDialog;
import com.snailgame.cjg.util.DialogUtils;

/**
 * Created by TAJ_C on 2015/10/21.
 */
public class AppAutoInstallSettingDialogActivity extends Activity {

    PopupDialog dialog;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AppAutoInstallSettingDialogActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = DialogUtils.getAppAutoInstallSettingDialog(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }
}
