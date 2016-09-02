package com.vikas.dev.sampleapp.models;

import io.realm.RealmObject;

/**
 * Created by vikasmalhotra on 9/1/16.
 */
public class UserServices extends RealmObject {

    private String userId;
    private String serviceId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
