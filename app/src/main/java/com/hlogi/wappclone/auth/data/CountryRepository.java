package com.hlogi.wappclone.auth.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.data.model.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CountryRepository {

    private static volatile CountryRepository sInstance = null;
    private LiveData<List<Country>> countryList;

    public static CountryRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CountryRepository.class) {
                if (sInstance == null) {
                    sInstance = new CountryRepository(context);
                }
            }
        }

        return sInstance;
    }

    private CountryRepository(Context context) {
        this.countryList = loadCountries(context);
    }

    public LiveData<List<Country>> getCountryList() {
        return countryList;
    }

    private static LiveData<List<Country>> loadCountries(Context context) {

        LiveData<List<Country>> countryList = null;

        JSONArray countries = loadJsonArray(context);
        try {
            List<Country> list = new ArrayList<>();
            assert countries != null;
            for (int i = 0; i < countries.length(); i++) {
                JSONObject country = countries.getJSONObject(i);

                list.add(new Country(country.getString("name"), country.getString("code")));

            }

            countryList = new MutableLiveData<>(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countryList;
    }

    private static JSONArray loadJsonArray(@NonNull Context context) {
        StringBuilder builder = new StringBuilder();
        InputStream in = context.getResources().openRawResource(R.raw.countries);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject json = new JSONObject(builder.toString());
            return json.getJSONArray("countries");

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }

        return null;
    }

}
