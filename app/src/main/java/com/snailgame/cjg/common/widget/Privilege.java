package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.cjg.personal.widget.PrivilegeDialog;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;


/**
 * 特权图标列表
 * Created by Pancl on 2015/5/7.
 */
public class Privilege extends LinearLayout implements View.OnClickListener {
    private int itemHeight;

    private Context context;
    private int margins;
    private PrivilegeDialog dialog;

    public Privilege(Context context) {
        super(context);
    }

    public Privilege(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Privilege);
        itemHeight = (int) a.getDimension(R.styleable.Privilege_itemHeight, 0);
        this.margins = (int) a.getDimension(R.styleable.Privilege_itemMargin,
                ResUtil.getDimension(R.dimen.privilege_margin_right));
        a.recycle();
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        this.context = context;
        dialog = new PrivilegeDialog(context);
    }

    /**
     * 重新绘制特权图标列表
     */
    public void resetView() {
        hidePopup();
        removeAllViews();
        if (GlobalVar.getInstance().getUsrPrivileges() != null) {
            List<UserPrivilegesModel.ModelItem> items = GlobalVar.getInstance().getUsrPrivileges().getItemList();
            for (UserPrivilegesModel.ModelItem item : items) {
                LayoutParams params;
                if (itemHeight == 0)
                    params = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                else {
                    params = new LayoutParams(itemHeight, itemHeight);
                }

                if (items.indexOf(item) == items.size() - 1) {
                    params.setMargins(0, 0, 0, 0);
                } else {
                    params.setMargins(0, 0, margins, 0);
                }
                FSSimpleImageView imageView = new FSSimpleImageView(context);
                imageView.setLayoutParams(new LayoutParams(itemHeight, itemHeight));
                imageView.setImageUrl(item.isOpened() ? item.getcLightIcon() : item.getcGrayIcon());
                imageView.setTag(item);
                addView(imageView, params);
            }

        }
    }


    /**
     * 增加特权点击效果
     */
    public void setViewClickListener() {
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            view.setOnClickListener(this);
        }
    }

    /**
     * 是否隐藏特权信息
     *
     * @param isShow
     */
    public void setPrivilegesVisibility(boolean isShow) {
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            view.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    @Override
    public void onClick(View view) {
        showPopupText(view);
    }

    /**
     * 展开弹出富文本
     *
     * @param view
     */
    private void showPopupText(View view) {
        UserPrivilegesModel.ModelItem item = (UserPrivilegesModel.ModelItem) view.getTag();
        if(dialog != null){
            dialog.showPopup(item);
        }
    }


    /**
     * 关闭所有弹出框
     */
    public void release() {
        hidePopup();
    }

    /**
     * 隐藏所有弹出框
     */
    private void hidePopup() {
        dialog.hidePopup();
    }
}
