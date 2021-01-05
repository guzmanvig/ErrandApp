package com.example.errand;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void retreiveOngoingErrands(final DatabaseListener listener) {
        final List<ModelErrandOngoing> oeArray = new ArrayList<>();
        database.collection("ongoing_errands")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String ongoingErrandId =  document.getId();
                                String volunteerId =  document.getString("volunteer_id");
                                String store =  document.getString("store");
                                GeoPoint gp = document.getGeoPoint("start_gps_position");
                                String category = document.getString("category");
                                String name = document.getString("name");
                                String reward = document.getString("MinimumReward");
                                String date = document.getString("Date");

                                ModelErrandOngoing oe = new ModelErrandOngoing(ongoingErrandId,volunteerId,store,gp,category,name,reward,date);
                                oeArray.add(oe);
                            }
                            listener.onOngoingErrandsFetchComplete(oeArray);

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void retreiveOngoingRequests(final DatabaseListener databaseListener) {
        final List<ModelErrandRequest> prArray = new ArrayList<>();
        database.collection("posted_errands")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String ongoingErrandId =  document.getString("ongoing_errand_id");
                                String requesterName =  document.getString("requester_name");
                                GeoPoint requesterPosition = document.getGeoPoint("requester_position");
                                String reward = document.getString("reward");
                                String acceptedStatus = document.getString("accepted_status");
                                String items = document.getString("items");
                                boolean requesterIsVulnerable = document.getBoolean("request_is_vulnerable");
                                String categories = document.getString("categories");
                                String personId = document.getString("person_id");

                                ModelErrandRequest pr = new ModelErrandRequest(requesterName,requesterPosition,acceptedStatus,requesterIsVulnerable, items, reward,ongoingErrandId,categories, personId);
                                prArray.add(pr);
                            }
                            databaseListener.onOngoingRequestsFetchComplete(prArray);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void postOngoingErrands(final ModelErrandOngoing oe) {
        Map<String, Object> data = new HashMap<>();
        data.put("volunteer_id", oe.getVolunteerId());
        data.put("store", oe.getStore());
        data.put("start_gps_position", oe.getVolunteerPosition());
        data.put("sys_creation_date", Timestamp.now());
        data.put("sys_update_date", null);
        data.put("category",oe.getCategory());
        data.put("MinimumReward",oe.getReward());
        data.put("Date",oe.getDate());
        data.put("name",oe.getName());

        database.collection("ongoing_errands")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        oe.setOngoingErrandId(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }


    public void postNewRequest(final ModelErrandRequest er) {
        Map<String, Object> data = new HashMap<>();
        data.put("ongoing_errand_id", er.getOngoingErrandId());
        data.put("requester_name", er.getRequesterName());
        data.put("requester_position", er.getRequesterPosition());
        data.put("reward", er.getReward());
        data.put("accepted_status", er.getAcceptedStatus());
        data.put("items", er.getItems());
        data.put("request_is_vulnerable", er.isRequesterIsVulnerable());
        data.put("categories",er.getCategories());
        data.put("person_id",er.getPersonId());

        database.collection("posted_errands")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

}
