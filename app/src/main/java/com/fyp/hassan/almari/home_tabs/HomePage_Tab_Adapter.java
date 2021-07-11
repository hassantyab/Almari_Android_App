package com.fyp.hassan.almari.home_tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fyp.hassan.almari.home_tabs.CartPage;
import com.fyp.hassan.almari.home_tabs.Favourite;
import com.fyp.hassan.almari.home_tabs.Home_tab;

/**
 * Created by Hassan on 28-Mar-18.
 */

public class HomePage_Tab_Adapter extends FragmentStatePagerAdapter {
    int noOfTab;

    public HomePage_Tab_Adapter(FragmentManager fm, int noOfTab) {
        super(fm);
        this.noOfTab=noOfTab;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                Home_tab h =new Home_tab();
                return h;
            case 1:
                Favourite f =new Favourite();
                return f;
            case 2:
                CartPage c= new CartPage();
                return c;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return noOfTab;
    }
}
