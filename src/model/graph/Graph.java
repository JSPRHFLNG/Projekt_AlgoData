package model.graph;

import java.util.List;

public class Graph<T> implements GraphInterface<T>
{

    /**
     * Extracts and returns a list of all vertices stored in the graph
     *
     * @return a list containing all {@link Vertex} objects
     */
    @Override
    public List<Vertex<T>> getAllVertices() {
        return List.of();
    }

    /**
     * Extracts and returns all edges connected to the vertex identified
     * by the given info
     *
     * @param info the identifier of the vertex
     * @return a list containing all {@link Edge} objects connected to
     * the vertex
     */
    @Override
    public List<Edge<T>> getEdges(Object info) {
        return List.of();
    }

    /**
     * Adds a new {@link Vertex} to the graph at the given position
     *
     * @param x
     * @param y
     * @param z
     * @param info the information and identifier of the vertex
     */
    @Override
    public void addVertex(double x, double y, double z, Object info) {

    }

    @Override
    public void addVertex(double x, double y, Object info) {

    }

    /**
     * Adds an undirected edge between two vertices, internally stored as
     * two directed {@link Edge} objects, one in each direction
     *
     * @param infoA the identifier of vertex A
     * @param infoB the identifier of vertex B
     */
    @Override
    public void addEdge(Object infoA, Object infoB) {

    }

    /**
     * Removes the {@link Vertex} object identified by info, and all
     * {@link Edge} objects connected to it
     *
     * @param info the identifier of the vertex to remove
     */
    @Override
    public void remove(Object info) {

    }

    /**
     * Returns the number of {@link Edge} objects in the graph. One double edge
     * between two vertices is counted as one edge
     *
     * @return number of edges
     */
    @Override
    public int numberOfEdges() {
        return 0;
    }

    /**
     * Returns the number of {@link Vertex} objects in the graph
     *
     * @return number of vertices
     */
    @Override
    public int numberOfVertices() {
        return 0;
    }
}
