package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener, CityArrayAdapter.OnCityDeleteListener {

    private Button addCityButton;
    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private ArrayAdapter<City> cityArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);

        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList);
        cityListView.setAdapter(cityArrayAdapter);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                    Log.e("Firestore", error.toString());
            }
            if (value != null) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");

                    cityArrayList.add(new City(name, province));
                }
                cityArrayAdapter.notifyDataSetChanged();
                }
        });


        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = cityArrayAdapter.getItem(i);
            CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(),"City Details");
        });

    }

    @Override
    public void updateCity(City city, String title, String year) {
        String oldCityName = city.getName();

        citiesRef.document(oldCityName).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Old document successfully deleted for update!");

                    city.setName(title);
                    city.setProvince(year);

                    citiesRef.document(city.getName()).set(city)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("Firestore", "New updated document successfully written!");
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error writing new document", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting old document", e);
                });
    }

    @Override
    public void addCity(City city){
        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error writing document", e);
                });

    }

    @Override
    public void onDeleteCity(City city) {
        if (city != null) {
            citiesRef.document(city.getName()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error deleting document", e);
                    });
        }
    }
}