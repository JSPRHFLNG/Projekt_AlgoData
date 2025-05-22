package view;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.function.Function;

public class GraphViewer<T> extends JFrame
{

    public GraphViewer(Graph<T> graph)
    {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 900);
        setLocationRelativeTo(null);

        List<Vertex<T>> vertices = graph.getAllVertices();
        GraphPanel<T> mainPanel = new GraphPanel<>(vertices);

        JTable table = GraphPanel.createVertexTable(vertices);
        JScrollPane tableScroll = new JScrollPane(table);

        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));
        functionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Function<JComponent, JComponent> leftAlignment = comp -> {
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            functionPanel.add(comp);
            return comp;
        };
        leftAlignment.apply(new JLabel("Functions"));
        functionPanel.add(Box.createVerticalStrut(10));

        // Add data function
        leftAlignment.apply(new JButton("Add data"));
        functionPanel.add(Box.createVerticalStrut(10));

        // Hämtar alla vertex
        String[] vertexNames = vertices.stream()
                .map(Vertex::toString)
                .toArray(String[]::new);

        // From combobox
        leftAlignment.apply(new JLabel("From:"));

        JComboBox<String> from = new JComboBox<>(vertexNames);
        from.setMaximumSize(new Dimension(Integer.MAX_VALUE, from.getPreferredSize().height));
        leftAlignment.apply(from);
        functionPanel.add(Box.createVerticalStrut(5));

        // To combobox
        leftAlignment.apply(new JLabel("To:"));
        JComboBox<String> to = new JComboBox<>(vertexNames);
        to.setMaximumSize(new Dimension(Integer.MAX_VALUE, to.getPreferredSize().height));
        leftAlignment.apply(to);
        functionPanel.add(Box.createVerticalStrut(5));

        // Calculate shortest path
        leftAlignment.apply(new JButton("Calculate shortest path"));
        functionPanel.add(Box.createVerticalStrut(5));

        // Highlight node, keep??
        leftAlignment.apply(new JButton("Highlight node"));
        functionPanel.add(Box.createVerticalStrut(5));

        JScrollPane functionScroll = new JScrollPane(functionPanel);
        JSplitPane rightVerticalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, functionScroll);
        rightVerticalPane.setResizeWeight(0.7);
        rightVerticalPane.setDividerLocation(350);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, rightVerticalPane);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(450);

        add(splitPane);
    }

    public class GraphPanel<T> extends JPanel {
        private final List<Vertex<T>> vertices;
        private Image backgroundMap;

        // These must correspond exactly to your map's SWEREF99TM bounding box in meters
        private final double MAP_MIN_X = 280000;   // Lower-left corner X
        private final double MAP_MAX_X = 900000;   // Upper-right corner X
        private final double MAP_MIN_Y = 6150000;  // Lower-left corner Y
        private final double MAP_MAX_Y = 7700000;  // Upper-right corner Y

        private double zoom = 1.0;
        private double panX = 0;
        private double panY = 0;
        private Point lastDragPoint;

        public GraphPanel(List<Vertex<T>> vertices) {
            this.vertices = vertices;
            setBackground(Color.WHITE);

            backgroundMap = new ImageIcon("data/sverigekarta450x900.png").getImage();

            // Zoom with mouse wheel
            addMouseWheelListener(e -> {
                double delta = 0.1;
                double oldZoom = zoom;

                if (e.getWheelRotation() < 0) {
                    zoom += delta;
                } else {
                    zoom = Math.max(zoom - delta, 0.1);
                }

                int mouseX = e.getX();
                int mouseY = e.getY();

                // Adjust pan to keep the zoom centered on mouse pointer
                panX = mouseX - (mouseX - panX) * (zoom / oldZoom);
                panY = mouseY - (mouseY - panY) * (zoom / oldZoom);

                repaint();
            });

            // Pan with mouse drag
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    lastDragPoint = e.getPoint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    Point current = e.getPoint();
                    panX += current.getX() - lastDragPoint.getX();
                    panY += current.getY() - lastDragPoint.getY();
                    lastDragPoint = current;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Anti-aliasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Apply pan and zoom transforms
            g2.translate(panX, panY);
            g2.scale(zoom, zoom);

            // Draw the background map scaled to the panel size
            g2.drawImage(backgroundMap, 0, 0, getWidth(), getHeight(), this);

            // Calculate scaling factors based on panel size and map coordinate extents
            double scaleX = (double) getWidth() / (MAP_MAX_X - MAP_MIN_X);
            double scaleY = (double) getHeight() / (MAP_MAX_Y - MAP_MIN_Y);

            for (Vertex<T> v : vertices) {
                double coordX = v.getX();
                double coordY = v.getY();

                // Convert SWEREF99TM coords to pixel positions
                int x = (int) ((coordX - MAP_MIN_X) * scaleX);
                int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY); // invert Y axis because pixel y=0 is top

                // Draw point
                g2.setColor(Color.RED);
                g2.fillOval(x - 4, y - 4, 8, 8);

                // Draw label
                g2.setColor(Color.BLACK);
                g2.drawString(v.getInfo().toString(), x + 6, y - 6);

                g2.setColor(Color.BLUE);
                g2.drawRect(0, 0, (int)((MAP_MAX_X - MAP_MIN_X)*scaleX), (int)((MAP_MAX_Y - MAP_MIN_Y)*scaleY));
            }
        }








        /*
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rita kanter
            for (Vertex<T> v : graph.getAllVertices())
            {
                List<Edge<T>> edges = graph.getEdges(v.getInfo());
                for (Edge<T> edge : edges) {
                    Vertex<T> from = edge.getFrom();
                    Vertex<T> to = edge.getTo();

                    if (from.getInfo().toString().compareTo(to.getInfo().toString()) < 0)
                    {
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
            for (Vertex<T> v : graph.getAllVertices())
            {
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

        private static <T> JTable createVertexTable(List<Vertex<T>> vertices)
        {
            String[] columnNames = {"Place: ", "X", "Y"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            for(Vertex<T> v : vertices)
            {
                Object[] row = {
                        v.getInfo().toString(),
                        String.format("%.2f", v.getX()),
                        String.format("%.2f", v.getY())
                };
                model.addRow(row);
            }
            return new JTable(model);
        }
    }
}