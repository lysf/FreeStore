package com.snailgame.cjg.personal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.UserPrivilegesModel;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.JumpUtil;
import com.snailgame.cjg.util.model.JumpInfo;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 代金券通知弹框
 * Created by pancl on 2015/5/7.
 */
public class VoucherNotifyActivity extends Activity {
    static String TAG = VoucherNotifyActivity.class.getName();

    private Dialog aDialog;

    public static Intent newIntent(Context context, List<UserPrivilegesModel.ModelItem> itemList) {
        Intent intent = new Intent(context, VoucherNotifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putParcelableArrayListExtra(AppConstants.KEY_PRIVILEGES, new ArrayList<>(itemList));
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<UserPrivilegesModel.ModelItem> items = intent.getParcelableArrayListExtra(AppConstants.KEY_PRIVILEGES);
            popUpVouchersDialog(items);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        if (aDialog != null) {
            aDialog.dismiss();
        }
    }

    /**
     * 弹出代金券领用对话框
     *
     * @param itemList
     */
    private void popUpVouchersDialog(List<UserPrivilegesModel.ModelItem> itemList) {
        if (!ListUtils.isEmpty(itemList)) {
            final List<UserPrivilegesModel.ModelItem> items = new ArrayList<>(itemList);
            UserPrivilegesModel.ModelItem item = items.get(0);

            if (item.isOpened() && TextUtils.equals(item.getcNotice(), UserPrivilegesModel.ModelItem.NEED_NOTICE)) {
                try {
                    JSONObject object = JSON.parseObject(item.getcNoticeInfo());

                    int type = object.containsKey("type") ? object.getIntValue("type") : 0;
                    String url = object.containsKey("url") ? object.getString("url") : "";
                    String pageId = object.containsKey("pageId") ? object.getString("pageId") : "";
                    String pageContent = object.containsKey("pageContent") ? object.getString("pageContent") : "";
                    String imgUrl = object.containsKey("imgUrl") ? object.getString("imgUrl") : "";

                    final JumpInfo messagePushExInfo = new JumpInfo();
                    messagePushExInfo.setPageId(pageId);
                    messagePushExInfo.setPageTitle(pageContent);
                    messagePushExInfo.setType(type);
                    messagePushExInfo.setUrl(url);

                    aDialog = new Dialog(this, R.style.Dialog);
                    aDialog.setContentView(R.layout.voucher_popup_dialog);
                    aDialog.setCanceledOnTouchOutside(false);
                    Button btnCancel = ButterKnife.findById(aDialog, R.id.btn_cancel);
                    Button btnOK = ButterKnife.findById(aDialog, R.id.btn_ok);
                    FSSimpleImageView popImg = ButterKnife.findById(aDialog, R.id.iv_voucher_popup_img);
                    if (TextUtils.isEmpty(imgUrl))
                        popImg.setVisibility(View.GONE);
                    else {
                        popImg.setVisibility(View.VISIBLE);
                        popImg.setImageUrl(imgUrl);
                    }

                    final TextView popMsg = ButterKnife.findById(aDialog, R.id.iv_voucher_popup_msg);
                    popMsg.setText(Html.fromHtml(pageContent));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            aDialog.dismiss();
                        }
                    });
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            aDialog.dismiss();
                            JumpUtil.JumpActivity(VoucherNotifyActivity.this, messagePushExInfo, null);
                        }
                    });
                    aDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            popUpVouchersDialog(items.subList(1, items.size()));
                        }
                    });
                    aDialog.show();

                    notifyPrivilegeChange(item.getiPrivilegeId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    popUpVouchersDialog(items.subList(1, items.size()));
                }
            } else {
                popUpVouchersDialog(items.subList(1, items.size()));
            }
        } else {
            finish();
        }
    }

    /**
     * 更新用户信息请求
     *
     * @param privilegeId
     */
    private void notifyPrivilegeChange(int privilegeId) {
        String url = JsonUrl.getJsonUrl().JSON_URL_NOTIFY_PRIVILEGE_CHANGE;
        String postBody = AccountUtil.getLoginParams().replace("?", "") + "&iPrivilegeId=" + privilegeId + "&notice=0";
        FSRequestHelper.newPostRequest(url, TAG, String.class, new IFDResponse<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, postBody);
    }
}