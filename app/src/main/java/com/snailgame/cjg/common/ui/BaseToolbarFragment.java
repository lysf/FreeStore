package com.snailgame.cjg.common.ui;

/**
 * Created by sunxy on 2015/9/6.
 */
public abstract class BaseToolbarFragment extends AbsBaseFragment {
//    static String TAG = BaseToolbarFragment.class.getName();
//
//    @Bind(R.id.gridMainPopup)
//    GridView gridMainPopup;    // 百宝箱
//    @Bind(R.id.bg_popup)
//    AlphaView mPopup;
//
//    private AsyncTask queryTask;
//    private List<TreasureBoxInfo> mTreasureBoxInfoList;
//    private MainActivity mMainActivity;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        setToolbarMarginTop();
//
////        if (this instanceof HomeFragment) {
////            mToolbarDrawable.setAlpha(0);
////        }
//
//        mPopup.setOnClickListener(mOnClickListener);
//    }
//
//    private void setToolbarMarginTop() {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolbar.getLayoutParams();
//        if (params != null)
//            params.topMargin = ComUtil.getStatesBarHeight();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        closeTreasureBox();
//    }
//
//
//    View.OnClickListener mOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.bg_popup:
//                    closeTreasureBox();    // 关闭百宝箱窗口
//                    break;
//                case R.id.treasurebox:
//                    showTreasureBox();    // 百宝箱
//                    break;
//                case R.id.notice_area:
//                    closeTreasureBox();    // 关闭百宝箱窗口
//                    startActivity(NoticeActivity.newIntent(getActivity()));
//                    break;
//            }
//        }
//    };
//
//    private void showTreasureBox() {
//        if (!closeTreasureBox()) {
//            createTreasureBox();
//            mPopup.setVisibility(View.VISIBLE);
//            showOrHideTreasureBox(true);
//        }
//    }
//
//
//    public boolean closeTreasureBox() {
//        if (mPopup != null && mPopup.getVisibility() == View.VISIBLE && !mPopup.onHideAnimation) {
//            mPopup.setVisibility(View.GONE);
//            showOrHideTreasureBox(false);
//            return true;
//        }
//        return false;
//    }
//
//    // 百宝箱菜单
//    private void createTreasureBox() {
//        if (mMainActivity != null)
//            mTreasureBoxInfoList = mMainActivity.getTreasureBoxInfoList();
//        if (mTreasureBoxInfoList == null || mTreasureBoxInfoList.isEmpty()) {
//            ArrayList<MainPopupData> dataList = new ArrayList<>();
//            String[] titleStrings = {"scanner", "flow", "control"};
//            int[] resourId = {R.drawable.messing_scanner, R.drawable.messing_flow_normal, R.drawable.messing_flow_control,};
//            int stringLength = titleStrings.length;
//            if (!EngineEnvironment.isTrafficEnable()) {
//                stringLength = stringLength - 1;
//            }
//
//            for (int i = 0; i < stringLength; i++) {
//                MainPopupData mainPopupData = new MainPopupData();
//                mainPopupData.setIconResourceId(resourId[i]);
//                dataList.add(mainPopupData);
//            }
//            MainPopupApdapter mainPopupApdapter = new MainPopupApdapter((MainActivity) getActivity(), dataList);
//            gridMainPopup.setAdapter(mainPopupApdapter);
//        } else {
//            TreasureBoxApdapter mainPopupApdapter = new TreasureBoxApdapter((MainActivity) getActivity(), mTreasureBoxInfoList, BaseToolbarFragment.this);
//            gridMainPopup.setAdapter(mainPopupApdapter);
//        }
//    }
//
//    private void showOrHideTreasureBox(boolean show) {
//        Animation translateAnimation;
//        int height = ComUtil.dpToPx(80) * -1;
//        if (show) {
//            translateAnimation = new TranslateAnimation(0, 0, height, 0);
//        } else {
//            translateAnimation = new TranslateAnimation(0, 0, 0, height);
//        }
//        translateAnimation.setDuration(AppConstants.POP_WINDOW_ANIMATION_DURATION);
//
//        gridMainPopup.setAnimation(translateAnimation);
//    }
//
//    /**
//     * 设置右上交消息数量
//     *
//     * @param count
//     */
//    private void setMsgNum(int count) {
//        if (mMsgButton == null)
//            return;
//        if (count > 0) {
//            mRedPointView.setVisibility(View.VISIBLE);
//        } else {
//            mRedPointView.setVisibility(View.GONE);
//        }
//    }
//
//
//    @Subscribe
//    public void callBack(RefreshMsgNumEvent event) {
//        final int count = event.getCount();
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                queryTask = MyGameDaoHelper.queryForAppInfoInThread(getActivity(), new MyGameDaoHelper.MyGameCallback() {
//                    @Override
//                    public void Callback(List<AppInfo> appInfos) {
//                        int updatecount = GameManageUtil.getUpdateInfos(getActivity(), appInfos, false).size();
//                        setMsgNum(count + updatecount);
//                    }
//                });
//            }
//        });
//    }
//
//
//
//    @Subscribe
//    public void clostTreasureBox(ClostTreasureEvent event) {
//        closeTreasureBox();
//    }
//
//
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (queryTask != null)
//            queryTask.cancel(true);
//        GlobalVar.getRequestQueue().cancelAll(TAG);
//    }
}
