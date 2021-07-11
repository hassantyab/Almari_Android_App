package com.fyp.hassan.almari.home_tabs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.Order_Activities.Checkout_tab;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;




public class CartPage extends Fragment implements View.OnClickListener {

    Context c;
    LayoutInflater inflater;
    ViewGroup viewGroup;
    private Bundle bundle;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<Product> cardList;
    ArrayList<String> Cartimglist;
    RecyclerView recyclerView;
    TextView tv_NoItem,totalprice;
    private UserSessionManager session;
    private View v;
    private Spinner Spin_quantity;
    private Button btn_Checkout;
    private SQLiteDatabase db;
    private String mParam1;
    private String mParam2;
    TabLayout tb;
    ViewPager vp;
    private RequestQueue requestQueue;
    private OnFragmentInteractionListener mListener;
    int Totalprice,NoOfOitems;
    LinearLayout cart_layout;
    ProgressBar progressBar;
    Wave wave;
    public CartPage() {

    }
    public static CartPage newInstance(String param1, String param2) {
        CartPage fragment = new CartPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_cart_page, container, false);

        cart_layout = (LinearLayout)v.findViewById(R.id.cart_layout);
        progressBar = (ProgressBar)v.findViewById(R.id.progressbar_cart);
        wave =new Wave();
        progressBar.setIndeterminateDrawable(wave);

        tb=(TabLayout)getActivity().findViewById(R.id.tablayout);
        vp= (ViewPager)getActivity().findViewById(R.id.HomeViewPager);
        session = new UserSessionManager(getContext());
        recyclerView = (RecyclerView) v.findViewById(R.id.CartRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(c));
        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        tv_NoItem = (TextView) v.findViewById(R.id.tv_No_of_Items);
        totalprice = (TextView) v.findViewById(R.id.Cart_total_price);
        btn_Checkout = (Button) v.findViewById(R.id.bt_Checkout);
        Spin_quantity = (Spinner) v.findViewById(R.id.Cart_Quantity);
        btn_Checkout.setOnClickListener(this);
        requestQueue= Volley.newRequestQueue(getContext());
        cardList = new ArrayList<>();
        this.inflater=inflater;
        this.viewGroup=container;

        if(session.isUserLoggedIn())
        {
            requestOnlineForCart();
        }
        else {
            setData();
        }
        return v;
    }


    public void onlineCartSetUp(JSONObject data) {
        try {
            if (data.has("user"))
            {
                cardList.clear();
                JSONObject jsonObject = new JSONObject(data.getString("user"));
                JSONArray jsonArray = jsonObject.getJSONArray("cart");
                NoOfOitems = 0;
                Totalprice=0;
               for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject  j1= jsonArray.getJSONObject(i);//Inside object
                    JSONObject j2 = new JSONObject(j1.getString("productId"));//inside productId
                    JSONArray j3 =j2.getJSONArray("Images");
                    ArrayList<String> img = new ArrayList<>();
                    img.add(j3.get(0).toString());

                    cardList.add(new Product(
                            j2.getString("_id"),
                            j2.getString("Title"),
                            j2.getString("Description"),
                            j1.getString("quantity"),
                            j2.getString("Price"),
                            j2.getString("BrandName"),
                            j2.getString("CategoryName"),
                            j2.getString("SubCategoryName"),
                            img,
                            j2.getString("AverageRating")
                            ));

                    Totalprice+=Integer.parseInt(cardList.get(i).getPrice())*Integer.parseInt(cardList.get(i).getQuantity());
                    NoOfOitems++;
                }
                tv_NoItem.setText("" + NoOfOitems);
                totalprice.setText(""+ Totalprice);
                if(!cardList.isEmpty()) {
                    CartAdapter adapter = new CartAdapter(getContext(), cardList, CartPage.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    cart_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    setViewLayout(R.layout.empty_cart);
                }
            }
            else
            {
                setViewLayout(R.layout.empty_cart);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            setViewLayout(R.layout.empty_cart);
        }
    }

    void requestOnlineForCart()
    {
        try {

            String url = getContext().getResources().getString(R.string.ApiAddress) + "api/cart/get";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId" , session.getUserId());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,  url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("onlineCart",response.toString());
                    onlineCartSetUp(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    requestOnlineForCart();
                }
            });
            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.c=getActivity().getApplicationContext();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    void setData()
    {
        try {

            cardList = new ArrayList<>();
            db = getActivity().openOrCreateDatabase("CartDatabase", android.content.Context.MODE_PRIVATE, null);
            String tableName= "CartTable";
            Cursor cursorTable = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
            if(cursorTable!=null && cursorTable.getCount()>0)
            {
                    Cursor cursor = db.rawQuery("select * from CartTable", null);
                    String ProductId = "0";
                    String ProductTitle = "Huawei P10 lite";
                    String Brandname = "Huawei";
                    String ProductDescription = "very fast";
                    String Price = "";
                    String Quantity = "";
                    String Image = "asd";
                     NoOfOitems = 0;
                     Totalprice=0;
                    if (cursor.moveToFirst())
                    {
                        do {
                            Cartimglist = new ArrayList<>();
                            ProductId = cursor.getString(cursor.getColumnIndex("ProductID"));
                            ProductTitle = cursor.getString(cursor.getColumnIndex("ProductTitle"));
                            Brandname = cursor.getString(cursor.getColumnIndex("CompanyName"));
                            ProductDescription = cursor.getString(cursor.getColumnIndex("ProductDescription"));
                            Price = cursor.getString(cursor.getColumnIndex("Price"));
                            Quantity = cursor.getString(cursor.getColumnIndex("Quantity"));
                            Image = cursor.getString(cursor.getColumnIndex("Image"));
                            Cartimglist.add(Image);

                            cardList.add(
                                    new Product(
                                            ProductId,
                                            ProductTitle,
                                            ProductDescription,
                                            Quantity,
                                            Price,
                                            Brandname,
                                            "",
                                            "",
                                            Cartimglist,
                                            "4"
                                    ));
                            Totalprice += Integer.parseInt(Price) * Integer.parseInt(Quantity);
                            NoOfOitems++;


                        } while (cursor.moveToNext());
                        tv_NoItem.setText("" + NoOfOitems);
                        totalprice.setText("" + Totalprice);
                        if (!cardList.isEmpty()) {
                            CartAdapter adapter = new CartAdapter(getContext(), cardList, CartPage.this);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            cart_layout.setVisibility(View.VISIBLE);
                        } else
                            {
                            setViewLayout(R.layout.empty_cart);
                        }
                    }
                    else
                    {
                        setViewLayout(R.layout.empty_cart);
                    }
                    db.close();
            }
            else
            {
                db.close();
                setViewLayout(R.layout.empty_cart);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onClick(View v) {

        if(v.getId()==btn_Checkout.getId())
        {
            try
            {
                if(!cardList.isEmpty()&& session.isUserLoggedIn() ) {
                    Intent in = new Intent(getContext(), Checkout_tab.class);
                    startActivity(in);
                }
                else
                {
                    Toast.makeText(getContext(),"Cart is empty or Login first",Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }
    private void setViewLayout(int id)
    {
     try {

         LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         v = inflater.inflate(id, viewGroup,false);
         ViewGroup rootView = (ViewGroup) getView();
         rootView.removeAllViews();
         rootView.addView(v);
     }
     catch (Exception e )
     {
         e.printStackTrace();
     }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
