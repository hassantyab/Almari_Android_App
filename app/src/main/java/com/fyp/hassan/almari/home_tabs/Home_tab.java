package com.fyp.hassan.almari.home_tabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Category_package.CategoryProductAdapter;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SalesPackage.Sales_Adapter;
import com.fyp.hassan.almari.SalesPackage.salesDetails;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.fyp.hassan.almari.SingleProductAvtivity.ProductListAdapter;
import com.fyp.hassan.almari.SingleProductAvtivity.SingleProductActivity;
import com.fyp.hassan.almari.SingleProductAvtivity.singleRecommedRecyclerview;
import com.fyp.hassan.almari.banners.Banner;
import com.fyp.hassan.almari.banners.RemoteBanner;
import com.fyp.hassan.almari.events.OnBannerClickListener;
import com.fyp.hassan.almari.views.BannerSlider;
import com.fyp.hassan.almari.views.banner_data;
import com.fyp.hassan.almari.views.banner_info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class Home_tab extends Fragment {

    private BannerSlider bannerSlider;
    private RecyclerView dealsrecyclerView,allSalesRecyclerView,mostViewedRecyclerView;
    private ProductListAdapter dealsadapter;
    private singleRecommedRecyclerview mostViewdAdapter;
    private Sales_Adapter sales_adapter;
    List<Product> saleslist,mostViewedList;
    List<salesDetails> AllsalesList;
    List<banner_data> banner_list ;
    List<banner_info> banner_infoList ;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private RequestQueue requestQueue;
    private OnFragmentInteractionListener mListener;

    public Home_tab() {
        // Required empty public constructor
    }

    public static Home_tab newInstance(String param1, String param2) {
        Home_tab fragment = new Home_tab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getContext());
    }
    private void setupViews() {
        try
        {
            String url=this.getResources().getString(R.string.ApiAddress) + "api/slider";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("banResponse", response.toString());
                    setupBannerSlider(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        setupViews();
                    }
                }
            });
            requestQueue.add(jsonArrayRequest);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    private void setupBannerSlider(JSONArray bannerArray){

        try {

                ArrayList<String> banner_urls= new ArrayList<>();
                banner_list = new ArrayList<>();
                banner_infoList=new ArrayList<>();
                for (int i= 0; i<bannerArray.length();i++) {
                    JSONObject jsonObject = bannerArray.getJSONObject(i);
                    String bId = jsonObject.getString("_id");

                    JSONArray productsArray = jsonObject.getJSONArray("products");
                    JSONObject product = productsArray.getJSONObject(0);
                    banner_info banner_info = new banner_info(
                            product.getString("productId"),
                            product.getString("category"),
                            product.getString("img"),
                            product.getString("title"),
                            product.getString("color")
                    );
                    banner_urls.add(product.getString("img"));
                    banner_infoList.add(banner_info);
                    banner_data banner_data = new banner_data(bId,banner_infoList);
                    banner_list.add(banner_data);
                }
                addBanners(banner_urls);
                bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void onClick(int position) {
                        //Toast.makeText(getContext(), "Banner with position " + String.valueOf(position) + " clicked!", Toast.LENGTH_SHORT).show();

                        try {
                            //Toast.makeText(getContext(), "productId" + banner_list.get(position).getBanner_info().get(0).getpId(), Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getContext(), SingleProductActivity.class);
                            in.putExtra("Pid", banner_list.get(position).getBanner_info().get(position).getpId());
                            getContext().startActivity(in);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void addBanners( ArrayList<String> imgUrls){

        List<Banner> remoteBanners=new ArrayList<>();
        //Add banners using image urls
        for(int i=0;i<imgUrls.size();i++)
        {
            remoteBanners.add(new RemoteBanner("https://s3.us-east-2.amazonaws.com/almari-2018/"+imgUrls.get(i)));
        }
        bannerSlider.setBanners(remoteBanners);

    }
    private void setSales()
    {
        try {
            String url = getContext().getResources().getString(R.string.ApiAddress) + "api/Sales";
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    processSales(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if(error instanceof TimeoutError)
                    {
                        setSales();
                    }
                }
            });
            requestQueue.add(jsonArrayRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void processSales(JSONArray response)
    {
        try {
            if (!response.toString().isEmpty())
            {
                for (int i = 0;i<response.length();i++)
                {
                    JSONObject jsonObject = response.getJSONObject(i);
                    AllsalesList.add(new salesDetails(jsonObject.getString("_id"),jsonObject.getString("salePercentage"),jsonObject.getString("saleImage")));
                }
                sales_adapter = new Sales_Adapter(getContext(),AllsalesList);
                allSalesRecyclerView.setAdapter(sales_adapter);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView=inflater.inflate(R.layout.fragment_home_tab, container, false);;
        try {
            myFragmentView = inflater.inflate(R.layout.fragment_home_tab, container, false);
            bannerSlider = (BannerSlider) myFragmentView.findViewById(R.id.banner_slider1);
            dealsrecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.hometab_salesRecyclerView);
            allSalesRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.hometab_allSalesRV);
            mostViewedRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.Rv_mostViewd);

            saleslist = new ArrayList<>();
            AllsalesList = new ArrayList<>();
            mostViewedList = new ArrayList<>();

            dealsrecyclerView.setHasFixedSize(true);
            dealsrecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            dealsrecyclerView.addItemDecoration(new decorator());

            allSalesRecyclerView.setHasFixedSize(true);
            allSalesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            allSalesRecyclerView.addItemDecoration(new decorator());

            mostViewedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mostViewedRecyclerView.setHasFixedSize(true);
            mostViewedRecyclerView.addItemDecoration(new mostViewedecorator());

            setMostView();
            setDeals();
            setSales();
            setupViews();
            return myFragmentView;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return myFragmentView;
    }

    private void setMostView()
    {
        String url = this.getResources().getString(R.string.ApiAddress) + "api/products/most-viewed" ;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try
                {
                    if (response!=null) {

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject js = response.getJSONObject(i);
                                JSONArray on=js.getJSONArray("Images");
                                ArrayList<String> imgArray = new ArrayList<>();
                                for(int j =0; j<on.length();j++) {
                                    imgArray.add(on.getString(j));
                                }
                                Product p = new Product(js.getString("_id"),js.getString("Title"),js.getString("Description"),js.getString("Quantity"),js.getString("Price"),js.getString("BrandName"),js.getString("CategoryName"),js.getString("SubCategoryName"),imgArray,js.getString("AverageRating"));
                                mostViewedList.add(p);
                            }
                            mostViewdAdapter=new singleRecommedRecyclerview(getContext(),mostViewedList);
                            mostViewedRecyclerView.setAdapter(mostViewdAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof TimeoutError)
                        {
                            setMostView();
                        }
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void setDeals()
    {
        String url = this.getString(R.string.ApiAddress) + "api/recommend/" + "5b61d30217a38d00141e6962" ;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("Recommed", response.toString());
                try
                {
                    for (int i =0; i <response.length();i++)
                    {
                        ArrayList<String> img = new ArrayList<>();
                        JSONObject js = response.getJSONObject(i);
                        JSONArray ja = js.getJSONArray("Images");
                        img.add(ja.getString(0));
                        Product p = new Product(js.getString("_id"),js.getString("Title"),js.getString("Description"),"5",js.getString("Price"),js.getString("BrandName"),js.getString("CategoryName"),js.getString("SubCategoryName"),img,js.getString("AverageRating"));
                        saleslist.add(p);
                    }
                    dealsadapter = new ProductListAdapter(getContext(),saleslist);
                    dealsrecyclerView.setAdapter(dealsadapter);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof TimeoutError)
                        {
                            setDeals();
                        }
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

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

    private class decorator extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            outRect.left=5;
            outRect.right=5;
            outRect.top=5;
            outRect.bottom=5;

        }
    }
    public class mostViewedecorator extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left=5;
            outRect.right=5;
            outRect.top=5;
            outRect.bottom=5;

        }
    }


}
