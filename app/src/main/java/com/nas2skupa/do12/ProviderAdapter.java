package com.nas2skupa.do12;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;

public class ProviderAdapter extends ArrayAdapter<ProviderClass> implements Filterable {

    private final Comparator<ProviderClass> nameComparator = new Comparator<ProviderClass>() {
        public int compare(ProviderClass o1, ProviderClass o2) {
            return o1.proName.compareTo(o2.proName);
        }
    };
    private final Comparator<ProviderClass> dateComparator = new Comparator<ProviderClass>() {
        public int compare(ProviderClass o1, ProviderClass o2) {
            return Integer.valueOf(o2.proID) - Integer.valueOf(o1.proID);
        }
    };
    private final Comparator<ProviderClass> ratingComparator = new Comparator<ProviderClass>() {
        public int compare(ProviderClass o1, ProviderClass o2) {
            if (o2.rating > o1.rating) return 1;
            else if (o2.rating < o1.rating) return -1;
            else return 0;
        }
    };
    private final Comparator<ProviderClass> distanceComparator = new Comparator<ProviderClass>() {
        @Override
        public int compare(ProviderClass lhs, ProviderClass rhs) {
            final int distance = (int) (lastLocation.distanceTo(lhs.getLocation()) - lastLocation.distanceTo(rhs.getLocation()));
            return distance;
        }
    };
    private ArrayList<ProviderClass> filteredData;
    private Context context;
    private int layoutResourceId;
    private ArrayList<ProviderClass> data = null;
    private Location lastLocation;
    private boolean sortByDistance;

    public ArrayList<ProviderClass> getData() {
        return data;
    }

    public enum SortType {
        NONE,
        NAME,
        LATEST,
        RATING,
        DISTANCE
    }

    public ProviderAdapter(Context context, int layoutResourceId, ArrayList<ProviderClass> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.filteredData = data;

        lastLocation = SmartLocation.with(getContext())
                .location(new LocationGooglePlayServicesWithFallbackProvider(getContext()))
                .getLastLocation();

        SmartLocation.with(getContext())
                .location(new LocationGooglePlayServicesWithFallbackProvider(getContext()))
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lastLocation = location;
                        if (sortByDistance) sort(distanceComparator);
                        notifyDataSetChanged();
                    }
                });
    }

    public int getCount() {
        return filteredData.size();
    }

    public ProviderClass getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
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
            holder.rating = (RatingBar)row.findViewById(R.id.ratingBar2);
            holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);
            LayerDrawable stars = (LayerDrawable) holder.rating.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#ffadbb02"), PorterDuff.Mode.SRC_IN);
            row.setTag(holder);
        }
        else
        {
            holder = (ProviderHolder)row.getTag();
        }
        ProviderClass provider = filteredData.get(position);
        holder.txtTitle.setText(provider.proName);
        holder.favIcon.setImageResource(provider.favIcon);
        holder.akcijaIcon.setImageResource(provider.akcijaIcon);
        holder.rating.setRating(provider.rating);
        if (lastLocation == null || (provider.latitude == 0 && provider.longitude == 0)) {
            holder.txtDistance.setVisibility(View.GONE);
        } else {
            holder.txtDistance.setVisibility(View.VISIBLE);
            holder.txtDistance.setText(distanceToString(lastLocation.distanceTo(provider.getLocation())));
        }
        holder.proObj = provider;
        row.setTag(holder);
        return row;
    }

    public void sortBy(SortType type) {
        sortByDistance = false;
        switch (type) {
            case NONE:
                break;
            case NAME:
                sort(nameComparator);
                break;
            case LATEST:
                sort(dateComparator);
                break;
            case RATING:
                sort(ratingComparator);
                break;
            case DISTANCE:
                if (lastLocation == null) {
                    Toast.makeText(context, R.string.msg_location_unknown_long, Toast.LENGTH_LONG).show();
                    break;
                }
                sortByDistance = true;
                sort(distanceComparator);
                break;
        }
        notifyDataSetChanged();
    }

    public void filterDiscounts(boolean enabled) {
        getFilter().filter(enabled ? "yes" : "no");
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint.equals("yes")) {
                    ArrayList<ProviderClass> nlist = new ArrayList<ProviderClass>();
                    for (ProviderClass provider : data)
                        if (provider.akcijaIcon == R.drawable.akcija_icon)
                            nlist.add(provider);

                    results.count = nlist.size();
                    results.values = nlist;
                } else {
                    results.count = data.size();
                    results.values = data;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<ProviderClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private String distanceToString(double distance) {
        if (distance < 1000)
            return String.format("%.0f m", distance);
        else
            return String.format("%.0f km", distance / 1000);
    }

    public static class ProviderHolder
    {
        ImageView favIcon;
        ImageView akcijaIcon;
        TextView txtTitle;
        RatingBar rating;
        TextView txtDistance;
        Parcelable proObj;
    }
}