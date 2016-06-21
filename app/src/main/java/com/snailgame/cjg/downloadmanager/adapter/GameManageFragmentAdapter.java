package com.snailgame.cjg.downloadmanager.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snailgame.cjg.downloadmanager.DownloadManageFragment;
import com.snailgame.cjg.downloadmanager.InstalledFragment;
import com.snailgame.cjg.downloadmanager.UpdateFragment;

/**
 * Uesr : MacSzh2013
 * Date : 14-2-11
 * Time : 下午3:26
 * Description :
 */
public class GameManageFragmentAdapter extends FragmentPagerAdapter {
    public final static int TAB_DOWNLOAD = 0;
    public final static int TAB_INSTALLED = 1;
    public final static int TAB_UPDATE = 2;

    private String[] string;

    public GameManageFragmentAdapter(FragmentManager fm, String[] string) {
        super(fm);
        this.string = string;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case TAB_DOWNLOAD:
                return new DownloadManageFragment();
            case TAB_INSTALLED:
                return new InstalledFragment();
            case TAB_UPDATE:
                return new UpdateFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        if(string==null)return 0;
        return string.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(string!=null&&position<string.length)
            return string[position];
        return null;
    }
}
