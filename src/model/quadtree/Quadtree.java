package model.quadtree;

import model.graph.Vertex;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for a spatial data structure. With the inner class Rectangle, this class
 * allows recursive subdivision in 2D into quadrants for efficient spatial queries and finding nearest neighbors.This
 * class allows 4 nodes in every quadrant. When every quadrant (node) exceeds this capacity, it subdivides the quadrant into
 * 4 child-nodes (northwest, northeast, southeast, southwest).
 *
 */

public class Quadtree<T> {
    private static final int CAPACITY = 4;
    private final Rectangle boundary;
    private final List<Vertex<T>> vertices;
    public boolean divided;
    private List<Rectangle> lastVisited = new ArrayList<>();
    public Quadtree<T> northeast, northwest, southeast, southwest;

    /**
     * Initiates the boundary.
     *
     * @param boundary The rectangle
     */
    public Quadtree(Rectangle boundary) {
        this.boundary = boundary;
        this.vertices = new ArrayList<>();
        this.divided = false;
    }

    public List<Rectangle> getLastVisited() {
        return lastVisited;
    }

    public void clearLastVisited() {
        lastVisited.clear();
    }

    /**
     * Inserting vertices if a rectangle has not been subdivided, and will
     * continue until the capacity is reached. When the number of nodes
     * exceeds the capacity, it will subdivide and move the remaining
     * nodes into child-nodes.
     *
     * @param vertex The vertex to be inserted
     */
    public boolean insert(Vertex<T> vertex) {
        if (!boundary.contains(vertex)) return false;

        if (!divided) {
            if (vertices.size() < CAPACITY) {
                vertices.add(vertex);
                return true;
            } else {
                subdivide();
                List<Vertex<T>> oldVertices = new ArrayList<>(vertices);
                vertices.clear();
                for (Vertex<T> v : oldVertices) {
                    boolean inserted = northeast.insert(v) || northwest.insert(v)
                            || southeast.insert(v) || southwest.insert(v);
                    if (!inserted) {
                        System.err.println("Could not insert the vertex: " + v.getX() + ", " + v.getY());
                    }
                }
            }
        }

        return (northeast.insert(vertex) || northwest.insert(vertex)
                || southeast.insert(vertex) || southwest.insert(vertex));
    }

    /**
     * Subdivide the boundary into 4 child-nodes,
     * northeast, northwest, southeast and southwest.
     *
     */
    private void subdivide()
    {
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

    /**
     * Spatial queries. Searches after vertices with a set range.
     * Checking if a rectangle intersects with another rectangle, if not,
     * then a list of found vertices will be created. If the rectangle
     * has been subdivided, then it will check for child-nodes.
     *
     * @param found List of found vertices
     * @param range Rectangle-object
     */
    public void query(Rectangle range, List<Vertex<T>> found) {
        lastVisited.add(boundary);
        System.out.printf("Querying boundary %s with range %s%n", boundary, range);

        if (!boundary.intersects(range))
        {
            System.out.println("  No intersection, skipping");
            return;
        }

        System.out.printf("  Checking %d vertices in this node%n", vertices.size());
        for (Vertex<T> v : vertices) {
            if (range.contains(v)) {
                lastVisited.add(boundary);

                found.add(v);
                System.out.printf("  Found vertex: (%.2f, %.2f)%n", v.getX(), v.getY());
            }
        }

        if (divided)
        {
            System.out.println("  Checking children...");
            northeast.query(range, found);
            northwest.query(range, found);
            southeast.query(range, found);
            southwest.query(range, found);
        }
    }

    /**
     * Finds the nearest neighbors.
     *
     */
    public Vertex<T> findNearest(double x, double y) {
        if(getAllVertices().isEmpty()) return null;
        return findNearestRecursive(x, y, null, Double.MAX_VALUE).vertex;
    }


    /**
     * Inner class that helps with finding nearest neighbors and
     * tracks the distance to a given point.
     *
     */
    private static class NearestResult<T>
    {
        Vertex<T> vertex;
        double distance;

        /**
         * Constructs a result with the given vertex and distance.
         *
         * @param vertex Nearest vertex found
         * @param distance The distance to that vertex
         */
        NearestResult(Vertex<T> vertex, double distance)
        {
            this.vertex = vertex;
            this.distance = distance;
        }
    }


    private double distanceToRectangle(double px, double py, Rectangle rectangle)
    {
        double left = rectangle.x - rectangle.width / 2;
        double right = rectangle.x + rectangle.width / 2;
        double top = rectangle.y - rectangle.height / 2;
        double bottom = rectangle.y + rectangle.height / 2;

        if (px >= left && px <= right && py >= top && py <= bottom)
        {
            return 0.0;
        }
        double dx = Math.max(0, Math.max(left - px, px - right));
        double dy = Math.max(0, Math.max(top - py, py - bottom));
        return Math.sqrt(dx * dx + dy * dy);
    }

    private NearestResult<T> findNearestRecursive(double x, double y, Vertex<T> currentBest, double bestDistance)
    {

        double distanceToRegion = distanceToRectangle(x, y, boundary);
        if(distanceToRegion >= bestDistance) {
            return new NearestResult<>(currentBest, bestDistance);
        }
        Vertex<T> localBest = currentBest;
        double localBestDistance = bestDistance;

        for (Vertex<T> vertex : vertices)
        {
            double dist = distance(x, y, vertex.getX(), vertex.getY());
            if (dist < localBestDistance) {
                localBest = vertex;
                localBestDistance = dist;
            }
        }

        if (divided)
        {
            NearestResult<T> result = northeast.findNearestRecursive(x, y, localBest, localBestDistance);
            localBest = result.vertex;
            localBestDistance = result.distance;

            result = northwest.findNearestRecursive(x, y, localBest, localBestDistance);
            localBest = result.vertex;
            localBestDistance = result.distance;

            result = southeast.findNearestRecursive(x, y, localBest, localBestDistance);
            localBest = result.vertex;
            localBestDistance = result.distance;

            result = southwest.findNearestRecursive(x, y, localBest, localBestDistance);
            localBest = result.vertex;
            localBestDistance = result.distance;
        }

        return new NearestResult<>(localBest, localBestDistance);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public Rectangle getBoundary() {
        return boundary;
    }


    public List<Rectangle> getAllBoundaries()
    {
        List<Rectangle> boundaries = new ArrayList<>();
        collectBoundaries(this, boundaries);
        return boundaries;
    }


    private void collectBoundaries(Quadtree<T> node, List<Rectangle> list)
    {
        list.add(node.getBoundary());
        if (node.divided) {
            collectBoundaries(node.northeast, list);
            collectBoundaries(node.northwest, list);
            collectBoundaries(node.southeast, list);
            collectBoundaries(node.southwest, list);
        }
    }

    public List<Vertex<T>> getAllVertices()
    {
        List<Vertex<T>> allVertices = new ArrayList<>(vertices);

        if (divided) {
            allVertices.addAll(northeast.getAllVertices());
            allVertices.addAll(northwest.getAllVertices());
            allVertices.addAll(southeast.getAllVertices());
            allVertices.addAll(southwest.getAllVertices());
        }

        return allVertices;
    }

    public Rectangle markRectangleContaining(Vertex<T> foundVertices) {
        if(!boundary.contains(foundVertices)) return null;

        // if this is a child-node
        if(!divided && boundary.contains(foundVertices)) {
            return boundary;
        }

        if(divided) {
            Rectangle r;

            r = northeast.markRectangleContaining(foundVertices);
            if(r != null) return r;
            r = northwest.markRectangleContaining(foundVertices);
            if (r != null) return r;
            r = southeast.markRectangleContaining(foundVertices);
            if (r != null) return r;
            r = southwest.markRectangleContaining(foundVertices);
            if (r != null) return r;
        }
        return null;
    }


    public static class Rectangle {

        public double x, y, width, height;

        /**
         * Creates a rectangle with the specified dimensions.
         *
         */
        public Rectangle(double x, double y, double width, double height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Checks if the vertex contains within the rectangle's boundary.
         *
         */
        public <T> boolean contains(Vertex<T> vertex)
        {
            double left = x - width / 2;
            double right = x + width / 2;
            double top = y - height / 2;
            double bottom = y + height / 2;

            return (vertex.getX() >= left &&
                    vertex.getX() <= right &&
                    vertex.getY() >= top &&
                    vertex.getY() <= bottom);

        }

        /**
         * Checks if this rectangle intersect with another rectangle.
         *
         */
        public boolean intersects(Rectangle range)
        {

            return !(range.x - range.width / 2 > x + width / 2 ||
                    range.x + range.width / 2 < x - width / 2 ||
                    range.y - range.height / 2 > y + height / 2 ||
                    range.y + range.height / 2 < y - height / 2);
        }

        @Override
        public String toString()
        {
            return String.format("Rectangle(center=(%.2f, %.2f), width=%.2f, height=%.2f, bounds=[%.2f-%.2f, %.2f-%.2f])",
                    x, y, width, height,
                    x - width/2, x + width/2,
                    y - height/2, y + height/2);
        }
    }
}
