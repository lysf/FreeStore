package com.snailgame.cjg.desktop.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.desktop.model.InstallGameInfo;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.fastdev.util.ResUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunxy on 2014/9/2.
 */
public class GameListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int gameListType;
    private List<InstallGameInfo> gameInfoList;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    public GameListAdapter(Context context, List<InstallGameInfo> gameInfoList, int gameListType, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.gameInfoList = gameInfoList;
        inflater = LayoutInflater.from(FreeStoreApp.getContext());
        this.gameListType = gameListType;
    }

    @Override
    public int getCount() {
        if (gameInfoList != null)
            return gameInfoList.size();
        return 0;
    }

    @Override
    public InstallGameInfo getItem(int position) {
        if (gameInfoList != null && position < gameInfoList.size())
            return gameInfoList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.install_game_item, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final InstallGameInfo installGameInfo = getItem(position);
        if (installGameInfo != null) {
            holder.selectedIcon.setVisibility(View.GONE);
            if (TextUtils.isEmpty(installGameInfo.getPackageName())) {
                //添加按钮
                holder.icon.setImageDrawable(ResUtil.getDrawable(R.drawable.add_mygame));
                holder.gameName.setText(ResUtil.getString(R.string.add_my_game));
            } else {
                holder.icon.setImageBitmap(installGameInfo.getAppIcon());
                holder.gameName.setText(installGameInfo.getAppName());
                if (installGameInfo.isSelected()) {
                    holder.selectedIcon.setVisibility(View.VISIBLE);
                    if (gameListType == AppConstants.ALL_INSTALLED_GAME) {
                        //显示对号
                        holder.selectedIcon.setImageResource(R.drawable.submit_add_game);
                    } else {
                        //显示删除按钮
                        holder.selectedIcon.setImageResource(R.drawable.delete_mygame);
                    }
                }
            }
        }

        holder.gameIconLayout.setTag(installGameInfo);
        holder.gameIconLayout.setOnClickListener(clickListener);
        if (longClickListener != null)
            holder.gameIconLayout.setOnLongClickListener(longClickListener);
        return convertView;
    }

    class Holder {
        @Bind(R.id.game_icon_layout)
        FrameLayout gameIconLayout;
        @Bind(R.id.gameInstallIcon)
        ImageView icon;
        @Bind(R.id.selectedIcon)
        ImageView selectedIcon;
        @Bind(R.id.gameInstallName)
        TextView gameName;

        public Holder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
