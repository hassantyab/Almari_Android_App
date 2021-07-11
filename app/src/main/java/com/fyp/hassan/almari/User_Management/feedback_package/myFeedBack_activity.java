package com.fyp.hassan.almari.User_Management.feedback_package;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.fyp.hassan.almari.BaseActivity;
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.Search_Activities.Search_Activity;
import com.fyp.hassan.almari.User_Management.MyAccount;
import com.fyp.hassan.almari.home_tabs.HomeActivity;

public class myFeedBack_activity extends BaseActivity {
    private Toolbar toolbar;
    private ImageView backBtn;
    private TextView toolbarTitle;
    private Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfeedback);
        toolbar=(Toolbar)findViewById(R.id.mainToolbar);
        backBtn=(ImageView)findViewById(R.id.toolbar_backbtn);
        toolbarTitle=(TextView)findViewById(R.id.toolbarHeading);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("Reviews");
        try {
            fragment_allmyFeedback fragment = new fragment_allmyFeedback();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.myfeeback_layout,fragment,fragment.getTag()).commit();

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        menu.findItem(R.id.Myaccount).setVisible(true);
        menu.findItem(R.id.cart_menu).setVisible(false);
        menu.findItem(R.id.menu_Home).setVisible(true);
        menu.findItem(R.id.Submenu_Myaccount).setVisible(true);
        menu.findItem(R.id.SaveMenu).setVisible(true);
        menu.findItem(R.id.SignIn).setVisible(false);
        menu.findItem(R.id.Logout).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {

                case R.id.cart_menu:
                    in = new Intent(this, HomeActivity.class);
                    in.putExtra("tab", 2);
                    startActivity(in);
                    return true;

                case R.id.menu_Home:
                    in = new Intent(this, HomeActivity.class);
                    startActivity(in);
                    return true;
                case R.id.Submenu_Myaccount:
                    in = new Intent(this, MyAccount.class);
                    startActivity(in);
                    return true;
                case R.id.SearchOption:
                    in = new Intent(this, Search_Activity.class);
                    startActivity(in);
                    return true;
                case R.id.MyOrders:
                    in = new Intent(this, MyOrdersListClass.class);
                    startActivity(in);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
