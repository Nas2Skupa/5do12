package com.nas2skupa.do12;

import com.nas2skupa.do12.R;
import java.util.ArrayList;
import java.util.HashMap;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class Subcats extends BaseListActivity {
 
    private ProgressDialog pDialog;
 
    // URL to get contacts JSON
    private String url = "http://nas2skupa.com/5do12/getSubcats.aspx?id=";
 
    // JSON Node names
    private static final String TAG_SUBCATS = "subcats";
    private static final String TAG_ID = "ID";
    private static final String TAG_NAME = "subcat";

	private String[] catSettings = new String[2];
 
    // subcats JSONArray
    JSONArray subcats = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> subcatList;
   
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcats);
        Bundle extras = getIntent().getExtras();
        String cat = extras.getString("cat");
        url+=cat;
        getCatSettings(cat);
        
        subcatList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
 
        // Listview on item click listener
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String subID = ((TextView) view.findViewById(R.id.subcatID))
                        .getText().toString();
            	//String name = "NEKO IME";
                // Starting detail view
                Intent in = new Intent(getApplicationContext(),
                        ProviderList.class);
                in.putExtra(TAG_ID, subID);
                in.putExtra(TAG_NAME, name);
                in.putExtra("color", catSettings[1]);
                startActivity(in);
            	//Toast.makeText(Subcats.this, TAG_NAME+"="+ name, Toast.LENGTH_SHORT).show();
            }
        });
 
        // Calling async task to get json
        new GetSubcats().execute();
    }

    
    @SuppressLint("NewApi") public void getCatSettings(String catID){
		TextView naslov = (TextView) findViewById(R.id.catname);
		catSettings= ((Globals) this.getApplication()).getCatSettings(Integer.parseInt(catID));
		naslov.setText(catSettings[0]);
		Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Helvetica.ttf");
		naslov.setTypeface(face);
		naslov.setTextSize(16);
		naslov.setAllCaps(true);
		naslov.setTextColor(Color.parseColor("#FFFFFF"));
		naslov.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		String bckclr=catSettings[1]; 
		naslov.setBackgroundColor(Color.parseColor(bckclr));
    }
    
    
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetSubcats extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Subcats.this);
            pDialog.setMessage(getString(R.string.pleaseWait));
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    subcats = jsonObj.getJSONArray(TAG_SUBCATS);
 
                   for (int i = 0; i < subcats.length(); i++) {
                        JSONObject c = subcats.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
 
                        // tmp hashmap for detail view 
                        HashMap<String, String> subcat = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        subcat.put(TAG_ID, id);
                        subcat.put(TAG_NAME, name);
 
                        subcatList.add(subcat);
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    Subcats.this, subcatList,
                    R.layout.list_item, new String[] { TAG_ID, TAG_NAME }, new int[] { R.id.subcatID, R.id.name });
 
            setListAdapter(adapter);
        }
        
        
        
        
    }
 
}
