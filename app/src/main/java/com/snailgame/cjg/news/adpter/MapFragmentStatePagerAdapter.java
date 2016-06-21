package com.snailgame.cjg.news.adpter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by yanHH on 2016/5/4.
 */
public abstract class MapFragmentStatePagerAdapter extends PagerAdapter {
    private final String TAG = MapFragmentStatePagerAdapter.class.getSimpleName();
    private final FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction = null;

//    private HashMap<String, Fragment> fragmentMap = new HashMap();
    private HashMap<String, Fragment.SavedState> fragmentSavedStateMap = new HashMap();
    private Fragment currentPrimaryItem = null;

    public MapFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public abstract String getFragmentTag(int i);

    public abstract Fragment getItem(int i);

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        String key = getFragmentTag(i);

        if (fragmentTransaction == null) {
            fragmentTransaction = fragmentManager.beginTransaction();
        }
        Fragment fragment = getItem(i);
        Fragment.SavedState savedState = fragmentSavedStateMap.get(key);
        if (savedState != null) {
            fragment.setInitialSavedState(savedState);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
//        fragmentMap.put(key, fragment);
        fragmentTransaction.add(viewGroup.getId(), fragment,key);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        if (fragmentTransaction == null) {
            fragmentTransaction = fragmentManager.beginTransaction();
        }
        if(fragment.getTag().equals(getFragmentTag(i))){
            fragmentSavedStateMap.put(fragment.getTag(), fragmentManager.saveFragmentInstanceState(fragment));
        }

//        fragmentMap.remove(getFragmentTag(i));
        fragmentTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                currentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            if(fragment.isResumed()){
                fragmentSavedStateMap.put(getFragmentTag(i), fragmentManager.saveFragmentInstanceState(fragment));
            }
            currentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup viewGroup) {
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            fragmentTransaction = null;
            fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle bundle = null;
        if (fragmentSavedStateMap.size() > 0) {
            bundle = new Bundle();
            bundle.putSerializable("states", fragmentSavedStateMap);
        }
//        for (Map.Entry<String, Fragment> entry : fragmentMap.entrySet()) {
//            if (entry.getValue() != null) {
//                if (bundle == null) {
//                    bundle = new Bundle();
//                }
//                fragmentManager.putFragment(bundle, "f" + entry.getKey(), entry.getValue());
//            }
//        }
        return bundle;
    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        if (parcelable != null) {
            Bundle bundle = (Bundle) parcelable;
            bundle.setClassLoader(classLoader);
//            fragmentMap.clear();
            fragmentSavedStateMap.clear();
            fragmentSavedStateMap = (HashMap<String, Fragment.SavedState>) bundle.getSerializable("states");
            for (String str : bundle.keySet()) {
                if (str.startsWith("f")) {
                    String substring = str.substring(1);
                    Fragment fragment = fragmentManager.getFragment(bundle, str);
                    if (fragment != null) {
                        fragment.setMenuVisibility(false);
//                        fragmentMap.put(substring, fragment);
                    }
                }
            }
        }
    }


    @Override
    public void startUpdate(ViewGroup viewGroup) {
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    public void setFragmentSavedStateMap(HashMap<String, Fragment.SavedState> fragmentSavedStateMap){
        this.fragmentSavedStateMap = fragmentSavedStateMap;
    }
    public HashMap<String, Fragment.SavedState> getFragmentSavedStateMap(){
        return this.fragmentSavedStateMap;
    }
}
