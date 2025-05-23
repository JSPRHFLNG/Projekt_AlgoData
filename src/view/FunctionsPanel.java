package view;

import model.dijkstra.Dijkstra;
import model.graph.Graph;
import model.graph.Vertex;
import model.quadtree.Quadtree;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FunctionsPanel<T> extends JPanel
{

    private MapGraphPanel<T> mapGraphPanel;
    private Graph<T> graph;
    private List<Vertex<T>> allVertices;
    private Function<JComponent, JComponent> leftAlign;
    private JComboBox<String> from;
    private JComboBox<String> to;
    private boolean showQuadTreeBounds = false;
    private String[] vertexNames;
    private JButton pathButton;
    private ActionListener highlightListener;




    public FunctionsPanel(Graph<T> graph, MapGraphPanel<T> mapGraphPanel)
    {
        this.graph = graph;
        this.mapGraphPanel=mapGraphPanel;
        allVertices = graph.getAllVertices();

        // Programmatically add buttons and functionality to gui.

        setupPanel();

        setupDijkstraControls();
        addRegionQueryButton(this, leftAlign);
        addRadiusSearchButton(this, leftAlign);
        addVisualizationControls(this, leftAlign);
        setupHighlighter();
    }


    private void setupPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.BLUE));

        leftAlign = comp ->
        {
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(comp);
            return comp;
        };
    }

    private void setupHighlighter()
    {
        // Highlight
        highlightListener = e ->
        {
            resetColors(graph.getAllVertices());
            highlightSelected(from, Color.YELLOW, allVertices);
            highlightSelected(to, Color.GREEN, allVertices);
            mapGraphPanel.repaint();
        };
        from.addActionListener(highlightListener);
        to.addActionListener(highlightListener);
    }


    private void setupDijkstraControls()
    {
        leftAlign.apply(new JLabel("Functions"));
        add(Box.createVerticalStrut(5));
        leftAlign.apply(new JButton("Add data"));
        add(Box.createVerticalStrut(10));

        vertexNames = new String[allVertices.size() + 1];
        vertexNames[0] = "Choose a location...";
        for (int i = 0; i < allVertices.size(); i++)
        {
            vertexNames[i + 1] = allVertices.get(i).getInfo().toString();
        }

        // From combobox
        leftAlign.apply(new JLabel("From:"));
        from = new JComboBox<>(vertexNames);
        from.setSelectedIndex(0);
        from.setForeground(Color.DARK_GRAY);
        from.setMaximumSize(new Dimension(Integer.MAX_VALUE, from.getPreferredSize().height));
        leftAlign.apply(from);
        add(Box.createVerticalStrut(10));


        // To combobox
        leftAlign.apply(new JLabel("To:"));
        to = new JComboBox<>(vertexNames);
        to.setSelectedIndex(0);
        to.setForeground(Color.DARK_GRAY);
        to.setMaximumSize(new Dimension(Integer.MAX_VALUE, to.getPreferredSize().height));
        leftAlign.apply(to);
        add(Box.createVerticalStrut(10));

        pathButton = new JButton("Calculate shortest path");
        pathButton.addActionListener(e -> calculateShortestPath(this.graph));
        leftAlign.apply(pathButton);
        add(Box.createVerticalStrut(10));


    }


    private void resetColors(List<Vertex<T>> vertices)
    {
        for (Vertex<T> v : vertices) v.setColor(Color.RED);
    }


    private JComboBox<String> createComboBox(String[] items)
    {
        JComboBox<String> box = new JComboBox<>(items);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, box.getPreferredSize().height));
        return box;
    }


    private void calculateShortestPath(Graph<T> graph)
    {
        // FLYTTA TILL EN EGEN METOD

            String fromID = (String) from.getSelectedItem();
            String toID = (String) to.getSelectedItem();

            if(fromID == null || toID == null || fromID.equals(toID)) {
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
            if(start == null || finish == null) {
                JOptionPane.showMessageDialog(null,
                        "Start- and finishnode could not be found. Start: " + start + " Finish: " + finish);
                return;
            }
            try {
                Dijkstra<T> dijkstra = new Dijkstra<>();
                Graph<T> pathGraph = dijkstra.getLowWeightPathGraph(graph, start, finish);

                if(pathGraph == null || pathGraph.getAllVertices().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No path was found between the nodes.");
                } else {
                    mapGraphPanel.setPathGraph(pathGraph);
                    JOptionPane.showMessageDialog(null,
                            "Shortest path calculated successfully! Path highlighted.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Error calculating shortest path: " + ex.getMessage());
                ex.printStackTrace();
            }
    }


    private void highlightSelected(JComboBox<String> comboBox, Color color, List<Vertex<T>> vertices)
    {
        String name = (String) comboBox.getSelectedItem();
        for (Vertex<T> v : vertices)
        {
            if (v.getInfo().toString().equals(name))
            {
                v.setColor(color);
                break;
            }
        }
    }


    // <------------  QuadTree ------------>
    private void addRegionQueryButton(JPanel functionPanel, Function<JComponent, JComponent> leftAlign) {
        JButton boundsButton = new JButton("Show coordinate bounds");
        boundsButton.addActionListener(e -> {
            Quadtree.Rectangle boundary = mapGraphPanel.qt.getBoundary();
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

        // FLYTTA TILL EN EGEN METOD MED COMBOBOX ISTÄLLET
        JButton quadTreeButton = new JButton("Find server halls close by");
        add(Box.createVerticalStrut(10));
        quadTreeButton.addActionListener(e -> {
            Quadtree.Rectangle boundary = mapGraphPanel.qt.getBoundary();
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
                    System.out.println("Quadtree boundary: " + mapGraphPanel.qt.getBoundary());
                    List<Vertex<T>> allVertices = mapGraphPanel.qt.getAllVertices();
                    System.out.println("Number of vertices in quadtree: " + allVertices.size());

                    Quadtree.Rectangle searchArea = new Quadtree.Rectangle(centerX, centerY, radius * 2, radius * 2);
                    List<Vertex<T>> results = new ArrayList<>();
                    mapGraphPanel.qt.query(searchArea, results);

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
                    mapGraphPanel.setSearchArea(searchArea);
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
        add(Box.createVerticalStrut(10));
        centerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, centerComboBox.getPreferredSize().height));

        for (Vertex<T> v : graph.getAllVertices()) {
            centerComboBox.addItem(v);  // toString() bör visa info, t.ex. v.getInfo()
        }
        leftAlign.apply(centerComboBox);
        functionPanel.add(centerComboBox);

        add(Box.createVerticalStrut(5));
        JLabel radiusLabel = new JLabel("Radie (meter):");
        leftAlign.apply(radiusLabel);
        functionPanel.add(radiusLabel);

        JSlider radiusSlider = new JSlider(0, 100000, 20000);
        radiusSlider.setMajorTickSpacing(20000);
        radiusSlider.setMinorTickSpacing(5000);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        leftAlign.apply(radiusSlider);
        functionPanel.add(radiusSlider);
        add(Box.createVerticalStrut(10));

        JButton searchButton = new JButton("Find nearby nodes");
        searchButton.addActionListener(e -> {
            Vertex<T> selected = (Vertex<T>) centerComboBox.getSelectedItem();
            double radius = radiusSlider.getValue();

            if (selected != null) {
                double centerX = selected.getX();
                double centerY = selected.getY();
                Quadtree.Rectangle searchArea = new Quadtree.Rectangle(centerX, centerY, radius * 2, radius * 2);

                List<Vertex<T>> candidates = new ArrayList<>();
                mapGraphPanel.qt.query(searchArea, candidates);

                List<Vertex<T>> withinRadius = new ArrayList<>();
                for (Vertex<T> v : candidates) {
                    double dx = v.getX() - centerX;
                    double dy = v.getY() - centerY;
                    if (Math.sqrt(dx * dx + dy * dy) <= radius) {
                        withinRadius.add(v);
                    }
                }

                mapGraphPanel.setSearchArea(searchArea);
                mapGraphPanel.setHighlightedVertices(withinRadius);
            }
        });

        leftAlign.apply(searchButton);
        functionPanel.add(searchButton);
    }


    private void quadTreeQuery(List<Vertex<T>> results, double centerX, double centerY, double radius) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(mapGraphPanel, "No serverhalls found in the specified area.");
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
        add(Box.createVerticalStrut(10));
        JCheckBox showQuadTree = new JCheckBox("Show QuadTree bounds");

        showQuadTree.addActionListener(e -> {
            showQuadTreeBounds = showQuadTree.isSelected();
            mapGraphPanel.setShowQuadTreeBound(showQuadTreeBounds);
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

}
