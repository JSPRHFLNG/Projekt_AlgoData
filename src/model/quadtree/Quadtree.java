package model.quadtree;

import java.util.ArrayList;
import java.util.List;

class Point {
    double x, y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class Rectangle {
    double x, y, width, height;

    public Rectangle(double x, double y, double width, double height) {
        this.x = x; // center x
        this.y = y; // center y
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point p) {
        return (p.x >= x - width / 2 &&
                p.x <= x + width / 2 &&
                p.y >= y - height / 2 &&
                p.y <= y + height / 2);
    }

    public boolean intersects(Rectangle range) {
        return !(range.x - range.width / 2 > x + width / 2 ||
                range.x + range.width / 2 < x - width / 2 ||
                range.y - range.height / 2 > y + height / 2 ||
                range.y + range.height / 2 < y - height / 2);
    }
}

class QuadTree {
    private static final int CAPACITY = 4;
    Rectangle boundary;
    List<Point> points;
    boolean divided;

    QuadTree northeast, northwest, southeast, southwest;

    public QuadTree(Rectangle boundary) {
        this.boundary = boundary;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    public boolean insert(Point p) {
        if (!boundary.contains(p)) return false;

        if (points.size() < CAPACITY) {
            points.add(p);
            return true;
        } else {
            if (!divided) subdivide();

            return (northeast.insert(p) || northwest.insert(p) ||
                    southeast.insert(p) || southwest.insert(p));
        }
    }

    private void subdivide() {
        double x = boundary.x;
        double y = boundary.y;
        double w = boundary.width / 2;
        double h = boundary.height / 2;

        northeast = new QuadTree(new Rectangle(x + w / 2, y - h / 2, w, h));
        northwest = new QuadTree(new Rectangle(x - w / 2, y - h / 2, w, h));
        southeast = new QuadTree(new Rectangle(x + w / 2, y + h / 2, w, h));
        southwest = new QuadTree(new Rectangle(x - w / 2, y + h / 2, w, h));

        divided = true;
    }

    public void query(Rectangle range, List<Point> found) {
        if (!boundary.intersects(range)) return;

        for (Point p : points) {
            if (range.contains(p)) {
                found.add(p);
            }
        }

        if (divided) {
            northeast.query(range, found);
            northwest.query(range, found);
            southeast.query(range, found);
            southwest.query(range, found);
        }
    }
}

public class Quadtree {
    public static void main(String[] args) {
        Rectangle boundary = new Rectangle(0, 0, 200, 200);
        QuadTree qt = new QuadTree(boundary);

        qt.insert(new Point(-10, -10));
        qt.insert(new Point(15, 15));
        qt.insert(new Point(-70, 80));
        qt.insert(new Point(50, -40));
        qt.insert(new Point(40, 60)); // causes subdivision

        Rectangle range = new Rectangle(0, 0, 100, 100);
        List<Point> found = new ArrayList<>();
        qt.query(range, found);

        System.out.println("Points found in range:");
        for (Point p : found) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }
}

