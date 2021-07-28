package com.huawei.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node
public class Service {

    @Id @GeneratedValue
    protected Long id;

    private String serviceId;

    private String name;

    @Relationship("CONNECTED_TO")
    private List<Service> serviceList;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public void addService(Service service) {
        if (serviceList == null) {
            serviceList = new ArrayList<>();
        }
        serviceList.add(service);
    }

    @Override
    public String toString() {
        return "{ Service: " + name + " }";
    }
}
