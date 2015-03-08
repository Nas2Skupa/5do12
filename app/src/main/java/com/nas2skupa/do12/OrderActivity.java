package com.nas2skupa.do12;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.SharedPreferences;

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

public class OrderActivity extends BaseActivity{

    // Widget GUI
    Button btnCalendar, btnTimePicker;
    ImageView btnNaruci;

    EditText  txtNote;
    TextView txtDate, txtTime, txtService,name_label, txtPrice, txtTermin;

    // Variable for storing current date and time
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String serviceID,providerID,userId, provider, service, termin, color, responseBody;
    private ProgressDialog pDialog;
    Boolean orderok=false;
    ProviderClass proClass;
    PricelistClass priceClass;
    TimePickerDialog tpd;
    int[] payOpts;
    SimpleDateFormat outputDateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        proClass = bundle.getParcelable("providerclass");
        priceClass = bundle.getParcelable("pricelistclass");
        color = bundle.getString("color");
        payOpts=bundle.getIntArray("paying");
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarOrder);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
        try {
            float rating = proClass.rating;
            ratingBar.setRating(rating);
        } catch (NumberFormatException e) {
        }

        serviceID = priceClass.plID;
        providerID = proClass.proID;
        provider = proClass.proName;
        service = priceClass.plName;
        termin = priceClass.plTermin;
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        userId = prefs.getString("id", "");

        try {
            txtService = (TextView) findViewById(R.id.usluga);
            txtService.setText(service);
            name_label=(TextView) findViewById(R.id.name_label);
            name_label.setOnClickListener(null);
            name_label.setText(provider);
            txtTermin = (TextView) findViewById(R.id.txtTermin);
            txtTermin.setText(termin+"'");

        }catch (Exception e){
        }
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtPrice.setText(priceClass.plPrice);
        for(int i=0;i<payOpts.length;i++){
            ImageView payO;
            if(payOpts[i]==2){
                payO = (ImageView) findViewById(R.id.visa);
                payO.setVisibility(View.VISIBLE);
            }
            if(payOpts[i]==3){
                payO = (ImageView) findViewById(R.id.american);
                payO.setVisibility(View.VISIBLE);
            }
            if(payOpts[i]==4){
                payO = (ImageView) findViewById(R.id.master);
                payO.setVisibility(View.VISIBLE);
            }
            if(payOpts[i]==5){
                payO = (ImageView) findViewById(R.id.diners);
                payO.setVisibility(View.VISIBLE);
            }
        }
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date nowDate=Calendar.getInstance().getTime();
        Log.d("DATUM:", DateFormat.format("dd-MM-yyyy", nowDate).toString());
        txtDate.setText(DateFormat.format("dd-MM-yyyy", nowDate).toString());
        txtTime.setText(String.format("%02d", today.hour)+":"+String.format("%02d", today.minute));
        txtDate.setOnClickListener(clickHandler);
        txtTime.setOnClickListener(clickHandler);
        btnNaruci.setOnClickListener(clickHandler);


    }

    View.OnClickListener clickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (v == txtDate) {

                // Process to get Current Date
                final Calendar c = Calendar.getInstance(new Locale("hr", "HR"));
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener dsl=new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        Calendar calendar = new GregorianCalendar();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.YEAR, year);
                        Date selDate=calendar.getTime();
                        txtDate.setText(DateFormat.format("dd-MM-yyyy", selDate).toString());

                    }
                };
                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(OrderActivity.this,dsl
                        , mYear, mMonth, mDay);
                dpd.setButton(DialogInterface.BUTTON_POSITIVE, "Odaberi",dpd);
                dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Odustani",dpd);
                dpd.show();
            }
            if (v == txtTime) {

                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog.OnTimeSetListener tsl=new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtTime.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    }
                };
                tpd = new TimePickerDialog(OrderActivity.this,tsl, mHour, mMinute, true);
                tpd.setButton(DialogInterface.BUTTON_POSITIVE, "Odaberi",tpd);
                tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Odustani",tpd);

                tpd.show();
            }
            if (v == btnNaruci){
                new setOrder().execute();
            }else{

            }
        }
    };
    private void initialize() {
        setContentView(R.layout.order);
        txtNote = (EditText) findViewById(R.id.txtNote);
        btnNaruci = (ImageView) findViewById(R.id.btnNaruci);
        txtNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnNaruci.performClick();
                }
                return false;
            }
        });
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
    private class setOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OrderActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Void... arg0) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://nas2skupa.com/5do12/clientOrder.aspx");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("uid", userId));
                nameValuePairs.add(new BasicNameValuePair("proid", providerID));
                nameValuePairs.add(new BasicNameValuePair("datum", txtDate.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("vrijeme", txtTime.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("serviceid", serviceID));
                nameValuePairs.add(new BasicNameValuePair("userNote", txtNote.getText().toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                     responseBody = EntityUtils.toString(response.getEntity());
                }else{
                    responseBody="no response";
                }
                Log.d("uid",userId);
                Log.d("proId",providerID);
                Log.d("DATE",txtDate.getText().toString());
                Log.d("TIME",txtTime.getText().toString());
                Log.d("sID",serviceID);
                Log.d("NOTE",txtNote.getText().toString());
                Log.d("RESPONSE",responseBody);


            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Create custom dialog object
            final Dialog dialog = new Dialog(OrderActivity.this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.orderdialog);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            // set values for custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.textDialog);
            String msg;
            msg="Ups, nešto nije prošlo ok. Probajte ponovno!";
            orderok=isNumeric(responseBody);
            if(orderok)
                msg="Upit je poslan!";
            text.setText(msg);
            ImageView okButton = (ImageView) dialog.findViewById(R.id.okButton);
            okButton.setImageResource(R.drawable.popup_green);

            dialog.show();

            // if decline button is clicked, close the custom dialog
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    if(orderok){
                        Bundle b = new Bundle();
                        b.putParcelable("providerclass", proClass);
                        b.putString("color", color);
                        Intent in = new Intent(getApplicationContext(),
                                SingleProvider.class);
                        in.putExtras(b);
                        startActivity(in);
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    
    
}