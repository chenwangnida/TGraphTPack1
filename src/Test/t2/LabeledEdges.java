package Test.t2;

import java.util.ArrayList;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class LabeledEdges {
    private static final String friend = "friend";
    private static final String enemy = "enemy";

    public static void main(String[] args) {
        DirectedGraph<String, RelationshipEdge> graph =
            new DefaultDirectedGraph<String, RelationshipEdge>(
                    new ClassBasedEdgeFactory<String, RelationshipEdge>(RelationshipEdge.class));

        ArrayList<String> people = new ArrayList<String>();
        people.add("John");
        people.add("James");
        people.add("Sarah");
        people.add("Jessica");

        // John is everyone's friend
        for (String person : people) {
            graph.addVertex(person);
            graph.addEdge(people.get(0), person, new RelationshipEdge<String>(people.get(0), person, friend));
        }

        // Apparently James doesn't really like John
        graph.addEdge("James", "John", new RelationshipEdge<String>("James", "John", enemy));

        // Jessica is Sarah and James's friend
        graph.addEdge("Jessica", "Sarah", new RelationshipEdge<String>("Jessica", "Sarah", friend));
        graph.addEdge("Jessica", "James", new RelationshipEdge<String>("Jessica", "James", friend));

        // But Sarah doesn't really like James
        graph.addEdge("Sarah", "James", new RelationshipEdge<String>("Sarah", "James", enemy));

         for (RelationshipEdge edge : graph.edgeSet()) {
        if (edge.toString().equals("enemy")) {
            System.out.printf(edge.getV1()+"is an enemy of "+ edge.getV2()+"\n");
        } else if (edge.toString().equals("friend")) {
            System.out.printf( edge.getV1()+" is a friend of "+ edge.getV2()+"\n");
        }
    }
    }

    public static class RelationshipEdge<V> extends DefaultEdge {
        private V v1;
        private V v2;
        private String label;

        public RelationshipEdge(V v1, V v2, String label) {
            this.v1 = v1;
            this.v2 = v2;
            this.label = label;
        }

        public V getV1() {
            return v1;
        }

        public V getV2() {
            return v2;
        }

        public String toString() {
            return label;
        }
    }
}