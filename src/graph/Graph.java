package graph;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class Graph{

    private static Set<CustomNode> nodes; //collection of all nodes
    private static Graph uniqueInstance = new Graph(); //singleton

    private Graph() {
        nodes = new HashSet<>();
    }

    public static Graph getGraph(){
        return uniqueInstance;
    }

    public static Set<CustomNode> getNodes (){
        return nodes;
    }

    public static void addNode(CustomNode node){
        nodes.add(node);
    }
}