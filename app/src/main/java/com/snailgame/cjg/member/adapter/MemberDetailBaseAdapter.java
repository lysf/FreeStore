package com.snailgame.cjg.member.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snailgame.cjg.member.model.MemberPrivilege;

import java.util.List;


/**
 * Created by TAJ_C on 2015/12/22.
 */
public class MemberDetailBaseAdapter extends BaseAdapter {

    protected Context context;
    protected List<MemberPrivilege.ModelItem.LevelPrivilege> list;


    public MemberDetailBaseAdapter(Context context, List<MemberPrivilege.ModelItem.LevelPrivilege> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public MemberPrivilege.ModelItem.LevelPrivilege getItem(int position) {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void refreshData(List<MemberPrivilege.ModelItem.LevelPrivilege> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void refreshData(int articeId) {
        if (list != null) {
            for (MemberPrivilege.ModelItem.LevelPrivilege info : list) {
                if (info.getArticleInfo() != null && info.getArticleInfo().getArticeId() == articeId) {
                    info.setIsReceive(true);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }
}


