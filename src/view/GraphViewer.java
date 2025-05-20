package view;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.ReadFromJSON;
import model.graph.Vertex;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GraphViewer<T> extends JFrame {

    public GraphViewer(Graph<T> graph) {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // centrerad

        List<Vertex<T>> vertices = graph.getAllVertices();
        GraphPanel<T> panel = new GraphPanel<>(vertices);

        JTable table = GraphPanel.createVertexTable(vertices);
        JScrollPane tableScroll = new JScrollPane(table);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, tableScroll);
        splitPane.setDividerLocation(600);
        add(splitPane);
    }

    private static class GraphPanel<T> extends JPanel {
        private final Graph<T> graph = new Graph<>();
        //private final int RADIUS = 16;
        private final List<Vertex<T>> vertices;
        private double minX, minY, maxX, maxY;
        private final int PADDING = 40;
        private Image backgroundMap;
        private final double MIN_LAT = 55.0;
        private final double MAX_LAT = 69.0;
        private final double MIN_LON = 11.0;
        private final double MAX_LON = 24.0;

        private int lonToX(double lon) {
            double normalized = (lon - MIN_LON) / (MAX_LON - MIN_LON);
            return (int)(normalized * getWidth());
        }

        private int latToY(double lat) {
            double normalized = (lat - MIN_LAT) / (MAX_LAT - MIN_LAT);
            return (int)((1 - normalized) * getHeight());
        }

        public GraphPanel(List<Vertex<T>> vertices) {
            this.vertices = vertices;
            setBackground(Color.WHITE);
            backgroundMap = new ImageIcon("data/Map.png").getImage();
        }

        private void computeBounds() {
            minX = Double.MAX_VALUE;
            minY = Double.MAX_VALUE;
            maxX = Double.MIN_VALUE;
            maxY = Double.MIN_VALUE;

            for(Vertex<T> v : vertices) {
                double x = v.getX();
                double y = v.getY();
                if(x < minX) minX = x;
                if(y < minY) minY = y;
                if(x > maxX) maxX = x;
                if(y > maxY) maxY = y;
            }
        }



        /*
        private double zoom = 0.01;
        private int transformX(double x) {
            double scale = (getWidth() - 2 * PADDING) / (maxX - minX);
            return (int) ((x - minX) * scale + zoom + PADDING);
        }

        private int transformY(double y) {
            double scale = (getHeight() - 2 * PADDING) / (maxY - minY);
            return (int) ((maxY - y) * scale + zoom + PADDING);
        }

         */

        /*
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

         */
        /*
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

         */

        private static <T> JTable createVertexTable(List<Vertex<T>> vertices) {
            String[] columnNames = {"Place: ", "X", "Y"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            for(Vertex<T> v : vertices) {
                Object[] row = {
                        v.getInfo().toString(),
                        String.format("%.2f", v.getX()),
                        String.format("%.2f", v.getY())
                };
                model.addRow(row);
            }
            return new JTable(model);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.red);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(backgroundMap, 0, 0, getWidth(), getHeight(), this);
            computeBounds();

            for(Vertex<T> v : vertices) {
                double lon = v.getX();
                double lat = v.getY();
                int x = lonToX(lon);
                int y = latToY(lat);
                String place = v.getInfo().toString();
                g2.setColor(Color.red);
                g2.fillOval(x - 3, y - 3, 6, 6);

                g2.setColor(Color.black);
                g2.drawString(place, x + 5, y - 5);
            }
        }
    }

    // --- Testprogram ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Vertex<String>> vertexList = ReadFromJSON.readData("data/svenska-orter.json");
            Graph<String> graph = new Graph<>();
            for(Vertex<String> v : vertexList) {
                graph.addVertex(v);
            }

            GraphViewer<String> viewer = new GraphViewer<>(graph);
            viewer.setVisible(true);
        });
    }
}