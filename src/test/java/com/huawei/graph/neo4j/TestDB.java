package com.huawei.graph.neo4j;

import com.huawei.db.ServiceRepository;
import com.huawei.model.Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDB {

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    public void loadJSON() {

        serviceRepository.deleteAll();
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

            for (Service s : serviceList ) {
                serviceRepository.save(s);
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

//                Service toService = serviceMap.get(to);
//                Service service = serviceMap.get(from);

                Service fromService = serviceRepository.getByServiceId(from);
                Service toService = serviceRepository.getByServiceId(to);

                fromService.addService(toService);

                serviceRepository.save(fromService);

            }



//            List<Service>saveList;
//
//            while (!serviceList.isEmpty()) {
//                saveList = new ArrayList<>();
//
//                int j = 0;
//                while (!serviceList.isEmpty() && j < 10) {
//                    Service service = serviceList.get(0);
//                    saveList.add(service);
//                    serviceList.remove(0);
//                    j++;
//                }
//
//                serviceRepository.saveAll(serviceList);
//
//            }




        } catch (Exception e) {
            e.printStackTrace();
        }

        assert true;

    }










}
