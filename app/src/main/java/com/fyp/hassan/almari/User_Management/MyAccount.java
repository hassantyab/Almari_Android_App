package com.fyp.hassan.almari.User_Management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.User_Management.myAccountDetail;
import com.fyp.hassan.almari.User_Management.feedback_package.myFeedBack_activity;
import com.fyp.hassan.almari.home_tabs.HomeActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


public class MyAccount extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar myToolbar;
    private Intent in;
    private String[] item,item2,item3,item4;
    private ArrayAdapter<String> adpater,adapter2,adapter3,adapter4;
    private ListView Lt_account,Lt_more,Lt_RateApp;
    private UserSessionManager session;
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        session=new UserSessionManager(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        backBtn= (ImageView)findViewById(R.id.UserAccount_backbtn);
        Lt_account= (ListView)findViewById(R.id.lt_Account);
        Lt_more= (ListView)findViewById(R.id.lt_more);
        Lt_RateApp= (ListView)findViewById(R.id.lt_RateApp);
        setUpListview();


        Lt_account.setOnItemClickListener(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void setUpListview() {

        item= new String[]{"My Profile","My Ratings","My Orders"};
        adpater= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item);
        Lt_account.setAdapter(adpater);

        item3=new String[]{"App version","Terms and Conditions","FAQ"};
        adapter3= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item3);
        Lt_more.setAdapter(adapter3);

        item4=new String[]{"Tell others about this app","Like the app?Rate it on Google!"};
        adapter4= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item4);
        Lt_RateApp.setAdapter(adapter4);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_account,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.accountMenu_home:
                in = new Intent(this,HomeActivity.class);
                startActivity(in);
                return true;
            case R.id.accountMenu_myOrders:
                if (session.isUserLoggedIn()) {
                    in = new Intent(this, MyOrdersListClass.class);
                    startActivity(in);
                    return true;
                } else {
                    Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.accountMenu_recentView:
                if (session.isUserLoggedIn()) {
                    in= new Intent(this,recentView_Activty.class);
                    startActivity(in);
                    return true;
                } else {
                    Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       try {
           if (position == 0) {
               if (session.isUserLoggedIn()) {
                   in = new Intent(getApplicationContext(), myAccountDetail.class);
                   startActivity(in);
               } else {
                   Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_SHORT).show();
               }
           }
           if (position == 1) {
               if (session.isUserLoggedIn()) {
                   in = new Intent(this, myFeedBack_activity.class);
                   startActivity(in);

               } else {
                   Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_SHORT).show();
               }
           }
           if (position == 2) {
               if (session.isUserLoggedIn()) {
                   in = new Intent(this, MyOrdersListClass.class);
                   startActivity(in);

               } else {
                   Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_SHORT).show();
               }
           }


       }catch (Exception e)
       {
           e.printStackTrace();
       }
    }
}
