package graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomNode {

    private String label;
    private Set<CustomEdge> edges; //collection of edges to neighbors

    private CustomNode(String label) {
        this.label = label;
        edges = new HashSet<>();
    }

    public void addEdge(CustomEdge edge) {
        edges.add(edge);
    }

    public static CustomNode newNode(String label){
        for (CustomNode node : Graph.getNodes()){
            if(Objects.equals(node.getLabel(), label)){
                return node;
            }
        }
        return new CustomNode(label);
    }

}
