package model.dijkstra;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.prioQ.PriorityQueue;

import java.util.HashMap;
import java.util.Map;


/**
 * A Dijkstra implementation for path finding within a graph structure.
 * Calculates the most efficient path between two vertices.
 * Utilizes a custom priority queue which is configured with a minimum-heap.
 * Uses a forward phase for tracking and a back tracking phase to create a graph object.
 * @param <T> the vertices unique identifier. The generic class of type T.
 */
public class Dijkstra<T>
{
    PriorityQueue<Vertex<T>, Double> prioQ;
    private Map<T, Double> lowestWeightsMap;
    private Map<T, Vertex<T>> previousNodesMap;
    /**
     * Calculates the path with the lowest weight given a start and end vertex in an existing graph object.
     * @param graph the graph object for the algorithm to traverse.
     * @param startVertex the vertex where the algorithm starts.
     * @param endVertex the vertex which the algorithm should reach.
     * @return a graph object which consists of the vertices and edges of the calculated path.
     */
    public Graph<T> getLowWeightPathGraph(Graph<T> graph, Vertex<T> startVertex, Vertex<T> endVertex)
    {
        // Try catch? graph != null && startVertex != null && endVertex != null
        lowestWeightsMap = new HashMap<>();
        previousNodesMap= new HashMap<>();
        prioQ = new PriorityQueue<>(graph.getAllVertices().size());

        prioQ.enqueue(startVertex, 0.0);

        for (Vertex<T> vtx : graph.getAllVertices())
        {
            lowestWeightsMap.put(vtx.getInfo(), Double.POSITIVE_INFINITY);
        }

        lowestWeightsMap.put(startVertex.getInfo(), 0.0);

        while (!prioQ.isEmpty())
        {
            Vertex<T> currentVertex = prioQ.dequeue();
            if (currentVertex.getInfo().equals(endVertex.getInfo())){break;}

            for (Edge<T> edge : graph.getEdges(currentVertex.getInfo()))
            {
                //System.out.println("From " + edge.getFrom().getInfo() + " to " + edge.getTo().getInfo() + "| Weight: " + edge.getWeight());
                Vertex<T> nextVertex = edge.getTo();
                double currentWeight = lowestWeightsMap.get(currentVertex.getInfo()) + edge.getWeight();
                if (currentWeight < lowestWeightsMap.get(nextVertex.getInfo()))
                {
                    lowestWeightsMap.put(nextVertex.getInfo(), currentWeight);
                    previousNodesMap.put(nextVertex.getInfo(), currentVertex);
                    prioQ.enqueue(nextVertex, currentWeight);
                }
            }
        }
/*
        // Debug print outs. -------------------------
        System.out.println("  Back tracking");
        System.out.println("previousNodesMap keys: " + previousNodesMap.keySet());
        System.out.println("Backtracking from: " + endVertex.getInfo());
        for (Map.Entry<T, Vertex<T>> entry : previousNodesMap.entrySet())
        {
            T key = entry.getKey();
            Vertex<T> value = entry.getValue();
            System.out.println(key + " : " + value);
        }
        // --------------------------------------------
 */

        Graph<T> pathGraph = new Graph<>();
        Vertex<T> currentVertex = endVertex;

        if (!previousNodesMap.containsKey(currentVertex.getInfo()))
        {
            System.out.println("End vertex is unreachable or does not exist.");
            return graph;
        }

        while (previousNodesMap.containsKey(currentVertex.getInfo()))
        {
            Vertex<T> previous = previousNodesMap.get(currentVertex.getInfo());

            if (previous == null){break;}

            if (pathGraph.getVertex(previous.getInfo()) == null)
            {
                pathGraph.addVertex(previous);
            }

            if (pathGraph.getVertex(currentVertex.getInfo()) == null)
            {
                pathGraph.addVertex(currentVertex);
            }

            // Extract edge paths from vertexGraph object.
            double weight = 0.0;
            for (Edge<T> edge : graph.getEdges(previous.getInfo()))
            {
                if (edge.getTo().getInfo().equals(currentVertex.getInfo()))
                {
                    weight = edge.getWeight();
                    break;
                }
            }
            pathGraph.addEdge(previous, currentVertex, weight);
            currentVertex = previous;
        }
        return pathGraph;
    }
}