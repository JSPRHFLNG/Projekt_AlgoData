package model.dijkstra;

import model.graph.Edge;
import model.graph.Graph;
import model.graph.Vertex;
import model.prioQ.PriorityQueue;

import java.util.HashMap;
import java.util.Map;


public class Dijkstra<T>
{

    public Graph<T> getLowWeightPathGraph(Graph<T> graph, Vertex<T> startVertex, Vertex<T> endVertex)
    {
        // Try catch? graph != null && startVertex != null && endVertex != null
        

        Map<T, Double> accumulatedWeights = new HashMap<>();
        Map<T, Vertex<T>> accumulatedSteps = new HashMap<>();
        PriorityQueue<Vertex<T>, Double> prioQ = new PriorityQueue<>(graph.getAllVertices().size());

        for (Vertex<T> v : graph.getAllVertices())
        {
            accumulatedWeights.put(v.getInfo(), Double.POSITIVE_INFINITY);
        }

        accumulatedWeights.put(startVertex.getInfo(), 0.0);
        prioQ.enqueue(startVertex, 0.0);

        while (!prioQ.isEmpty())
        {
            Vertex<T> currentVertex = prioQ.dequeue();

            if (currentVertex.getInfo().equals(endVertex.getInfo()))
            {
                System.out.println("Break !");
                break;
            }
            System.out.println("Dequeued: " + currentVertex.getInfo());
            System.out.println("Processing edges:");
            for (Edge<T> edge : graph.getEdges(currentVertex.getInfo()))
            {
                System.out.println("  " + edge.getFrom().getInfo() + " -> " + edge.getTo().getInfo() + " (w: " + edge.getWeight() + ")");
                Vertex<T> nextVertex = edge.getTo();
                double compareNext = accumulatedWeights.get(currentVertex.getInfo()) + edge.getWeight();

                if (compareNext < accumulatedWeights.get(nextVertex.getInfo()))
                {
                    accumulatedWeights.put(nextVertex.getInfo(), compareNext);

                    accumulatedSteps.put(nextVertex.getInfo(), currentVertex);

                    // Köa igen med uppdaterad weight.
                    prioQ.enqueue(nextVertex, compareNext);
                    System.out.println("Re queue..");
                }
            }
        }


        System.out.println("Back tracking");

        // Skapa ett nytt graph-objekt för Dijkstras-väg, back-track.
        Graph<T> pathGraph = new Graph<>();
        Vertex<T> current = endVertex;

        if (!accumulatedSteps.containsKey(current.getInfo()))
        {
            System.out.println("Does not contain!!");
            return pathGraph;
        }

        while (accumulatedSteps.containsKey(current.getInfo()))
        {
            Vertex<T> previous = accumulatedSteps.get(current.getInfo());
            System.out.println("Backtracking from: " + endVertex.getInfo());
            System.out.println("accumulatedSteps keys: " + accumulatedSteps.keySet());

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