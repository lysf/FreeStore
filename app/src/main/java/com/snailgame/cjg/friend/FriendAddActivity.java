package com.snailgame.cjg.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 添加联系人
 * Created by TAJ_C on 2016/5/16.
 */
public class FriendAddActivity extends SwipeBackActivity implements View.OnKeyListener {
    ActionBarViewHolder actionBarViewHolder;

    @Bind(R.id.friend_contact_container)
    View friendContactView;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, FriendAddActivity.class);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        initActionBar();
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_friend_add;
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        View view = getLayoutInflater().inflate(R.layout.snail_custom_actionbar_search, null);
        actionBarViewHolder = new ActionBarViewHolder(view);

        actionBarViewHolder.autoCompSearcher = (EditText) view.findViewById(R.id.search_src_text);
        actionBarViewHolder.autoCompSearcher.setHint(R.string.friend_search_account_hint);

        actionBarViewHolder.autoCompSearcher.setOnKeyListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actionBarViewHolder.autoCompSearcher.requestFocus();
                showKeyBoard();
            }
        }, 500);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(view, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int action = event.getAction();
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                if (action == KeyEvent.ACTION_UP) {
                    clickOnSearch();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(actionBarViewHolder.autoCompSearcher, 0);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actionBarViewHolder.autoCompSearcher.getWindowToken(), 0);
    }

    /**
     * 输入框的判空
     */
    private void hint4NoInput() {
        ToastUtils.showMsg(this, ResUtil.getString(R.string.input_search_key));
        actionBarViewHolder.autoCompSearcher.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        actionBarViewHolder.autoCompSearcher.requestFocus();
        actionBarViewHolder.autoCompSearcher.setText(null);
    }

    /**
     * 点击ActionBar上搜索按钮
     */
    void clickOnSearch() {
        String searchText = actionBarViewHolder.autoCompSearcher.getText().toString();
        if (TextUtils.isEmpty(searchText) && !TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getHotSearch()))
            searchText = SharedPreferencesUtil.getInstance().getHotSearch();
        if (!TextUtils.isEmpty(searchText) && TextUtils.getTrimmedLength(searchText) > 0) {

            if (searchText.length() > 0 && searchText.length() > AppConstants.SEARCH_TEXT_MAX_LENGTH) {
                ToastUtils.showMsgLong(getApplicationContext(), ResUtil.getString(R.string.search_text_length_too_long));
                return;
            }
            hideKeyboard();
            friendContactView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_container,
                    FriendAccountSearchFragment.getInstance(searchText)).commitAllowingStateLoss();
        } else {
            hint4NoInput();
        }
    }


    @OnClick(R.id.friend_contact_container)
    public void showFriendContactActivity() {
        startActivity(new Intent(this, FriendContactActivity.class));
    }

    class ActionBarViewHolder {
        @Bind(R.id.root_view)
        View mRootView;
        @Bind(R.id.search_close_btn)
        View ivCloseSearcher;

        @Bind(R.id.search_autocomplete_loading)
        ProgressBar pbAutoCompleteLoading;

        @Bind(R.id.search_src_text)
        EditText autoCompSearcher;

        @Bind(R.id.search_plate)
        View searchPlate;

        public ActionBarViewHolder(View view) {
            ButterKnife.bind(this, view);
            mRootView.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        }

        @OnClick(R.id.back)
        void pressBackKey() {
            finish();
        }

        @OnClick(R.id.search_btn)
        void searchApp() {
            clickOnSearch();
        }

        @OnClick(R.id.search_close_btn)
        void searchClose() {
            if (!TextUtils.isEmpty(actionBarViewHolder.autoCompSearcher.getText())) {
                actionBarViewHolder.autoCompSearcher.setText(null);
                if (actionBarViewHolder.ivCloseSearcher != null && actionBarViewHolder.ivCloseSearcher.getVisibility() != View.GONE) {
                    actionBarViewHolder.ivCloseSearcher.setVisibility(View.GONE);
                }
            }
        }

    }

}
