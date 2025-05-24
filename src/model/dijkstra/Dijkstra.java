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

            // Break condition. If the currentVertex is the endVertex the algorithm is done.
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

        // Debug print outs. -------------------------
        System.out.println("  Back tracking");
        System.out.println("accumulatedSteps keys: " + previousNodesMap.keySet());
        System.out.println("Backtracking from: " + endVertex.getInfo());
        for (Map.Entry<T, Vertex<T>> entry : previousNodesMap.entrySet())
        {
            T key = entry.getKey();
            Vertex<T> value = entry.getValue();
            System.out.println(key + " : " + value);
        }
        // --------------------------------------------

        Graph<T> pathGraph = new Graph<>();
        Vertex<T> currentVertex = endVertex;

        if (!previousNodesMap.containsKey(currentVertex.getInfo()))
        {
            System.out.println("End vertex is unreachable or does not exist.");
            return graph;
        }

        while (previousNodesMap.containsKey(currentVertex.getInfo()))
        {
            // Get the endVertex previous vertex, (its key (current): value (previous).
            Vertex<T> previous = previousNodesMap.get(currentVertex.getInfo());

            // Break condition. Loop completed when start vertex is reached because it has no previous vertex.
            if (previous == null){break;}

            // If pathGraph does not yet contain previous vertex, add it.
            if (pathGraph.getVertex(previous.getInfo()) == null)
            {
                pathGraph.addVertex(previous);
            }
            // If pathGraph does not yet contain currentVertex, add it.
            if (pathGraph.getVertex(currentVertex.getInfo()) == null)
            {
                pathGraph.addVertex(currentVertex);
            }

            // Extract edge paths from original Graph object.
            double weight = 0.0;
            for (Edge<T> edge : graph.getEdges(previous.getInfo()))
            {
                // Identify the edge that goes to the currentVertex to extract the weight value.
                if (edge.getTo().getInfo().equals(currentVertex.getInfo()))
                {
                    weight = edge.getWeight();
                    break;
                }
            }
            // Add the edge to the pathGraph.
            pathGraph.addEdge(previous, currentVertex, weight);

            // Swap and keep tracking the path next step.
            currentVertex = previous;
        }
        return pathGraph;
    }
}