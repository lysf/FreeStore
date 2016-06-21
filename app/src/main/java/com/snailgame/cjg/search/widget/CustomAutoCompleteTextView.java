package com.snailgame.cjg.search.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by andy on 13-7-9.
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    public static final String AUTO_COMPLETE_TEXT = "text";

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get(AUTO_COMPLETE_TEXT);
    }


}
