package model.graph;

import java.awt.*;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Edge<T> implements Comparable<Edge<T>>
{
    private Vertex<T> from;
    private Vertex<T> to;
    private double distance;
    private Color color;

    public Edge(Vertex<T> from, Vertex<T> to)
    {
        if(from != null && to != null)
        {
            this.from = from;
            this.to = to;
            this.distance = calcEuclideanDist(this.from, this.to);
        }
    }


    private double calcEuclideanDist(Vertex<T> from, Vertex<T> to)
    {
        double dX = from.getX() - to.getX();
        double dY = from.getY() - to.getY();
        return sqrt(dX*dX+dY*dY);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Vertex<T> getTo() {
        return to;
    }

    public void setTo(Vertex<T> to) {
        this.to = to;
    }

    public Vertex<T> getFrom() {
        return from;
    }

    public void setFrom(Vertex<T> from) {
        this.from = from;
    }


    @Override
    public int compareTo(Edge<T> other) {
        return Double.compare(this.distance, other.distance);
    }
}