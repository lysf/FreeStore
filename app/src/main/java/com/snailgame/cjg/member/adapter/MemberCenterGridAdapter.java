package com.snailgame.cjg.member.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewActivity;
import com.snailgame.cjg.common.widget.FSSimpleImageView;
import com.snailgame.cjg.global.FreeStoreApp;
import com.snailgame.cjg.global.GlobalVar;
import com.snailgame.cjg.member.MemberDetailActivity;
import com.snailgame.cjg.member.model.MemberInfoModel;
import com.snailgame.cjg.member.model.MemberLayoutChildContentModel;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.fastdev.util.ResUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * create by lic
 * 会员中心页面礼包Adapter
 */
public class MemberCenterGridAdapter extends BaseAdapter {
    public final static String TAG = MemberCenterGridAdapter.class.getSimpleName();
    private Activity activity;
    private List<MemberLayoutChildContentModel> dataList;
    private MemberInfoModel memberInfoModel;

    public MemberCenterGridAdapter(Activity activity, List<MemberLayoutChildContentModel> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        this.memberInfoModel = GlobalVar.getInstance().getMemberInfo();
    }

    public void refresh() {
        this.memberInfoModel = GlobalVar.getInstance().getMemberInfo();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public MemberLayoutChildContentModel getItem(int position) {
        if (dataList != null && position < dataList.size())
            return dataList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(FreeStoreApp.getContext()).inflate(
                    R.layout.activity_member_center_grid_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MemberLayoutChildContentModel memberLayoutChildContentModel = getItem(position);
        if (memberLayoutChildContentModel != null) {
            String lightIconUrl = null;
            final String title = memberLayoutChildContentModel.getsTitle();
            String normalIconUrl = memberLayoutChildContentModel.getsImageUrl();
            String cSource = memberLayoutChildContentModel.getcSource();
            String lightId = null;
            try {
                JSONObject jsonObject = new JSONObject(memberLayoutChildContentModel.getsExtend());
                lightId = jsonObject.optString("p1");
                lightIconUrl = jsonObject.optString("p2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (lightId != null && memberInfoModel != null && memberInfoModel.getCurrentlevel() != null &&
                    cSource != null && cSource.equals(MemberLayoutChildContentModel.SOURCE_TYPE_PRIVILEGE)) {
                int ilightId = 0;
                try {
                    ilightId = Integer.valueOf(lightId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ilightId <= memberInfoModel.getCurrentlevel().getiLevelId()) {
                    viewHolder.icon.setImageUrlAndReUse(lightIconUrl);
                } else {
                    viewHolder.icon.setImageUrlAndReUse(normalIconUrl);
                }
                viewHolder.name.setTextColor(ResUtil.getColor(R.color.general_text_color));
            } else {
                viewHolder.name.setTextColor(ResUtil.getColor(R.color.invaild_text_color));
                viewHolder.icon.setImageUrlAndReUse(normalIconUrl);
            }
            if (title != null && !TextUtils.isEmpty(title))
                viewHolder.name.setText(title);
            String jumpType = null;
            if (memberLayoutChildContentModel.getsExtend() != null && !TextUtils.isEmpty(memberLayoutChildContentModel.getsExtend())) {
                try {
                    JSONObject jsonObject = new JSONObject(memberLayoutChildContentModel.getsExtend());
                    jumpType = jsonObject.optString("p3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final String temJumpType = jumpType;
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(temJumpType)) {
                        try {
                            activity.startActivity(MemberDetailActivity.newIntent(activity, title, temJumpType, Integer.parseInt(memberLayoutChildContentModel.getsRefId())));
                        } catch (Exception e) {
                        }
                    } else if (!TextUtils.isEmpty(memberLayoutChildContentModel.getsJumpUrl())) {
                        activity.startActivity(WebViewActivity.newIntent(activity, memberLayoutChildContentModel.getsJumpUrl()));
                    } else {
                        ToastUtils.showMsg(activity, activity.getString(R.string.stay_tuned));
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.icon)
        public FSSimpleImageView icon;
        @Bind(R.id.name)
        public TextView name;
        @Bind(R.id.root_view)
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
