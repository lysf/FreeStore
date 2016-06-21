package com.snailgame.cjg.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.nineoldandroids.view.ViewHelper;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.inter.PagerSlideEventInterface;
import com.snailgame.cjg.common.widget.LoadMoreListView;
import com.snailgame.cjg.common.widget.PagerSlidingTabStrip;
import com.snailgame.cjg.event.FillSearchEvent;
import com.snailgame.cjg.event.TabChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.UmengAnalytics;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.search.adapter.SearchHistoryAdapter;
import com.snailgame.cjg.search.adapter.SearchTipsResultAdapter;
import com.snailgame.cjg.search.model.HotKeyModel;
import com.snailgame.cjg.search.model.SearchKeyModel;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UrlUtils;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;
import com.snailgame.fastdev.util.LogUtils;
import com.snailgame.fastdev.util.ResUtil;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 搜索
 */
public class AppSearchActivity extends SwipeBackActivity implements View.OnKeyListener, ISearchTabController {

    static final String TAG = AppSearchActivity.class.getName();
    @Bind(R.id.ll_search_result)
    LinearLayout llSearchResult;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    @Bind(R.id.search_history_hot_layout)
    RelativeLayout rlSearchHistoryHotLayout;
    @Bind(R.id.lv_search_tips)
    LoadMoreListView lvSearchTips;
    /**
     * 固定标题栏
     */
    @Bind(R.id.search_stick_hot_title)
    LinearLayout stickHotTitle;
    @Bind(R.id.search_stick_history_title)
    LinearLayout stickHistoryTitle;
    @Bind(R.id.change_search_key)
    Button btnChangeSearchKey;
    /**
     * 搜索确认按钮区域
     */
    private Activity mContext;
    private int historyTitleMarginTop = 0;
    /**
     * 标识当前搜索框输入的文字是否来自于自动搜索建议中的选项
     * 如果是，当输入文字发生变化时，理应不再弹出搜索建议
     */
    private boolean isFillingSearcher = false;
    ActionBarViewHolder actionBarViewHolder;
    private List<SearchKeyModel.ModelItem> tipSearchResultLists = new ArrayList<>();
    private SearchTipsResultAdapter mTipSearchResultAdapter;
    /**
     * 显示历史记录的可换行自动控件
     */
    private KeywordsStars keywordsFlow;
    /**
     * 随机生成热搜词，且显著降低重复次数
     */
    private List<HotKeyModel.ModelItem> hotKeyItemList;
    private int segLen;
    private ArrayList<Integer> hotKeyIndexList = new ArrayList<>();
    private int[] randomIndexes;

    private ShakeHelper shakeHelper;
    /**
     * 搜索历史适配器
     */
    private SearchHistoryAdapter searchHistoryAdapter;
    /**
     * 最多搜索历史记录项
     */
    private static final int SEARCH_HISTORY_TOTAL = 20;
    ArrayList<String> searchHistoryList = new ArrayList<>(SEARCH_HISTORY_TOTAL);
    private boolean resultTabChangeToAll = false;

    private boolean isRequestInit;

    public static Intent newIntent(Context context) {
        return newIntent(context, SearchFragmentAdapter.TAB_ALL);
    }

    /**
     * 预设结果显示的页签
     *
     * @param context
     * @param searchResultTabIndex 预设页签值
     * @return
     */
    public static Intent newIntent(Context context, int searchResultTabIndex) {
        Intent intent = new Intent(context, AppSearchActivity.class);
        intent.putExtra(AppConstants.KEY_SEARCH_RESULT_TAB, searchResultTabIndex);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_app_search;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoute = createRoute();
        mContext = AppSearchActivity.this;
        initActionBar();

        /*
        初始化震动传感器
         */
        shakeHelper = new ShakeHelper();
        shakeHelper.getShakeList();

        /*
         * 初始化搜索页
         */
        LayoutInflater layoutInflater = LayoutInflater.from(FreeStoreApp.getContext());
        LinearLayout lv_header = (LinearLayout) layoutInflater.inflate(R.layout.activity_app_search_header, null);
        initStickViews();
        initHotViews(lv_header);
        initHistoryViews(lv_header);

        SearchFragmentAdapter mAdapter = new SearchFragmentAdapter(getSupportFragmentManager(), ResUtil.getStringArray(R.array.search_result_tabs));
        mAdapter.setRoute(mRoute);
        mAdapter.setGuessFavourite(shakeHelper);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(SearchFragmentAdapter.TABS.length);
        tabStrip.setViewPager(mViewPager, SearchFragmentAdapter.TABS.length, new PagerSlideEventInterface() {
            @Override
            public void viewPagerPageSelected(int position) {
            }
        });
        if (getIntent() != null) {
            mViewPager.setCurrentItem(getIntent().getIntExtra(AppConstants.KEY_SEARCH_RESULT_TAB, SearchFragmentAdapter.TAB_ALL));
        }
        llSearchResult.setVisibility(View.INVISIBLE);
        //搜索提示下拉框
        lvSearchTips.setDividerHeight(1);
        lvSearchTips.setDivider(new ColorDrawable(ResUtil.getColor(R.color.common_line_color)));
        lvSearchTips.setSelector(R.drawable.ab_btn_selector_normal);
        lvSearchTips.setBackgroundColor(ResUtil.getColor(R.color.common_window_bg));
        lvSearchTips.setVisibility(View.GONE);

        MainThreadBus.getInstance().register(this);
    }

    /**
     * 初始化固定标题栏Views
     */
    private void initStickViews() {
        stickHotTitle.setOnClickListener(null); //阻止OnClick事件在FrameLayout内透传
        stickHistoryTitle.setOnClickListener(null); //阻止OnClick事件在FrameLayout内透传
    }

    /**
     * 当点击关键字
     *
     * @param keyword
     */
    private void onKeywordClick(CharSequence keyword) {
        fillSearcher(keyword);
        clickOnSearch();
    }

    /**
     * 维护搜索历史列表
     */
    private void updateSearchKeywords(String keyword) {
        //搜索历史列表重排序
        if (searchHistoryList.contains(keyword)) {
            searchHistoryList.remove(keyword);
        }
        if (searchHistoryList.size() == SEARCH_HISTORY_TOTAL) {
            searchHistoryList.remove(SEARCH_HISTORY_TOTAL - 1);
        }
        ArrayList<String> newSearchHistoryList = new ArrayList<>();
        newSearchHistoryList.add(keyword);
        newSearchHistoryList.addAll(searchHistoryList);
        searchHistoryList.clear();
        searchHistoryList.addAll(newSearchHistoryList);
        //列表保存
        SharedPreferencesUtil.getInstance().setSearchHistoryList(JSONArray.toJSONString(searchHistoryList));
        //更新ListView
        searchHistoryAdapter.notifyDataSetChanged();
        stickHistoryTitle.setVisibility(View.VISIBLE);
    }

    /**
     * 填充搜索输入框
     *
     * @param keyword
     */
    private void fillSearcher(CharSequence keyword) {
        isFillingSearcher = true;
        actionBarViewHolder.autoCompSearcher.setText(keyword);
        actionBarViewHolder.autoCompSearcher.setSelection(actionBarViewHolder.autoCompSearcher.getText().length());
        isFillingSearcher = false;
    }

    @Subscribe
    public void onFillSearch(FillSearchEvent event) {
        fillSearcher(event.getKeyword());
    }

    @Subscribe
    public void onTabChange(TabChangeEvent event) {
        mViewPager.setCurrentItem(event.getTabPosition());
    }

    /**
     * 初始化搜索历史
     *
     * @param lv_header
     */
    private void initHistoryViews(final LinearLayout lv_header) {
        initHistoryKeywords(searchHistoryList);
        searchHistoryAdapter = new SearchHistoryAdapter(mContext, searchHistoryList);
        //搜索历史ListView
        final LoadMoreListView historyKeywordListView = (LoadMoreListView) rlSearchHistoryHotLayout.findViewById(R.id.content);
        historyKeywordListView.addHeaderView(lv_header);
        historyKeywordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.search_item_keyword);
                if (textView != null) {
                    refreshRoute(AppConstants.STATISTCS_THIRD_HISTORY);
                    onKeywordClick(textView.getText());
                }
            }
        });

        historyKeywordListView.setAdapter(searchHistoryAdapter);
        historyKeywordListView.setOnScrollListener(historyScrollLinstener);
        //固定搜索历史标题栏
        stickHistoryTitle.setVisibility(searchHistoryList.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        Button search_delete_key = (Button) rlSearchHistoryHotLayout.findViewById(R.id.search_delete_key);
        search_delete_key.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryList.clear();
                SharedPreferencesUtil.getInstance().setSearchHistoryList(JSONArray.toJSONString(searchHistoryList));
                searchHistoryAdapter.notifyDataSetChanged();
                stickHistoryTitle.setVisibility(View.INVISIBLE);
            }
        });
        //历史标题栏MarginTop初始化
        historyTitleMarginTop = ResUtil.getDimensionPixelSize(R.dimen.search_history_title_margin_top);
    }

    /**
     * 搜索历史列表滚动事件监听，处理固定标题栏滚动
     */
    private AbsListView.OnScrollListener historyScrollLinstener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        public int getScrollY(AbsListView view) {
            View c = view.getChildAt(0);
            if (c == null) {
                return 0;
            }

            int firstVisiblePosition = view.getFirstVisiblePosition();
            int top = c.getTop();

            int marginTop = 0;
            if (firstVisiblePosition >= 1) {
                marginTop = historyTitleMarginTop;
            }

            return -top + firstVisiblePosition * c.getHeight() + marginTop;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int scrollY = getScrollY(view);
            int translation = historyTitleMarginTop;
            ViewHelper.setTranslationY(stickHistoryTitle, Math.max(-scrollY, -translation));
            if (scrollY + stickHotTitle.getHeight() > translation) {
                ViewHelper.setTranslationY(stickHotTitle, translation - (scrollY + stickHotTitle.getHeight()));
            } else {
                ViewHelper.setTranslationY(stickHotTitle, Math.max(-scrollY, 0));
            }
        }
    };

    /**
     * 初始化搜索历史记录
     *
     * @param searchHistoryList
     */
    private void initHistoryKeywords(ArrayList<String> searchHistoryList) {
        String searchHistoryListString = SharedPreferencesUtil.getInstance().getSearchHistoryList();
        try {
            List<String> list = JSON.parseArray(searchHistoryListString, String.class);
            searchHistoryList.addAll(list);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 初始化热搜词
     *
     * @param lv_header
     */
    private void initHotViews(LinearLayout lv_header) {
        keywordsFlow = (KeywordsStars) lv_header.findViewById(R.id.search_history_query);
        keywordsFlow.setOnItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshRoute(AppConstants.STATISTCS_THIRD_HOTKEY);
                onKeywordClick(((TextView) v).getText());
            }
        });
        keywordsFlow.go2Show(KeywordsStars.ANIMATION_IN);
        //获取热搜词数据
        createHotKeyTask();
    }

    /**
     * 获取 热门 数据
     */
    private void createHotKeyTask() {
        String url = JsonUrl.getJsonUrl().JSON_URL_SEARCH_HOTKEY;
        btnChangeSearchKey.setVisibility(View.GONE);
        FSRequestHelper.newGetRequest(url, TAG,
                HotKeyModel.class, new IFDResponse<HotKeyModel>() {
                    @Override
                    public void onSuccess(HotKeyModel model) {
                        if (model == null || ListUtils.isEmpty(model.getItemList())) {
                            return;
                        }
                        hotKeyItemList = model.getItemList();
                        segLen = 12;
                        if (hotKeyItemList.size() < segLen) {
                            segLen = hotKeyItemList.size();
                        }
                        initHotKeyIndexlist(hotKeyItemList.size());
                        changeSearchKey();
                    }

                    @Override
                    public void onNetWorkError() {
                    }

                    @Override
                    public void onServerError() {
                    }
                }, true, true, new ExtendJsonUtil());
    }

    /**
     * 随机生成一组随机热搜词
     */
    @OnClick(R.id.change_search_key)
    void changeSearchKey() {
        if (hotKeyItemList != null) {
            keywordsFlow.rubKeywords();
            List<HotKeyModel.ModelItem> random = getRandomSearchList();
            for (HotKeyModel.ModelItem modelItem : random) {
                keywordsFlow.feedKeyword(modelItem.getsKeyWord());
            }
            keywordsFlow.go2Show(KeywordsStars.ANIMATION_IN);
            btnChangeSearchKey.setVisibility(View.VISIBLE);
        }
    }

    private List<HotKeyModel.ModelItem> getRandomSearchList() {
        generateRandomIndexes();
        List<HotKeyModel.ModelItem> randomHotKeyItems = new ArrayList<>();
        for (int randomIndex : randomIndexes) {
            randomHotKeyItems.add(hotKeyItemList.get(randomIndex));
        }
        return randomHotKeyItems;
    }

    private void initHotKeyIndexlist(int size) {
        hotKeyIndexList.clear();
        for (int i = 0; i < size; i++) {
            hotKeyIndexList.add(i);
        }
    }

    private void generateRandomIndexes() {
        if (randomIndexes != null) {
            for (int randomIndex : randomIndexes) {
                hotKeyIndexList.remove(Integer.valueOf(randomIndex));
            }
        }
        if (hotKeyIndexList.size() < segLen) {
            initHotKeyIndexlist(hotKeyItemList.size());
        }

        Random random = new Random();
        int randIndex;
        int tempLen = hotKeyIndexList.size();
        randomIndexes = new int[segLen];
        for (int i = 0; i < segLen; i++) {
            randIndex = Math.abs(random.nextInt()) % tempLen;
            randomIndexes[i] = hotKeyIndexList.get(randIndex);
            hotKeyIndexList.set(randIndex, hotKeyIndexList.get(tempLen - 1));
            hotKeyIndexList.set(tempLen - 1, randomIndexes[i]);
            tempLen--;
        }
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        View view = getLayoutInflater().inflate(R.layout.snail_custom_actionbar_search, null);
        actionBarViewHolder = new ActionBarViewHolder(view);

        actionBarViewHolder.autoCompSearcher = (EditText) view.findViewById(R.id.search_src_text);
        if (!TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getHotSearch()))
            actionBarViewHolder.autoCompSearcher.setHint(getString(R.string.hot_search) + SharedPreferencesUtil.getInstance().getHotSearch());
        actionBarViewHolder.autoCompSearcher.addTextChangedListener(new CustomTextWatcher());
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
            onBackPress();
        }

        @OnClick(R.id.search_btn)
        void searchApp() {
            refreshRoute(AppConstants.STATISTCS_THIRD_ENTER);
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
//            hideResultList();
        }

    }

    /**
     * 点击ActionBar上搜索按钮
     */
    void clickOnSearch() {
        rlSearchHistoryHotLayout.setVisibility(View.GONE);
        String searchText = actionBarViewHolder.autoCompSearcher.getText().toString();
        if (TextUtils.isEmpty(searchText) && !TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getHotSearch())) {
            searchText = SharedPreferencesUtil.getInstance().getHotSearch();
            searchText = searchText.replaceAll("\"", "");
        }

        if (!TextUtils.isEmpty(searchText) && TextUtils.getTrimmedLength(searchText) > 0) {
            if (llSearchResult.getVisibility() == View.VISIBLE) {
                showKeyBoard();
            }
            if (searchText.length() > 0 && searchText.length() > AppConstants.SEARCH_TEXT_MAX_LENGTH) {
                ToastUtils.showMsgLong(getApplicationContext(), ResUtil.getString(R.string.search_text_length_too_long));
                return;
            }
            lvSearchTips.setVisibility(View.GONE); //隐藏下拉提示;
            hideKeyboard();
            llSearchResult.setVisibility(View.VISIBLE);
            search(searchText);
            updateSearchKeywords(searchText);
        } else {
            hint4NoInput();
        }
    }

    /**
     * 隐藏搜索结果
     */
    private void hideResultList() {
        rlSearchHistoryHotLayout.setVisibility(View.VISIBLE);
        clearResult();
        llSearchResult.setVisibility(View.INVISIBLE);
        resultTabChangeToAll = true;
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
     * 拦截键盘输入，侦听按下Enter按钮
     *
     * @param view
     * @param keyCode
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        int action = keyEvent.getAction();
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                if (action == KeyEvent.ACTION_UP) {
                    refreshRoute(AppConstants.STATISTCS_THIRD_ENTER);
                    clickOnSearch();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengAnalytics.PAGE_SEARCH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengAnalytics.PAGE_SEARCH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FreeStoreApp.getRequestQueue().cancelAll(TAG);
        FreeStoreApp.getRequestQueue().cancelAll(ShakeHelper.TAG);
        MainThreadBus.getInstance().unregister(this);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actionBarViewHolder.autoCompSearcher.getWindowToken(), 0);
    }

    public void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(actionBarViewHolder.autoCompSearcher, 0);
    }

    /**
     * 当搜索结果显示或者下拉提示显示，按返回键要隐藏之
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPress();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * actionbar返回按钮 与back按键 响应方法
     */
    private void onBackPress() {
        if (rlSearchHistoryHotLayout.getVisibility() != View.VISIBLE) {
            hideResultList();
        } else if (lvSearchTips.getVisibility() == View.VISIBLE) {
            lvSearchTips.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    /**
     * 侦听输入框变化
     */
    private class CustomTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            if (isFillingSearcher)
                return;

            if (isRequestInit) {
                if (actionBarViewHolder.pbAutoCompleteLoading != null) {
                    actionBarViewHolder.pbAutoCompleteLoading.setVisibility(View.GONE);
                }
            }

            if (s == null || s.toString().trim().length() == 0) {
                if (actionBarViewHolder.ivCloseSearcher != null && actionBarViewHolder.ivCloseSearcher.getVisibility() != View.GONE) {
                    actionBarViewHolder.ivCloseSearcher.setVisibility(View.GONE);
                }
                lvSearchTips.setVisibility(View.GONE);
//                if (llSearchResult.getVisibility() == View.VISIBLE) {
//                    hideResultList();
//                }
                return;
            }

            if (s.toString().length() > 0 && s.toString().length() > AppConstants.SEARCH_TEXT_MAX_LENGTH) {
                ToastUtils.showMsgLong(getApplicationContext(), ResUtil.getString(R.string.search_text_length_too_long));
                return;
            }

            if (actionBarViewHolder.ivCloseSearcher != null && actionBarViewHolder.ivCloseSearcher.getVisibility() != View.VISIBLE) {
                actionBarViewHolder.ivCloseSearcher.setVisibility(View.VISIBLE);
            }

            createAutoCompAsyncTask();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private void createAutoCompAsyncTask() {
        actionBarViewHolder.ivCloseSearcher.setVisibility(View.GONE);
        actionBarViewHolder.pbAutoCompleteLoading.setVisibility(View.VISIBLE);
        isRequestInit = true;
        FSRequestHelper.newGetRequest(buildSearchKeyUrl(actionBarViewHolder.autoCompSearcher.getText().toString()), TAG,
                SearchKeyModel.class, new IFDResponse<SearchKeyModel>() {
                    @Override
                    public void onSuccess(SearchKeyModel model) {
                        actionBarViewHolder.ivCloseSearcher.setVisibility(View.VISIBLE);
                        actionBarViewHolder.pbAutoCompleteLoading.setVisibility(View.GONE);

                        if (model == null || ListUtils.isEmpty(model.getItemList())) {
                            return;
                        }
                        tipSearchResultLists.clear();
                        tipSearchResultLists.addAll(model.getItemList());

                        if (tipSearchResultLists.size() > 0) {

                            lvSearchTips.setVisibility(View.VISIBLE);
                            if (mTipSearchResultAdapter == null) {
                                mTipSearchResultAdapter = new SearchTipsResultAdapter(mContext, tipSearchResultLists);
                                lvSearchTips.setAdapter(mTipSearchResultAdapter);

                                lvSearchTips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if (tipSearchResultLists.size() > 0) {
                                            String input = tipSearchResultLists.get(position).getsAppName();
                                            fillSearcher(input);
                                            clickOnSearch();
                                            refreshRoute(AppConstants.STATISTCS_THIRD_ENTER);
                                            llSearchResult.setVisibility(View.VISIBLE);
                                            search(input);
                                            updateSearchKeywords(input);
                                        }
                                    }
                                });
                            } else {
                                mTipSearchResultAdapter.notifyDataSetChanged();
                            }
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

    private String buildSearchKeyUrl(String mKey) {
        try {
            String keyWord = URLEncoder.encode(mKey, "utf-8");
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put(AppConstants.PARAMS_SEARCH_KEY_WORD, keyWord);
            return UrlUtils.buildUrl(JsonUrl.getJsonUrl().JSON_URL_SEARCH_KEY_LIST, paramsMap);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(e.getMessage());
            return null;
        }
    }

    /**
     * 用于统计路径
     */
    private int[] createRoute() {
        // 搜索
        int[] route = new int[]{
                AppConstants.STATISTCS_FIRST_SEARCH,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL,
                AppConstants.STATISTCS_DEFAULT_NULL};
        return route;
    }

    private void refreshRoute(int searchType) {
        mRoute[AppConstants.STATISTCS_DEPTH_THREE] = searchType;
        refreshRoute(mRoute);
    }

    @Override
    public void search(String searchText) {
        if (resultTabChangeToAll) {
            MainThreadBus.getInstance().post(new TabChangeEvent(SearchFragmentAdapter.TAB_ALL));
            resultTabChangeToAll = false;
        }
        for (int tabIndex : SearchFragmentAdapter.TABS) {
            ((ISearchTabController) getSupportFragmentManager().
                    findFragmentByTag(getFragmentName(R.id.viewpager, tabIndex))).search(searchText);
        }
    }

    @Override
    public void refreshRoute(int[] route) {
        for (int tabIndex : SearchFragmentAdapter.TABS) {
            ((ISearchTabController) getSupportFragmentManager().
                    findFragmentByTag(getFragmentName(R.id.viewpager, tabIndex))).refreshRoute(route);
        }
    }

    @Override
    public void clearResult() {
        for (int tabIndex : SearchFragmentAdapter.TABS) {
            ((ISearchTabController) getSupportFragmentManager().
                    findFragmentByTag(getFragmentName(R.id.viewpager, tabIndex))).clearResult();
        }
    }

    private String getFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }
}
