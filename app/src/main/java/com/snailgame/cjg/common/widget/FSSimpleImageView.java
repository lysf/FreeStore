package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.snailgame.cjg.R;
import com.snailgame.cjg.util.HostUtil;
import com.snailgame.fastdev.util.ResUtil;
import com.snailgame.fastdev.view.SimpleImageView;

/**
 * Created by xixh on 2015/11/2.
 */
public class FSSimpleImageView extends SimpleImageView {
    public FSSimpleImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public FSSimpleImageView(Context context) {
        super(context);

    }

    public FSSimpleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FSSimpleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FSSimpleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FSSimpleImageView(Context context, boolean hasOveylay) {
        super(context);
        if (hasOveylay)
            getHierarchy().setControllerOverlay(ResUtil.getDrawable(R.drawable.ab_btn_selector));
    }



    /**
     * 图片加载Uri解析
     *
     * @param uriString
     * @return
     */
    public Uri UriParse(String uriString) {
        if (TextUtils.isEmpty(uriString))
            uriString = "default_img_url_null";
        uriString = HostUtil.replaceUrl(uriString);

        return Uri.parse(uriString);
    }

    /**
     * Sets a new placeholder drawable
     */
    public void setPlaceHolderImage() {
        setPlaceHolderImageRes(R.drawable.pic_ragle_loading);
    }


    /**
     * view reused in list
     *
     * @param url
     */
    @Override
    public void setImageUrlAndReUse(String url) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(UriParse(url))
                .setAutoPlayAnimations(false)
                .setOldController(getController())
                .build();

        setController(controller);
    }

    /**
     * Sets a new Overlay drawable and placeholder drawable.
     */
    public void setControllerOverlayAndPlaceHolder() {
        setControllerOverlayAndPlaceHolderRes(R.drawable.ab_btn_selector, R.drawable.pic_ragle_loading);
    }

}
