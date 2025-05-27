package timecomplexity;

import model.graph.Graph;
import model.graph.Vertex;

import java.util.*;

public class TestGraphSizeN<T> {
    private final Map<Integer, Graph<T>> graphMap = new HashMap<>();
    private final List<Vertex<T>> allVertices;

    public TestGraphSizeN(List<Vertex<T>> vertices) {
        this.allVertices = vertices;
    }


    public void prepareGraphs(int[] sizes) {
        for (int size : sizes) {
            if (size <= allVertices.size()) {
                List<Vertex<T>> subList = new ArrayList<>(allVertices.subList(0, size));
                Graph<T> graph = new Graph<>(subList);
                graphMap.put(size, graph);
            } else {
                System.err.println("Not enough data in Json for size " + size);
            }
        }
    }


    public Graph<T> getGraph(int size) {
        return graphMap.get(size);
    }


    public Set<Integer> getSizes() {
        return graphMap.keySet();
    }
}
