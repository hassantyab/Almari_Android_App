package com.fyp.hassan.almari.Order_Activities;

import com.fyp.hassan.almari.LoginClasses.Sign_In_Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Order_Tab_Adapter extends FragmentStatePagerAdapter {

    int numoftab;

    public Order_Tab_Adapter(FragmentManager fm, int NmOfTab)
    {
        super(fm);
        this.numoftab=NmOfTab;
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                    AddressFragment ad = new AddressFragment();
                    return ad;
            case 1:
                paymentFragment pf = new paymentFragment();
                return pf;
            default:
                return null;
        }
    }


    


    @Override
    public int getCount() {
        return numoftab;
    }
}
