package com.huawei.db;

import com.huawei.model.Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Controller
public class ServiceFileRepository {

    private List<Service> serviceList;

    public List<Service> getServiceList() {
        return serviceList;
    }

    @PostConstruct
    private void getServicesFromFile() {

        List<Service> serviceList = new ArrayList<>();
        Map<String, Service> serviceMap = new HashMap<>();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("services.json");

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new InputStreamReader(inputStream));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray nodes = (JSONArray) jsonObject.get("nodes");

            Iterator<JSONObject> iterator = nodes.iterator();
            while (iterator.hasNext()) {

                JSONObject jo = iterator.next();

                Service service = new Service();
                service.setServiceId((String) jo.get("id"));
                service.setName((String) jo.get("title"));
                serviceList.add(service);

                serviceMap.put(service.getServiceId(), service);

            }

            JSONArray edges = (JSONArray) jsonObject.get("edges");

            int i = 0;
            iterator = edges.iterator();
            while (iterator.hasNext()) {

                JSONObject jo = iterator.next();

                String from = (String) jo.get("from");
                String to = (String) jo.get("to");

                System.out.println(i + ": connecting " + from + " to " + to);
                i++;

                Service fromService = serviceMap.get(from);
                Service toService = serviceMap.get(to);

                fromService.addService(toService);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        this.serviceList = serviceList;

    }

}
