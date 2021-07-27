package com.huawei.graph.solver;

import com.huawei.db.ServiceFileRepository;
import com.huawei.db.ServiceRepository;
import com.huawei.model.Service;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMiddle;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelectorWithTies;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSolver {

//    @Autowired
//    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceFileRepository serviceFileRepository;

    @Test
    public void example() {

        int n = 8;
        Model model = new Model(n + "-queens problem");
        IntVar[] vars = new IntVar[n];
        for (int q = 0; q < n; q++) {
            vars[q] = model.intVar("Q_" + q, 1, n);
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                model.arithm(vars[i], "!=", vars[j]).post();
                model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
                model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
            }
        }
        Solution solution = model.getSolver().findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }


    }

    @Test
    public void simpleTest() {

        int localInstallationCost = 2;
        int separationCost = 100;

        Model model = new Model("service placement");

        List<Service> services = serviceFileRepository.getServiceList();

//        List<Service> services = serviceRepository.findAll();

        Map<String, BoolVar> varMap = new HashMap<>();

        IntVar cost = model.intVar(0);
        BoolVar isLocal = null;
        for (Service s : services) {
            if (s.getName().equals("SDG")) {
                isLocal = model.boolVar(s.getName(), true);

            } else {
                isLocal = model.boolVar(s.getName());

            }

            varMap.put(s.getName(), isLocal);
            cost = cost.add(isLocal.mul(localInstallationCost)).intVar();

        }

        for (Service s: services ) {

            if (s.getServiceList() != null)
                for (Service target: s.getServiceList()) {
                    BoolVar sourceVar = varMap.get(s.getName());
                    BoolVar targetVar = varMap.get(target.getName());
                    BoolVar xor = sourceVar.xor(targetVar).boolVar();
                    cost = cost.add(xor.mul(separationCost).intVar()).intVar();

                }

        }

        boolean MAX = Model.MINIMIZE;

        model.setObjective(MAX, cost);

        Solver solver = model.getSolver();

        Solution optimalSolution = solver.findOptimalSolution(cost, MAX, null);
        System.out.println(optimalSolution);
        System.out.println(optimalSolution.getIntVal(cost));

        for (String key : varMap.keySet()) {
            System.out.println(key + ": " + optimalSolution.getIntVal(varMap.get(key)));
        }

    }


    private List<Service> getServices() {

        List<Service> serviceList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Service s = new Service("srv" + i);
            serviceList.add(s);
        }

        serviceList.get(0).addService(serviceList.get(1));
        serviceList.get(2).addService(serviceList.get(3));

        return serviceList;
    }

    @Test
    public void maxit() {

        Model model = new Model("service placement");

        IntVar cost = model.intVar(0, 100);

        BoolVar b1 = model.boolVar("b1", true);
        BoolVar b2 = model.boolVar("b2");

        cost = cost.add(b1.mul(10)).intVar();
        cost = cost.add(b2.mul(10)).intVar();

        cost = cost.add(b1.xor(b2).mul(100)).intVar();

        boolean MAX = Model.MINIMIZE;

        model.setObjective(MAX, cost);

        Solver solver = model.getSolver();

        Solution optimalSolution = solver.findOptimalSolution(cost, MAX, null);
        System.out.println(optimalSolution);
        System.out.println(optimalSolution.getIntVal(cost));


    }


    private void prettyPrint(Model model, IntVar[] open, int W, IntVar[] supplier, int S, IntVar tot_cost) {
        StringBuilder st = new StringBuilder();
        st.append("Solution #").append(model.getSolver().getSolutionCount()).append("\n");
        for (int i = 0; i < W; i++) {
            if (open[i].getValue() > 0) {
                st.append(String.format("\tWarehouse %d supplies customers : ", (i + 1)));
                for (int j = 0; j < S; j++) {
                    if (supplier[j].getValue() == (i + 1)) {
                        st.append(String.format("%d ", (j + 1)));
                    }
                }
                st.append("\n");
            }
        }
        st.append("\tTotal C: ").append(tot_cost.getValue());
        System.out.println(st.toString());
    }


    @Test
    public void loadJSON() {

        List<Service> serviceList = getServicesFromFile();

        assert !serviceList.isEmpty();

        System.out.println("done!");

    }


    private List<Service> getServicesFromFile() {

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


        return serviceList;


    }



}
