package com.papad.perfectpitch;

import android.support.design.widget.TabLayout;
import android.app.Fragment;

/**
 * Created by nikojpapa on 4/26/16.
 */
public class TabListener implements TabLayout.OnTabSelectedListener {
    private Fragment fragment;

    public TabListener(Fragment frag) {
        fragment= frag;
    }

    public void onTabSelected(TabLayout.Tab tab) {

    }

    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void onTabUnselected(TabLayout.Tab tab) {

    }
}
