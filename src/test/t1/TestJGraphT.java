package test.t1;
/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

/* -----------------
 * HelloJGraphT.java
 * -----------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 27-Jul-2003 : Initial revision (BN);
 *
 */

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

/**
 * A complex introduction to using JGraphT.
 *
 * @author Chen Wang
 * @since Sep 3, 2016
 */
public final class TestJGraphT {
	private TestJGraphT() {
	} // ensure non-instantiability.

	/**
	 * The starting point for the demo.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		DirectedGraph<String, ServiceEdge> stringGraph = createStringGraph();

		System.out.println("graph printing " + stringGraph.toString());

		Set<String> allVertice = stringGraph.vertexSet();

		List<String> dangleVerticeList = new ArrayList<String>();
		for (String v : allVertice) {
			int relatedOutDegree = stringGraph.outDegreeOf(v);
			System.out.println("degree is :" + relatedOutDegree);

			if (relatedOutDegree == 0 && !v.equals("v5")) {
				dangleVerticeList.add(v);
			}
		}

		// recursion for find end tangle, remove them and update graph
		removeAlltangle(stringGraph, dangleVerticeList);

		System.out.println(stringGraph.toString());

		// get edge list of the LogestPaths
		List<String> vertexList = getLongestPathVertexList(stringGraph);
		for (String s : vertexList) {
			System.out.println(s);

		}

		// calculate Semantic distance
		DirectedGraph<String, ServiceEdge> ontologyDAG = createADG();

		Double semanticDistance = CalculateSimilarityMeasure(ontologyDAG, "v7", "v8");
		System.out.println("############semantic distanceValue:" + semanticDistance);
	}

	public static void removeAlltangle(DirectedGraph<String, ServiceEdge> stringGraph, List<String> dangleVerticeList) {
		// Iterator the endTangle
		for (String danglevertice : dangleVerticeList) {

			Set<ServiceEdge> relatedEdge = stringGraph.incomingEdgesOf(danglevertice);

			for (ServiceEdge edge : relatedEdge) {
				String potentialTangleVertice = stringGraph.getEdgeSource(edge);

				System.out.println("potentialTangleVertice:" + potentialTangleVertice);
			}

			Set<ServiceEdge> ralatedEdgeSave = new HashSet<ServiceEdge>();
			ralatedEdgeSave.addAll(relatedEdge);

			stringGraph.removeVertex(danglevertice);

			for (ServiceEdge edge : ralatedEdgeSave) {
				String potentialTangleVertice = stringGraph.getEdgeSource(edge);
				int relatedOutDegree = stringGraph.outDegreeOf(potentialTangleVertice);
				List<String> dangleVerticeList1 = new ArrayList<String>();
				if (relatedOutDegree == 0) {
					dangleVerticeList1.add(potentialTangleVertice);
					removeAlltangle(stringGraph, dangleVerticeList1);
				} else {
					return;
				}

			}

		}
	}

	public static List<String> getLongestPathVertexList(DirectedGraph g) {
		// A algorithm to find all paths
		AllDirectedPaths<String, ServiceEdge> allPath = new AllDirectedPaths<String, ServiceEdge>(g);
		List<GraphPath<String, ServiceEdge>> pathList = allPath.getAllPaths("v1", "v5", true, null);

		List<ServiceEdge> edgeList;
		List<ServiceEdge> LongestEdgeList;
		int MaxPathLength = 0;
		int IndexPathLength = 0;

		for (int i = 0; i < pathList.size(); i++) {

			int pathLength = pathList.get(i).getEdgeList().size();
			if (pathLength > MaxPathLength) {
				IndexPathLength = i;
				MaxPathLength = pathLength;
			}
		}
		System.out.println(MaxPathLength + "index:" + IndexPathLength);
		// return pathList.get(IndexPathLength).getEdgeList();
		return Graphs.getPathVertexList(pathList.get(IndexPathLength));
	}

	public static double CalculateSimilarityMeasure(DirectedGraph<String, ServiceEdge> g, String a, String b) {

		double similarityValue;
		// find the lowest common ancestor
		String lca = new NaiveLcaFinder<String, ServiceEdge>(g).findLca(a, b);

		//

		double N = new DijkstraShortestPath(g, "v1", lca).getPathLength() + 1;
		double N1 = new DijkstraShortestPath(g, "v1", a).getPathLength() + 1;
		double N2 = new DijkstraShortestPath(g, "v1", b).getPathLength() + 1;

		double sim = 2 * N / (N1 + N2);
		System.out.println("SemanticDistance:" + sim + " ##################");

		double L = new DijkstraShortestPath(g, lca, a).getPathLength()
				+ new DijkstraShortestPath(g, lca, b).getPathLength();

		int D = MaxDepth(g) + 1;
		int r = 1;
		double simNew = 2 * N * (Math.pow(Math.E, -r * L / D)) / (N1 + N2);
		System.out.println("SemanticDistance2:" + simNew + " ##################");

		if (isNeighbourConcept(g, a, b) == true) {
			similarityValue = simNew;
		} else {
			similarityValue = sim;
		}

		return similarityValue;
	}

	private static boolean isNeighbourConcept(DirectedGraph<String, ServiceEdge> g, String a, String b) {

		boolean isNeighbourConcept = false;
		Set<ServiceEdge> incomingEdgeList1 = g.incomingEdgesOf(a);
		Set<ServiceEdge> incomingEdgeList2 = g.incomingEdgesOf(b);

		for (ServiceEdge e1 : incomingEdgeList1) {
			String source1 = g.getEdgeSource(e1);
			for (ServiceEdge e2 : incomingEdgeList2) {
				String source2 = g.getEdgeSource(e2);
				if (source1.equals(source2)) {
					isNeighbourConcept = true;
				}
			}
		}

		return isNeighbourConcept;
	}

	private static int MaxDepth(DirectedGraph<String, ServiceEdge> g) {

		int depth = 0;

		Set<String> verticeset = g.vertexSet();

		// update the depth while iterator successor
		for (String v : verticeset) {
			List<String> verticeList = Graphs.successorListOf(g, v);

			if (verticeList.size() > 0) {
				depth++;
			}
		}

		System.out.println("the depth of DAG:" + depth);

		return depth;

	}

	private static DirectedAcyclicGraph<String, ServiceEdge> createADG() {

		DirectedAcyclicGraph<String, ServiceEdge> g = new DirectedAcyclicGraph<String, ServiceEdge>(ServiceEdge.class);

		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		String v5 = "v5";
		String v6 = "v6";
		String v7 = "v7";
		String v8 = "v8";

		// add the vertices
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		g.addVertex(v8);

		// add edges to create a circuit
		g.addEdge(v1, v2);
		g.addEdge(v1, v3);
		g.addEdge(v3, v4);
		g.addEdge(v3, v5);
		g.addEdge(v3, v6);
		g.addEdge(v6, v7);
		g.addEdge(v6, v8);

		return g;

	}

	private static DirectedGraph<String, ServiceEdge> createStringGraph() {
		DirectedGraph<String, ServiceEdge> g = new DefaultDirectedGraph<String, ServiceEdge>(ServiceEdge.class);

		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		String v5 = "v5";
		String v6 = "v6";
		String v7 = "v7";
		String v8 = "v8";
		String v9 = "v9";
		String v10 = "v10";
		String v11 = "v11";
		String v12 = "v12";
		String v13 = "v13";

		// add the vertices
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		g.addVertex(v8);
		g.addVertex(v9);
		g.addVertex(v10);
		g.addVertex(v11);
		g.addVertex(v12);
		g.addVertex(v13);

		// add edges to create a circuit
		g.addEdge(v1, v2, new ServiceEdge(0.00, 0.00));
		g.addEdge(v2, v3, new ServiceEdge(0.00, 0.00));
		g.addEdge(v2, v12, new ServiceEdge(0.00, 0.00));
		g.addEdge(v12, v13, new ServiceEdge(0.00, 0.00));
		g.addEdge(v13, v4, new ServiceEdge(0.00, 0.00));
		g.addEdge(v3, v6, new ServiceEdge(0.00, 0.00));
		g.addEdge(v6, v7, new ServiceEdge(0.00, 0.00));
		g.addEdge(v6, v8, new ServiceEdge(0.00, 0.00));
		g.addEdge(v7, v9, new ServiceEdge(0.00, 0.00));
		g.addEdge(v8, v9, new ServiceEdge(0.00, 0.00));
		g.addEdge(v3, v4, new ServiceEdge(0.00, 0.00));
		g.addEdge(v4, v5, new ServiceEdge(0.00, 0.00));
		g.addEdge(v3, v10, new ServiceEdge(0.00, 0.00));
		g.addEdge(v10, v11, new ServiceEdge(0.00, 0.00));

		return g;
	}
}

// End HelloJGraphT.java
