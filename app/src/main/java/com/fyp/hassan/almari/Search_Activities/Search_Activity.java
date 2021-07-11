package com.fyp.hassan.almari.Search_Activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.SearchRecentSuggestions;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.hassan.almari.Category_package.CategoryActivity;
import com.fyp.hassan.almari.Customer_Order.MyOrdersListClass;
import com.fyp.hassan.almari.home_tabs.HomeActivity;
import com.fyp.hassan.almari.R;
import com.fyp.hassan.almari.SingleProductAvtivity.Product;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Search_Activity extends AppCompatActivity implements SearchView.OnQueryTextListener

{
   private Toolbar myToolbar;
   private static final int RECORD_REQUEST_CODE = 101;
   private Intent in;
   private URI uri ;
   RecyclerView recyclerView;
   Search_Adapter adapter;
   RecyclerView.LayoutManager layoutManager;
   private EditText search_field;
   private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
   private boolean permissionToRecordAccepted = false;
   private MicrophoneHelper microphoneHelper;
   private SpeechToText speechService;
   private boolean listening = false;
   private MicrophoneInputStream capture;
   private View mView;
   private ImageView backBtn;
   AlertDialog dialog;
   private RequestQueue requestQueue;
   List<Product> productList;
   private LinearLayout search_resultLayout;
   private ProgressBar progressBar;
   private  Button seeAll_btn;
   private String searchQuery="";
   private TextView ResultcategoryName,queryText,searchTotalResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_field=(EditText)findViewById(R.id.Search_Et_Search);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        progressBar=(ProgressBar)findViewById(R.id.search_activity_pd);
        search_resultLayout =(LinearLayout)findViewById(R.id.Search_result);
        queryText=(TextView)findViewById(R.id.Search_Text);
        seeAll_btn=(Button)findViewById(R.id.Search_AllResult_btn);
        searchTotalResults=(TextView)findViewById(R.id.Search_TotalResults);
        ResultcategoryName =(TextView)findViewById(R.id.Search_categoryName);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        requestQueue = Volley.newRequestQueue(this);
        recyclerView=(RecyclerView)findViewById(R.id.Search_recylerView) ;

        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        backBtn= (ImageView) findViewById(R.id.search_backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seeAll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in=new Intent(Search_Activity.this, CategoryActivity.class);
                in.putExtra("searchResults",searchQuery);
                startActivity(in);

            }
        });
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i("User", "Permission has been denied by user");
                } else {
                    Log.i("User", "Permission has been granted by user");
                }
                return;
            }
            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }
    //Record a message via Watson Speech to Text
    private void recordMessage() {
        //mic.setEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        mView=inflater.inflate(R.layout.record_popup,null,false);
        Button btn_cancel=(Button)mView.findViewById(R.id.search_micro_button);
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(mView);
        dialog = mBuilder.create();


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(listening)
                try {
                    dialog.dismiss();
                    capture.close();
                    listening = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        speechService = new SpeechToText();
        speechService.setUsernameAndPassword(getString(R.string.ibm_username), getString(R.string.ibm_password));

        if(!listening) {
            capture = new MicrophoneInputStream(true);
            dialog.show();

                    try {
                        speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                        listening = true;
                    } catch (Exception e) {
                        showError(e);
                    }
                }
        else {
            try {

                capture.close();
                listening = false;
                Toast.makeText(this,"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        }
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                //.model("en-UK_NarrowbandModel")
                .interimResults(true)
                .inactivityTimeout(5)

                //.speakerLabels(true)
                .build();
    }
    //Watson Speech to Text Methods.
    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechResults speechResults) {
            System.out.println(speechResults);

            /*recoTokens = new SpeakerLabelsDiarization.RecoTokens();
            if(speechResults.getSpeakerLabels() !=null)
            {
                recoTokens.add(speechResults);
                Log.i("SPEECHRESULTS",speechResults.getSpeakerLabels().get(0).toString());
            }*/
            if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);
            }
        }
        @Override public void onConnected() {
        }
        @Override public void onError(Exception e) {
            showError(e);
            listening=false;
        }
        @Override public void onDisconnected() {
            dialog.dismiss();
            listening=false;
            try {
                capture.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {
            dialog.dismiss();
            listening=false;
        }
        @Override
        public void onListening() {

        }
        @Override
        public void onTranscriptionComplete() {
            dialog.dismiss();
            listening=false;
        }
    }
    private void showMicText(final String text) {
        dialog.dismiss();
        listening=false;
        search_field.setText(text);
    }
    private void showError(final Exception e) {
        dialog.dismiss();
        e.printStackTrace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem menuItem= menu.findItem(R.id.SearchField);
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Search_to_home:
                in = new Intent(this,HomeActivity.class);
                startActivity(in);
                return true;

                case R.id.Seacrch_myOrder:
                in = new Intent(this, MyOrdersListClass.class);
                startActivity(in);
                return true;

            case R.id.Search_Microphone:
              /*  int permission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    makeRequest();
                }
                recordMessage();
                */
                return true;
            case R.id.SettingS2:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case android.R.id.home:
                in = new Intent(this,HomeActivity.class);
                startActivity(in);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void searchOnline( String text)
    {
        try {
            String url = this.getResources().getString(R.string.ApiAddress) + "api/search";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text",text);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        Log.i("searchResult", response.toString());
                        setAdapterData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setAdapterData(JSONObject data)
    {
        try {
            int numOfResults=0;
            ArrayList<String > imgList;
            productList =new ArrayList<>();
            JSONObject topProduct= data.getJSONObject("TopProduct");
            ResultcategoryName.setText("" + topProduct.getString("Category"));
            queryText.setText(searchQuery +" in");
            JSONArray allResult =data.getJSONArray("allResults");

            for(int i = 0 ; i<allResult.length();i++)
            {
                imgList = new ArrayList<>();
                JSONObject js = allResult.getJSONObject(i);
                imgList.add(js.getJSONArray("Images").get(0).toString());
                productList.add(new Product(
                        js.getString("_id"),
                        js.getString("Title"),
                        js.getString("Description"),
                        js.getString("Quantity"),
                        js.getString("Price"),
                        js.getString("BrandName"),
                        js.getString("CategoryName"),
                        js.getString("SubCategoryName"),
                        imgList,
                        js.getString("AverageRating")
                ));
                numOfResults++;
            }
            if(!productList.isEmpty()) {
                adapter = new Search_Adapter(this, productList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                seeAll_btn.setText("See all Results(" +numOfResults+")");
                searchTotalResults.setText(""+numOfResults);
                showLayout();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            search_resultLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideLayout()
    {
        search_resultLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showLayout()
    {
        search_resultLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideLayout();
        searchQuery=query;
        requestQueue.cancelAll("");
        searchOnline(query);
        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.length()>2) {
            hideLayout();
            searchQuery=newText;
            requestQueue.cancelAll("");
            searchOnline(newText);

        }
        //search_field.setText(newText);
        return true;
    }


}
