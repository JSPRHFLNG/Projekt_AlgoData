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
    private JComboBox<String> cbbDijkstraFrom;
    private JComboBox<String> cbbDijkstraTo;
    private boolean isShowQuadTreeBounds = false;
    private boolean isShowVertices = true;
    private boolean isShowDelaunay = true;
    private boolean isShowDijkstra = true;
    private boolean isShowMST = false;
    private String[] vertexNames;
    private JButton pathButton;
    private ActionListener highlightListener;




    public FunctionsPanel(Graph<T> graph, MapGraphPanel<T> mapGraphPanel)
    {
        this.graph = graph;
        this.mapGraphPanel=mapGraphPanel;
        this.allVertices = graph.getAllVertices();

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
        cbbDijkstraFrom.addActionListener(e -> {
            String selectedName = (String) cbbDijkstraFrom.getSelectedItem();

            // Om "Choose a location..." eller null så gör inget
            if (selectedName == null || selectedName.equals("Choose a location...")) {
                mapGraphPanel.setFromVertex(null);
                return;
            }

            // Leta upp vertex med matchande namn
            Vertex<T> selectedVertex = null;
            for (Vertex<T> v : allVertices) {
                if (v.getInfo().toString().equals(selectedName)) {
                    selectedVertex = v;
                    break;
                }
            }

            if (selectedVertex != null) {
                mapGraphPanel.setFromVertex(selectedVertex);
            }
        });

        cbbDijkstraTo.addActionListener(e -> {
            String selectedName = (String) cbbDijkstraTo.getSelectedItem();

            // Om "Choose a location..." eller null så gör inget
            if (selectedName == null || selectedName.equals("Choose a location...")) {
                mapGraphPanel.setToVertex(null);
                return;
            }

            // Leta upp vertex med matchande namn
            Vertex<T> selectedVertex = null;
            for (Vertex<T> v : allVertices) {
                if (v.getInfo().toString().equals(selectedName)) {
                    selectedVertex = v;
                    break;
                }
            }

            if (selectedVertex != null) {
                mapGraphPanel.setToVertex(selectedVertex);
            }
        });
    }

    private String[] getVertexNameListForCbb()
    {
        vertexNames = new String[allVertices.size() + 1];
        vertexNames[0] = "Choose a location...";
        for (int i = 0; i < allVertices.size(); i++)
        {
            vertexNames[i + 1] = allVertices.get(i).getInfo().toString();
        }
        return vertexNames;
    }

    private void setupDijkstraControls()
    {
        leftAlign.apply(new JLabel("                                     Functions"));
        add(Box.createVerticalStrut(10));


        leftAlign.apply(new JLabel("Data input"));
        add(Box.createVerticalStrut(5));
        leftAlign.apply(new JButton("Add data"));
        add(Box.createVerticalStrut(10));


        leftAlign.apply(new JLabel("<html><span style='font-weight:bold; font-size:13pt;'>Dijkstra algorithm</span></html>"));
        add(Box.createVerticalStrut(5));


        // From combobox
        leftAlign.apply(new JLabel("Select from:"));
        cbbDijkstraFrom = new JComboBox<>(getVertexNameListForCbb());
        cbbDijkstraFrom.setSelectedIndex(0);
        cbbDijkstraFrom.setForeground(Color.DARK_GRAY);
        cbbDijkstraFrom.setMaximumSize(new Dimension(Integer.MAX_VALUE, cbbDijkstraFrom.getPreferredSize().height));
        leftAlign.apply(cbbDijkstraFrom);
        add(Box.createVerticalStrut(5));


        // To combobox
        leftAlign.apply(new JLabel("Select to:"));
        cbbDijkstraTo = new JComboBox<>(getVertexNameListForCbb());
        cbbDijkstraTo.setSelectedIndex(0);
        cbbDijkstraTo.setForeground(Color.DARK_GRAY);
        cbbDijkstraTo.setMaximumSize(new Dimension(Integer.MAX_VALUE, cbbDijkstraTo.getPreferredSize().height));
        leftAlign.apply(cbbDijkstraTo);
        add(Box.createVerticalStrut(5));

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

            String fromID = (String) cbbDijkstraFrom.getSelectedItem();
            String toID = (String) cbbDijkstraTo.getSelectedItem();

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
                        "Start or finish node could not be found. Start: " + start + " Finish: " + finish);
                return;
            }
            try {
                Dijkstra<T> dijkstra = new Dijkstra<>();
                Graph<T> pathGraph = dijkstra.getLowWeightPathGraph(graph, start, finish);

                if(pathGraph == null || pathGraph.getAllVertices().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No path was found between the nodes.");
                } else {
                    mapGraphPanel.setDijkstraGraph(pathGraph);
                    JOptionPane.showMessageDialog(null,
                            "Lowest weight path calculated successfully! Path is highlighted.");
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
    private void addRegionQueryButton(JPanel functionPanel, Function<JComponent, JComponent> leftAlign)
    {
        leftAlign.apply(new JLabel("<html><span style='font-weight:bold; font-size:13pt;'>Quadtree</span></html>"));
        add(Box.createVerticalStrut(5));
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
        add(Box.createVerticalStrut(5));

        // FLYTTA TILL EN EGEN METOD MED COMBOBOX ISTÄLLET
        JButton quadTreeButton = new JButton("Open search dialogue");
        add(Box.createVerticalStrut(5));


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

                    // Highlight search area
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
        add(quadTreeButton);
        leftAlign.apply(quadTreeButton);
    }


    private void addRadiusSearchButton(JPanel functionPanel, Function<JComponent, JComponent> leftAlign)
    {
        add(Box.createVerticalStrut(10));
        JComboBox<Vertex<T>> centerComboBox = new JComboBox<>();
        for (Vertex<T> v : graph.getAllVertices()) {
            centerComboBox.addItem(v);  // toString() bör visa info, t.ex. v.getInfo()
        }
        leftAlign.apply(centerComboBox);
        centerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, centerComboBox.getPreferredSize().height));
        centerComboBox.setSelectedIndex(0);
        add(centerComboBox);
        add(Box.createVerticalStrut(5));

        JLabel radiusLabel = new JLabel("Radius (km):");
        leftAlign.apply(radiusLabel);
        add(radiusLabel);

        JSlider radiusSlider = new JSlider(0, 100, 10);
        radiusSlider.setMajorTickSpacing(10);
        radiusSlider.setMinorTickSpacing(5);
        radiusSlider.setPaintTicks(true);
        radiusSlider.setPaintLabels(true);
        leftAlign.apply(radiusSlider);
        add(radiusSlider);

        JButton searchButton = new JButton("Find nearby nodes");
        searchButton.addActionListener(e -> {
            Vertex<T> selected = (Vertex<T>) centerComboBox.getSelectedItem();
            double radius = radiusSlider.getValue()*10000;

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
        add(Box.createVerticalStrut(5));
        add(searchButton);
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
    private void addVisualizationControls(JPanel functionsPanel, Function<JComponent, JComponent> leftAlign) {

        add(Box.createVerticalStrut(8));
        JCheckBox showVertices = new JCheckBox("Show/hide vertices", true);
        showVertices.addActionListener(e -> {
            isShowVertices = showVertices.isSelected();
            mapGraphPanel.setShowVertices(isShowVertices);
            repaint();
        });
        leftAlign.apply(showVertices);


        JCheckBox showDelaunay = new JCheckBox("Show/hide delaunay", true);
        showDelaunay.addActionListener(e -> {
            isShowDelaunay = showDelaunay.isSelected();
            mapGraphPanel.setShowDelaunay(isShowDelaunay);
            repaint();
        });
        leftAlign.apply(showDelaunay);


        JCheckBox showQuadTree = new JCheckBox("Show/hide QuadTree bounds");
        showQuadTree.addActionListener(e -> {
            isShowQuadTreeBounds = showQuadTree.isSelected();
            mapGraphPanel.setShowQuadTreeBound(isShowQuadTreeBounds);
            repaint();
        });
        leftAlign.apply(showQuadTree);


        JCheckBox showDijkstra = new JCheckBox("Show/hide Dijkstra", true);
        showDijkstra.addActionListener(e -> {
            isShowDijkstra = showDijkstra.isSelected();
            mapGraphPanel.setShowDijkstra(isShowDijkstra);
            repaint();
        });
        leftAlign.apply(showDijkstra);


        JCheckBox showMST = new JCheckBox("Show/hide MST");
        showMST.addActionListener(e -> {
            isShowMST = showMST.isSelected();
            mapGraphPanel.setShowMST(isShowMST);
            repaint();
        });
        leftAlign.apply(showMST);


        /*
        JButton rebuildButton = new JButton("Rebuild QuadTree");
        rebuildButton.addActionListener(e -> {
            //mainPanel.rebuildQuadTree();
            JOptionPane.showMessageDialog(this, "QuadTree rebuilt successfully!");
        });
        leftAlign.apply(rebuildButton);
        add(Box.createVerticalStrut(5));

        JButton clearSearchButton = new JButton("Clear search area");
        clearSearchButton.addActionListener(e -> {
            //mainPanel.clearSearchArea();
        });
        leftAlign.apply(clearSearchButton);
         */
    }

}
