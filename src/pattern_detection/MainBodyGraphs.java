package pattern_detection;

import graph.CustomEdge;
import graph.CustomNode;
import graph.Graph;


public class MainBodyGraphs{
    public MainBodyGraphs() {
        makeMediatorMainBody();
        // TODO : rest of the patterns
    }

    public  Graph mediator = new Graph();

    private void makeMediatorMainBody(){
        CustomNode m = new CustomNode("Mediator");
        CustomNode c = new CustomNode("Colleague");
        CustomNode cm = new CustomNode("ConcreteMediator");
        CustomNode cc = new CustomNode("ConcreteColleague");

        cm.addEdge(new CustomEdge(cm,m,0,2,3));
        cm.addEdge(new CustomEdge(cm,cc,2,11,13));
        cm.addEdge(new CustomEdge(c,m,2,11,13));
        cm.addEdge(new CustomEdge(cc,c,0,2,3));

        mediator.addNode(m);
        mediator.addNode(c);
        mediator.addNode(cm);
        mediator.addNode(cc);
    }

}

