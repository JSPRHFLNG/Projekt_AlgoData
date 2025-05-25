package view;


import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.quadtree.Quadtree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;


public class MapGraphPanel<T> extends JPanel
{

    private Graph<T> vertexGraph;
    private Graph<T> delaunayGraph;
    private Graph<T> dijkstraGraph;
    private Graph<T> mstGraph;
    private Graph<T> quadResultGraph;

    private Quadtree.Rectangle searchArea = null;
    private Vertex<T> fromVertex = null;
    private Vertex<T> toVertex = null;


    private boolean isShowVertices = true;
    private boolean isShowDelaunay = true;
    private boolean isShowQuadTreeBound = false;
    private boolean isShowDijkstra = true;
    private boolean isShowMST = false;
    private final List<Vertex<T>> vertices;
    private final Image backgroundMap;
    Quadtree<T> qt;


    // SWEREF99TM coordinates, reference points.
    private final double MAP_MIN_X = 258000;   // Lower-left corner X
    private final double MAP_MAX_X = 930000;   // Upper-right corner X
    private final double MAP_MIN_Y = 6120000;  // Lower-left corner Y
    private final double MAP_MAX_Y = 7700000;  // Upper-right corner Y

    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;
    private Point lastDragPoint;

    private List<Vertex<T>> highlightedVertices = new ArrayList<>();


    public MapGraphPanel(Graph<T> graph, Graph<T> delaunayGraph, Graph<T> mstGraph)
    {
        this.vertices = graph.getAllVertices();
        this.vertexGraph = graph;
        this.delaunayGraph = delaunayGraph;
        this.mstGraph = mstGraph;
        this.dijkstraGraph = new Graph<>();


        backgroundMap = new ImageIcon("data/serverkarta-sverige390x920.png").getImage();
        setBackground(Color.WHITE);

        Quadtree.Rectangle boundary = new Quadtree.Rectangle(
                (MAP_MIN_X + MAP_MAX_X) / 2,
                (MAP_MIN_Y + MAP_MAX_Y) / 2,
                MAP_MAX_X - MAP_MIN_X,
                MAP_MAX_Y - MAP_MIN_Y
        );
        qt = new Quadtree<>(boundary);

        for(Vertex<T> v : vertices)
        {
            qt.insert(v);
        }

        // RETURNS ALL THE SERVER HALLS INSIDE THE CURRENT NODE/RECTANGLE
        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
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

                    JOptionPane.showMessageDialog(MapGraphPanel.this,
                            String.format("Closest server hall: %s\nDistance: %.2f units\n%s",
                                    nearest.getInfo(), distance, nearest.toString()));
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

        // Mouse drag pan.
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


    public void setHighlightedVertices(List<Vertex<T>> list)
    {
        this.highlightedVertices = list;
        repaint();
    }

    public void setDijkstraGraph(Graph<T> pathGraph)
    {
        this.dijkstraGraph = pathGraph;
        repaint();
    }

    public void setSearchArea(Quadtree.Rectangle area)
    {
        this.searchArea = area;
        repaint();
    }

    public void setShowVertices(boolean show)
    {
        this.isShowVertices = show;
        repaint();
    }

    public void setShowQuadTreeBound(boolean show)
    {
        this.isShowQuadTreeBound = show;
        repaint();
    }

    public void setShowDelaunay(boolean show)
    {
        this.isShowDelaunay = show;
        repaint();
    }

    public void setShowDijkstra(boolean show)
    {
        this.isShowDijkstra = show;
        repaint();
    }

    public void setShowMST(boolean show)
    {
        this.isShowMST = show;
        repaint();
    }

    public void setFromVertex(Vertex<T> v) {
        this.fromVertex = v;
        repaint();
    }

    public void setToVertex(Vertex<T> v) {
        this.toVertex = v;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Anti-aliasing.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pan and zoom transforms.
        g2.translate(panX, panY);
        g2.scale(zoom, zoom);

        // Background image scaled to panel size.
        g2.drawImage(backgroundMap, 0, 0, getWidth(), getHeight(), this);

        // Scaling factors based on BOUNDARY size and map coordinate extents.
        double scaleX = (double) getWidth() / (MAP_MAX_X - MAP_MIN_X);
        double scaleY = (double) getHeight() / (MAP_MAX_Y - MAP_MIN_Y);

        // RITA UPP DELAUNAY
        if (isShowDelaunay && delaunayGraph != null)
        {
            g2.setColor(new Color(0, 64, 255));
            for (Edge<T> edge : delaunayGraph.getAllEdges())
            {
                Vertex<T> from = edge.getFrom();
                Vertex<T> to = edge.getTo();

                int x1 = (int) ((from.getX() - MAP_MIN_X) * scaleX);
                int y1 = (int) (getHeight() - (from.getY() - MAP_MIN_Y) * scaleY);
                int x2 = (int) ((to.getX() - MAP_MIN_X) * scaleX);
                int y2 = (int) (getHeight() - (to.getY() - MAP_MIN_Y) * scaleY);

                g2.drawLine(x1, y1, x2, y2);
            }

        }

        // RITA UPP VERTICES
        g2.setColor(Color.RED);
        if (isShowVertices && vertexGraph != null)
        {
            for (Vertex<T> v : vertices)
            {
                double coordX = v.getX();
                double coordY = v.getY();

                // SWEREF99TM coords to pixel positions.
                int x = (int) ((coordX - MAP_MIN_X) * scaleX);
                // invert Y-axis because pixel y = 0 is top.
                int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY);

                // Draw point
                g2.setColor(v.getColor());
                g2.fillOval(x - 4, y - 4, 8, 8);

                // Draw label
                g2.setColor(Color.BLACK);
                g2.drawString(v.getInfo().toString(), x + 6, y - 6);


            }
        }

        // För att färgsätta highlightade verticer och dess ettiketter
        if (fromVertex != null) {
            int x = (int) ((fromVertex.getX() - MAP_MIN_X) * scaleX);
            int y = (int) (getHeight() - (fromVertex.getY() - MAP_MIN_Y) * scaleY);

            // Markera vertex med gul färg
            g2.setColor(Color.YELLOW);
            g2.fillOval(x - 6, y - 6, 12, 12);

            // Skriv label med gul färg
            g2.setColor(Color.YELLOW);
            g2.drawString(fromVertex.getInfo().toString(), x + 6, y - 6);
        }

        if (toVertex != null) {
            int x = (int) ((toVertex.getX() - MAP_MIN_X) * scaleX);
            int y = (int) (getHeight() - (toVertex.getY() - MAP_MIN_Y) * scaleY);

            // Markera vertex med grön färg
            g2.setColor(Color.GREEN);
            g2.fillOval(x - 6, y - 6, 12, 12);

            // Skriv label med grön färg
            g2.setColor(Color.GREEN);
            g2.drawString(toVertex.getInfo().toString(), x + 6, y - 6);
        }

        // RITA UPP QUADTREE STRUKTUR
        if (isShowQuadTreeBound && qt != null)
        {
            g2.setColor(Color.BLACK);
            for (Quadtree.Rectangle r : qt.getAllBoundaries()) {
                double x1 = (r.x - r.width / 2 - MAP_MIN_X) * scaleX;
                double y1 = getHeight() - ((r.y + r.height / 2 - MAP_MIN_Y) * scaleY);
                double w = r.width * scaleX;
                double h = r.height * scaleY;

                g2.draw(new Rectangle2D.Double(x1, y1, w, h));
            }
        }


        // RITA UPP MST
        if (isShowMST && mstGraph != null)
        {
            g2.setColor(Color.GREEN);
            for (Edge<T> edge : mstGraph.getAllEdges())
            {
                Vertex<T> from = edge.getFrom();
                Vertex<T> to = edge.getTo();

                int x1 = (int) ((from.getX() - MAP_MIN_X) * scaleX);
                int y1 = (int) (getHeight() - (from.getY() - MAP_MIN_Y) * scaleY);
                int x2 = (int) ((to.getX() - MAP_MIN_X) * scaleX);
                int y2 = (int) (getHeight() - (to.getY() - MAP_MIN_Y) * scaleY);

                g2.drawLine(x1, y1, x2, y2);
            }
            g2.setColor(Color.RED);
            for (Vertex<T> v : mstGraph.getAllVertices())
            {
                double coordX = v.getX();
                double coordY = v.getY();

                // SWEREF99TM coords to pixel positions
                int x = (int) ((coordX - MAP_MIN_X) * scaleX);
                // invert Y-axis because pixel y = 0 is top.
                int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY);

                // Draw point
                g2.setColor(v.getColor());
                g2.fillOval(x - 4, y - 4, 8, 8);

                // Draw label
                g2.setColor(Color.BLACK);
                g2.drawString(v.getInfo().toString(), x + 6, y - 6);
            }

        }



        // MARKERA NÄRMASTE GRANNAR
        g2.setColor(Color.YELLOW);
        for (Vertex<T> v : highlightedVertices) {
            int x = (int) ((v.getX() - MAP_MIN_X) * scaleX);
            int y = (int) (getHeight() - (v.getY() - MAP_MIN_Y) * scaleY);
            g2.fillOval(x - 6, y - 6, 12, 12); // större cirkel
        }

/*
        for (Vertex<T> v : vertices) {
            double coordX = v.getX();
            double coordY = v.getY();

            // SWEREF99TM coords to pixel positions
            int x = (int) ((coordX - MAP_MIN_X) * scaleX);
             // invert Y-axis because pixel y = 0 is top.
            int y = (int) (getHeight() - (coordY - MAP_MIN_Y) * scaleY);

            // Draw point
            g2.setColor(v.getColor());
            g2.fillOval(x - 4, y - 4, 8, 8);

            // Draw label
            g2.setColor(Color.BLACK);
            g2.drawString(v.getInfo().toString(), x + 6, y - 6);

            g2.setColor(Color.BLUE);
            g2.drawRect(0, 0, (int)((MAP_MAX_X - MAP_MIN_X)*scaleX), (int)((MAP_MAX_Y - MAP_MIN_Y)*scaleY));
        }

 */

        if(isShowDijkstra && dijkstraGraph != null) {
            g2.setColor(Color.PINK);
            g2.setStroke(new BasicStroke(2));
            for (Edge<T> edge : dijkstraGraph.getAllEdges()) {
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


}
