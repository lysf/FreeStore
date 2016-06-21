package com.snailgame.cjg.common.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.snailgame.cjg.R;
import com.snailgame.fastdev.util.ResUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import third.BottomSheet.BottomSheet;
import third.BottomSheet.ClosableSlidingLayout;


/**
 * Created by lic on 2015/5/21.
 */
public class ShareDialog extends BottomSheet {

    public final static int WECHAT_FRIEND= 0;
    public final static int WEIBO = 1;
    public final static int MICRO_MSG = 2;
    public final static int MORE = 3;

    @Bind(R.id.gridView)
    GridView gridView;
    private AdapterView.OnItemClickListener onItemClickListener;

    public ShareDialog(Context context) {
        super(context);
    }

    public ShareDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) View.inflate(getContext(), R.layout.dialog_share, null);
        setContentDialogView(mDialogView);
        Drawable[] drawables = new Drawable[]{
                ResUtil.getDrawable(R.drawable.weixin_up),
                ResUtil.getDrawable(R.drawable.weibo_up),
                ResUtil.getDrawable(R.drawable.share_wechat_friends_selector_normal),
                ResUtil.getDrawable(R.drawable.share_more_selector_normal)
        };
        String[] strings = new String[]{
                getContext().getString(R.string.wechat_friend),
                getContext().getString(R.string.sina_weibo),
                getContext().getString(R.string.wechat_friend_group),
                getContext().getString(R.string.more)
        };
        gridView.setAdapter(new ShareDialogAdapter(getContext(), drawables, strings));
        gridView.setOnItemClickListener(onItemClickListener);
    }

    @OnClick(R.id.btn_cancel)
    void cancle() {
        dismiss();
    }

    @Override
    public AbsListView getAbsListView() {
        return gridView;
    }


    class ShareDialogAdapter extends BaseAdapter {

        private Context context;
        private Drawable[] drawables;
        private String[] strings;

        public ShareDialogAdapter(Context context, Drawable[] drawables, String[] strings) {
            this.context = context;
            this.drawables = drawables;
            this.strings = strings;
        }

        @Override
        public int getCount() {
            if (drawables != null) {
                return drawables.length;
            } else {
                return 0;
            }
        }

        @Override
        public Drawable getItem(int position) {
            if (drawables != null) {
                return drawables[position];
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dialog_share_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.contentIV.setImageDrawable(drawables[position]);
            holder.contentTV.setText(strings[position]);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.text_content)
            TextView contentTV;
            @Bind(R.id.img_content)
            ImageView contentIV;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

        }
    }

}
