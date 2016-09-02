package com.vikas.dev.sampleapp.models;

import io.realm.RealmObject;

/**
 * Created by vikasmalhotra on 9/1/16.
 */
public class Service extends RealmObject {

    private String serviceName;
    private String serviceId;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
