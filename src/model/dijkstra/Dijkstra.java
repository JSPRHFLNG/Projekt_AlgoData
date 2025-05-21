package model.dijkstra;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;

import java.util.*;

public class Dijkstra<T>
{

    public Graph<T> createShortestPathToAllGraph(Graph<T> graph, Vertex<T> startVertex)
    {
        Graph<T> shortestPath = new Graph<>();

        Map<T, Double> dist = new HashMap<>();
        PriorityQueue<VertexDistance<T>> pq = new PriorityQueue<>();

        for (Vertex<T> v : graph.getAllVertices())
        {
            dist.put(v.getInfo(), Double.POSITIVE_INFINITY);
        }
        dist.put(startVertex.getInfo(), 0.0);
        pq.add(new VertexDistance<>(startVertex, 0.0));

        while (!pq.isEmpty())
        {
            VertexDistance<T> current = pq.poll();
            Vertex<T> u = current.vertex;

            if (current.distance > dist.get(u.getInfo())){continue;}


            if (shortestPath.getVertex(u.getInfo()) == null)
            {
                shortestPath.addVertex(u);
            }

            for (Edge<T> edge : graph.getEdges(u.getInfo()))
            {
                Vertex<T> v = edge.getTo();
                double altDist = dist.get(u.getInfo()) + edge.getDistance();

                if (altDist < dist.getOrDefault(v.getInfo(), Double.POSITIVE_INFINITY))
                {
                    dist.put(v.getInfo(), altDist);
                    pq.add(new VertexDistance<>(v, altDist));

                    if (shortestPath.getVertex(v.getInfo()) == null)
                    {
                        shortestPath.addVertex(v);
                    }
                    shortestPath.addEdge(u, v, edge.getDistance());
                }
            }
        }
        return shortestPath;
    }


    public Graph<T> createShortestPathTwoVerticesGraph(Graph<T> graph, Vertex<T> startVertex, Vertex<T> endVertex)
    {
        Map<T, Double> dist = new HashMap<>();
        Map<T, Vertex<T>> prev = new HashMap<>();
        PriorityQueue<VertexDistance<T>> pq = new PriorityQueue<>();

        for (Vertex<T> v : graph.getAllVertices())
        {
            dist.put(v.getInfo(), Double.POSITIVE_INFINITY);
        }
        dist.put(startVertex.getInfo(), 0.0);
        pq.add(new VertexDistance<>(startVertex, 0.0));

        while (!pq.isEmpty())
        {
            VertexDistance<T> current = pq.poll();
            Vertex<T> u = current.vertex;

            if (u.getInfo().equals(endVertex.getInfo()))
            {
                break;
            }

            for (Edge<T> edge : graph.getEdges(u.getInfo()))
            {
                Vertex<T> v = edge.getTo();
                double alt = dist.get(u.getInfo()) + edge.getDistance();

                if (alt < dist.get(v.getInfo())) {
                    dist.put(v.getInfo(), alt);
                    prev.put(v.getInfo(), u);
                    pq.add(new VertexDistance<>(v, alt));
                }
            }
        }

        Graph<T> pathGraph = new Graph<>();
        Vertex<T> current = endVertex;

        if (!prev.containsKey(current.getInfo()) && !current.getInfo().equals(startVertex.getInfo()))
        {
            return pathGraph;
        }

        while (current != null && prev.containsKey(current.getInfo()))
        {
            Vertex<T> previous = prev.get(current.getInfo());  // directly get Vertex<T>

            if (previous == null) break;

            if (pathGraph.getVertex(previous.getInfo()) == null)
            {
                pathGraph.addVertex(previous);
            }
            if (pathGraph.getVertex(current.getInfo()) == null)
            {
                pathGraph.addVertex(current);
            }

            double distance = 0;
            for (Edge<T> edge : graph.getEdges(previous.getInfo()))
            {
                if (edge.getTo().getInfo().equals(current.getInfo()))
                {
                    distance = edge.getDistance();
                    break;
                }
            }

            pathGraph.addEdge(previous, current, distance);
            current = previous;
        }

        return pathGraph;
    }


    // Kanske kan byggas in i den egna priority queue? Nu används Javas PriorityQueue.
    private static class VertexDistance<T> implements Comparable<VertexDistance<T>> {
        Vertex<T> vertex;
        double distance;

        VertexDistance(Vertex<T> vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(VertexDistance<T> other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}