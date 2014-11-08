package com.nas2skupa.do12;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProviderAdapter extends ArrayAdapter<Provider>{

    Context context;
    int layoutResourceId;   
    ArrayList<Provider> data = null;
   
    public ProviderAdapter(Context context, int layoutResourceId,  ArrayList<Provider> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProviderHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new ProviderHolder();
            holder.favIcon = (ImageView)row.findViewById(R.id.favIcon);
            holder.akcijaIcon = (ImageView)row.findViewById(R.id.akcijaIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.providerID = (TextView)row.findViewById(R.id.providerID);
            holder.rating = (RatingBar)row.findViewById(R.id.ratingBar2);
            LayerDrawable stars = (LayerDrawable) holder.rating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
            row.setTag(holder);
        }
        else
        {
            holder = (ProviderHolder)row.getTag();
        }
       
        Provider provider = data.get(position);
        holder.providerID.setText(provider.proID);
        holder.txtTitle.setText(provider.title);
        holder.favIcon.setImageResource(provider.favIcon);
        holder.akcijaIcon.setImageResource(provider.akcijaIcon);
        holder.rating.setRating(provider.rating);
       
        return row;
    }
   
    static class ProviderHolder
    {
        ImageView favIcon;
        ImageView akcijaIcon;
        TextView txtTitle;
        TextView providerID;
        RatingBar rating;
    }
}