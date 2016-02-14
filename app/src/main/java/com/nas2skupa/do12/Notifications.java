package com.nas2skupa.do12;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Notifications extends BaseActivity {

    private ListView listView1;
    ArrayList<Order> listArray = new ArrayList<Order>();
    View header = null;
    //    View filter = null;
    private Context context;
    private NotificationsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        context = this;
        header = getLayoutInflater().inflate(R.layout.listview_header_row, null);
//        filter = getLayoutInflater().inflate(R.layout.listview_filter_row, null);
        adapter = new NotificationsAdapter(this, R.layout.listview_notification_row, listArray);
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
                showOrderDialog(holder.orderObj);
            }
        });

        getMessages();
    }

    private void getMessages() {
        final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        String userId = prefs.getString("id", "");
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getNotifications.aspx")
                .appendQueryParameter("userId", userId)
                .build();
        new HttpRequest(context, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        if (result != null) parseServerResult(result);
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

    private void sendConfirmation(String orderId, String confirmed) {
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/confirmUser.aspx")
                .appendQueryParameter("orderId", orderId)
                .appendQueryParameter("confirmed", confirmed)
                .build();
        new HttpRequest(context, uri, true)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        if (result != null) {
                            Toast.makeText(context, R.string.confirmationSent, Toast.LENGTH_LONG);
                            getMessages();
                        }
                    }
                });
    }

    public void showOrderDialog(final Order order) {
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String description = String.format("%s\n%s - %s\n%s (%s kn)",
                order.proName,
                tf.format(order.startTime),
                tf.format(order.endTime),
                order.serviceName,
                order.servicePrice);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String title = new SimpleDateFormat("d.M.yyyy.").format(order.date);
        builder.setMessage(description);
        if (order.providerConfirm.equals("1") && order.userConfirm.equals("0")) {
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sendConfirmation(order.id, "1");
                }
            });
        }
        if (!order.providerConfirm.equals("2") && !order.userConfirm.equals("2")) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sendConfirmation(order.id, "2");
                }
            });
        } else title += " - " + getString(R.string.canceled).toUpperCase();
        builder.setNeutralButton(R.string.close, null);
        builder.setTitle(title);
        builder.show();
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