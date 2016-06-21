package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.detail.model.CommentCommitModel;
import com.snailgame.cjg.event.CommentChangedEvent;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ResUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;

/**
 * Created by lic on 2015/6/5.
 */
public class CommentDialog extends BottomSheet implements View.OnClickListener {
    static String TAG = CommentDialog.class.getName();

    @Bind(R.id.rb_commit_level)
    RatingBar mCommitLevelView;
    @Bind(R.id.et_comment)
    EditText mContentEditView;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;  //提交按钮
    @Bind(R.id.tv_total_comment)
    TextView mTotalView;
    @Bind(R.id.rb_comment)
    RatingBar mCommentLevelView;
    @Bind(R.id.tv_comment_score)
    TextView mScoreView;
    private int appId;
    private int totalNum;
    private float commentLevel;
    private int currentCommitLevel = 5;
    private String status = "0";
    private static final int CODE_ILLEGAL_CHARACTER = 9002; //提交敏感词 ,服务器返回的code码
    private static final String VALUE_REPLY = "3";
    private Handler handler = new Handler();

    public CommentDialog(Context context) {
        super(context);
    }

    public CommentDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.comment_dialog, null);
        setContentDialogView(mDialogView);
        mBtnCommit.setOnClickListener(this);
        mTotalView.setText(String.valueOf(totalNum));

        //评分 精度小数点后一位
        BigDecimal b = new BigDecimal(commentLevel);
        commentLevel = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        if (commentLevel > 5.0)
            commentLevel = 5.0f;

        mScoreView.setText(String.valueOf(commentLevel));
        mCommentLevelView.setRating(commentLevel);

        mContentEditView.setPadding(0, ResUtil.getDimensionPixelSize(R.dimen.dimen_10dp),
                0, ResUtil.getDimensionPixelSize(R.dimen.dimen_10dp));
        mContentEditView.addTextChangedListener(mTextWatcher);
        mContentEditView.setFocusable(false);
        mCommitLevelView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                currentCommitLevel = (int) rating;
            }
        });
        getUsrComment();
    }

    public CommentDialog setData(int appId, int totalNum, float commentLevel) {
        this.appId = appId;
        this.totalNum = totalNum;
        this.commentLevel = commentLevel;
        return this;
    }

    @Override
    public AbsListView getAbsListView() {
        return null;
    }

    //弹出软键盘
    public void showKeyboard() {
        if (mContentEditView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //设置可获得焦点
                    mContentEditView.setFocusable(true);
                    mContentEditView.setFocusableInTouchMode(true);
                    //请求获得焦点
                    mContentEditView.requestFocus();
                    //调用系统输入法
                    InputMethodManager inputManager = (InputMethodManager) mContentEditView
                            .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mContentEditView, 0);
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                if (status.equals(VALUE_REPLY)) {
                    ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_reply_alrealy));
                    break;
                }
                if (TextUtils.isEmpty(mContentEditView.getText().toString())) {
                    ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_none_hint));
                } else {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("iAppId", String.valueOf(appId));
                    parameters.put("cContent", mContentEditView.getText().toString());
                    parameters.put("iLevel", String.valueOf(currentCommitLevel));

                    String url = JsonUrl.getJsonUrl().JSON_URL_USR_COMMENT;

                    FSRequestHelper.newPostRequest(url, "", CommentCommitModel.class, new IFDResponse<CommentCommitModel>() {
                        @Override
                        public void onSuccess(CommentCommitModel result) {
                            if (result != null && result.getCode() == 0) {
                                ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_commit_success));
                                MainThreadBus.getInstance().post(new CommentChangedEvent());
                                dismiss();
                            } else if (result != null && result.getCode() == CODE_ILLEGAL_CHARACTER) {
                                ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_illegal_character));
                            } else {
                                if (TextUtils.isEmpty(result.getMsg()))
                                    ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_commit_failed));
                                else
                                    ToastUtils.showMsg(getContext(), result.getMsg());
                                dismiss();
                            }

                        }

                        @Override
                        public void onNetWorkError() {
                            ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_commit_failed));
                            dismiss();
                        }

                        @Override
                        public void onServerError() {
                            ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_commit_failed));
                            dismiss();
                        }
                    }, parameters, true, new ExtendJsonUtil());
                }
                break;
            default:
                break;
        }
    }


    /**
     * 获取用户之前评论
     */
    private void getUsrComment() {
        String url = JsonUrl.getJsonUrl().JSON_URL_USR_COMMENT + "?iAppId=" + appId;

        FSRequestHelper.newGetRequest(url, TAG, CommentCommitModel.class, new IFDResponse<CommentCommitModel>() {
            @Override
            public void onSuccess(CommentCommitModel result) {
                if (result != null && result.getCode() == 0) {
                    mContentEditView.setText(result.getItemModel().getsContent());
                    mCommitLevelView.setRating(result.getItemModel().getiScore());
                    currentCommitLevel = result.getItemModel().getiScore();
                    status = result.getItemModel().getcStatus();
                    if (false == status.equals(VALUE_REPLY)) { //3 代表已回复
                        editViewReqestFocuse();
                    } else {
                        ToastUtils.showMsg(getContext(), ResUtil.getString(R.string.comment_reply_alrealy));
                        mContentEditView.setFocusable(false);
                    }
                    setupCommitBtn(result.getItemModel().getsContent());
                } else {
                    editViewReqestFocuse();
                }
            }

            @Override
            public void onNetWorkError() {
            }

            @Override
            public void onServerError() {
            }
        }, false, true, new ExtendJsonUtil());
    }

    private void editViewReqestFocuse() {
        mContentEditView.setFocusable(true);
        //延时200毫秒弹出对话框
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                showKeyboard();
            }
        }, 200);
    }


    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String content = s.toString();
            setupCommitBtn(content);
        }
    };

    private void setupCommitBtn(String content) {
        if (status.equals(VALUE_REPLY)) {//3 代表已回复
            mBtnCommit.setTextColor(ResUtil.getColor(R.color.general_text_color));
            mBtnCommit.setBackgroundResource(R.drawable.comment_input_selector);
            return;
        }

        if (TextUtils.isEmpty(content)) {
            mBtnCommit.setTextColor(ResUtil.getColor(R.color.general_text_color));
            mBtnCommit.setBackgroundResource(R.drawable.comment_input_selector);
        } else {
            mBtnCommit.setTextColor(ResUtil.getColor(R.color.white));
            mBtnCommit.setBackgroundResource(R.drawable.btn_green_selector);
        }
    }
}
