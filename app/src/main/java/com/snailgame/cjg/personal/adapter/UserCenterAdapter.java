package com.snailgame.cjg.personal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.personal.model.ScratchInfoModel;
import com.snailgame.cjg.personal.model.UserInfo;
import com.snailgame.cjg.util.SharedPreferencesUtil;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yanHH on 2016/6/13.
 */
public class UserCenterAdapter extends BaseAdapter {
    private UserInfo mUsrInfo;
    private String[] itemTitles = ResUtil.getStringArray(R.array.persional_center_array);
    private int[] itemIcons = new int[]{
            R.drawable.personal_member_center,
            R.drawable.personal_code_exchange,
            R.drawable.personal_task,
            R.drawable.personal_scratch,
            R.drawable.person_my_order,
            R.drawable.person_shopping_car,
            R.drawable.person_my_address,
            R.drawable.person_spree,
            R.drawable.person_free_card,
            R.drawable.person_setting};

    public UserCenterAdapter(UserInfo userInfo) {
        this.mUsrInfo = userInfo;
    }

    @Override
    public int getCount() {
        return itemTitles.length + 2;
    }

    @Override
    public Object getItem(int i) {
        return itemTitles[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(R.layout.item_user_center_adapter_layout, null);
            itemViewHolder = new ItemViewHolder(convertView);
            convertView.setTag(R.id.tag_first, itemViewHolder);
            convertView.setTag(R.id.tag_second, i);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag(R.id.tag_first);
        }
        itemViewHolder.scratchImg.setVisibility(View.GONE);
        if (i >= itemTitles.length) {//填充剩余位置
            i = itemTitles.length - 1;
            itemViewHolder.myFunction.setVisibility(View.INVISIBLE);
            itemViewHolder.item_top_icon.setVisibility(View.INVISIBLE);
        }else{
            itemViewHolder.myFunction.setVisibility(View.VISIBLE);
            itemViewHolder.item_top_icon.setVisibility(View.VISIBLE);
        }
        itemViewHolder.myFunction.setText(itemTitles[i]);
        itemViewHolder.item_top_icon.setImageDrawable(ResUtil.getDrawable(itemIcons[i]));
        if (i == 3) {
            int scratchStatus = SharedPreferencesUtil.getInstance().getScratchStatus();
            if (mUsrInfo != null) {
                itemViewHolder.scratchImg.setVisibility(scratchStatus == ScratchInfoModel.ScratchInfo.STATUS_FREE ? View.VISIBLE : View.GONE);
            }
        }
        return convertView;
    }

    class ItemViewHolder {
        @Bind(R.id.my_function)
        TextView myFunction;

        @Bind(R.id.item_top_icon)
        ImageView item_top_icon;

        @Bind(R.id.scratch_img_red)
        ImageView scratchImg;


        public ItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
