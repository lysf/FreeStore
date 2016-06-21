package third.com.snail.trafficmonitor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.utils.Utils;
import com.snailgame.cjg.R;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.snail.trafficmonitor.engine.data.bean.TrafficInfo;
import third.com.snail.trafficmonitor.ui.adapter.TimeBucketAdapter;


/**
 * Created by lic on 2014/12/17.
 * 折线图和柱状图承载的fragment
 */
public class TimeBucketInsideFragment extends BaseFragment {
    private final static String TAG = TimeBucketInsideFragment.class.getSimpleName();
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private TimeBucketAdapter adapter;
    /**是否已经被初始化的标志*/
    private boolean isInit;

    public static TimeBucketInsideFragment newInstance(boolean isMobile, ArrayList<TrafficInfo> trafficList,
                                                       String appId, int firstPager) {
        TimeBucketInsideFragment f = new TimeBucketInsideFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATA, trafficList);
        args.putBoolean(IS_MOBILE, isMobile);
        args.putString(APP_ID, appId);
        args.putInt(TimeBucketActivity.FIRST_PAGE, firstPager);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.engine_fragment_time_bucket, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //判断第一页显示移动网络并且当前是移动网络页面就初始化
        if (getArguments().getInt(TimeBucketActivity.FIRST_PAGE) == 0) {
            if (getArguments().getBoolean(IS_MOBILE)) {
                initView();
            }
        }
        return view;
    }

    private void initView() {
        isInit = true;
        adapter = new TimeBucketAdapter(getArguments().<TrafficInfo>getParcelableArrayList(DATA),
                getActivity());
        adapter.setOnItemClickListener(new TimeBucketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.content, LineChartFragment.newInstance(getArguments().getBoolean(IS_MOBILE),
                        getArguments().<TrafficInfo>getParcelableArrayList(DATA).get(position).getDay(),
                        getArguments().getString(APP_ID)));
                ft.commit();
            }
        });
        // 设置Adapter
        recyclerView.setAdapter(adapter);
        int currentDay = Calendar.getInstance().get(Calendar.DATE);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int sum = (int) (width / (Utils.convertDpToPixel(60)));
        recyclerView.scrollToPosition(currentDay - sum);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.content, LineChartFragment.newInstance(getArguments().getBoolean(IS_MOBILE), currentDay,
                getArguments().getString(APP_ID)));
        ft.commit();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //如果页面没有初始化并且可见的话就初始化
        if (isVisibleToUser && !isInit) {
            if (recyclerView != null) {
                initView();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
