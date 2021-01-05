package com.example.errand;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class PostOngoingErrandActivity extends FragmentActivity {

    Database database = new Database();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ongoing_errand);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        getDeviceLocation();

        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostOngoingErrandActivity.this);
                builder.setMessage("Minimum reward you are asking for doing an errand. Other people will choose to give you this or more");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        findViewById(R.id.post_errand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postErrand();
                onBackPressed();
            }
        });
    }


    private void postErrand() {
        EditText nameEditText = findViewById(R.id.name_edit_text);
        String name = nameEditText.getText().toString();

        EditText storeEditText = findViewById(R.id.store_edit_text);
        String store = storeEditText.getText().toString();

        EditText dateEditText = findViewById(R.id.time_edit_text);
        String date = dateEditText.getText().toString();

        EditText rewardEditText = findViewById(R.id.reward_edit_text);
        String reward = rewardEditText.getText().toString();

        String categories = "";
        CheckBox groceriesCheckbox = findViewById(R.id.groceries_checkbox);
        if (groceriesCheckbox.isChecked()) {
            categories = categories + "Groceries";
        }
        CheckBox foodCheckbox = findViewById(R.id.food_checkbox);
        if (foodCheckbox.isChecked()) {
            String separator = categories.equals("") ? "" : " - ";
            categories = categories + separator +  "Food";
        }
        CheckBox medicineCheckbox = findViewById(R.id.medicine_checkbox);
        if (medicineCheckbox.isChecked()) {
            String separator = categories.equals("") ? "" : " - ";
            categories = categories + separator +  "Medicine";
        }
        CheckBox cleaningCheckbox = findViewById(R.id.cleaning_checkbox);
        if (cleaningCheckbox.isChecked()) {
            String separator = categories.equals("") ? "" : " - ";
            categories = categories + separator +  "Cleaning";
        }
        CheckBox otherCheckbox = findViewById(R.id.other_checkbox);
        if (otherCheckbox.isChecked()) {
            String separator = categories.equals("") ? "" : " - ";
            categories = categories + separator +  "Other";
        }

        double random = 0.001 + Math.random() * (0.004);
        GeoPoint position = new GeoPoint(mLastKnownLocation.getLatitude() + random, mLastKnownLocation.getLongitude() + random);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = sharedPref.getString("AccountID", "");

        ModelErrandOngoing errandOngoing = new ModelErrandOngoing(
                "",
                id,
                store,
                position,
                categories,
                name,
                reward,
                date
        );

        database.postOngoingErrands(errandOngoing);

    }

    private void checkLocationPermission() {

        mLocationPermissionGranted = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                        } else {
                            Log.d("ERRAND", "Current location is null. Using defaults.");
                            Log.e("ERRAND", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
