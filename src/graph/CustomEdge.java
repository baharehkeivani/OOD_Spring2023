package graph;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomEdge {

    private CustomNode from;
    private CustomNode to;
    private int weight; // 2 = association , 3 = composition , 5 = delegation , 7 = aggregation

    public CustomEdge(CustomNode from, CustomNode to, int weight) {
        super();
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

}