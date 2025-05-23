package model.quadtree;

import model.graph.Graph;
import model.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Quadtree<T> {
    private static final int CAPACITY = 4;
    Rectangle boundary;
    List<Vertex<T>> vertices;
    boolean divided;

    Quadtree<T> northeast, northwest, southeast, southwest;


    public Quadtree(Rectangle boundary) {
        this.boundary = boundary;
        this.vertices = new ArrayList<>();
        this.divided = false;
    }

    public Quadtree(Graph<T> graph) {
        this.boundary = calculateBoundary(graph);
        this.vertices = new ArrayList<>();
        this.divided = false;

        for (Vertex<T> v : graph.getAllVertices()) {
            insert(v);
        }
    }

    public Quadtree(Graph<T> graph, Rectangle boundary) {
        this.boundary = boundary;
        this.vertices = new ArrayList<>();
        this.divided = false;

        for (Vertex<T> v : graph.getAllVertices()) {
            insert(v);
        }
    }

    public Quadtree(double minX, double minY, double maxX, double maxY) {
        this(new Rectangle(
                (minX + maxX) / 2,
                (minY + maxY) / 2,
                maxX - minX,
                maxY - minY
        ));
    }

    private Rectangle calculateBoundary(Graph<T> graph) {
        List<Vertex<T>> verticesWithin = graph.getAllVertices();
        if (verticesWithin.isEmpty()) {
            return new Rectangle(0, 0, 100, 100);
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Vertex<T> v : verticesWithin) {
            minX = Math.min(minX, v.getX());
            maxX = Math.max(maxX, v.getX());
            minY = Math.min(minY, v.getY());
            maxY = Math.max(maxY, v.getY());
        }

        double padding = 20;
        double width = (maxX - minX) + 2 * padding;
        double height = (maxY - minY) + 2 * padding;
        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        return new Rectangle(centerX, centerY, width, height);
    }

    public boolean insert(Vertex<T> vertex) {
        if (!boundary.contains(vertex)) return false;

        if (!divided && vertices.size() < CAPACITY) {
            vertices.add(vertex);
            return true;
        } else {
            if (!divided) {
                subdivide();
                for (Vertex<T> v : vertices) {
                    if(!(northeast.insert(v) || northwest.insert(v) || southeast.insert(v) || southwest.insert(v))) {

                    }
                }
                vertices.clear();
            }

            return (northeast.insert(vertex) || northwest.insert(vertex) ||
                    southeast.insert(vertex) || southwest.insert(vertex));
        }
    }

    private void subdivide() {
        double x = boundary.x;
        double y = boundary.y;
        double w = boundary.width / 2;
        double h = boundary.height / 2;

        northeast = new Quadtree<>(new Rectangle(x + w / 2, y - h / 2, w, h));
        northwest = new Quadtree<>(new Rectangle(x - w / 2, y - h / 2, w, h));
        southeast = new Quadtree<>(new Rectangle(x + w / 2, y + h / 2, w, h));
        southwest = new Quadtree<>(new Rectangle(x - w / 2, y + h / 2, w, h));

        divided = true;
    }

    public void query(Rectangle range, List<Vertex<T>> found) {
        System.out.printf("Querying boundary %s with range %s%n", boundary, range);

        if (!boundary.intersects(range)) {
            System.out.println("  No intersection, skipping");
            return;
        }

        System.out.printf("  Checking %d vertices in this node%n", vertices.size());
        for (Vertex<T> v : vertices) {
            if (range.contains(v)) {
                found.add(v);
                System.out.printf("  Found vertex: (%.2f, %.2f)%n", v.getX(), v.getY());
            }
        }

        if (divided) {
            System.out.println("  Checking children...");
            northeast.query(range, found);
            northwest.query(range, found);
            southeast.query(range, found);
            southwest.query(range, found);
        }
    }

    public void rebuild(Graph<T> graph) {
        vertices.clear();
        divided = false;
        northeast = northwest = southeast = southwest = null;
        this.boundary = calculateBoundary(graph);

        for (Vertex<T> v : graph.getAllVertices()) {
            insert(v);
        }
    }

    public Vertex<T> findNearest(double x, double y) {
        List<Vertex<T>> allVertices = getAllVertices();
        if (allVertices.isEmpty()) return null;

        Vertex<T> nearest = allVertices.getFirst();
        double nearestDist = distance(x, y, nearest.getX(), nearest.getY());

        for (Vertex<T> v : allVertices) {
            double dist = distance(x, y, v.getX(), v.getY());
            if (dist < nearestDist) {
                nearest = v;
                nearestDist = dist;
            }
        }

        return nearest;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public boolean isDivided() {
        return divided;
    }

    public Rectangle getBoundary() {
        return boundary;
    }
    public List<Rectangle> getAllBoundaries() {
        List<Rectangle> boundaries = new ArrayList<>();
        collectBoundaries(this, boundaries);
        return boundaries;
    }

    private void collectBoundaries(Quadtree<T> node, List<Rectangle> list) {
        list.add(node.getBoundary());
        if (node.divided) {
            collectBoundaries(node.northeast, list);
            collectBoundaries(node.northwest, list);
            collectBoundaries(node.southeast, list);
            collectBoundaries(node.southwest, list);
        }
    }


    public List<Vertex<T>> getAllVertices() {
        List<Vertex<T>> allVertices = new ArrayList<>(vertices);

        if (divided) {
            allVertices.addAll(northeast.getAllVertices());
            allVertices.addAll(northwest.getAllVertices());
            allVertices.addAll(southeast.getAllVertices());
            allVertices.addAll(southwest.getAllVertices());
        }

        return allVertices;
    }

    public void printStructure(int depth) {
        String indent = "  ".repeat(depth);
        System.out.printf("%sNode at depth %d: %s, vertices: %d, divided: %b%n",
                indent, depth, boundary, vertices.size(), divided);

        if (divided) {
            System.out.printf("%s├─ NE:%n", indent);
            northeast.printStructure(depth + 1);
            System.out.printf("%s├─ NW:%n", indent);
            northwest.printStructure(depth + 1);
            System.out.printf("%s├─ SE:%n", indent);
            southeast.printStructure(depth + 1);
            System.out.printf("%s└─ SW:%n", indent);
            southwest.printStructure(depth + 1);
        }
    }

    // Inre Rectangle-klass
    public static class Rectangle {
        public double x, y, width, height;

        public Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public <T> boolean contains(Vertex<T> vertex) {
            double left = x - width / 2;
            double right = x + width / 2;
            double top = y - height / 2;
            double bottom = y + height / 2;

            boolean inside = (vertex.getX() >= left &&
                    vertex.getX() <= right &&
                    vertex.getY() >= top &&
                    vertex.getY() <= bottom);
            return inside;
        }

        public boolean intersects(Rectangle range) {
            boolean intersects = !(range.x - range.width / 2 > x + width / 2 ||
                    range.x + range.width / 2 < x - width / 2 ||
                    range.y - range.height / 2 > y + height / 2 ||
                    range.y + range.height / 2 < y - height / 2);
            return intersects;
        }

        @Override
        public String toString() {
            return String.format("Rectangle(center=(%.2f, %.2f), width=%.2f, height=%.2f, bounds=[%.2f-%.2f, %.2f-%.2f])",
                    x, y, width, height,
                    x - width/2, x + width/2,
                    y - height/2, y + height/2);
        }
    }
}
