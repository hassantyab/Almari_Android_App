package com.fyp.hassan.almari.Order_Activities;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.home_tabs.CartAdapter;
import com.fyp.hassan.almari.home_tabs.CartPage;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class paymentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button paymentBtn,paypalBtn;
    private List<OrderProduct> orderProductList;
    private String OrderProductId = "0";
    private String OrderQuantity,price = "";
    private TextView tv_totalprice;
    private int Totalprice;
    private SQLiteDatabase db;
    private RadioGroup paymentChoice;
    private OnFragmentInteractionListener mListener;
    private UserSessionManager session;
    private RequestQueue requestQueue;
    private String url;
    private Order order;
    private PayPalConfiguration payPalConfiguration;
    private List<Product> productList;
    private RelativeLayout payment_layout;
    private ProgressBar progressBar;
    private Wave wave;
    private String paymentId,state;
    private int totalPrice=0;
    public paymentFragment()
    {

    }



    public static paymentFragment newInstance(String param1, String param2) {
        paymentFragment fragment = new paymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getContext(),PayPalService.class));
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_payment, container, false);
        payPalConfiguration = new PayPalConfiguration().
                environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(getContext().getResources().getString(R.string.Paypal_client_id));

        payment_layout =(RelativeLayout)v.findViewById(R.id.payment_layout);
        progressBar =(ProgressBar)v.findViewById(R.id.payment_progressBar);
        wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        paymentBtn=(Button)v.findViewById(R.id.paymentButton);
        paypalBtn=(Button)v.findViewById(R.id.payment_paypalBtn);
        orderProductList = new ArrayList<>();
        tv_totalprice=(TextView)v.findViewById(R.id.payments_totalPrice);
        session= new UserSessionManager(getContext());
        paymentChoice=(RadioGroup)v.findViewById(R.id.payment_RadioGroup);
        requestQueue= Volley.newRequestQueue(getContext());
        productList = new ArrayList<>();
        order = new Order();

        //payPal
        Intent intent = new Intent(getContext(),PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        getActivity().startService(intent);

        if(session.isUserLoggedIn())
        {
            getOnlineCart();
        }

        paypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay_with_paypal();
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                        setPayment();
                        orderProductList.clear();
                        if(!productList.isEmpty()) {
                            postOrder(true);
                        }
                    }
                catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

        });
        return v;
    }


    private void postOrder(boolean ordinary)
    {
        try {
            hideLayout();
            for (int i = 0; i < productList.size(); i++)
            orderProductList.add(new OrderProduct(productList.get(i).getId(), productList.get(i).getQuantity()));

            order.setUserId(session.getUserId());
            order.setShippingAddress(session.getAddress());
            order.setpList(orderProductList);
            JSONArray jsonArray = convertData(orderProductList);

            if (jsonArray.length() > 0) {
                JSONObject jsonObject = new JSONObject();
               if(ordinary) {
                   jsonObject.put("userId", order.getUserId());
                   jsonObject.put("products", jsonArray);
                   jsonObject.put("shippingAddress", session.getAddress());
                   jsonObject.put("paymentType", order.getPaymentType());
                   jsonObject.put("totalPrice",""+totalPrice);
               }
               else if(ordinary==false)
               {
                   jsonObject.put("userId", order.getUserId());
                   jsonObject.put("products", jsonArray);
                   jsonObject.put("shippingAddress", session.getAddress());
                   jsonObject.put("paymentType", "Paypal");
                   jsonObject.put("paypalPaymentId",paymentId);
                   jsonObject.put("paypalStatus",state);
                   jsonObject.put("totalPrice",""+totalPrice);
               }
                Log.i("myText", jsonObject.toString());

                url=getContext().getString(R.string.ApiAddress)+"api/order";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("orderResponse", response.toString());
                        processData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Order can't be processed", Toast.LENGTH_SHORT).show();

                    }
                }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("authorization", "bearer " + session.getUserToken());
                        return params;
                    }
                };
                requestQueue.add(jsonObjectRequest);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void getOnlineCart()
    {
        try {

            String url = getContext().getResources().getString(R.string.ApiAddress) + "api/cart/get";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId" , session.getUserId());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,  url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        //Toast.makeText(getContext(),response.toString(),Toast.LENGTH_SHORT).show();
                    try {
                        if (response.has("user"))
                        {
                            productList.clear();
                            JSONObject jsonObject = new JSONObject(response.getString("user"));
                            JSONArray jsonArray = jsonObject.getJSONArray("cart");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject  j1= jsonArray.getJSONObject(i);//Inside object
                                JSONObject j2 = new JSONObject(j1.getString("productId"));//inside productId
                                JSONArray j3 =j2.getJSONArray("Images");
                                ArrayList<String> img = new ArrayList<>();
                                img.add(j3.get(0).toString());
                                productList.add(new Product(
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

                                totalPrice=totalPrice +Integer.parseInt(j2.getString("Price")) * Integer.parseInt(j1.getString("quantity"));
                                tv_totalprice.setText("Total: " + totalPrice);

                            }
                            showLayout();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        getOnlineCart();
                    }
                }
            });
            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void pay_with_paypal()
    {
        int price= totalPrice/120;
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal("" +price),"USD","Payment for almari",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,7171);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7171)
        {
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(paymentConfirmation!=null)
                {
                    try
                    {
                        JSONObject paymentDetails = paymentConfirmation.toJSONObject();
                        Log.i("paymentFragment",  paymentDetails.toString());
                         paymentId= paymentDetails.getJSONObject("response").getString("id");
                         state=paymentDetails.getJSONObject("response").getString("state");
                         postOrder(false);


                    }
                    catch (Exception e )
                    {

                    }
                }
            }
            else if(resultCode== Activity.RESULT_CANCELED)
            {
                Toast.makeText(getContext(),"Request canceled",Toast.LENGTH_SHORT).show();
            }
        }
        else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(getContext(),"Invalid credentials",Toast.LENGTH_SHORT).show();
        }
    }
    void setPayment()
    {
            int selected = paymentChoice.getCheckedRadioButtonId();
            switch (selected)
            {
                case R.id.payment_COD:
                    order.setPaymentType("CashOnDelivery");
                    break;
            }

        }
    private void showLayout()
    {
            payment_layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    private void hideLayout()
    {
        payment_layout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void processData(JSONObject data)
    {
            try {
                if (!data.toString().isEmpty()) {
                    showLayout();
                    String tableName= "CartTable";
                    db = getContext().openOrCreateDatabase("CartDatabase", MODE_PRIVATE, null);
                    try {
                        db.execSQL("delete from " + tableName);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Intent in = new Intent(getContext(), HomeActivity.class);
                    startActivity(in);
                    db.close();
                    getActivity().finish();

                } else {
                    Toast.makeText(getContext(), "Order failed", Toast.LENGTH_SHORT).show();
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    private JSONArray convertData(List<OrderProduct> list)
    {
         JSONArray jsonArray = new JSONArray();
        try {



            for (int i = 0; i < list.size(); i++) {
                JSONObject jb = new JSONObject();
                jb.put("productId", list.get(i).productId);
                jb.put("quantity",list.get(i).quantity);

                jsonArray.put(jb);
            }
            return jsonArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonArray;
     }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
