package com.example.lab5_starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityArrayAdapter extends ArrayAdapter<City> {
    private ArrayList<City> cities;
    private Context context;
    private OnCityDeleteListener deleteListener;

    public interface OnCityDeleteListener {
        void onDeleteCity(City city);
    }

    public CityArrayAdapter(Context context, ArrayList<City> cities){
        super(context, 0, cities);
        this.cities = cities;
        this.context = context;

        if (context instanceof OnCityDeleteListener) {
            deleteListener = (OnCityDeleteListener) context;
        }
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_city, parent, false);
        }

        City city = cities.get(position);
        TextView cityName = view.findViewById(R.id.textCityName);
        TextView provinceName = view.findViewById(R.id.textCityProvince);
        Button deleteButton = view.findViewById(R.id.buttonDelete);

        cityName.setText(city.getName());
        provinceName.setText(city.getProvince());

        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteCity(city);
            }
        });

        return view;
    }
}
