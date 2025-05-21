package view;

import model.graph.Graph;
import model.graph.JsonToVertex;
import model.graph.ReadFromJSON;
import model.graph.Vertex;

import javax.swing.*;
import java.util.List;

public class GuiApp
{

    public void launch()
    {
        SwingUtilities.invokeLater(() -> {
            //List<Vertex<String>> vertexList = ReadFromJSON.readData("data/svenska-orter.json");

            List<Vertex<String>> vtxList;
            try {
                vtxList = JsonToVertex.readJson();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Graph<String> graph = new Graph<>();
            for (Vertex<String> v : vtxList) {
                graph.addVertex(v);
            }

            graph.triangulate();

            GraphViewer<String> viewer = new GraphViewer<>(graph);
            viewer.setVisible(true);
        });
    }
}
