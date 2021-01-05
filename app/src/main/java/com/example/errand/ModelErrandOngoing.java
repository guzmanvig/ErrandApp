package com.example.errand;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class ModelErrandOngoing {
    private String ongoingErrandId;
    private String volunteerId;
    private String store;
    private GeoPoint volunteerPosition;
    private String category;
    private String name;
    private String reward;
    private String date;

    public void setOngoingErrandId(String ongoingErrandId) {
        this.ongoingErrandId = ongoingErrandId;
    }

    @Override
    public String toString() {
        return "OngoingErrandModel{" +
                "ongoingErrandId='" + ongoingErrandId + '\'' +
                ", volunteerId='" + volunteerId + '\'' +
                ", store='" + store + '\'' +
                ", gp=" + volunteerPosition +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", reward=" + reward +
                ", date=" + date +
                '}';
    }

    public ModelErrandOngoing(String ongoingErrandId, String volunteerId, String store, GeoPoint voulnteerPosition, String category, String name, String reward, String date) {
        this.ongoingErrandId = ongoingErrandId;
        this.volunteerId = volunteerId;
        this.store = store;
        this.volunteerPosition = voulnteerPosition;
        this.category = category;
        this.name = name;
        this.reward = reward;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getReward() {
        return reward;
    }

    public String getDate() {
        return date;
    }

    public String getOngoingErrandId() {
        return ongoingErrandId;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public String getStore() {
        return store;
    }

    public GeoPoint getVolunteerPosition() {
        return volunteerPosition;
    }
}
