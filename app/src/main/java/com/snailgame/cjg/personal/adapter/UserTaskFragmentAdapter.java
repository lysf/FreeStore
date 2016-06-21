package com.snailgame.cjg.personal.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snailgame.cjg.R;
import com.snailgame.cjg.personal.UserTaskFragment;
import com.snailgame.fastdev.util.ResUtil;

/**
 * Created by TAJ_C on 2015/11/3.
 */
public class UserTaskFragmentAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] mTaskTitles;
    private Fragment[] fragments = new Fragment[2];


    public UserTaskFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        mTaskTitles = ResUtil.getStringArray(R.array.task_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case UserTaskFragment.TAB_TASK_ONCE:
                fragments[0] = UserTaskFragment.newInstance(UserTaskFragment.TAB_TASK_ONCE);
                return fragments[0];
            case UserTaskFragment.TAB_TASK_WEEK:
                fragments[1] = UserTaskFragment.newInstance(UserTaskFragment.TAB_TASK_WEEK);
                return fragments[1];
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTaskTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mTaskTitles[position];
    }
}
