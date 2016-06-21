package com.snailgame.cjg.seekgame.rank.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.DownloadViewHolder;
import com.snailgame.cjg.common.widget.FSSimpleImageView;

import butterknife.Bind;

/**
 * Created by sunxy on 2015/3/24.
 */
public class RankViewHolder  extends DownloadViewHolder {
    @Bind(R.id.rank_app_icon)
    FSSimpleImageView rankAppIcon;
    @Bind(R.id.rank_app_title)
    TextView rankAppTitle;
    @Bind(R.id.rank_app_rate)
    RatingBar ratingBar;
    @Bind(R.id.no_rate)
    public TextView noRate;

    @Bind(R.id.pb_detail_download)
    ProgressBar mDownloadProgressBar;
    @Bind(R.id.tv_state_download)
    TextView mDownloadStateView;
    public View itemView;

    public RankViewHolder(Context context, View itemView) {
        super(context, itemView);
        this.itemView=itemView;
    }
}