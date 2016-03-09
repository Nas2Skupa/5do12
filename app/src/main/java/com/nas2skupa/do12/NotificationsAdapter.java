package com.nas2skupa.do12;

import android.app.Activity;
import android.content.Context;
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
            holder.statusText = (TextView)row.findViewById(R.id.status_text);
            holder.statusIcon = (ImageView)row.findViewById(R.id.status_icon);
            row.setTag(holder);
        }
        else
        {
            holder = (NotificationsHolder)row.getTag();
        }

        Order order = data.get(position);
        holder.provider.setText(order.proName);
        holder.service.setText(order.serviceName);

        if (order.userConfirm.equals("1") && order.providerConfirm.equals("1")) {
            holder.statusText.setText(R.string.confirmed);
            holder.statusIcon.setImageResource(R.drawable.ic_done_black_36px);
        }
        else if (order.userConfirm.equals("2") || order.providerConfirm.equals("2")) {
            holder.statusText.setText(R.string.canceled);
            holder.statusIcon.setImageResource(R.drawable.ic_clear_black_36px);
        }
        else {
            if (order.providerConfirm.equals("0"))
                holder.statusText.setText(R.string.waiting_confirmation_provider);
            else
                holder.statusText.setText(R.string.waiting_confirmation_user);

            holder.statusIcon.setImageResource(R.drawable.ic_alarm_black_36px);
        }
        holder.orderObj = order;
        row.setTag(holder);
        return row;
    }

    public static class NotificationsHolder
    {
        TextView provider;
        TextView service;
        TextView statusText;
        ImageView statusIcon;
        Order orderObj;
    }
}