package graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Graph {

    private  Set<CustomNode> nodes; //collection of all nodes

    public Graph() {
        nodes = new HashSet<>();
    }

    public Graph(Set<CustomNode> nodes){
        this.nodes = nodes;
    }

    public  Set<CustomNode> getNodes() {
        return nodes;
    }

    public  void addNode(CustomNode node) {
        nodes.add(node);
    }

    public CustomNode newNode(String label) {
        for (CustomNode node :nodes) {
            if (Objects.equals(node.getLabel(), label)) {
                return node;
            }
        }
        return new CustomNode(label);
    }

    // TODO implement enrich

    // In the Graph class, add a method to check if two nodes are isomorphic
    public boolean areNodesIsomorphic(CustomNode node1, CustomNode node2) {
        if (!Objects.equals(node1.getLabel(), node2.getLabel())) {
            return false;
        }

        Set<CustomEdge> edges1 = node1.getEdges();
        Set<CustomEdge> edges2 = node2.getEdges();

        if (edges1.size() != edges2.size()) {
            return false;
        }

        for (CustomEdge edge1 : edges1) {
            boolean found = false;
            for (CustomEdge edge2 : edges2) {
                if (Objects.equals(edge1.getFrom().getLabel(), edge2.getFrom().getLabel()) &&
                        Objects.equals(edge1.getTo().getLabel(), edge2.getTo().getLabel()) &&
                        edge1.getWeight() == edge2.getWeight()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    // In the Graph class, add a method to check if two graphs are isomorphic
    public boolean areGraphsIsomorphic(Graph graph1, Graph graph2) {
        Set<CustomNode> nodes1 = graph1.getNodes();
        Set<CustomNode> nodes2 = graph2.getNodes();

        if (nodes1.size() != nodes2.size()) {
            return false;
        }

        for (CustomNode node1 : nodes1) {
            //
            boolean found = false;
            for (CustomNode node2 : nodes2) {
                if (areNodesIsomorphic(node1, node2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    // In the Graph class, add a method to find a subgraph within another graph
    public boolean findSubgraph( Graph subGraph) {
        Set<CustomNode> subNodes = subGraph.getNodes();

        for (CustomNode node : this.nodes) {
            Graph candidateSubgraph = new Graph();
            candidateSubgraph.addNode(node);

            for (CustomEdge edge : node.getEdges()) {
                candidateSubgraph.addNode(edge.getFrom());
                candidateSubgraph.addNode(edge.getTo());
            }

            if (areGraphsIsomorphic(candidateSubgraph, subGraph)) {
                return true;
            }
        }

        return false;
    }


}