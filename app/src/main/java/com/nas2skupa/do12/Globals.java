package com.nas2skupa.do12;

import android.app.Application;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Globals extends Application {

    static LinkedHashMap<String, City> cities = new LinkedHashMap<String, City>();

    static ArrayList<String> getCities(int i) {
        ArrayList<String> ret = new ArrayList<String>();
        if (i!=1) {
            ret.add("Svi gradovi");
        }
        for (City city : cities.values())
            ret.add(city.name);
        return ret;
    }

    static ArrayList<String> getDistricts(String city, int i) {
        ArrayList<String> ret = new ArrayList<String>();
        if (i!=1) {
            ret.add("Svi kvartovi");
        }
        if (cities.containsKey(city))
            for (District district : cities.get(city).districts.values())
                ret.add(district.name);
        return ret;
    }

    private String[] catSettings = new String[2];

    public String[] getCatSettings(int CatId) {

        switch (CatId) {

            case 1:
                catSettings[0] = "zdravstvene usluge";
                catSettings[1] = "#7ec5c4";
                return catSettings;
            case 2:
                catSettings[0] = "servis i održavanje";
                catSettings[1] = "#8c7c66";
                return catSettings;
            case 3:
                catSettings[0] = "ljepota";
                catSettings[1] = "#a80364";
                return catSettings;
            case 4:
                catSettings[0] = "intelektualne usluge";
                catSettings[1] = "#49019a";
                return catSettings;
            case 5:
                catSettings[0] = "dom i obitelj";
                catSettings[1] = "#687f95";
                return catSettings;
            case 6:
                catSettings[0] = "sport i rekreacija";
                catSettings[1] = "#7dad91";
                return catSettings;
            case 7:
                catSettings[0] = "turizam i ugostiteljstvo";
                catSettings[1] = "#e2a217";
                return catSettings;
            case 8:
                catSettings[0] = "prijevoz";
                catSettings[1] = "#919294";
                return catSettings;
            case 9:
                catSettings[0] = "kultura i zabava";
                catSettings[1] = "#826882";
                return catSettings;


            default:
                return catSettings;
        }
    }

}
