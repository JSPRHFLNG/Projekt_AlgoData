package view;


import model.graph.Graph;
import model.quadtree.Quadtree;

import javax.swing.*;

public class GraphNetworkViewer<T> extends JFrame {
    private MapGraphPanel<T> mapGraphPanel;
    private VertexTablePanel<T> tablePanel;
    private FunctionsPanel<T> functionsPanel;

    private JSplitPane verticalRight;
    private JSplitPane horizontal;

    public GraphNetworkViewer(Graph<T> graph, Graph<T> delaunayGraph, Graph<T> mstGraph, Quadtree<T> qt) {
        setup();

        mapGraphPanel = new MapGraphPanel<>(graph, delaunayGraph, mstGraph, qt);
        tablePanel = new VertexTablePanel<>(graph.getAllVertices());
        functionsPanel = new FunctionsPanel<>(delaunayGraph, mapGraphPanel);

        verticalRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, functionsPanel);
        verticalRight.setResizeWeight(0.7);
        verticalRight.setDividerLocation(300);

        horizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapGraphPanel, verticalRight);
        horizontal.setResizeWeight(0.65);
        horizontal.setDividerLocation(370);

        add(horizontal);
    }

    private void setup() {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(670, 920);
        setLocationRelativeTo(null);
    }
}
