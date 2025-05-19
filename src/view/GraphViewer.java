package view;


import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphViewer<T> extends JFrame {

    public GraphViewer(Graph<T> graph) {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // centrerad

        GraphPanel<T> panel = new GraphPanel<>(graph);
        add(panel);
    }

    private class GraphPanel<T> extends JPanel {
        private final Graph<T> graph;
        private final int RADIUS = 16;

        public GraphPanel(Graph<T> graph) {
            this.graph = graph;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rita kanter
            for (Vertex<T> v : graph.getAllVertices()) {
                List<Edge<T>> edges = graph.getEdges(v.getInfo());
                for (Edge<T> edge : edges) {
                    Vertex<T> from = edge.getFrom();
                    Vertex<T> to = edge.getTo();

                    if (from.getInfo().toString().compareTo(to.getInfo().toString()) < 0) {
                        g2.setColor(edge.getColor() != null ? edge.getColor() : Color.GRAY);
                        int x1 = (int) from.getX();
                        int y1 = (int) from.getY();
                        int x2 = (int) to.getX();
                        int y2 = (int) to.getY();
                        g2.drawLine(x1, y1, x2, y2);

                        String distStr = String.format("%.1f", edge.getDistance());
                        int midX = (x1 + x2) / 2;
                        int midY = (y1 + y2) / 2;
                        g2.setColor(Color.RED);
                        g2.drawString(distStr, midX + 5, midY - 5);
                    }
                }
            }

            // Rita noder
            for (Vertex<T> v : graph.getAllVertices()) {
                int x = (int) v.getX();
                int y = (int) v.getY();
                g2.setColor(v.getColor() != null ? v.getColor() : Color.BLACK);
                g2.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(v.getInfo().toString(), x + RADIUS + 2, y - RADIUS - 2);

                // Rita höjd (zElevation).
                String zText = String.format("%.1f", v.getZ());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(zText);
                int textHeight = fm.getAscent();

                g2.setColor(Color.GREEN);
                g2.drawString(zText, x - textWidth / 2, y + textHeight / 4); // Center it inside the node
            }
        }
    }

    // --- Testprogram ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Graph<String> graph = new Graph<>();

            graph.addVertex(100, 100, 2, "A");
            graph.addVertex(200, 150, 4,"B");
            graph.addVertex(300, 100, 400,"C");

            //graph.getAllVertices().get(0).setColor(Color.RED);
            //graph.getAllVertices().get(1).setColor(Color.BLUE);
            //graph.getAllVertices().get(2).setColor(Color.GREEN);

            graph.addEdge("A", "B");
            graph.addEdge("B", "C");
            graph.addEdge("C", "A");

            // Sätt färger på kanterna om du vill
            for (Vertex<String> v : graph.getAllVertices()) {
                for (Edge e : graph.getEdges(v.getInfo())) {
                    e.setColor(Color.LIGHT_GRAY); // för enkelhet, annars kan du variera dem
                }
            }
            GraphViewer<String> viewer = new GraphViewer<>(graph);
            viewer.setVisible(true);
        });
    }
}