package model.mst;


import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

import java.util.*;

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
        PriorityQueue<Edge<T>> edgeQueue = new PriorityQueue<>();

        visited.add(rootVertex);
        mstGraph.addVertex(rootVertex);
        edgeQueue.addAll(delaunayGraph.getEdges(rootVertex.getInfo()));

        while (!edgeQueue.isEmpty())
        {
            Edge<T> edge = edgeQueue.poll();
            Vertex<T> toVertex = edge.getTo();
            Vertex<T> fromVertex = edge.getFrom();

            if (visited.contains(toVertex) && visited.contains(fromVertex))
            {
                continue;
            }

            Vertex<T> newVertex;
            if(visited.contains(fromVertex))
            {
                newVertex = toVertex;
            }
            else
            {
                newVertex = fromVertex;
            }

            mstGraph.addVertex(newVertex);
            mstGraph.addEdge(fromVertex, toVertex, edge.getDistance());
            visited.add(newVertex);

            List<Edge<T>> edgesOfVertex = delaunayGraph.getEdges(newVertex.getInfo());
            for (Edge<T> edg : edgesOfVertex)
            {
                if (!visited.contains(edg.getTo()) || !visited.contains(edg.getFrom()))
                {
                    edgeQueue.add(edg);
                }
            }
        }
        return mstGraph;
    }
}
