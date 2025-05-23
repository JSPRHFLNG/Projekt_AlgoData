package view;

import model.dijkstra.Dijkstra;
import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.quadtree.Quadtree;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphViewer<T> extends JFrame {

    private final GraphPanel<T> mainPanel;
    private final Graph<T> graph;
    //private Vertex<T> selectedVertex = null;
    //private Vertex<T> hoveredVertex = null;
    private boolean showQuadTreeBounds = false;
    private JComboBox<String> from;
    private JComboBox<String> to;


    public GraphViewer(Graph<T> graph) {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(670, 920);
        setLocationRelativeTo(null);

        List<Vertex<T>> vertices = graph.getAllVertices();
        this.mainPanel = new GraphPanel<>(vertices, graph);
        this.graph = graph;

        JScrollPane tableScroll = createTableScroll(vertices);
        JScrollPane functionScroll = createFunctionPanel(vertices);

        JSplitPane rightVerticalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, functionScroll);
        rightVerticalPane.setResizeWeight(0.7);
        rightVerticalPane.setDividerLocation(300);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, rightVerticalPane);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(370);

        add(splitPane);
    }

    private JScrollPane createTableScroll(List<Vertex<T>> vertices) {
        JTable table = GraphPanel.createVertexTable(vertices);
        return new JScrollPane(table);
    }

    private JScrollPane createFunctionPanel(List<Vertex<T>> vertices) {
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS));
        functionPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        Function<JComponent, JComponent> leftAlign = comp -> {
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            functionPanel.add(comp);
            return comp;
        };

        leftAlign.apply(new JLabel("Functions"));
        functionPanel.add(Box.createVerticalStrut(10));

        leftAlign.apply(new JButton("Add data"));
        functionPanel.add(Box.createVerticalStrut(10));

        // Hämta alla vertex och lägg till en placeholder först
        List<Vertex<T>> allVertices = new ArrayList<>(mainPanel.graph.getAllVertices());
        String[] vertexNames = new String[allVertices.size() + 1];
        vertexNames[0] = "Choose a location...";
        for (int i = 0; i < allVertices.size(); i++) {
            vertexNames[i + 1] = allVertices.get(i).getInfo().toString();
        }

        // From combobox
        leftAlign.apply(new JLabel("From:"));
        from = new JComboBox<>(vertexNames);
        from.setSelectedIndex(0);
        from.setForeground(Color.DARK_GRAY);
        from.setMaximumSize(new Dimension(Integer.MAX_VALUE, from.getPreferredSize().height));
        leftAlign.apply(from);
        functionPanel.add(Box.createVerticalStrut(5));

        // To combobox
        leftAlign.apply(new JLabel("To:"));
        to = new JComboBox<>(vertexNames);
        to.setSelectedIndex(0);
        to.setForeground(Color.DARK_GRAY);
        to.setMaximumSize(new Dimension(Integer.MAX_VALUE, to.getPreferredSize().height));
        leftAlign.apply(to);
        functionPanel.add(Box.createVerticalStrut(5));

        // Namn till vertex-map för highlighting
        Map<String, Vertex<T>> nameToVertex = allVertices.stream()
                .collect(Collectors.toMap(v -> v.getInfo().toString(), v -> v));

        // Highlight-lyssnare
        ActionListener highlightListener = e -> {
            String fromName = (String) from.getSelectedItem();
            String toName = (String) to.getSelectedItem();

            // Återställ alla till röd
            for (Vertex<T> v : allVertices) {
                v.setColor(Color.RED);
            }

            if (fromName != null && !fromName.equals("Choose a location...")) {
                Vertex<T> v = nameToVertex.get(fromName);
                if (v != null) v.setColor(Color.YELLOW);
            }

            if (toName != null && !toName.equals("Choose a location...")) {
                Vertex<T> v = nameToVertex.get(toName);
                if (v != null) v.setColor(Color.GREEN);
            }

            mainPanel.repaint();
        };

        from.addActionListener(highlightListener);
        to.addActionListener(highlightListener);


        // <--------------------------------------------->

        // FLYTTA TILL EN EGEN METOD
        JButton shortestPathButton = new JButton("Calculate shortest path");
        shortestPathButton.addActionListener(e -> {
            String fromID = (String) from.getSelectedItem();
            String toID = (String) to.getSelectedItem();

            if(fromID == null || toID == null || fromID.equals(toID)) {
                JOptionPane.showMessageDialog(null, "Choose two nodes to calculate shortest path.");
                return;
            }

            Graph<T> mainGraph = graph;
            Vertex<T> start = null;
            Vertex<T> finish = null;

            for (Vertex<T> vertex : mainGraph.getAllVertices()) {
                if (vertex.toString().equals(fromID)) {
                    start = vertex;
                }
                if (vertex.toString().equals(toID)) {
                    finish = vertex;
                }
            }
            if(start == null || finish == null) {
                JOptionPane.showMessageDialog(null,
                        "Start- and finishnode could not be found. Start: " + start + " Finish: " + finish);
                return;
            }
            try {
                Dijkstra<T> dijkstra = new Dijkstra<>();
                Graph<T> pathGraph = dijkstra.getLowWeightPathGraph(mainGraph, start, finish);

                if(pathGraph == null || pathGraph.getAllVertices().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No path was found between the nodes.");
                } else {
                    mainPanel.setPathGraph(pathGraph);
                    JOptionPane.showMessageDialog(null,
                            "Shortest path calculated successfully! Path highlighted.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Error calculating shortest path: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // STÄDA
        /*
        // Checkboxes
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JCheckBox mstCheckbox = new JCheckBox("Lager 1");
        JCheckBox dijkstraCheckbox = new JCheckBox("Lager 2");

        mstCheckbox.setMargin(new Insets(0, 0, 0, 0));
        dijkstraCheckbox.setMargin(new Insets(0, 0, 0, 0));
        checkboxPanel.add(mstCheckbox);
        checkboxPanel.add(Box.createHorizontalStrut(5));
        checkboxPanel.add(dijkstraCheckbox);

        leftAlign.apply(checkboxPanel);
        functionPanel.add(Box.createVerticalStrut(5));

         */

        // KNAPPAR SOM VISAS I GUI
        leftAlign.apply(shortestPathButton);
        functionPanel.add(Box.createVerticalStrut(10));
        addRegionQueryButton(functionPanel, leftAlign);
        addRadiusSearchButton(functionPanel, leftAlign);
        addVisualizationControls(functionPanel, leftAlign);
        return new JScrollPane(functionPanel);
    }

    private void calculateAndShowShortestPath() {
        String fromID = (String) from.getSelectedItem();
        String toID = (String) to.getSelectedItem();

        if (fromID == null || toID == null || fromID.equals(toID)) {
            JOptionPane.showMessageDialog(null, "Choose two nodes to calculate shortest path.");
            return;
        }
        Vertex<T> start = null;
        Vertex<T> finish = null;

        for (Vertex<T> vertex : graph.getAllVertices()) {
            if (vertex.toString().equals(fromID)) {
                start = vertex;
            }
            if (vertex.toString().equals(toID)) {
                finish = vertex;
            }
        }

        if (start == null || finish == null) {
            JOptionPane.showMessageDialog(null,
                    "Start- and finishnode could not be found. Start: " + start + " Finish: " + finish);
            return;
        }
        try {
            Dijkstra<T> dijkstra = new Dijkstra<>();
            Graph<T> pathGraph = dijkstra.getLowWeightPathGraph(graph, start, finish);

            if (pathGraph == null || pathGraph.getAllVertices().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No path was found between the nodes.");
            } else {
                mainPanel.setPathGraph(pathGraph);
                JOptionPane.showMessageDialog(null,
                        "Shortest path calculated successfully! Path highlighted.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error calculating shortest path: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
        return comboBox;
    }

    // <------------  QuadTree ------------>
    private void addRegionQueryButton(JPanel functionPanel, Function<JComponent, JComponent> leftAlign) {
        JButton boundsButton = new JButton("Show coordinate bounds");
        boundsButton.addActionListener(e -> {
            Quadtree.Rectangle boundary = mainPanel.qt.getBoundary();
            double minX = boundary.x - boundary.width / 2;
            double maxX = boundary.x + boundary.width / 2;
            double minY = boundary.y - boundary.height / 2;
            double maxY = boundary.y + boundary.height / 2;

            String message = String.format(
                    "QuadTree Coordinate Bounds:\n\n" +
                            "X Range: %.0f to %.0f\n" +
                            "Y Range: %.0f to %.0f\n\n" +
                            "Center: (%.0f, %.0f)\n" +
                            "Size: %.0f × %.0f\n\n" +
                            "Use coordinates within these ranges for searches!",
                    minX, maxX, minY, maxY,
                    boundary.x, boundary.y, boundary.width, boundary.height
            );
            JOptionPane.showMessageDialog(this, message, "Coordinate Bounds", JOptionPane.INFORMATION_MESSAGE);
        });
        leftAlign.apply(boundsButton);
        functionPanel.add(Box.createVerticalStrut(5));

        // FLYTTA TILL EN EGEN METOD MED COMBOBOX ISTÄLLET
        JButton quadTreeButton = new JButton("Find server halls close by");
        quadTreeButton.addActionListener(e -> {
            Quadtree.Rectangle boundary = mainPanel.qt.getBoundary();
            double centerX = boundary.x;
            double centerY = boundary.y;

            String input = JOptionPane.showInputDialog(this,
                    String.format("Enter area (3 numbers separated by commas):\n" +
                                    "Format: centerX, centerY, radius\n" +
                                    "Example: %.0f, %.0f, 50000\n\n" +
                                    "Valid X range: %.0f to %.0f\n" +
                                    "Valid Y range: %.0f to %.0f",
                            centerX, centerY, 50000.0,
                            boundary.x - boundary.width/2, boundary.x + boundary.width/2,
                            boundary.y - boundary.height/2, boundary.y + boundary.height/2));

            if (input != null && !input.trim().isEmpty()) {
                try {
                    String[] parts = input.split(",");
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Input must have exactly 3 numbers separated by commas.");
                    }
                    centerX = Double.parseDouble(parts[0].trim());
                    centerY = Double.parseDouble(parts[1].trim());
                    double radius = Double.parseDouble(parts[2].trim());

                    // Debug output
                    System.out.println("Quadtree boundary: " + mainPanel.qt.getBoundary());
                    List<Vertex<T>> allVertices = mainPanel.qt.getAllVertices();
                    System.out.println("Number of vertices in quadtree: " + allVertices.size());

                    Quadtree.Rectangle searchArea = new Quadtree.Rectangle(centerX, centerY, radius * 2, radius * 2);
                    List<Vertex<T>> results = new ArrayList<>();
                    mainPanel.qt.query(searchArea, results);

                    List<Vertex<T>> withinRadius = new ArrayList<>();
                    System.out.println("Querying quadtree with rectangle: " + searchArea);
                    System.out.println("Number of candidates found: " + results.size());

                    for (Vertex<T> v : results) {
                        double dx = v.getX() - centerX;
                        double dy = v.getY() - centerY;
                        if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                            withinRadius.add(v);
                        }
                    }
                    System.out.println("Number of candidates within radius: " + withinRadius.size());

                    // Highlight the search area
                    mainPanel.setSearchArea(searchArea);
                    quadTreeQuery(withinRadius, centerX, centerY, radius);

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers (decimals and scientific notation allowed).");
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(this, iae.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
                }
            }
        });
        functionPanel.add(quadTreeButton);
        leftAlign.apply(quadTreeButton);
    }

    private void addRadiusSearchButton(JPanel functionPanel, Function<JComponent, JComponent> leftAlign) {
        JComboBox<Vertex<T>> centerComboBox = new JComboBox<>();
        for (Vertex<T> v : mainPanel.graph.getAllVertices()) {
            centerComboBox.addItem(v);  // toString() bör visa info, t.ex. v.getInfo()
        }
        leftAlign.apply(centerComboBox);
        functionPanel.add(centerComboBox);

        JLabel radiusLabel = new JLabel("Radie (meter):");
        leftAlign.apply(radiusLabel);
        functionPanel.add(radiusLabel);

        JSlider radiusSlider = new JSlider(1000, 100000, 20000);
        radiusSlider.setMajorTickSpacing(20000);
        radiusSlider.setMinorTickSpacing(5000);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        leftAlign.apply(radiusSlider);
        functionPanel.add(radiusSlider);

        JButton searchButton = new JButton("Hitta närliggande noder");
        searchButton.addActionListener(e -> {
            Vertex<T> selected = (Vertex<T>) centerComboBox.getSelectedItem();
            double radius = radiusSlider.getValue();

            if (selected != null) {
                double centerX = selected.getX();
                double centerY = selected.getY();
                Quadtree.Rectangle searchArea = new Quadtree.Rectangle(centerX, centerY, radius * 2, radius * 2);

                List<Vertex<T>> candidates = new ArrayList<>();
                mainPanel.qt.query(searchArea, candidates);

                List<Vertex<T>> withinRadius = new ArrayList<>();
                for (Vertex<T> v : candidates) {
                    double dx = v.getX() - centerX;
                    double dy = v.getY() - centerY;
                    if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                        withinRadius.add(v);
                    }
                }

                mainPanel.setSearchArea(searchArea);
                mainPanel.setHighlightedVertices(withinRadius);
            }
        });

        leftAlign.apply(searchButton);
        functionPanel.add(Box.createVerticalStrut(5));
        functionPanel.add(searchButton);
    }

    private void quadTreeQuery(List<Vertex<T>> results, double centerX, double centerY, double radius) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "No serverhalls found in the specified area.");
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(String.format("Found %d vertices within %.0f units of (%.0f, %.0f):\n\n",
                results.size(), radius, centerX, centerY));

        for (int i = 0; i < Math.min(results.size(), 10); i++) { // Show max 10 results
            Vertex<T> v = results.get(i);
            double dx = v.getX() - centerX;
            double dy = v.getY() - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            message.append(String.format("• %s (%.0f units away)\n", v, distance));
        }
        if (results.size() > 10) {
            message.append(String.format("\n... and %d more vertices", results.size() - 10));
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    // <------------- VISUALISERA QUADTREE STRUKTUR ------------->
    private void addVisualizationControls(JPanel functionPanel, Function<JComponent, JComponent> leftAlign) {
        JCheckBox showQuadTree = new JCheckBox("Show QuadTree bounds");

        showQuadTree.addActionListener(e -> {
            showQuadTreeBounds = showQuadTree.isSelected();
            mainPanel.setShowQuadTreeBound(showQuadTreeBounds);
            repaint();
        });
        leftAlign.apply(showQuadTree);
        functionPanel.add(Box.createVerticalStrut(10));

        /*
        JButton rebuildButton = new JButton("Rebuild QuadTree");
        rebuildButton.addActionListener(e -> {
            //mainPanel.rebuildQuadTree();
            JOptionPane.showMessageDialog(this, "QuadTree rebuilt successfully!");
        });
        leftAlign.apply(rebuildButton);
        functionPanel.add(Box.createVerticalStrut(5));

        JButton clearSearchButton = new JButton("Clear search area");
        clearSearchButton.addActionListener(e -> {
            //mainPanel.clearSearchArea();
        });
        leftAlign.apply(clearSearchButton);
         */
    }

    public static class GraphPanel<T> extends JPanel {
        private final List<Vertex<T>> vertices;
        private final Graph<T> graph;
        private final Image backgroundMap;
        private Graph<T> pathGraph;
        private Quadtree<T> qt;
        private Quadtree.Rectangle searchArea = null;
        private boolean showQuadTreeBound = false;

        // These must correspond exactly to your map's SWEREF99TM bounding box in meters
        private final double MAP_MIN_X = 258000;   // Lower-left corner X
        private final double MAP_MAX_X = 930000;   // Upper-right corner X
        private final double MAP_MIN_Y = 6120000;  // Lower-left corner Y
        private final double MAP_MAX_Y = 7700000;  // Upper-right corner Y

        private double zoom = 1.0;
        private double panX = 0;
        private double panY = 0;
        private Point lastDragPoint;
        private List<Vertex<T>> highlightedVertices = new ArrayList<>();

        public void setHighlightedVertices(List<Vertex<T>> list) {
            this.highlightedVertices = list;
            repaint();
        }

        public void setPathGraph(Graph<T> pathGraph) {
            this.pathGraph = pathGraph;
            repaint();
        }

        public void setSearchArea(Quadtree.Rectangle area) {
            this.searchArea = area;
            repaint();
        }

        public void setShowQuadTreeBound(boolean show) {
            this.showQuadTreeBound = show;
            repaint();
        }

        // STÄDA BORT SEN
        /*
        public void clearSearchArea() {
            this.searchArea = null;
            repaint();
        }

         */

        /*
        public void rebuildQuadTree() {
            qt = new Quadtree<>(graph, searchArea);
            System.out.println("\n=== QuadTree Structure After Rebuild ===");
            qt.printStructure(0);
            System.out.println("=========================================\n");
            repaint();
        }

         */

        public GraphPanel(List<Vertex<T>> vertices, Graph<T> graph) {
            this.vertices = vertices;
            this.graph = graph;
            pathGraph = new Graph<>();

            backgroundMap = new ImageIcon("data/serverkarta-sverige390x920.png").getImage();
            setBackground(Color.WHITE);

            Quadtree.Rectangle boundary = new Quadtree.Rectangle(
                    (MAP_MIN_X + MAP_MAX_X) / 2,
                    (MAP_MIN_Y + MAP_MAX_Y) / 2,
                    MAP_MAX_X - MAP_MIN_X,
                    MAP_MAX_Y - MAP_MIN_Y
            );
            qt = new Quadtree<>(boundary);

            for(Vertex<T> v : vertices) {
                qt.insert(v);
            }

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    double screenX = (e.getX() - panX) / zoom;
                    double screenY = (e.getY() - panY) / zoom;

                    double scaleX = (double) getWidth() / (MAP_MAX_X - MAP_MIN_X);
                    double scaleY = (double) getHeight() / (MAP_MAX_Y - MAP_MIN_Y);

                    double mapX = MAP_MIN_X + screenX / scaleX;
                    double mapY = MAP_MAX_Y - screenY / scaleY;

                    Vertex<T> nearest = qt.findNearest(mapX, mapY);
                    if (nearest != null) {
                        double dx = nearest.getX() - mapX;
                        double dy = nearest.getY() - mapY;
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance < 5000) {
                            JOptionPane.showMessageDialog(GraphPanel.this,
                                    String.format("Nearest vertex: %s\nDistance: %.2f units\n%s",
                                            nearest, distance, nearest.getInfo()));
                            return;
                        }
                    }

                    List<Vertex<T>> results = new ArrayList<>();
                    qt.query(new Quadtree.Rectangle(mapX, mapY, 20, 20), results);

                    if (!results.isEmpty()) {
                        Vertex<T> v = results.get(0);
                        JOptionPane.showMessageDialog(GraphPanel.this, v.getInfo());
                    } else {
                        JOptionPane.showMessageDialog(GraphPanel.this, "No vertices nearby.");
                    }
                }
            });



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

            // Calculate scaling factors based on BOUNDARY size and map coordinate extents
            double scaleX = (double) getWidth() / (MAP_MAX_X - MAP_MIN_X);
            double scaleY = (double) getHeight() / (MAP_MAX_Y - MAP_MIN_Y);


            // RITA UPP QUADTREE STRUKTUR
            if (showQuadTreeBound && qt != null) {
                g2.setColor(Color.MAGENTA);
                for (Quadtree.Rectangle r : qt.getAllBoundaries()) {
                    double x1 = (r.x - r.width / 2 - MAP_MIN_X) * scaleX;
                    double y1 = getHeight() - ((r.y + r.height / 2 - MAP_MIN_Y) * scaleY);
                    double w = r.width * scaleX;
                    double h = r.height * scaleY;

                    g2.draw(new Rectangle2D.Double(x1, y1, w, h));
                }
            }



            // MARKERA NÄRMASTE GRANNAR
            g2.setColor(Color.YELLOW);
            for (Vertex<T> v : highlightedVertices) {
                int x = (int) ((v.getX() - MAP_MIN_X) * scaleX);
                int y = (int) (getHeight() - (v.getY() - MAP_MIN_Y) * scaleY);
                g2.fillOval(x - 6, y - 6, 12, 12); // större cirkel
            }



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
                int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY); // invert Y axis because pixel y=0 is top

                // Draw point
                g2.setColor(v.getColor());
                g2.fillOval(x - 4, y - 4, 8, 8);

                // Draw label
                g2.setColor(Color.BLACK);
                g2.drawString(v.getInfo().toString(), x + 6, y - 6);

                g2.setColor(Color.BLUE);
                g2.drawRect(0, 0, (int)((MAP_MAX_X - MAP_MIN_X)*scaleX), (int)((MAP_MAX_Y - MAP_MIN_Y)*scaleY));
            }

            if(pathGraph != null) {
                g2.setColor(Color.PINK);
                for (Edge<T> edge : pathGraph.getAllEdges()) {
                    Vertex<T> from = edge.getFrom();
                    Vertex<T> to = edge.getTo();

                    int x1 = (int) ((from.getX() - MAP_MIN_X) * scaleX);
                    int y1 = (int) (getHeight() - (from.getY() - MAP_MIN_Y) * scaleY);
                    int x2 = (int) ((to.getX() - MAP_MIN_X) * scaleX);
                    int y2 = (int) (getHeight() - (to.getY() - MAP_MIN_Y) * scaleY);

                    g2.drawLine(x1, y1, x2, y2);
                }
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