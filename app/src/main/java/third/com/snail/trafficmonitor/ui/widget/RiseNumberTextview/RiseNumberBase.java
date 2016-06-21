package third.com.snail.trafficmonitor.ui.widget.RiseNumberTextview;


/**
 * Created by lic on 15/1/06.
 * 带有数字增加动画的textview的接口
 */
public interface RiseNumberBase {
    void start();

    RiseNumberTextView withNumber(float number);

    RiseNumberTextView withNumber(int number);

    RiseNumberTextView setDuration(long duration);

    void setOnEnd(RiseNumberTextView.EndListener callback);
}
