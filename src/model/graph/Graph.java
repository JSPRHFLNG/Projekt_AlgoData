package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph<T> implements GraphInterface<T>
{
    private int nVertices;
    private int nEdges;
    private HashMap<T, Vertex<T>> vertices;
    private HashMap<T, ArrayList<Edge<T>>> edges;

    public Graph()
    {
        vertices = new HashMap<>();
        edges = new HashMap<>();
        nVertices = 0;
        nEdges = 0;
    }


    /**
     * Extracts and returns a list of all vertices stored in the graph
     *
     * @return a list containing all {@link Vertex} objects
     */
    @Override
    public List<Vertex<T>> getAllVertices()
    {
        return new ArrayList<>(vertices.values());
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
    public List<Edge<T>> getEdges(T info)
    {
        return edges.getOrDefault(info, new ArrayList<>());
    }


    /**
     * Adds a new {@link Vertex} to the graph at the given position
     * Does not allow duplicates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param info the identifier of the vertex
     */
    @Override
    public void addVertex(double x, double y,T info)
    {
        if(!vertices.containsKey(info)){
            vertices.put(info, new Vertex<>(x, y, info));
            edges.put(info, new ArrayList<>());
            nVertices++;
        }
    }


    /**
     * Adds a new {@link Vertex} to the graph at the given position.
     * Does not allow duplicates.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @param info the information and identifier of the vertex
     */
    @Override
    public void addVertex(double x, double y, double z,T info)
    {
        if(!vertices.containsKey(info))
        {
            vertices.put(info, new Vertex<>(x, y, z,info));
            edges.put(info, new ArrayList<>());
            nVertices++;
        }
    }


    /**
     * Adds an undirected edge between two vertices, internally stored as
     * two directed {@link Edge} objects, one in each direction
     *
     * @param infoA the identifier of vertex A
     * @param infoB the identifier of vertex B
     */
    @Override
    public void addEdge(T infoA, T infoB)
    {
        if(infoA == null || infoB == null){return;}

        if(vertices.containsKey(infoA) && vertices.containsKey(infoB))
        {
            Vertex<T> vertexA = vertices.get(infoA);
            Vertex<T> vertexB = vertices.get(infoB);
            Edge<T> edgeAB = new Edge<>(vertexA, vertexB);
            Edge<T> edgeBA = new Edge<>(vertexB, vertexA);

            edges.get(infoA).add(edgeAB);
            edges.get(infoB).add(edgeBA);
            nEdges++;
        }
    }


    /**
     * Removes the {@link Vertex} object identified by info, and all
     * {@link Edge} objects connected to it
     *
     * @param info the identifier of the vertex to remove
     */
    @Override
    public void remove(T info)
    {
        if(!vertices.containsKey(info) || info == null)
        {
            return;
        }

        nEdges -= edges.get(info).size() / 2;
        for(T key : edges.keySet())
        {
            edges.get(key).
                    removeIf(edge->
                            edge.getTo().getInfo().equals(info));
        }
        vertices.remove(info);
        edges.remove(info);
        nVertices--;
    }


    /**
     * Returns the number of {@link Edge} objects in the graph. One double edge
     * between two vertices is counted as one edge
     *
     * @return number of edges
     */
    @Override
    public int numberOfEdges()
    {
        return nEdges;
    }


    /**
     * Returns the number of {@link Vertex} objects in the graph
     *
     * @return number of vertices
     */
    @Override
    public int numberOfVertices()
    {
        return nVertices;
    }

}
