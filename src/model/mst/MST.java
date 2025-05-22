package model.mst;


import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.prioQ.PriorityQueue;

import java.util.HashMap;
import java.util.Map;


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

        Map<Vertex<T>, Boolean> visited = new HashMap<>();
        PriorityQueue<Edge<T>, Double> edgeQueue = new PriorityQueue<>(delaunayGraph.getAllEdges().size());

        visited.put(rootVertex, true);
        mstGraph.addVertex(rootVertex);

        // Enqueue all edges connected to root
        for (Edge<T> edge : delaunayGraph.getEdges(rootVertex.getInfo()))
        {
            edgeQueue.enqueue(edge, edge.getWeight());
        }

        while (!edgeQueue.isEmpty())
        {
            Edge<T> edge = edgeQueue.dequeue();
            Vertex<T> fromVertex = edge.getFrom();
            Vertex<T> toVertex = edge.getTo();
            boolean isFromVisited = visited.getOrDefault(fromVertex, false);
            boolean isToVisited = visited.getOrDefault(toVertex, false);

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
            visited.put(newVertex, true);
            mstGraph.addVertex(newVertex);
            mstGraph.addEdge(fromVertex, toVertex, edge.getWeight());


            // Enqueue all edges from the new vertex
            for (Edge<T> nextEdge : delaunayGraph.getEdges(newVertex.getInfo()))
            {
                Vertex<T> u = nextEdge.getFrom();
                Vertex<T> v = nextEdge.getTo();

                boolean uVisited = visited.getOrDefault(u, false);
                boolean vVisited = visited.getOrDefault(v, false);

                // Only enqueue if it connects to an unvisited vertex
                if (!uVisited || !vVisited)
                {
                    edgeQueue.enqueue(nextEdge, nextEdge.getWeight());
                }
            }
        }
        return mstGraph;
    }
}


