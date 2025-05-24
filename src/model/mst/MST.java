package model.mst;


import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.prioQ.PriorityQueue;
import java.util.HashSet;
import java.util.Set;


/**
 * Minimum Spanning Tree (MST) utilizing Prim's Algorithm.
 * @param <T> the type in the Graph. The generic class type T.
 */

//https://www.geeksforgeeks.org/prims-minimum-spanning-tree-mst-greedy-algo-5/

public class MST<T>
{
    public Graph<T> createMST(Graph<T> delaunayGraph, Vertex<T> rootVertex)
    {
        Graph<T> mstGraph = new Graph<>();

        Set<Vertex<T>> visited = new HashSet<>();
        PriorityQueue<Edge<T>, Double> prioQ = new PriorityQueue<>(delaunayGraph.getAllEdges().size());

        visited.add(rootVertex);
        mstGraph.addVertex(rootVertex);

        // Enqueue all edges connected to root (start vertex).
        for (Edge<T> edge : delaunayGraph.getEdges(rootVertex.getInfo()))
        {
            prioQ.enqueue(edge, edge.getWeight());
        }

        while (!prioQ.isEmpty())
        {
            Edge<T> edge = prioQ.dequeue();
            Vertex<T> fromVertex = edge.getFrom();
            Vertex<T> toVertex = edge.getTo();

            boolean isFromVisited = visited.contains(fromVertex);
            boolean isToVisited = visited.contains(toVertex);

            if (isFromVisited && isToVisited)
            {
                continue;
            }

            Vertex<T> newVertex;
            if(isFromVisited)
            {
                newVertex = toVertex;
            }
            else
            {
                newVertex = fromVertex;
            }

            visited.add(newVertex);
            mstGraph.addVertex(newVertex);
            mstGraph.addEdge(fromVertex, toVertex, edge.getWeight());

            // Enqueue all edges from the new vertex that connect to unvisited vertices.
            for (Edge<T> edg : delaunayGraph.getEdges(newVertex.getInfo()))
            {
                Vertex<T> fromVtx = edg.getFrom();
                Vertex<T> toVtx = edg.getTo();

                boolean isFromVtxVisited = visited.contains(fromVtx);
                boolean isToVtxVisited = visited.contains(toVtx);

                if (!isFromVtxVisited || !isToVtxVisited)
                {
                    prioQ.enqueue(edg, edg.getWeight());
                }
            }
        }
        return mstGraph;
    }
}