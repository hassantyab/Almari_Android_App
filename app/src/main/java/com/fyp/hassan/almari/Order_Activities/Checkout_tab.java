package com.fyp.hassan.almari.Order_Activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fyp.hassan.almari.home_tabs.CustomViewPager;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;

public class Checkout_tab extends AppCompatActivity implements AddressFragment.OnFragmentInteractionListener,paymentFragment.OnFragmentInteractionListener,LoginFragment.OnFragmentInteractionListener

{

    public TabLayout tb;
    public CustomViewPager vp;
    private Order_Tab_Adapter Tab_adapter;
    private Toolbar toolbar;
    private ImageView backbtn;
    private UserSessionManager session;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_tab);
        vp=(CustomViewPager) findViewById(R.id.CheckoutViewPager);
        session=new UserSessionManager(this);
        backbtn=(ImageView)findViewById(R.id.checkout_menu_back_btn);
        toolbar=(Toolbar)findViewById(R.id.Checkout_toolbar);
        tb=(TabLayout)findViewById(R.id.CheckoutTabLayout);
        tb.addTab(tb.newTab().setText("Address").setIcon(R.drawable.home_tab_logo));
        tb.addTab(tb.newTab().setText("Payment").setIcon(R.drawable.payment_tab_logo));
        tb.setTabGravity(TabLayout.GRAVITY_FILL);
        Tab_adapter=new Order_Tab_Adapter(getSupportFragmentManager(),tb.getTabCount());
        vp.setAdapter(Tab_adapter);

        final ViewGroup vg1 = (ViewGroup) tb.getChildAt(0);
        ViewGroup vgTab1 = (ViewGroup) vg1.getChildAt(1);
        vgTab1.setEnabled(false);

        vp.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb));

        tb.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.getAdapter().notifyDataSetChanged();

              switch (tab.getPosition())
              {
                  case 0:
                    {
                            tb.getTabAt(0).select();
                            vp.setCurrentItem(0);
                            break;
                    }
              }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tb.setTabTextColors(getResources().getColor(R.color.normal), getResources().getColor(R.color.selected));
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId()==backbtn.getId())
                {
                    finish();
                }
            }
        });

    }


    

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
