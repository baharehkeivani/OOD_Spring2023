package graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomNode {

    static int nodeCnt = 0;

    private String label;
    private int index;
    private Set<CustomEdge> edges; //collection of edges to neighbors
    private int value = 1; // mean that class has no relationships (among defined types)

    private boolean visited = false; // for dfs

    public CustomNode(String label) {
        this.label = label;
        edges = new HashSet<>();
        index = nodeCnt;
        nodeCnt++;
    }

    public void addEdge(CustomEdge edge) {
        // phase 2
        edges.add(edge);
        // phase 3
        int tmpWeight = 1;
        if (Objects.equals(this.label, edge.getFrom().getLabel())) {
            tmpWeight = edge.getIncomeW();
        } else if (Objects.equals(this.label, edge.getTo().getLabel())) {
            tmpWeight = edge.getOutcomeW();
        }
        this.value *= tmpWeight;
    }

    public void DFS() {
        this.visited = true;
        for (CustomEdge edge : edges) {
            CustomNode nextNode = edge.getFrom() == this ? edge.getTo() : edge.getFrom();
            if (!nextNode.visited) {
                nextNode.DFS();
            }
        }
    }
}
