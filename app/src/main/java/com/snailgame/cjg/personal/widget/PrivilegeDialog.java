package com.snailgame.cjg.personal.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.personal.adapter.PrivilegeDialogAdapter;
import com.snailgame.cjg.personal.model.PrivilegeDialogModel;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.fastdev.image.BitmapManager;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.ButterKnife;

/**
 * Created by TAJ_C on 2015/7/22.
 */
public class PrivilegeDialog {

    private Context mContext;
    private Dialog aDialog;
    private TextView tvNotopenTitle;
    private TextView tvOpen;
    private TextView tvOpenTitle;
    private LoadMoreListView lvPrivilegeAwards;

    public PrivilegeDialog(Context context) {
        this.mContext = context;
        aDialog = new Dialog(context, R.style.Dialog);
        aDialog.setContentView(R.layout.privilege_dialog);
        aDialog.setCanceledOnTouchOutside(true);
        tvNotopenTitle = ButterKnife.findById(aDialog, R.id.tv_notopen_title);
        tvOpen = ButterKnife.findById(aDialog, R.id.btn_open);
        tvOpenTitle = ButterKnife.findById(aDialog, R.id.tv_open_title);
        lvPrivilegeAwards = ButterKnife.findById(aDialog, R.id.lv_privilege_awards);
    }

    /**
     * 隐藏所有弹出框
     */
    public void hidePopup() {
        if (aDialog != null && aDialog.isShowing()) {
            aDialog.dismiss();
        }
    }


    /**
     * 弹出弹框
     *
     * @param item
     */
    public void showPopup(final UserPrivilegesModel.ModelItem item) {
        final PrivilegeDialogModel model = JSON.parseObject(item.getsDesc(), PrivilegeDialogModel.class);
        if (model == null) {
            hidePopup();
        } else {
            if (item.isOpened()) {
                tvNotopenTitle.setVisibility(View.GONE);
                tvOpenTitle.setVisibility(View.VISIBLE);
                setParsedText(item, tvOpenTitle, model.getLightTitle());
                tvOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aDialog.dismiss();
                    }
                });
                tvOpen.setText(R.string.i_know);
            } else {
                tvNotopenTitle.setVisibility(View.VISIBLE);
                tvOpenTitle.setVisibility(View.GONE);
                setParsedText(item, tvNotopenTitle, model.getGrayTitle());
                tvOpen.setText(model.getButtonText());
                tvOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(WebViewActivity.newIntent(mContext, getClickUrl(item.getcOpenInfo())));
                        aDialog.dismiss();
                    }
                });
            }
            PrivilegeDialogAdapter privilegeDialogAdapter = new PrivilegeDialogAdapter(model.getItemList());
            lvPrivilegeAwards.setAdapter(privilegeDialogAdapter);
            aDialog.show();
        }
    }

    private void setParsedText(UserPrivilegesModel.ModelItem item, final TextView textView, final String title) {
        textView.setText(title.replaceAll(AppConstants.PRIVILEGE_PLACEHOLDER_STRING, "      "));
        int maxSize = ResUtil.getDimensionPixelSize(R.dimen.dimen_20dp);
        BitmapManager.showImg(item.isOpened() ? item.getcLightIcon() : item.getcLightIcon(),
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            Bitmap bitmap = response.getBitmap();

                            SpannableString spannableString = new SpannableString(title);
                            int index = title.indexOf(AppConstants.PRIVILEGE_PLACEHOLDER_STRING);
                            if (index >= 0) {
                                spannableString.setSpan(new ImageSpan(mContext, bitmap), index,
                                        index + AppConstants.PRIVILEGE_PLACEHOLDER_STRING.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            textView.setText(spannableString);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, maxSize, maxSize
        );
    }

    /**
     * 获取点击的超链接
     *
     * @param openInfoJson
     * @return
     */
    private String getClickUrl(String openInfoJson) {
        LogUtils.e(openInfoJson);
        try {
            JSONObject object = JSON.parseObject(openInfoJson);

            if (object.containsKey("url")) {
                LogUtils.e(object.getString("url"));
                return object.getString("url");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
