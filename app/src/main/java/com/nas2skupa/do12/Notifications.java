package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Notifications extends BaseActivity {

    private ListView listView1;
    ArrayList<Order> listArray = new ArrayList<Order>();
    // URL to get contacts JSON
    private Uri baseUri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getAkc.aspx").build();
    // JSON Node names
    private static final String TAG_ARRAY = "akcije";
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "name";
    private static final String TAG_CAT = "category";
    JSONArray providers = null;
    View header = null;
    View filter = null;
    private Context context;
    private NotificationsAdapter adapter;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        context = this;
        header = getLayoutInflater().inflate(R.layout.listview_header_row, null);
//        filter = getLayoutInflater().inflate(R.layout.listview_filter_row, null);
        adapter = new NotificationsAdapter(this, R.layout.listview_notification_row, listArray);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        getSubCatSettings("poruke", "#0090db", header);

        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.addHeaderView(header);
//        listView1.addHeaderView(filter);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            @SuppressLint("NewApi")
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationsAdapter.NotificationsHolder holder = (NotificationsAdapter.NotificationsHolder) view.getTag();
                Order orderclass = (Order)holder.orderObj;
                Bundle b = new Bundle();
                b.putParcelable("orderclass", orderclass);

                // TODO: digni popup
            }
        });

        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getOrders.aspx")
                .appendQueryParameter("userId", userId)
                .appendQueryParameter("year", String.valueOf("2015"))
                .appendQueryParameter("month", String.valueOf("7"))
                .build();
        new HttpRequest(getApplicationContext(), uri, true)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        parseServerResult(result);
                    }
                });
    }

    private void parseServerResult(String result) {
        listArray.clear();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArray = jsonObj.getJSONArray("orders");
            for (int i = 0; i < jsonArray.length(); i++) {
                Order order = new Order(jsonArray.getJSONObject(i));
                listArray.add(order);
            }
        } catch (JSONException e) {
            Log.e("ActionHttpRequest", "Error parsing server data.");
            e.printStackTrace();
        }
        listView1.setAdapter(adapter);
    }

    @SuppressLint("NewApi")
    public void getSubCatSettings(String subcatTitle, String color, View header) {
        TextView naslov = (TextView) header.findViewById(R.id.subcatname);
        naslov.setText(subcatTitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Helvetica.ttf");
        naslov.setTypeface(face);
        naslov.setTextSize(16);
        naslov.setAllCaps(true);
        naslov.setTextColor(Color.parseColor("#FFFFFF"));
        naslov.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        String bckclr = color;
        naslov.setBackgroundColor(Color.parseColor(bckclr));
    }
}