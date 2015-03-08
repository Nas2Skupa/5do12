package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends BaseActivity {

	private ProgressDialog pDialog;
    TextView header = null;
    TextView pUsername, pName, pAddress, pMobile, pEmail, pPasswordlbl;
    LinearLayout mobLL, addressLL;
    String oldPasstxt,newPasstxt,renewPasstxt, mobiteltxt,addresstxt, responseBody;
    String cId="";
    String qId="";
    String cName="";
    String qName="";
    View filter = null;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);
        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        header = (TextView) findViewById(R.id.subcatname);
        filter = getLayoutInflater().inflate(R.layout.listview_filter_row, null);
        getSubCatSettings("profil", "#adbb00");
        pUsername=(TextView) findViewById(R.id.pUsername);
        pUsername.setText(prefs.getString("username",""));
        pName=(TextView) findViewById(R.id.pName);
        pName.setText(prefs.getString("name","")+" "+prefs.getString("surname",""));
        pAddress=(TextView) findViewById(R.id.pAdresa);
        pAddress.setText(prefs.getString("home_add","")+System.getProperty("line.separator")+prefs.getString("quartName","")+", "+prefs.getString("cityName",""));
        pMobile=(TextView) findViewById(R.id.pMobile);
        pMobile.setText(prefs.getString("cell",""));
        pEmail=(TextView) findViewById(R.id.pEmail);
        pEmail.setText(prefs.getString("email",""));
        pPasswordlbl=(TextView) findViewById(R.id.pPasswordlbl);
        mobLL=(LinearLayout) findViewById(R.id.mobLL);
        addressLL=(LinearLayout) findViewById(R.id.addressLL);
        mobLL.setOnClickListener(clickHandler);
        addressLL.setOnClickListener(clickHandler);
        pPasswordlbl.setOnClickListener(clickHandler);

    }

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == addressLL) {
                final Dialog passDialog = new Dialog(ProfilActivity.this);
                passDialog.setContentView(R.layout.addressdialog);
                passDialog.setTitle("Promjena adrese");
                final TextView adresa = (TextView) passDialog.findViewById(R.id.addresstxt);
                final TextView errTxt = (TextView) passDialog.findViewById(R.id.errorTxt);
                Spinner gradovi = (Spinner) passDialog.findViewById(R.id.cities);
                Spinner kvartovi =(Spinner) passDialog.findViewById(R.id.districts);
                CitiesFilter citiesFilter = new CitiesFilter(ProfilActivity.this, gradovi, kvartovi,1);
                gradovi.setSelection(((ArrayAdapter)gradovi.getAdapter()).getPosition(prefs.getString("cityName", "")));
                kvartovi.setSelection(((ArrayAdapter)kvartovi.getAdapter()).getPosition(prefs.getString("quartName", "")));
                citiesFilter.setOnFilterChangedListener(new CitiesFilter.OnFilterChangedListener() {
                    @Override
                    public void onFilterChanged(String city, String district) {
                        Log.d("STRING CITY",city);
                        Log.d("STRING DISTRICT",district);
                        City cityObj = Globals.cities.get(city);
                        if (cityObj != null) {

                            Log.d("STRING CITYOBJ",cityObj.name);
                            cId=cityObj.id;
                            cName=cityObj.name;
                            District districtObj = cityObj.districts.get(district);
                            if (districtObj != null){
                                Log.d("STRING DISTRICTOBJ",districtObj.name);
                                qId=districtObj.id;
                                qName=districtObj.name;
                            }
                        }
                    }
                });
                Button okButton = (Button) passDialog.findViewById(R.id.save);
                Button cancelButton = (Button) passDialog.findViewById(R.id.cancel);
                passDialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addresstxt=adresa.getText().toString();
                        if(addresstxt.length()<3){
                            errTxt.setText("Unesite adresu!");
                            errTxt.setVisibility(View.VISIBLE);
                            return;
                        }
                        Integer[] asyncParams={3};
                        new changeData().execute(asyncParams);
                        passDialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passDialog.dismiss();
                    }
                });
            }
            if (v == mobLL) {
                final Dialog passDialog = new Dialog(ProfilActivity.this);
                passDialog.setContentView(R.layout.mobdialog);
                passDialog.setTitle("Promjena broja mobitela");
                final TextView mobitel = (TextView) passDialog.findViewById(R.id.mobtxt);
                final TextView errTxt = (TextView) passDialog.findViewById(R.id.errorTxt);
                Button okButton = (Button) passDialog.findViewById(R.id.save);
                Button cancelButton = (Button) passDialog.findViewById(R.id.cancel);
                passDialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mobiteltxt=mobitel.getText().toString();
                        if(mobiteltxt.length()<9){
                            errTxt.setText("Neispravan broj mobitel!");
                            errTxt.setVisibility(View.VISIBLE);
                            return;
                        }
                        Integer[] asyncParams={2};
                        new changeData().execute(asyncParams);
                        passDialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passDialog.dismiss();
                    }
                });
            }
            if (v == pPasswordlbl) {
                final Dialog passDialog = new Dialog(ProfilActivity.this);
                passDialog.setContentView(R.layout.passdialog);
                passDialog.setTitle("Promjena lozinke");
                final TextView oldPass = (TextView) passDialog.findViewById(R.id.oldpass);
                final TextView newPass = (TextView) passDialog.findViewById(R.id.newpass);
                final TextView renewPass = (TextView) passDialog.findViewById(R.id.renewpass);
                final TextView errTxt = (TextView) passDialog.findViewById(R.id.errorTxt);
                Button okButton = (Button) passDialog.findViewById(R.id.save);
                Button cancelButton = (Button) passDialog.findViewById(R.id.cancel);
                passDialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oldPasstxt=oldPass.getText().toString();
                        newPasstxt=newPass.getText().toString();
                        renewPasstxt=renewPass.getText().toString();
                        if(!newPasstxt.equals(renewPasstxt)){
                            errTxt.setText("Polje \"Provjera nove lozinke\" ne odgovara polju \"Nova lozinka\"!");
                            errTxt.setVisibility(View.VISIBLE);
                            return;
                        }
                        if(newPasstxt.length()<6){
                            errTxt.setText("Nova lozinka mora imati najmanje 6 znakova!");
                            errTxt.setVisibility(View.VISIBLE);
                            return;
                        }
                        Integer[] asyncParams={1};
                        new changeData().execute(asyncParams);
                        passDialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passDialog.dismiss();
                    }
                });
            }
        }
    };


    @SuppressLint("NewApi")
	public void getSubCatSettings(String subcatTitle, String color){
		TextView naslov = (TextView) findViewById(R.id.subcatname);
		naslov.setText(subcatTitle);
		Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Helvetica.ttf");
		naslov.setTypeface(face);
		naslov.setTextSize(16);
		naslov.setAllCaps(true);
		naslov.setTextColor(Color.parseColor("#FFFFFF"));
		naslov.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		String bckclr=color;
		naslov.setBackgroundColor(Color.parseColor(bckclr));
    }
    static boolean isNumeric(String str)
    {
        try
        {
            int i = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    private class changeData extends AsyncTask<Integer, Void, Void> {

        Integer changeCase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ProfilActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Integer... arg0) {
            changeCase=arg0[0];
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://nas2skupa.com/5do12/changeUser.aspx");
            prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
            String userId = prefs.getString("id", "");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("case", String.valueOf(changeCase)));
                nameValuePairs.add(new BasicNameValuePair("uid", userId));
                if(changeCase==1){
                    nameValuePairs.add(new BasicNameValuePair("oldpass", oldPasstxt));
                    nameValuePairs.add(new BasicNameValuePair("newpass", newPasstxt));
                }
                if(changeCase==2){
                    nameValuePairs.add(new BasicNameValuePair("mobitel", mobiteltxt));
                }
                if(changeCase==3){
                    nameValuePairs.add(new BasicNameValuePair("address", addresstxt));
                    nameValuePairs.add(new BasicNameValuePair("city", cId));
                    nameValuePairs.add(new BasicNameValuePair("quart", qId));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    responseBody = EntityUtils.toString(response.getEntity());
                }else{
                    responseBody="no response";
                }
                Log.d("ResponseBody", responseBody);

            } catch (ClientProtocolException e) {
                Log.d("Protocol Excpetion", e.toString());
            } catch (IOException e) {
                Log.d("IO Excpetion", e.toString());
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String msg="";
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(changeCase==1){
                if (responseBody.equals("true")){
                    msg="Lozinka uspješno promijenjena.";
                }else{
                    msg="Neispravna stara lozinka! Pokušajte ponovno.";
                }
            }
            if(changeCase==2){
                if (responseBody.equals("true")){
                    msg="Broj mobitela uspješno promijenjen.";
                    prefs.edit().putString("cell", mobiteltxt).commit();
                }else{
                    msg="Promijena broja mobitela nije uspijela! Pokušajte ponovno.";
                }
            }
            if(changeCase==3){
                if (responseBody.equals("true")){
                    msg="Adresa uspješno promijenjena.";
                    prefs.edit().putString("city", cId).commit();
                    prefs.edit().putString("cityName", cName).commit();
                    prefs.edit().putString("quart", qId).commit();
                    prefs.edit().putString("quartName", qName).commit();
                    prefs.edit().putString("home_add", addresstxt).commit();
                }else{
                    msg="Promijena adrese nije uspijela! Pokušajte ponovno.";
                }
            }
            final Dialog dialog = new Dialog(ProfilActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.orderdialog);

            TextView text = (TextView) dialog.findViewById(R.id.textDialog);
            text.setText(msg);
            ImageView okButton = (ImageView) dialog.findViewById(R.id.okButton);
            okButton.setImageResource(R.drawable.popup_green);

            dialog.show();

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(getIntent());
                    finish();
                }
            });
        }
    }
}