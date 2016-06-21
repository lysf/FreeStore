package com.snailgame.cjg.desktop.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FixedSupportV4BugFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.widget.EmptyView;
import com.snailgame.cjg.desktop.adapter.GameListAdapter;
import com.snailgame.cjg.desktop.drag.ShakeGridView;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.fastdev.util.ResUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunxy on 2015/4/10.
 */
public abstract class BaseDeskFragment extends FixedSupportV4BugFragment {
    @Bind(R.id.gameListGrid)
    ShakeGridView gridView;
    @Bind(R.id.add_game)
    TextView btnOk;
    @Bind(R.id.cancle_add)
    TextView btnCancle;
    @Bind(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.mygame_search)
    ImageView ic_search;
    @Bind(R.id.desk_toolbar)
    View mToolbar;

    protected GameListAdapter adapter;
    protected ArrayList<InstallGameInfo> gameLists;
    protected EmptyView mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameLists = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.desk_game_fragment, container, false);
        ButterKnife.bind(this, view);
        mToolbar.setBackgroundDrawable(new ColorDrawable(ResUtil.getColor(R.color.red)));
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyView = new EmptyView(getActivity(), gridView);
//        mEmptyView.getEmptyView().setClickable(true);
    }
}
