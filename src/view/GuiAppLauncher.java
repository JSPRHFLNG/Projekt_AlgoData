package view;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import model.delaunay.Delaunay;
import model.dijkstra.Dijkstra;
import model.graph.Graph;
import model.graph.JsonToVertex;
import model.graph.Vertex;
import model.mst.MST;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GuiAppLauncher
{

    public void launch()
    {
        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(new FlatMacLightLaf());
                UIManager.put("Panel.background", new Color(235,235,235));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // 1. Läser in en lista med Vertiser.
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

            Delaunay<String> dt = new Delaunay<>();
            Graph<String> dtGraph = dt.triangulate(graph);

            GraphNetworkViewer<String> viewer = new GraphNetworkViewer<>(graph, dtGraph);
            viewer.setVisible(true);
        });
    }
}
