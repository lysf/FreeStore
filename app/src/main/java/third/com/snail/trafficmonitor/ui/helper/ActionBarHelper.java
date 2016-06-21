package third.com.snail.trafficmonitor.ui.helper;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snailgame.cjg.R;

/**
 * Created by kevin on 14/12/25.
 */
public class ActionBarHelper {

    public static void makeCommonActionBar(ActionBarActivity activity, @StringRes int title) {
        makeCommonActionBar(activity, activity.getString(title));
    }

    public static void makeCommonActionBar(final ActionBarActivity activity, @NonNull String title) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) return;
        View view = LayoutInflater.from(activity).inflate(R.layout.engine_actionbar_custom_layout, null);
        view.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.red)));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        TextView titleTv = (TextView) view.findViewById(R.id.engine_ab_title);
        if (titleTv == null) {
            throw new RuntimeException("Custom actionbar must have at least a TextView as title!");
        }
        titleTv.setText(title);
        titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                activity.overridePendingTransition(0, R.anim.engine_slide_right_out_anim);
            }
        });
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(view);
    }

}
