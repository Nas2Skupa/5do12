package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SingleProvider extends BaseActivity {

    // JSON node keys
    private static final String TAG_ARRAY = "provider";
    private static final String TAG_ID = "ID";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GMAP = "gmap";
    private static final String TAG_ABOUT = "about";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_SUBCAT = "subcat";
    private static final String TAG_RATING = "rating";

    private String color;

    private String url = "http://nas2skupa.com/5do12/getSinglePro.aspx?id=";
    PricelistAdapter adapter;
    private ListView listView1;
    JSONArray providers = null;
    JSONObject provider = null;
    JSONArray pricelists = null;
    JSONObject pricelist = null;
    JSONArray phones = null;
    JSONObject phone = null;
    JSONArray payopt = null;
    JSONObject paying = null;
    private ProgressDialog pDialog;
    private String proId;
    ArrayList<PricelistClass> listArray = new ArrayList<PricelistClass>();
    List<Integer> payingArr = new ArrayList<Integer>();
    List<String> phonesArr = new ArrayList<String>();
    View footer = null;
    private RatingBar ratingBar;
    private RatingBar ratingBarBig;
    private float userRating;
    TextView lblAbout,lblEmail,lblWeb, lblAddress;
    ImageView btnFav, btnVise, btnMap;
    Dialog favDialog;
    LinearLayout moreLayout;
    Boolean detailOn = false, isFav=false;
    ProviderClass proClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_provider);
        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        proClass = bundle.getParcelable("providerclass");
        color = bundle.getString("color");
        proId=proClass.proID;
        url += proId;

        btnFav = (ImageView) findViewById(R.id.tofav);
        btnVise = (ImageView) findViewById(R.id.details);
        btnMap = (ImageView) findViewById(R.id.showMap);
        lblAbout = (TextView) findViewById(R.id.about_label);
        lblEmail = (TextView) findViewById(R.id.email);
        lblWeb = (TextView) findViewById(R.id.web);
        lblAddress = (TextView) findViewById(R.id.address);

        btnFav.setOnClickListener(clickHandler);
        btnVise.setOnClickListener(clickHandler);
        btnMap.setOnClickListener(clickHandler);
        lblAbout.setOnClickListener(clickHandler);
        lblEmail.setOnClickListener(clickHandler);
        lblWeb.setOnClickListener(clickHandler);
        lblAddress.setOnClickListener(clickHandler);

        if(proClass.proFav.equals("1")){
            isFav=true;
            btnFav.setImageResource(R.drawable.fav_icon_enabled);
        }
        new GetProvider().execute();
        footer = (View) getLayoutInflater().inflate(R.layout.service_listview_footer_row, null);
        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PricelistAdapter.PricelistHolder holder = (PricelistAdapter.PricelistHolder)view.getTag();
                PricelistClass pricelistclass = (PricelistClass)holder.priceObj;
                Bundle b = new Bundle();
                b.putParcelable("pricelistclass", pricelistclass);
                b.putParcelable("providerclass", proClass);
                int[] payArray = new int[payingArr.size()];
                for(int i = 0; i < payingArr.size(); i++) payArray[i] = payingArr.get(i);
                b.putIntArray("paying",payArray);
                b.putString("color", color);
                Intent in = new Intent(getApplicationContext(),
                        OrderActivity.class);
                in.putExtras(b);
                startActivity(in);
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
        ratingBarBig = (RatingBar) findViewById(R.id.ratingBarBig);
        LayerDrawable starsBig = (LayerDrawable) ratingBarBig.getProgressDrawable();
        starsBig.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
        ratingBarBig.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    userRating = rating;
                    new sendRating().execute(null, null, null);
                }
            }
        });
    }
    View.OnClickListener phoneClick = new View.OnClickListener(){
        public void onClick(View v) {
            try {
                String uri = ((TextView) v).getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + uri));
                startActivity(intent);
            }catch (Exception e){
                Log.d("CALL", e.toString());
            }

        }
    };

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == btnFav) {
                new addToFav().execute(null,null,null);
            }
            if(v==lblEmail){
                try{
                    String uri = "mailto:" + lblEmail.getText() + "?subject=" + "Upit sa 5do12";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch(Exception e){
                    Log.d("Email:",e.toString());
            }
            }
            if(v==lblWeb){
                try{
                    String uri = lblWeb.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch(Exception e){
                    Log.d("WWW:",e.toString());
                }
            }
            if(v == lblAddress){
                try{
                    String uri = "geo:0,0?q="+lblAddress.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch(Exception e){
                    Log.d("MAPA:",e.toString());
                }
            }
            if (v == btnVise || v==lblAbout) {
                moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnVise.getLayoutParams();
                if (detailOn==false) {
                    lblAbout.setMaxLines(Integer.MAX_VALUE);
                    moreLayout.setVisibility(View.VISIBLE);
                    detailOn=true;
                    btnVise.setImageResource(R.drawable.more_arrow_up);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    btnVise.setLayoutParams(params);
                }else{
                    lblAbout.setMaxLines(4);
                    moreLayout.setVisibility(View.GONE);
                    detailOn=false;
                    btnVise.setImageResource(R.drawable.more_arrow_down);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
                    btnVise.setLayoutParams(params);
                }
            }
        }
    };

    private class addToFav extends AsyncTask<Object, Object, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SingleProvider.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = prefs.getString("id", "");
                ServiceHandler sh = new ServiceHandler();
                String setFav="1";
                if(isFav==true){
                    setFav="0";

                }
                String url = "http://nas2skupa.com/5do12/setFav.aspx?userId=" + userId + "&proId=" + provider.getString(TAG_ID) + "&fav="+setFav;
                msg = sh.makeServiceCall(url, ServiceHandler.GET);
                Log.d("Response: ", "> " + msg);
            } catch (Exception ex) {
                Log.d("Error :", ex.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("Post exe",o.toString());
            if(isFav==false){
                isFav=true;
                btnFav.setImageResource(R.drawable.fav_icon_enabled);
            }else{
                isFav=false;
                btnFav.setImageResource(R.drawable.fav_icon);
            }
        }
    }

    public void rateProvider(View view) {
        ratingBar.setVisibility(View.GONE);
        ratingBarBig.setVisibility(View.VISIBLE);
        btnFav.setVisibility(View.GONE);
    }

    private class sendRating extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                if (userRating == 0) return null;
                final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                String userId = prefs.getString("id", "");
                ServiceHandler sh = new ServiceHandler();
                String url = "http://nas2skupa.com/5do12/setRate.aspx?userId=" + userId + "&proId=" + provider.getString(TAG_ID) + "&rate=" + (int) userRating;
                msg = sh.makeServiceCall(url, ServiceHandler.GET);
                Log.d("Response: ", "> " + msg);
            } catch (Exception ex) {
                Log.d("Error :", ex.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ratingBar.setVisibility(View.VISIBLE);
            ratingBarBig.setVisibility(View.GONE);
            btnFav.setVisibility(View.VISIBLE);
            if (o != null) {
                listArray.clear();
                adapter.clear();
                new GetProvider().execute();
            }
        }
    }

    private class GetProvider extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SingleProvider.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    providers = jsonObj.getJSONArray(TAG_ARRAY);
                    for (int i = 0; i < providers.length(); i++) {
                        provider = providers.getJSONObject(i);
                        pricelists = provider.getJSONArray("cjenik");
                        for (int j = 0; j < pricelists.length(); j++) {
                            JSONObject p = pricelists.getJSONObject(j);
                            String serviceID = p.getString("serviceID");
                            String service = p.getString("service");
                            String action = p.getString("action");
                            String termin = p.getString("termin");
                            int akcija = 0;
                            String price = p.getString("reg_price") + " " + getString(R.string.currency);
                            if (action.equals("1")) {
                                price = p.getString("action_price") + " " + getString(R.string.currency);
                                akcija = R.drawable.akcija_icon;
                            }
                            Log.d("ServiceID in populate:"+service,serviceID);
                            PricelistClass currPrice = new PricelistClass(serviceID, service, price,termin, akcija);
                            listArray.add(currPrice);
                        }
                        payopt = provider.getJSONArray("payopt");
                        for (int j = 0; j < payopt.length(); j++) {
                            JSONObject p = payopt.getJSONObject(j);
                            payingArr.add(Integer.parseInt(p.getString("paying")));
                        }
                        phones = provider.getJSONArray("phones");
                        for (int j = 0; j < phones.length(); j++) {
                            JSONObject p = phones.getJSONObject(j);
                            phonesArr.add(p.getString("phone"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            TextView lblName = (TextView) findViewById(R.id.name_label);
            TextView lblAbout = (TextView) findViewById(R.id.about_label);
            TextView lblWorking = (TextView) findViewById(R.id.working);
            LinearLayout llMore = (LinearLayout) findViewById(R.id.moreLayout);
            lblAbout.setBackgroundColor(Color.parseColor(color));
            llMore.setBackgroundColor(Color.parseColor(color));
            try {
                lblName.setText(provider.getString(TAG_NAME));
                lblAbout.setText(provider.getString(TAG_ABOUT));
                LinearLayout phones= (LinearLayout) findViewById(R.id.phonesLL);
                final float scale = getResources().getDisplayMetrics().density;
                int padding = (int) (5 * scale + 0.5f);
                for(int i = 0; i < phonesArr.size(); i++){
                    TextView phoneView = new TextView(SingleProvider.this);
                    phoneView.setText(phonesArr.get(i));
                    phoneView.setTextColor(Color.parseColor("#FFFFFF"));
                    phoneView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    phoneView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.call, 0, 0, 0);
                    phoneView.setCompoundDrawablePadding(padding);
                    phoneView.setPadding(padding, 0, 0, padding);
                    phoneView.setGravity(Gravity.CENTER_VERTICAL);
                    phoneView.setTag("phone" + i);
                    phoneView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    phoneView.setOnClickListener(phoneClick);
                    phones.addView(phoneView);
                }
                lblAddress.setText(provider.getString("address")+", "+provider.getString("cityName"));
                lblAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.google_maps_logo_transparent, 0, 0, 0);
                lblAddress.setCompoundDrawablePadding(padding);
                lblWeb.setText(Html.fromHtml("<u>" + provider.getString("web") + "</u>"));
                lblWeb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.web, 0, 0, 0);
                lblWeb.setCompoundDrawablePadding(padding);
                lblEmail.setText(Html.fromHtml("<u>" + provider.getString("email") + "</u>"));
                lblEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email, 0, 0, 0);
                lblEmail.setCompoundDrawablePadding(padding);
                lblWorking.setText(provider.getString("workingH"));
                int[] payArray = new int[payingArr.size()];
                for(int i = 0; i < payingArr.size(); i++) payArray[i] = payingArr.get(i);
                for(int i=0;i<payArray.length;i++){
                    ImageView payO;
                    if(payArray[i]==2){
                        payO = (ImageView) findViewById(R.id.visa);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==3){
                        payO = (ImageView) findViewById(R.id.american);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==4){
                        payO = (ImageView) findViewById(R.id.master);
                        payO.setVisibility(View.VISIBLE);
                    }
                    if(payArray[i]==5){
                        payO = (ImageView) findViewById(R.id.diners);
                        payO.setVisibility(View.VISIBLE);
                    }
                }
                try {
                    float rating = Float.parseFloat(provider.getString(TAG_RATING));
                    ratingBar.setRating(rating);
                } catch (NumberFormatException e) {
                }
            }catch (JSONException e){
                Log.e("JSON EXC", e.toString());
            }

            adapter = new PricelistAdapter(SingleProvider.this,
                    R.layout.listview_service_row, listArray);

            listView1.setAdapter(adapter);
        }


    }
}
