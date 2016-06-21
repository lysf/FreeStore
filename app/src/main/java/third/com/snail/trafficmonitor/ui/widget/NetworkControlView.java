package third.com.snail.trafficmonitor.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;

/**
 * Created by kevin on 14/11/20.
 */
public class NetworkControlView extends RelativeLayout {
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mMobileCost;
    private CheckBox mWifiCtrl;
    private CheckBox mMobileCtrl;
    private View mRoot;

    public NetworkControlView(Context context) {
        this(context, null);
    }

    public NetworkControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.engine_network_control_view, this, true);
        mIcon = (ImageView) mRoot.findViewById(R.id.image);
        mTitle = (TextView) mRoot.findViewById(R.id.title);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mMobileCost = (TextView) mRoot.findViewById(R.id.mobile_cost);
        } else {
            mMobileCost.setVisibility(GONE);
        }
        mWifiCtrl = (CheckBox) mRoot.findViewById(R.id.wifi_ctrl);
        mMobileCtrl = (CheckBox) mRoot.findViewById(R.id.mobile_ctrl);
    }

    public void setIcon(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    public TextView getMobileCostTv() {
        return mMobileCost;
    }

    public CharSequence getTitle() {
        return mTitle.getText();
    }

    public void setWifiListener(CompoundButton.OnCheckedChangeListener listener) {
        mWifiCtrl.setOnCheckedChangeListener(listener);
    }

    public void setMobileListener(CompoundButton.OnCheckedChangeListener listener) {
        mMobileCtrl.setOnCheckedChangeListener(listener);
    }

    public void toggleWifiCtrl() {
        mWifiCtrl.toggle();
    }

    public void toggleMobileCtrl() {
        mMobileCtrl.toggle();
    }

    public void setWifiChecked(boolean enable) {
        mWifiCtrl.setChecked(enable);
    }

    public void setMobileChecked(boolean enable) {
        mMobileCtrl.setChecked(enable);
    }

    public boolean isWifiChecked() {
        return mWifiCtrl.isChecked();
    }

    public boolean isMobileChecked() {
        return mMobileCtrl.isChecked();
    }

    public void disableWifi() {
        mWifiCtrl.setEnabled(false);
    }

    public void disableMobile() {
        mMobileCtrl.setEnabled(false);
    }
}
