package com.huawei.graph.core;

import com.huawei.db.ServiceFileRepository;
import com.huawei.model.Service;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
class GraphTests {

	@Autowired
	private ServiceFileRepository serviceFileRepository;

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
	public void findCircles() {

		List<Service> serviceList = serviceFileRepository.getServiceList();

		Set<Service>visited = new HashSet<>();
		List<Service> stack = new ArrayList<>();

		for (Service s: serviceList) {

			if (visited.contains(s)){
				continue;
			}

			scan(s, visited, stack, new HashSet<>());

		}



	}


	private void scan(Service service, Set<Service>visited, List<Service> stack, Set<Service>stackVisited) {

		visited.add(service);
		stack.add(service);

		if (service.getServiceList() == null || service.getServiceList().size() == 0) {
			System.out.println(stack);
			return;
		}


		for (Service s: service.getServiceList()) {

			if (stackVisited.contains(s)){
				stack.add(s);
				System.out.println(stack);
				int last = stack.size();
				stack.remove(last-1);
				continue;
			}

			stackVisited.add(s);

			scan(s, visited, stack, stackVisited);

		}

		int last = stack.size();
		stack.remove(last-1);
		stackVisited.remove(service);


	}

}
