package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.home.model.ContentModel;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;

/**
 * Created by sunxy on 2014/9/19.
 * 模板基类 统一处理标题栏
 */
public abstract class ModuleBaseAdapter extends BaseAdapter {
    protected Activity context;
    protected ModuleModel moduleModel;
    protected ArrayList<ContentModel> children;
    protected LayoutInflater inflater;
    protected int[] mRoute;

    public ModuleBaseAdapter(Activity context, ModuleModel moduleModel, int[] route) {
        this.context = context;
        this.moduleModel = moduleModel;
        children = moduleModel.getChilds();
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        mRoute = route;
    }

    @Override
    public ContentModel getItem(int position) {
        if (children != null && position < children.size())
            return children.get(position);
        return null;
    }

    @Override
    public int getCount() {
        //+1模板顶部栏目
        if (ListUtils.isEmpty(children))
            return 1;
        return children.size() + 1;
    }

    protected View getHeaderView(boolean isShowHeaderDivider) {
        View header = inflater.inflate(R.layout.home_card_header, null);
        if (!isShowHeaderDivider)
            header.findViewById(R.id.header_divider).setVisibility(View.GONE);

        if (moduleModel != null) {
            RelativeLayout headerRoot = (RelativeLayout) header.findViewById(R.id.headerRoot);
            TextView title = (TextView) header.findViewById(R.id.card_header_title);
            TextView more = (TextView) header.findViewById(R.id.card_header_more);

            if (!TextUtils.isEmpty(moduleModel.getsTitle())) {
                title.setText(Html.fromHtml(moduleModel.getsTitle()));

                if (!TextUtils.isEmpty(moduleModel.getsSubtitle())) {
                    more.setText(moduleModel.getsSubtitle());
                }
                if (!TextUtils.isEmpty(moduleModel.getsImageUrl())) {
                    setDrawableRight(more, moduleModel.getsImageUrl());
                }
                headerRoot.setOnClickListener(onClickListener);
            } else {
                more.setVisibility(View.GONE);
                headerRoot.setBackgroundColor(ResUtil.getColor(R.color.white));
            }

        }
        return header;
    }

    /**
     * 模板栏目左侧小图标（网络获取图标）
     *
     * @param title   栏目标题
     * @param iconUrl 图标URL
     */
    private void setDrawableRight(final TextView title, String iconUrl) {
        BitmapManager.showImg(iconUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            Bitmap bitmap = response.getBitmap();
                            BitmapDrawable drawable = new BitmapDrawable(ResUtil.getResources(), bitmap);
                            setDrawableRight(title, drawable);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
    }

    protected void setDrawableRight(TextView textView, Drawable drawable) {
        drawable.setBounds(0, 0, ComUtil.dpToPx(15), ComUtil.dpToPx(15));
        textView.setCompoundDrawables(null, null, drawable, null);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JumpUtil.templateJump(context, moduleModel.getsPinUrl(), moduleModel.getcSource(), "", "", mRoute);
        }
    };


}
