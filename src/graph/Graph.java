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

    public boolean findSubgraph(Graph subgraph) {
        for (CustomNode subgraphNode : subgraph.getNodes()) {
            boolean found = false;
            for (CustomNode graphNode : nodes) {
                if (graphNode.getLabel().equals(subgraphNode.getLabel())) {
                    graphNode.DFS();
                    found = true;
                    break;
                }
            }
            if (!found || !subgraphNode.isVisited()) {
                return false;
            }
        }
        return true;
    }



}