package com.snailgame.cjg.friend.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.ColorTextView;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.fastdev.util.ResUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TAJ_C on 2016/5/25.
 */
public class FriendTagUtil {

    private Map<String, Integer> colorMap = new HashMap<>();

    private int getTagColor(String tag) {
        if (tag.equals(ResUtil.getString(R.string.news_top))) {
            return R.color.red;
        } else if (tag.contains(ResUtil.getString(R.string.news_adv))) {
            return R.color.color_009bfe;
        } else {
            int color = 0;
            if (colorMap == null) {
                colorMap = new HashMap<>();
            }

            if (colorMap.get(tag) == null) {
                int random = (int) (Math.random() * 3);
                switch (random) {
                    case 0:
                        color = R.color.color_17ce8b;
                        break;
                    case 1:
                        color = R.color.color_ff9a23;
                        break;
                    case 2:
                        color = R.color.color_13bfde;
                    default:
                        break;
                }
                colorMap.put(tag, color);
                return color;
            } else {
                return colorMap.get(tag).intValue();
            }

        }
    }

    @NonNull
    public ColorTextView getColorTextView(Context context, String tag) {
        ColorTextView view = new ColorTextView(context);
        view.setPadding(ComUtil.dpToPx(4), ComUtil.dpToPx(0), ComUtil.dpToPx(4), ComUtil.dpToPx(0));
        view.setColor(ResUtil.getColor(getTagColor(tag)));
        view.setText(tag);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(10);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ComUtil.dpToPx(14));
        lp.setMargins(0, 0, ComUtil.dpToPx(8), 0);
        view.setLayoutParams(lp);
        return view;
    }
}
