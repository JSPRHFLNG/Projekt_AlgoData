package model.graph;

import java.util.List;

public interface GraphInterface<T> {
    /**
     * Extracts and returns a list of all vertices stored in the graph
     *
     * @return a list containing all {@link Vertex} objects
     */
    public List<Vertex<T>> getAllVertices();

    /**
     * Extracts and returns all edges connected to the vertex identified
     * by the given info
     *
     * @param info the identifier of the vertex
     * @return a list containing all {@link Edge} objects connected to
     * the vertex
     */
    public List<Edge<T>> getEdges(T info);

    /**
     * Adds a new {@link Vertex} to the graph at the given position
     *
     * @param x,   the x coordinate
     * @param y,   the y coordinate
     * @param info the information and identifier of the vertex
     */
    public void addVertex(double x, double y, T info);

    public void addVertex(Vertex<T> vertex);

    /**
     * Adds an undirected edge between two vertices, internally stored as
     * two directed {@link Edge} objects, one in each direction
     *
     * @param infoA the identifier of vertex A
     * @param infoB the identifier of vertex B
     */
    public void addEdge(T infoA, T infoB);

    /**
     * Removes the {@link Vertex} object identified by info, and all
     * {@link Edge} objects connected to it
     *
     * @param info the identifier of the vertex to remove
     */
    public void remove(T info);

    /**
     * Returns the number of {@link Edge} objects in the graph. One double edge
     * between two vertices is counted as one edge
     *
     * @return number of edges
     */
    public int numberOfEdges();

    /**
     * Returns the number of {@link Vertex} objects in the graph
     *
     * @return number of vertices
     */
    public int numberOfVertices();

    void addEdge(Vertex<T> fromVertex, Vertex<T> toVertex, double distance);
}