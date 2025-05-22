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
        setTitle("Visual Networks");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(670, 920);
        setLocationRelativeTo(null);

        List<Vertex<T>> vertices = graph.getAllVertices();

        GraphPanel<T> mainPanel = new GraphPanel<>(vertices, graph);

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
        Function<JComponent, JComponent> rightAlignment = comp -> {
            comp.setAlignmentX(Component.RIGHT_ALIGNMENT);
            functionPanel.add(comp);
            return comp;
        };
        leftAlignment.apply(new JLabel("             Control Panel"));
        functionPanel.add(Box.createVerticalStrut(30));

        // DATA ---------------------------------------------------------------------------------------

        // Add data function
        leftAlignment.apply(new JButton("Add data"));
        functionPanel.add(Box.createVerticalStrut(10));

        // DIJKSTRA ---------------------------------------------------------------------------------------

        // Hämtar alla vertex
        String[] vertexNames = vertices.stream()
                .map(Vertex::toString)
                .toArray(String[]::new);




        leftAlignment.apply(new JLabel("Bandwidth routing"));
        // From combobox
        leftAlignment.apply(new JLabel("From:"));
        JComboBox<String> ddlDijkstraFrom = new JComboBox<>(vertexNames);
        ddlDijkstraFrom.setMaximumSize(new Dimension(Integer.MAX_VALUE, ddlDijkstraFrom.getPreferredSize().height));
        leftAlignment.apply(ddlDijkstraFrom);
        functionPanel.add(Box.createVerticalStrut(5));

        // To combobox
        leftAlignment.apply(new JLabel("To:"));
        JComboBox<String> ddlDijkstraTo = new JComboBox<>(vertexNames);
        ddlDijkstraTo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ddlDijkstraTo.getPreferredSize().height));
        leftAlignment.apply(ddlDijkstraTo);
        functionPanel.add(Box.createVerticalStrut(5));

        // Calculate best (low weight) path
        leftAlignment.apply(new JButton("Calculate"));
        functionPanel.add(Box.createVerticalStrut(20));

        // ------------------------------------------------------------------------------------------------








        leftAlignment.apply(new JLabel("Filter Mapview"));
        functionPanel.add(Box.createVerticalStrut(5));
        leftAlignment.apply(new JCheckBox("Server locations"));
        leftAlignment.apply(new JCheckBox("Triangulated grid"));
        leftAlignment.apply(new JCheckBox("Minimum span network"));
        leftAlignment.apply(new JCheckBox("Bandwidth routing"));
        leftAlignment.apply(new JCheckBox("Servers by area"));




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
        private final Graph<T> graph;
        private Image backgroundMap;

        // These must correspond exactly to your map's SWEREF99TM bounding box in meters
        private final double MAP_MIN_X = 258000;   // Lower-left corner X
        private final double MAP_MAX_X = 930000;   // Upper-right corner X
        private final double MAP_MIN_Y = 6120000;  // Lower-left corner Y
        private final double MAP_MAX_Y = 7700000;  // Upper-right corner Y

        private double zoom = 1.0;
        private double panX = 0;
        private double panY = 0;
        private Point lastDragPoint;

        public GraphPanel(List<Vertex<T>> vertices, Graph<T> graph) {
            this.vertices = vertices;
            this.graph = graph;
            setBackground(Color.WHITE);

            backgroundMap = new ImageIcon("data/serverkarta-sverige390x920.png").getImage();

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

            g2.setColor(Color.BLUE);
            for (Edge<T> edge : graph.getAllEdges()) {
                Vertex<T> from = edge.getFrom();
                Vertex<T> to = edge.getTo();

                int x1 = (int) ((from.getX() - MAP_MIN_X) * scaleX);
                int y1 = (int) (getHeight() - (from.getY() - MAP_MIN_Y) * scaleY);
                int x2 = (int) ((to.getX() - MAP_MIN_X) * scaleX);
                int y2 = (int) (getHeight() - (to.getY() - MAP_MIN_Y) * scaleY);

                g2.drawLine(x1, y1, x2, y2);
            }

            for (Vertex<T> v : vertices) {
                double coordX = v.getX();
                double coordY = v.getY();

                // Convert SWEREF99TM coords to pixel positions
                int x = (int) ((coordX - MAP_MIN_X) * scaleX);
                // invert Y axis because pixel y=0 is top
                int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY);

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