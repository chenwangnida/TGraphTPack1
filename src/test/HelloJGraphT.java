package test;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.alg.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.Graphs;

/**
 * A simple introduction to using JGraphT.
 *
 * @author Barak Naveh
 * @since Jul 27, 2003
 */
public final class HelloJGraphT {
	private HelloJGraphT() {
	} // ensure non-instantiability.

	/**
	 * The starting point for the demo.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		DirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();

		System.out.println("graph printing " + stringGraph.toString());
		Set<DefaultEdge> allEdgefromOnetoAntoher = stringGraph.getAllEdges("v1", "v2");
		Set<DefaultEdge> allEdge = stringGraph.edgeSet();
		Set<String> allVertice = stringGraph.vertexSet();

		List<String> dangleVerticeList = new ArrayList<String>();
		String dangleVertice;
		for (String v : allVertice) {
			System.out.println("vertice iterator:" + v.toString());
			int relatedOutDegree = stringGraph.outDegreeOf(v);
			System.out.println("degree is :" + relatedOutDegree);

			if (relatedOutDegree == 0 && !v.equals("v5")) {
				dangleVerticeList.add(v);
				System.out.println("dangleVertice:" + dangleVerticeList.get(0));
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
		DirectedGraph<String, DefaultEdge> ontologyDAG = createADG();

		CalculateSimilarityMeasure(ontologyDAG, "v4", "v7");
	}

	public static void removeAlltangle(DirectedGraph<String, DefaultEdge> stringGraph, List<String> dangleVerticeList) {
		// Iterator the endTangle
		for (String danglevertice : dangleVerticeList) {

			Set<DefaultEdge> relatedEdge = stringGraph.incomingEdgesOf(danglevertice);

			for (DefaultEdge edge : relatedEdge) {
				String potentialTangleVertice = stringGraph.getEdgeSource(edge);

				System.out.println("potentialTangleVertice:" + potentialTangleVertice);
			}

			Set<DefaultEdge> ralatedEdgeSave = new HashSet<DefaultEdge>();
			ralatedEdgeSave.addAll(relatedEdge);

			stringGraph.removeVertex(danglevertice);

			for (DefaultEdge edge : ralatedEdgeSave) {
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
		AllDirectedPaths<String, DefaultEdge> allPath = new AllDirectedPaths<String, DefaultEdge>(g);
		List<GraphPath<String, DefaultEdge>> pathList = allPath.getAllPaths("v1", "v5", true, null);

		List<DefaultEdge> edgeList;
		List<DefaultEdge> LongestEdgeList;
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

	public static void CalculateSimilarityMeasure(DirectedGraph<String, DefaultEdge> g, String a, String b) {

		// find the lowest common ancestor
		String lca = new NaiveLcaFinder<String, DefaultEdge>(g).findLca(a, b);

		//

		double N = new DijkstraShortestPath(g, "v1", lca).getPathLength();
		double N1 = new DijkstraShortestPath(g, "v1", a).getPathLength();
		double N2 = new DijkstraShortestPath(g, "v1", b).getPathLength();

		double sim = 2 * N / (N1 + N2);
		System.out.println("SemanticDistance:" + sim + " ##################");

		double L = new DijkstraShortestPath(g, a, b).getPathLength();

		int D = MaxDepth(g);

	}

	private static int MaxDepth(DirectedGraph<String, DefaultEdge> g) {

		Set<String> verticeset = g.vertexSet();

		for (String s : verticeset) {
			System.out.println("iterator vertice set in sequence of s:"+s);

//			Graphs.successorListOf(g, null);
		}
		return 0;

	}

	private static DirectedAcyclicGraph<String, DefaultEdge> createADG() {

		DirectedAcyclicGraph<String, DefaultEdge> g = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);

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

	private static DirectedGraph<String, DefaultEdge> createStringGraph() {
		DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

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
		g.addEdge(v1, v2);
		g.addEdge(v2, v3);
		g.addEdge(v2, v12);
		g.addEdge(v12, v13);
		g.addEdge(v13, v4);
		g.addEdge(v3, v6);
		g.addEdge(v6, v7);
		g.addEdge(v6, v8);
		g.addEdge(v7, v9);
		g.addEdge(v8, v9);
		g.addEdge(v3, v4);
		g.addEdge(v4, v5);
		g.addEdge(v3, v10);
		g.addEdge(v10, v11);

		return g;
	}
}

// End HelloJGraphT.java
