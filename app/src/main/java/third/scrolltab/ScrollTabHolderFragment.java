package third.scrolltab;

import android.widget.AbsListView;

import com.snailgame.cjg.common.ui.AbsBaseFragment;

public abstract class ScrollTabHolderFragment extends AbsBaseFragment implements ScrollTabHolder {

	protected ScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
		// nothing
	}

}