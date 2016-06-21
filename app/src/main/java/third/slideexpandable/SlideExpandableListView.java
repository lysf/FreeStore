package third.slideexpandable;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.snailgame.cjg.common.widget.LoadMoreListView;

/**
 * Simple subclass of listview which does nothing more than wrap
 * any ListAdapter in a SlideExpandalbeListAdapter
 */
class SlideExpandableListView extends LoadMoreListView {
	private SlideExpandableListAdapter adapter;

	public SlideExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Collapses the currently open view.
	 *
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if(adapter!=null) {
			return adapter.collapseLastOpen();
		}
		return false;
	}

	public void setAdapter(ListAdapter adapter) {
		this.adapter = new SlideExpandableListAdapter(adapter);
		super.setAdapter(this.adapter);
	}

	/**
	 * Registers a OnItemClickListener for this listview which will
	 * expand the item by default. Any other OnItemClickListener will be overriden.
	 *
	 * To undo call setOnItemClickListener(null)
	 *
	 * Important: This method call setOnItemClickListener, so the value will be reset
	 */
	public void enableExpandOnItemClick() {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				SlideExpandableListAdapter adapter = (SlideExpandableListAdapter)getAdapter();
				adapter.getExpandToggleButton(view).performClick();
			}
		});
	}


	@Override
	public Parcelable onSaveInstanceState() {
        if (adapter != null)
		    return adapter.onSaveInstanceState(super.onSaveInstanceState());
        else
            return super.onSaveInstanceState();
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if(!(state instanceof AbstractSlideExpandableListAdapter.SavedState)) {
			super.onRestoreInstanceState(state);
            return;
		}

		AbstractSlideExpandableListAdapter.SavedState ss = (AbstractSlideExpandableListAdapter.SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());

        if(adapter != null){
            adapter.onRestoreInstanceState(ss);
        }
	}
}