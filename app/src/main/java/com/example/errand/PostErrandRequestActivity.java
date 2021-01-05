package com.example.errand;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
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


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class PostErrandRequestActivity extends FragmentActivity implements AddItemDialogListener{

    TextView itemsAddedTextView;
    List<Item> itemsAdded = new ArrayList<>();
    String mErrandId = "";
    Database database = new Database();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_errand_request);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
        getDeviceLocation();


        if (getIntent().hasExtra("ErrandId")) {
            mErrandId = getIntent().getStringExtra("ErrandId");
        }

        itemsAddedTextView = findViewById(R.id.items_text_view);


        findViewById(R.id.add_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddItemDialog addItemDialog = new AddItemDialog(PostErrandRequestActivity.this);
                addItemDialog.show(fm, "map_dialog");

            }
        });

        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostErrandRequestActivity.this);
                builder.setMessage("Give a reward to the person who will do the errand. The person will see it and decide if he is willing to do it for you. This reward is in addition to the cost of your products");
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

        findViewById(R.id.post_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest();
                onBackPressed();
            }
        });
    }

    private void postRequest() {
        EditText nameEditText = findViewById(R.id.name_edit_text);
        String name = nameEditText.getText().toString();

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

        RadioButton vulnerableRadioButton = findViewById(R.id.vulnerable_radio_button);
        boolean vulnerable = vulnerableRadioButton.isChecked();

        EditText rewardEditText = findViewById(R.id.reward_edit_text);
        String reward = rewardEditText.getText().toString();

        String items = "";
        for (Item item : itemsAdded) {
            String separator = items.equals("") ? "" : " , ";
            items = items + separator + item.toString();
        }

        double random = 0.001 + Math.random() * (0.004);
        GeoPoint position = new GeoPoint(mLastKnownLocation.getLatitude() + random, mLastKnownLocation.getLongitude() + random);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String id = sharedPref.getString("AccountID", "");

        ModelErrandRequest errandRequest = new ModelErrandRequest(
                name,
                position,
                "PENDING",
                vulnerable,
                items,
                reward,
                mErrandId,
                categories,
                id); // PersonID o be filled here

        database.postNewRequest(errandRequest);

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
                            // Set the map's camera position to the current location of the device.
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

    @Override
    public void onItemAdded(Item item) {
        String itemsAddedString = itemsAddedTextView.getText().toString();
        if (itemsAdded.isEmpty()) {
            itemsAddedTextView.setText(item.toString());
        } else {
            itemsAddedTextView.setText(itemsAddedString + ", " + item.toString());
        }
        itemsAdded.add(item);
    }
}
