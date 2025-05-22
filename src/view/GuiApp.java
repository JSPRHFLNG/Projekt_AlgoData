package view;

import model.delaunay.Delaunay;
import model.dijkstra.Dijkstra;
import model.graph.Graph;
import model.graph.JsonToVertex;
import model.graph.Vertex;
import model.mst.MST;

import javax.swing.*;
import java.util.List;

public class GuiApp
{

    public void launch()
    {
        SwingUtilities.invokeLater(() -> {

            // 1. Läser in en lista med Vertiser.
            List<Vertex<String>> vtxList;
            try {
                vtxList = JsonToVertex.readJson();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //2. En ny graph skapas med endast punkter(vertiser).
            Graph<String> graph = new Graph<>();
            for (Vertex<String> v : vtxList) {
                graph.addVertex(v);
            }

            //3. Graph trianguleras med Delaunay.
            Delaunay<String> dt = new Delaunay<>();
            Graph<String> dtGraph = dt.triangulate(graph);

            //4. Här följer olika alternativ och klasser som skapar separata Graph-objekt för sin tillämpning

            // Ex. MST tar emot "kartans" grundläggande Graph som är triangulariserad och skapar ett ny MST-graph som kan visas på kartan.
            MST<String> mst = new MST<>();

            // Ex. Dijkstras tar emot "kartans" grundläggande Graph som är triangulariserad och skapar en ny Dijksta-graph som innehåller noder och edges för den kortaste vägen
            Dijkstra<String> dickstra = new Dijkstra<>();
            Graph<String> dickGraph = dickstra.createShortestPathToAllGraph(graph, graph.getAllVertices().getFirst());
            Graph<String> dickGraph2 = dickstra.createShortestPathTwoVerticesGraph(dtGraph, graph.getAllVertices().getFirst(), graph.getAllVertices().getLast());


            // Ett MST läggs ut på kartan.
            GraphViewer<String> viewer = new GraphViewer<>(dtGraph);
            viewer.setVisible(true);
        });
    }
}
