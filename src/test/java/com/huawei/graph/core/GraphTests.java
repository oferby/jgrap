package com.huawei.graph.core;

import com.huawei.db.ServiceFileRepository;
import com.huawei.model.Service;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
class GraphTests {

	Logger logger = LoggerFactory.getLogger(GraphTests.class);

	@Autowired
	private ServiceFileRepository serviceFileRepository;

	FileWriter myWriter;

	@Test
	void testGraph() {

		Graph<String, DefaultEdge> g = new DirectedMultigraph<>(DefaultEdge.class);

		String n1 = "Node1";
		String n2 = "Node2";
		String n3 = "Node3";
		String n4 = "Node4";



		g.addVertex(n1);
		g.addVertex(n2);
		g.addVertex(n3);
		g.addVertex(n4);

		g.addEdge(n1, n2);
		g.addEdge(n2, n3);
		g.addEdge(n3, n4);
		g.addEdge(n4, n2);


	}


	@Test
	public void findCircles() throws IOException {

		myWriter = new FileWriter("/tmp/log.txt");


		List<Service> serviceList = serviceFileRepository.getServiceList();

		Set<Service> rootServices = new HashSet<>(serviceList);

//		find client-only services to start the search with
		for (Service s: serviceList) {

			if (s.getServiceList() != null) {

				for (Service target: s.getServiceList() ) {
					rootServices.remove(target);
				}

			}

		}


		Set<Service>visited = new HashSet<>();
		Set<Service> hasCircle = new HashSet<>();

		for (Service s: rootServices) {

			if (visited.contains(s)){
				continue;
			}

			scan(s, visited, new ArrayList<>(), new HashSet<>(), hasCircle);

		}

		myWriter.close();

	}


	private void scan(Service service, Set<Service>visited, List<Service> stack, Set<Service>stackVisited, Set<Service> hasCircle) throws IOException {

		stack.add(service);

		if (hasCircle.contains(service)) {
			System.out.println("STOP: " + service + " already in circle");
			myWriter.write("STOP: " + service + " already in circle\n");
			myWriter.write("STOP STACK: " + stack + "\n\n");
			int last = stack.size();
			stack.remove(last-1);
			return;
		}


		if (stackVisited.contains(service)) {

			hasCircle.add(service);

			List<Service>circle = new ArrayList<>();
			int i = 0;
			while (!stack.get(i).equals(service) ){
				i++;
			}

			for (int j = i; j < stack.size(); j++) {
				circle.add(stack.get(j));
			}

			System.out.println("CIRCLE: " + circle);
			myWriter.write("CIRCLE: " + circle+ "\n\n");
			int last = stack.size();
			stack.remove(last-1);
			return;
		}

		stackVisited.add(service);
		visited.add(service);


		if (service.getServiceList() == null || service.getServiceList().size() == 0) {
//			logger.debug("PATH: " + stack);
			System.out.println("PATH: " + stack);
			myWriter.write("PATH: " + stack + "\n\n");

			int last = stack.size();
			stack.remove(last-1);
			return;
		}



		for (Service s: service.getServiceList()) {

			scan(s, visited, stack, stackVisited, hasCircle);

		}

		int last = stack.size();
		stack.remove(last-1);
		stackVisited.remove(service);

	}

}
