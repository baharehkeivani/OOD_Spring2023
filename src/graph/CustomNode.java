package graph;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomNode {

    private String label;
    private Set<CustomEdge> edges; //collection of edges to neighbors

    public CustomNode(String label) {
        this.label = label;
        edges = new HashSet<>();
    }

    public void addEdge(CustomEdge edge) {
        edges.add(edge);
    }

}
