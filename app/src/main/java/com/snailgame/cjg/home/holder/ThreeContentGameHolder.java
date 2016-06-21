package com.snailgame.cjg.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.widget.FSSimpleImageView;

import butterknife.Bind;

/**
 * Created by sunxy on 2015/4/15.
 */
public class ThreeContentGameHolder extends DownloadViewHolder {
    @Bind(R.id.game_icon)
    public FSSimpleImageView gameIcon;
    @Bind(R.id.game_icon_label)
    public FSSimpleImageView gameIconLabel;
    @Bind(R.id.game_title)
    public TextView gameppTitle;

    @Bind(R.id.pb_detail_download)
    public ProgressBar mDownloadProgressBar;
    @Bind(R.id.tv_state_download)
    public TextView mDownloadStateView;
    public View itemView;

    @Bind(R.id.tv_view)
    public View viewBtn;

    public ThreeContentGameHolder(Context context, View itemView) {
        super(context, itemView);
        this.itemView = itemView;
    }
}