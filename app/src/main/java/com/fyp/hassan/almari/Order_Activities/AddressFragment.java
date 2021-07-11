package com.fyp.hassan.almari.Order_Activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.home_tabs.CustomViewPager;
import com.fyp.hassan.almari.LoginClasses.UserSessionManager;
import com.fyp.hassan.almari.R;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressFragment extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText newAdress,ad_address,ad_number;
    private TextView UserAddress,UserNumber,addNewAdress;
    private Button next,saveData;
    private CheckBox checkBox;
    private String address;
    private OnFragmentInteractionListener mListener;
    private UserSessionManager session;
    private TabLayout tb;
    private RelativeLayout relativeLayout;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private Wave wave;
    private CardView fbCard, cardData;
    private  String fname,lname,email,phone,gender;
    public AddressFragment() {

    }

    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_address, container, false);
        try {
            requestQueue = Volley.newRequestQueue(getContext());
            session = new UserSessionManager(getContext());
            relativeLayout = (RelativeLayout) v.findViewById(R.id.address_layout);
            tb = (TabLayout) getActivity().findViewById(R.id.CheckoutTabLayout);
            progressBar = (ProgressBar) v.findViewById(R.id.address_progressBar);
            fbCard = (CardView) v.findViewById(R.id.fbCardDataLayout);
            cardData = (CardView) v.findViewById(R.id.CardDataLayout);
            ad_address = (EditText) v.findViewById(R.id.ad_address);
            ad_number = (EditText) v.findViewById(R.id.ad_number);
            saveData = (Button) v.findViewById(R.id.saveDataBTn);
            wave = new Wave();
            progressBar.setIndeterminateDrawable(wave);
            UserAddress = (TextView) v.findViewById(R.id.user_address);
            UserNumber = (TextView) v.findViewById(R.id.user_phoneNumber);
            newAdress = (EditText) v.findViewById(R.id.user_newAddress);
            checkBox = (CheckBox) v.findViewById(R.id.user_checkBox);
            addNewAdress = (TextView) v.findViewById(R.id.user_AddAddress);
            next = (Button) v.findViewById(R.id.Address_tab_nextBtn);
            setUpelement();

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        newAdress.setVisibility(View.GONE);
                    } else {
                        newAdress.setVisibility(View.VISIBLE);
                    }
                }
            });
            addNewAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (newAdress.getVisibility() == View.VISIBLE) {
                        newAdress.setVisibility(View.GONE);
                        checkBox.setChecked(true);
                    } else {
                        newAdress.setVisibility(View.VISIBLE);
                        checkBox.setChecked(false);
                    }
                }
            });

            saveData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateDetails();
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAddress()) {
                        //Toast.makeText(getContext(),totalPrice.getText() ,Toast.LENGTH_SHORT).show();

                           CustomViewPager vp = (CustomViewPager) getActivity().findViewById(R.id.CheckoutViewPager);
                           vp.setCurrentItem(1);

                    }
                    else
                    {
                        Toast.makeText(getContext(),"Enter valid Address",Toast.LENGTH_LONG).show();
                    }
                }
            });



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return v;
    }



        private void setUpelement() {
        try {
            if (session.isUserLoggedIn()) {
                String userid=session.getUserId();
                String url = this.getString(R.string.ApiAddress) + "api/user/"+userid;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("userInfo",response.toString());
                        processData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof TimeoutError)
                        {
                            setUpelement();
                        }
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processData(JSONObject data)
    {

        try {

            fname=data.getString("firstName");
            lname=data.getString("lastName");
            email=data.getString("email");
            gender=data.getString("gender");

            if(!data.getString("phoneNumber").isEmpty() && !data.getString("address").isEmpty())
            {
                //Toast.makeText(getContext(),data.toString(),Toast.LENGTH_SHORT).show();
                UserAddress.setText(data.getString("address"));
                UserNumber.setText(data.getString("phoneNumber"));
                progressBar.setVisibility(View.GONE);
                cardData.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                cardData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                fbCard.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateDetails()
    {
        try {

            String num = ad_number.getText().toString();
            final String uaddress = ad_address.getText().toString();
                if(validateFields(uaddress,num)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", session.getUserId());
                    jsonObject.put("firstName", fname);
                    jsonObject.put("lastName", lname);
                    jsonObject.put("email", email);
                    jsonObject.put("address", uaddress);
                    jsonObject.put("phoneNumber", num);
                    jsonObject.put("gender", gender);


                    String url = this.getString(R.string.ApiAddress) + "api/user/update";
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            Log.d("userUpdate", response.toString());
                            session.setAddress(uaddress);
                            Log.d("userAdd", session.getAddress());
                            CustomViewPager vp = (CustomViewPager) getActivity().findViewById(R.id.CheckoutViewPager);
                            vp.setCurrentItem(1);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();

                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean validateFields(String Address,String number)
    {

        if(number.matches("\\d{12}") && Address.length()>1)
        {
            return true;
        }

        if (Address.length()<=1)
            ad_address.setError("Please enter a valid address");

        if(!number.matches("\\d{12}"))
            ad_number.setError("Please enter valid number");

        return false;
    }
    private boolean checkAddress()
    {

        if(checkBox.isChecked())
        {
            address=UserAddress.getText().toString();
            if(!address.isEmpty()) {
                session.setAddress(address);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            address= newAdress.getText().toString();
            if (address.isEmpty())
            {
                newAdress.setError("Enter valid address");
                return false;
            }
            else{
                if(address.length()>1) {
                    session.setAddress(address);
                    return true;
                }
                else
                {
                    return false;
                }
            }

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
