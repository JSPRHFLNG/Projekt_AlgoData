package model.graph;

import java.awt.*;

public class Vertex<T>
{
    private T info;
    private double x;
    private double y;
    private double z;
    private Color color;


    /**
     * Creates a Vertex object with x and y coordinates.
     * @param xCoordinate
     * @param yCoordinate
     * @param info the object identifier of generic class type T.
     */
    public Vertex(double xCoordinate, double yCoordinate, T info)
    {
        this(xCoordinate, yCoordinate, 0, info);
    }


    /**
     * Creates a Vertex object with x, y and z coordinate.
     * @param xCoordinate
     * @param yCoordinate
     * @param zElevation
     * @param info the object identifier of generic class type T.
     */
    public Vertex(double xCoordinate, double yCoordinate, double zElevation, T info)
    {
        this.x=xCoordinate;
        this.y=yCoordinate;
        this.z=zElevation;
        this.info = info;
        this.color = Color.BLACK;
    }


    /**
     *  Getters and setters
     * @return this.*, * = instance field value.
     */

    public T getInfo()
    {
        return info;
    }


    public void setInfo(T info)
    {
        this.info = info;
    }


    public double getX()
    {
        return x;
    }


    public void setX(double x)
    {
        this.x = x;
    }


    public double getY()
    {
        return y;
    }


    public void setY(double y)
    {
        this.y = y;
    }


    public Color getColor()
    {
        return color;
    }


    public void setColor(Color color)
    {
        this.color = color;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}