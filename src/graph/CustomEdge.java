package graph;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomEdge {
    private CustomNode from;
    private CustomNode to;
    private int weight; // 2 = association , 3 = composition , 5 = delegation , 7 = aggregation , 0= inheritance
    /**
     * 1 is default since for relations such as composition that
     * they are not mentioned in the proposed method in the paper 2 ,
     * we will multiply 1, so it won't affect the result
     **/
    private int incomeW = 1; // for detection based on the paper 2 -> inheritance = 2 , aggregation = 5 , association = 11
    private int outcomeW = 1;  // for detection based on the paper 2 -> inheritance = 3 , aggregation = 7 , association = 13

    public CustomEdge(CustomNode from, CustomNode to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public CustomEdge(CustomNode from, CustomNode to, int weight, int incomeW, int outcomeW) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.incomeW = incomeW;
        this.outcomeW = outcomeW;
    }
}