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

        // Enqueue start vertex.
        prioQ.enqueue(startVertex, 0.0);


        // Set all vertex weights to infinity.
        for (Vertex<T> vtx : graph.getAllVertices())
        {
            lowestWeightsMap.put(vtx.getInfo(), Double.POSITIVE_INFINITY);
        }
        // Set start vertex weight to 0.
        lowestWeightsMap.put(startVertex.getInfo(), 0.0);


        // Iterate while there is vertices in the queue.
        while (!prioQ.isEmpty())
        {
            // Take out lowest weight vertex from prioQ (min-heap), as currentVertex.
            Vertex<T> currentVertex = prioQ.dequeue();

            // Base-case. If the currentVertex is the endVertex the algorithm is done.
            if (currentVertex.getInfo().equals(endVertex.getInfo())){break;}

            // Given currentVertex extract and iterate its outgoing edges.
            for (Edge<T> edge : graph.getEdges(currentVertex.getInfo()))
            {
                System.out.println(edge.getFrom().getInfo() + " -> " + edge.getTo().getInfo() + " (weight: " + edge.getWeight() + ")");

                // Track the edge forward to the next vertex.
                Vertex<T> nextVertex = edge.getTo();

                // Extract the accumulated weight to get from start to the currentVertex and add this edge weight.
                double currentWeight = lowestWeightsMap.get(currentVertex.getInfo()) + edge.getWeight();

                // If the path from currentVertex to this nextVertex is cheaper.
                if (currentWeight < lowestWeightsMap.get(nextVertex.getInfo()))
                {
                    // Then add (key currentVertex : value currentWeight), because it is the cheapest path so far from start to the nextVertex.
                    lowestWeightsMap.put(nextVertex.getInfo(), currentWeight);

                    // And add the step (key nextVertex : value currentVertex) to be able to back track.
                    previousNodesMap.put(nextVertex.getInfo(), currentVertex);

                    // Enqueue nextVertex with the updated currentWeight
                    prioQ.enqueue(nextVertex, currentWeight);
                }
            }
        }


        System.out.println("Back tracking");

        // Skapa ett nytt graph-objekt för Dijkstras-väg, back-track.
        Graph<T> pathGraph = new Graph<>();
        Vertex<T> current = endVertex;

        if (!previousNodesMap.containsKey(current.getInfo()))
        {
            System.out.println("Does not contain!!");
            return pathGraph;
        }

        while (previousNodesMap.containsKey(current.getInfo()))
        {
            Vertex<T> previous = previousNodesMap.get(current.getInfo());
            System.out.println("Backtracking from: " + endVertex.getInfo());
            System.out.println("accumulatedSteps keys: " + previousNodesMap.keySet());

            if (previous == null)
            {
                System.out.println("Previous null == Break !");
                break;
            }

            if (pathGraph.getVertex(previous.getInfo()) == null)
            {
                pathGraph.addVertex(previous);
            }

            if (pathGraph.getVertex(current.getInfo()) == null)
            {
                pathGraph.addVertex(current);
            }

            double weight = 0.0;
            for (Edge<T> edge : graph.getEdges(previous.getInfo()))
            {
                if (edge.getTo().getInfo().equals(current.getInfo()))
                {
                    weight = edge.getWeight();
                    break;
                }
            }

            pathGraph.addEdge(previous, current, weight);
            current = previous;
        }
        System.out.println(" returning pathGraph..?");
        return pathGraph;
    }
}