package third.com.snail.trafficmonitor.ui.widget;

/**
 * Created by kevin on 15/1/5.
 */
public interface ILoading {
    void prepareLoading();
    void showLoading();
    void dismissLoading(boolean hasData);
}
