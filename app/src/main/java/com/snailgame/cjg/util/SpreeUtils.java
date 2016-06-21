package com.snailgame.cjg.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.event.SpreeGetSuccessEvent;
import com.snailgame.cjg.event.SpreeTaoSuccessEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.personal.model.SpreeGetResult;
import com.snailgame.cjg.personal.model.SpreeModel;
import com.snailgame.cjg.personal.model.SpreeTaoResult;
import com.snailgame.cjg.spree.model.SpreeGiftInfo;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import third.slideexpandable.ExpandCollapseAnimation;

/**
 * Created by sunxy on 2014/11/17.
 */
public class SpreeUtils {

    public static final int REMAIN_NUM_UNLIMITED = -1;

    private static final int animationDuration = 330;


    /**
     * 礼包兑换
     *
     * @param activity
     * @param keyArray
     * @param spreeInfo
     */
    public static void getSpreeAction(final Activity activity, final ArrayList<String> keyArray,
                                      final SpreeGiftInfo spreeInfo, final String TAG) {
        String convertType = spreeInfo.getConvertType();
        if (!TextUtils.isEmpty(convertType)) {
            if (convertType.equals(SpreeModel.EXCHANGE_TYPE_INTEGRAL) || convertType.equals(SpreeModel.EXCHANGE_TYPE_TUTU)) {

                //如果 是积分或者兔兔币兑换，则糖醋兑换框
                String numTxt = spreeInfo.getIntegral() + ResUtil.getString(
                        convertType.equals(SpreeModel.EXCHANGE_TYPE_INTEGRAL)
                                ? R.string.slide_menu_point : R.string.slide_menu_currency);

                SpannableString builder = new SpannableString(ResUtil.getString(R.string.spree_popup_hint, numTxt));
                builder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)), 7, 7 + numTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                DialogUtils.showTwoButtonDialog(activity, ResUtil.getString(R.string.unbind_tips),
                        builder, new DialogUtils.ConfirmClickedLister() {
                            @Override
                            public void onClicked() {
                                createSpreeGetTask(activity, keyArray, spreeInfo, TAG);
                            }
                        });

            } else {
                if (spreeInfo.getiRemianNum() > 0)
                    createSpreeGetTask(activity, keyArray, spreeInfo, TAG);
                else
                    createSpreeTaoTask(activity, keyArray, spreeInfo, TAG);
            }
        }

    }

    /**
     * 领取/兑换礼包
     *
     * @param activity
     * @param keyArray
     * @param spreeInfo
     * @param TAG
     */
    private static void createSpreeGetTask(final Activity activity, final ArrayList<String> keyArray, final SpreeGiftInfo spreeInfo, String TAG) {
        String url;
        if (spreeInfo.getConvertType().equals(SpreeModel.EXCHANGE_TYPE_FREE))
            url = JsonUrl.getJsonUrl().JSON_URL_GET_SPREE;
        else if (spreeInfo.getConvertType().equals(SpreeModel.EXCHANGE_TYPE_INTEGRAL) || spreeInfo.getConvertType().equals(SpreeModel.EXCHANGE_TYPE_TUTU))
            url = JsonUrl.getJsonUrl().JSON_URL_EXCHANGE_SPREE;
        else {
            ToastUtils.showMsg(activity, ResUtil.getString(R.string.current_version_not_support));
            return;
        }

        String postBody = "iSId=" + spreeInfo.getiArticleId();
        final String key = String.valueOf(spreeInfo.getiArticleId());

        if (keyArray == null)
            return;

        if (keyArray.contains(key)) {
            ToastUtils.showMsg(activity, R.string.spree_doubleclick_notify);
            return;
        } else {
            keyArray.add(key);

            FSRequestHelper.newPostRequest(url, TAG, SpreeGetResult.class, new IFDResponse<SpreeGetResult>() {
                @Override
                public void onSuccess(SpreeGetResult result) {
                    if (result != null) {
                        if (result.getCode() == 0) {
                            createGiftResultDialog(activity, ResUtil.getString(R.string.get_gift_success),
                                    result.getItem(), true, ResUtil.getString(R.string.get_gift_success_title));
                            if (spreeInfo != null) {
                                spreeInfo.setcCdkey(result.getItem());
                                MainThreadBus.getInstance().post(new SpreeGetSuccessEvent(spreeInfo));
                            }
                        } else if (result.getCode() == AppConstants.ERR_CODE_NOT_BIND_PHONE) {
                            createNotBindPhoneDialog(activity, result.getMsg(),
                                    ResUtil.getString(R.string.get_gift_failed_title));
                        } else {
                            createGiftResultDialog(activity, result.getMsg(), "", false,
                                    ResUtil.getString(R.string.get_gift_failed_title));
                        }
                    } else {
                        createGiftResultDialog(activity, "UNKNOWN_ERROR", "", false,
                                ResUtil.getString(R.string.get_gift_failed_title));
                    }
                    keyArray.remove(key);
                }

                @Override
                public void onNetWorkError() {
                    ToastUtils.showMsg(activity, ResUtil.getString(R.string.spree_get_error));
                    keyArray.remove(key);
                }

                @Override
                public void onServerError() {
                    ToastUtils.showMsg(activity, ResUtil.getString(R.string.spree_get_error));
                    keyArray.remove(key);
                }
            }, postBody);
        }
    }


    /**
     * 淘号
     *
     * @param activity
     * @param keyArray
     * @param spreeInfo
     * @param TAG
     */
    private static void createSpreeTaoTask(final Activity activity, final ArrayList<String> keyArray, final SpreeGiftInfo spreeInfo, String TAG) {
        String url = JsonUrl.getJsonUrl().JSON_URL_TAO_SPREE;

        String postBody = "iSId=" + spreeInfo.getiArticleId();
        final String key = String.valueOf(spreeInfo.getiArticleId());

        if (keyArray == null)
            return;

        if (keyArray.contains(key)) {
            ToastUtils.showMsg(activity, R.string.spree_doubleclick_notify);
            return;
        } else {
            keyArray.add(key);

            FSRequestHelper.newPostRequest(url, TAG, SpreeTaoResult.class, new IFDResponse<SpreeTaoResult>() {
                @Override
                public void onSuccess(SpreeTaoResult result) {
                    if (result != null) {
                        if (result.getCode() == 0) {
                            if (result.isVal())
                                createGiftResultDialog(activity,
                                        ResUtil.getString(R.string.tao_got_gift_success),
                                        result.getItem(), true, ResUtil.getString(R.string.tao_gift_success_title));
                            else
                                createGiftResultDialog(activity, ResUtil.getString(R.string.tao_gift_success),
                                        result.getItem(), true, ResUtil.getString(R.string.tao_gift_success_title));
                            if (spreeInfo != null) {
                                spreeInfo.setiTao(spreeInfo.getiTao() + 1);
                                MainThreadBus.getInstance().post(new SpreeTaoSuccessEvent(spreeInfo));
                            }
                        } else {
                            createGiftResultDialog(activity, result.getMsg(), "", false,
                                    ResUtil.getString(R.string.tao_gift_failed_title));
                        }
                    } else {
                        createGiftResultDialog(activity, "UNKNOWN_ERROR", "", false,
                                ResUtil.getString(R.string.tao_gift_failed_title));
                    }

                    keyArray.remove(key);
                }

                @Override
                public void onNetWorkError() {
                    ToastUtils.showMsg(activity, ResUtil.getString(R.string.spree_tao_error));
                    keyArray.remove(key);
                }

                @Override
                public void onServerError() {
                    ToastUtils.showMsg(activity, ResUtil.getString(R.string.spree_tao_error));
                    keyArray.remove(key);
                }
            }, postBody);
        }

    }


    /**
     * 弹出 获取礼包 结果的Dialog
     *
     * @param msgShow 对话框内容
     * @param isSucc  是否获取礼包成功
     */
    public static void createGiftResultDialog(final Activity mActivity, String msgShow, final String cdKey, boolean isSucc, String title) {
        Dialog dialog = DialogUtils.spreeResultDialog(mActivity, msgShow, cdKey, isSucc, title);
        dialog.show();
    }

    /**
     * 获取礼包 用户未绑定手机号，提示绑定手机
     */
    public static void createNotBindPhoneDialog(final Activity mActivity, String msgShow, String title) {
        Dialog dialog = DialogUtils.spreeNotBindPhoneDialog(mActivity, msgShow, title);
        dialog.show();
    }

    public static List<SpreeGiftInfo> initInfoData(List<SpreeGiftInfo> listData) {
        if (listData == null)
            return null;

        for (SpreeGiftInfo item : listData) {
            String info = item.getsIntro();
            try {
                JSONObject object = new JSONObject(info);
                if (object.has("content"))
                    item.setContent(object.getString("content"));
                if (object.has("useMethod"))
                    item.setUseMethod(object.getString("useMethod"));
                if (object.has("deadline"))
                    item.setDeadline(object.getString("deadline"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listData;
    }


    /**
     * 设置礼包兑换  按钮
     *
     * @param context
     * @param cdKeyView
     * @param exchangeBtn
     * @param item
     */
    public static void setExchangeBtn(final Context context, TextView cdKeyView,
                                      TextView exchangeBtn, final SpreeGiftInfo item, boolean isSolid) {
        int whiteColor = context.getResources().getColor(R.color.white);
        exchangeBtn.setTextColor(isSolid ? whiteColor : context.getResources().getColor(R.color.btn_green_normal));

        exchangeBtn.setBackgroundResource(isSolid ?
                R.drawable.btn_green_selector : R.drawable.btn_exchange_selector);

        if (item.getiRemianNum() > 0) {
            if (TextUtils.isEmpty(item.getcCdkey())) {
                cdKeyView.setVisibility(View.GONE);
                if (item.getIntegral() > 0) {
                    exchangeBtn.setText(String.format(ResUtil.getString(R.string.score_for_exchange), item.getIntegral()));
                } else {
                    exchangeBtn.setText(R.string.not_get);
                }
            } else {
                exchangeBtn.setTextColor(isSolid ? whiteColor : context.getResources().getColor(R.color.copy_text_color));
                exchangeBtn.setText(R.string.copy);
                exchangeBtn.setBackgroundResource(isSolid ? R.drawable.btn_blue_selector : R.drawable.btn_copy_selecotr);
                exchangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //复制
                        ComUtil.copyToClipBoard(context, item.getcCdkey());
                    }
                });
                cdKeyView.setVisibility(View.VISIBLE);
                String headerTxt = ResUtil.getString(R.string.exchange_code);
                String currency = headerTxt + item.getcCdkey();
                SpannableStringBuilder currencyBuilder = new SpannableStringBuilder();
                currencyBuilder.append(currency);
                currencyBuilder.setSpan(new ForegroundColorSpan(ResUtil.getColor(R.color.red)), headerTxt.length(), currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cdKeyView.setText(currencyBuilder);
            }
        } else {
            if (item.getIntegral() > 0) { //需积分/兔兔币兑换的礼包不可淘号
                exchangeBtn.setText(R.string.none_get);
                exchangeBtn.setTextColor(isSolid ? whiteColor : context.getResources().getColor(R.color.discondition_normal));
                exchangeBtn.setBackgroundResource(isSolid ? R.drawable.btn_grey_selector : R.drawable.btn_frame_invalid);
                exchangeBtn.setOnClickListener(null);
            } else {
                exchangeBtn.setText(R.string.spree_tao);
                exchangeBtn.setTextColor(isSolid ? whiteColor : context.getResources().getColor(R.color.btn_yellow_normal));
                exchangeBtn.setBackgroundResource(isSolid ? R.drawable.btn_yellow_selector : R.drawable.btn_frame_yellow);
            }
            cdKeyView.setVisibility(View.GONE);
        }
    }


    public static void animateView(final View target, final int type) {
        Animation anim = new ExpandCollapseAnimation(
                target,
                type
        );
        anim.setDuration(animationDuration);
        final Animation.AnimationListener al = (Animation.AnimationListener) target.getTag(R.id.tag_animation);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                al.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                al.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                al.onAnimationEnd(animation);
            }
        });
        target.startAnimation(anim);
    }
}
