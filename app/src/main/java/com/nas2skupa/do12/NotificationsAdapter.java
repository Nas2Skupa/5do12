package com.nas2skupa.do12;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationsAdapter extends ArrayAdapter<Order>{

    Context context;
    int layoutResourceId;
    ArrayList<Order> data = null;

    public NotificationsAdapter(Context context, int layoutResourceId, ArrayList<Order> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NotificationsHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NotificationsHolder();
            holder.provider = (TextView)row.findViewById(R.id.provider_name);
            holder.service = (TextView)row.findViewById(R.id.service_name);
            holder.providerConfirm = (ImageView)row.findViewById(R.id.provider_confirm);
            holder.userConfirm = (ImageView)row.findViewById(R.id.user_confirm);
            row.setTag(holder);
        }
        else
        {
            holder = (NotificationsHolder)row.getTag();
        }

        Order order = data.get(position);
        holder.provider.setText(order.proName);
        holder.service.setText(order.serviceName);
        holder.providerConfirm.setImageResource(GetIcon(order.providerConfirm));
        holder.userConfirm.setImageResource(GetIcon(order.userConfirm));
        holder.orderObj = order;
        row.setTag(holder);
        return row;
    }

    public int GetIcon(String confirm) {
        if (confirm.equals("1")) return R.drawable.nar_potvrdi;
        else if (confirm.equals("2")) return R.drawable.nar_odbij;
        else return R.drawable.nar_ponudi;
    }

    public static class NotificationsHolder
    {
        TextView provider;
        TextView service;
        ImageView providerConfirm;
        ImageView userConfirm;
        Parcelable orderObj;
    }
}