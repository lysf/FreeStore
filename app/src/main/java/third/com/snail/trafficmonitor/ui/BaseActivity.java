package third.com.snail.trafficmonitor.ui;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by kevin on 14/12/15.
 */
public abstract class BaseActivity extends ActionBarActivity {
    protected void makeToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void makeToastLong(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, content, Toast.LENGTH_LONG).show();
            }
        });
    }
}
