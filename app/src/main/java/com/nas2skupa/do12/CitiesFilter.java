package com.nas2skupa.do12;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Ranko on 21.11.2014..
 */
public class CitiesFilter {
    private ArrayList<String> cities = new ArrayList<String>();
    private ArrayList<String> districts = new ArrayList<String>();
    private ArrayAdapter<String> citiesAdapter;
    private ArrayAdapter<String> districtsAdapter;
    private Spinner citiesSpinner;
    private Spinner districtsSpinner;
    private SharedPreferences sharedPreferences;
    private String currentCity;
    private String currentDistrict;
    private int addAll=0;

    public CitiesFilter(Context context, Spinner citiesSpinner, final Spinner districtsSpinner, int iaddAll) {
        this.addAll=iaddAll;
        this.citiesSpinner = citiesSpinner;
        this.districtsSpinner = districtsSpinner;

        sharedPreferences = context.getSharedPreferences("filter", Context.MODE_PRIVATE);
        currentCity = sharedPreferences.getString("city", "");
        currentDistrict = sharedPreferences.getString("district", "");

        cities.addAll(Globals.getCities(addAll));
        if (cities.contains(currentCity))
            citiesSpinner.setSelection(cities.indexOf(currentCity));
        districts.addAll(Globals.getDistricts(currentCity,addAll));
        if (districts.contains(currentDistrict))
            districtsSpinner.setSelection(districts.indexOf(currentDistrict));

        citiesAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, cities);
        districtsAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, districts);
        citiesAdapter.setDropDownViewResource(R.layout.spiner_dropdown_item);
        districtsAdapter.setDropDownViewResource(R.layout.spiner_dropdown_item);

        this.citiesSpinner.setAdapter(citiesAdapter);
        this.districtsSpinner.setAdapter(districtsAdapter);

        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cities.get(position).equals(currentCity)) return;
                currentCity = cities.get(position);
                sharedPreferences.edit().putString("city", currentCity).commit();
                districts.clear();
                districts.addAll(Globals.getDistricts(currentCity,addAll));
                if (!districts.contains(currentDistrict))
                    currentDistrict = districts.get(0);
                districtsSpinner.setSelection(districts.indexOf(currentDistrict));
                if (onFilterChangedListener != null)
                    onFilterChangedListener.onFilterChanged(currentCity, currentDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (districts.get(position).equals(currentDistrict)) return;
                currentDistrict = districts.get(position);
                sharedPreferences.edit().putString("district", currentDistrict).commit();
                if (onFilterChangedListener != null)
                    onFilterChangedListener.onFilterChanged(currentCity, currentDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private OnFilterChangedListener onFilterChangedListener;

    public interface OnFilterChangedListener {
        public void onFilterChanged(String city, String district);
    }

    public void setOnFilterChangedListener(OnFilterChangedListener listener) {
        onFilterChangedListener = listener;
        if (onFilterChangedListener != null)
            onFilterChangedListener.onFilterChanged(currentCity, currentDistrict);
    }
}
