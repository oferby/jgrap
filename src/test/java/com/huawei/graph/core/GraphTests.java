package com.huawei.graph.core;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GraphTests {

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

}
