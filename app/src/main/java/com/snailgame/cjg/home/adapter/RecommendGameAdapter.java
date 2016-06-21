package com.snailgame.cjg.home.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.adapter.CommonListItemAdapter;
import com.snailgame.cjg.common.model.AppInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.home.model.ModuleModel;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

/**
 * 首页游戏推荐
 * Created by sunxy on 2014/9/24.
 */
public class RecommendGameAdapter extends CommonListItemAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private ModuleModel moduleModel;
    private Activity context;
    private int[] mRoute;
    private List<AppInfo> gameLists;

    public RecommendGameAdapter(Activity context, ModuleModel moduleModel, List<AppInfo> gameLists, int[] route) {
        super(context, gameLists, AppConstants.FRAGMENT_HOME, route);
        this.moduleModel = moduleModel;
        this.context = context;
        this.gameLists = gameLists;
        mRoute = route;
    }

    @Override
    public int getCount() {
        if (gameLists == null)
            return 1;//顶部标题栏
        return super.getCount() + 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int p = position;
        if (position == 0) {
            convertView = getHeaderView(true);
        } else {
            convertView = super.getView(position - 1, convertView, parent);
        }

        //解决首页精品游戏 最后一条有横线
        if (convertView != null) {
            CommonListItemViewHolder holder = (CommonListItemViewHolder) convertView.getTag(TAG_NORMAL_LIST);
            if (holder != null) {
                if (position >= getCount() - 1)
                    holder.divider.setVisibility(View.GONE);
                else {
                    holder.divider.setVisibility(View.VISIBLE);
                }

            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInfo appInfo = getItem(p - 1);
                if (appInfo != null) {
                    int[] route = mRoute.clone();
                    route[AppConstants.STATISTCS_DEPTH_FOUR] = p + 1;
                    JumpUtil.startDetailActivity(context, appInfo.getAppId(), route);
                }
            }
        });
        return convertView;
    }

    //TODO 与ModuleBaseAdapter合并
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
