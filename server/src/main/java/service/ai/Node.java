package service.ai;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final List<Node> children = new ArrayList<>();

    public double evaluate() {
        return 0.0;
    }

    public List<Node> getChildren() {
        return children;
    }
}
