package com.nas2skupa.do12;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ranko on 2.11.2014..
 */
public class Order implements Parcelable {
    public String id;
    public String proId;
    public String proName;
    public String userId;
    public String serviceId;
    public String serviceName;
    public String servicePrice;
    public Date date;
    public Date startTime;
    public Date endTime;
    public String userConfirm;
    public String providerConfirm;
    public String userNote;
    public String providerNote;

    public Order(JSONObject jsonObj) {
        DateFormat df = new SimpleDateFormat("M/d/yyyy hh:mm:ss aa");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");

        try {
            this.id = jsonObj.optString("id");
            this.proId = jsonObj.optString("proId");
            this.proName = jsonObj.optString("proName");
            this.userId = jsonObj.optString("userId");
            this.serviceId = jsonObj.optString("serviceId");
            this.serviceName = jsonObj.optString("serviceName");
            this.servicePrice = jsonObj.optString("servicePrice");
            this.date = df.parse(jsonObj.optString("date"));
            this.startTime = tf.parse(jsonObj.optString("startTime"));
            this.endTime = tf.parse(jsonObj.optString("endTime"));
            this.userConfirm = jsonObj.optString("userConfirm");
            this.providerConfirm = jsonObj.optString("providerConfirm");
            this.userNote = jsonObj.optString("userNote");
            this.providerNote = jsonObj.optString("providerNote");
        } catch (ParseException e) {
            Log.e("Order", "Invalid JSON data object");
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(proName);
        parcel.writeString(serviceName);
        parcel.writeString(providerConfirm);
        parcel.writeString(userConfirm);
    }
}
