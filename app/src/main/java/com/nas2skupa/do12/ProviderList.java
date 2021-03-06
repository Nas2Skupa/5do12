package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProviderList extends BaseActivity {

    private ListView listView1;
    // URL to get contacts JSON
    private Uri baseUri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getPro.aspx").build();
    private String subcategoryId;
    // JSON Node names
    private static final String TAG_ARRAY = "provider";
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "name";
    private String color;
    // subcats JSONArray
    JSONArray providers = null;
    View header = null;
    View filter = null;
    private Context context;
    private ProviderAdapter adapter;
    private SharedPreferences preferences;

    private SortDialog sortDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prolist);
        Bundle extras = getIntent().getExtras();
        String subcat = extras.getString("subcat");
        color = extras.getString("color");
        subcategoryId = extras.getString("ID");

        context = this;
        header = getLayoutInflater().inflate(R.layout.listview_header_row, null);
        filter = getLayoutInflater().inflate(R.layout.listview_filter_row, null);
        adapter = new ProviderAdapter(this, R.layout.listview_provider_row, new ArrayList<ProviderClass>());
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        getSubCatSettings(subcat, color, header);

        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.addHeaderView(header);
        listView1.addHeaderView(filter);
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                ProviderAdapter.ProviderHolder holder = (ProviderAdapter.ProviderHolder) view.getTag();
                ProviderClass providerclass = (ProviderClass) holder.proObj;
                Bundle b = new Bundle();
                b.putParcelable("providerclass", providerclass);
                b.putString("color", color);
                Intent in = new Intent(getApplicationContext(),
                        SingleProvider.class);
                in.putExtras(b);
                startActivity(in);
            }
        });

        sortDialog = new SortDialog(context, adapter);
        ImageView listSettingsBtn = (ImageView) findViewById(R.id.listSettingsBtn);
        listSettingsBtn.setVisibility(View.VISIBLE);
        listSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog.show();
            }
        });

        CitiesFilter citiesFilter = new CitiesFilter(this, (Spinner) findViewById(R.id.cities), (Spinner) findViewById(R.id.districts), 0);
        citiesFilter.setOnFilterChangedListener(new CitiesFilter.OnFilterChangedListener() {
            @Override
            public void onFilterChanged(String city, String district) {
                Uri.Builder builder = baseUri.buildUpon();
                builder.appendQueryParameter("ID", subcategoryId);
                builder.appendQueryParameter("u", preferences.getString("id", ""));
                builder.appendQueryParameter("countId", "1");
                City cityObj = Globals.cities.get(city);
                if (cityObj != null) {
                    builder.appendQueryParameter("cid", cityObj.id);
                    District districtObj = cityObj.districts.get(district);
                    if (districtObj != null)
                        builder.appendQueryParameter("qid", districtObj.id);
                }
                new HttpRequest(context, builder.build(), false).setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        if (result != null) parseServerResult(result);
                    }
                });
            }
        });
    }

    private void parseServerResult(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            providers = jsonObj.getJSONArray(TAG_ARRAY);
            ArrayList<ProviderClass> newList = new ArrayList<>();

            for (int i = 0; i < providers.length(); i++) {
                JSONObject c = providers.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String favore = c.getString("favorite");
                String action = c.getString("akcija");
                double lat = c.optDouble("lat", 0);
                double lon = c.optDouble("lon", 0);
                int fav = R.drawable.blank;
                int akcija = R.drawable.blank;
                if (favore.equals("1")) {
                    fav = R.drawable.fav_icon_enabled;
                }
                if (action.equals("1")) {
                    akcija = R.drawable.akcija_icon;
                }
                float rating = 0;
                try {
                    rating = Float.parseFloat(c.getString("rating"));
                } catch (NumberFormatException e) {
                }
                ProviderClass currProvider = new ProviderClass(id, name, favore, null, fav, akcija, rating, (float) lat, (float) lon);
                newList.add(currProvider);
            }
            adapter.clear();
            adapter.addAll(newList);
            adapter.filterDiscounts(sortDialog.shouldFilterDiscounts());
            adapter.sortBy(sortDialog.getSortType());
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e("ProviderListHttpRequest", "Error parsing server data.");
            e.printStackTrace();
        }
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