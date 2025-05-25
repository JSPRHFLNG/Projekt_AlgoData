package timecomplexity;

import model.delaunay.Delaunay;
import model.dijkstra.Dijkstra;
import model.graph.Graph;
import model.graph.JsonToVertex;
import model.graph.Vertex;
import model.mst.MST;
import model.quadtree.Quadtree;
import view.MapCoordinateConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TestTimeComplexity
{

    private static final int testRepeater = 10000;
    private final static String csvFile = "data/time_complexity_results.csv";

    public static void main(String[] args) throws Exception
    {
        List<Vertex<String>> allVertices = JsonToVertex.readJson(true);
        TestGraphSizeN<String> testGraph = new TestGraphSizeN<>(allVertices);
        int[] sizes = {50, 100, 200, 400, 800, 1600, 3200, 6400};

        testGraph.prepareGraphs(sizes);

        try (FileWriter csvWriter = new FileWriter(csvFile))
        {
            // CSV.
            csvWriter.append("Size,Delaunay,Dijkstra,QuadtreeBuild,MST\n");

            // Print.
            System.out.printf("%-6s | %-10s | %-10s | %-17s | %-10s%n",
                    "Size", "Delaunay", "Dijkstra", "Quadtree (build)", "MST");
            System.out.println("  --------------------    Nanoseconds    --------------------");

            for (int size : sizes)
            {
                Graph<String> originalGraph = testGraph.getGraph(size);
                if (originalGraph == null) continue;

                Vertex<String> start = originalGraph.getAllVertices().getFirst();
                Vertex<String> end = originalGraph.getAllVertices().getLast();


                long delaunayTime = 0;
                Graph<String> triangulatedGraph = null;

                for (int i = 0; i < testRepeater; i++)
                {
                    Delaunay<String> dt = new Delaunay<>();
                    long startTime = System.nanoTime();
                    Graph<String> temp = dt.triangulate(originalGraph);
                    long endTime = System.nanoTime();
                    delaunayTime += (endTime - startTime);
                    if (triangulatedGraph == null)
                    {
                        triangulatedGraph = temp;
                    }
                }
                delaunayTime /= testRepeater;


                long dijkstraTime = measureDijkstra(triangulatedGraph, start, end);
                long quadtreeBuildTime = measureQuadtreeBuild(triangulatedGraph);
                long mstTime = measureMST(triangulatedGraph, start);

                // Print.
                System.out.printf("%-9d | %-9d | %-9d | %-17d | %-10d%n",
                        size, delaunayTime, dijkstraTime, quadtreeBuildTime, mstTime);

                // CSV.
                csvWriter.append(String.format("%d,%d,%d,%d,%d\n",
                        size, delaunayTime, dijkstraTime, quadtreeBuildTime, mstTime));
            }

        } catch (IOException e) {
            System.err.println("Write error CSV file: " + e.getMessage());
        }
    }


    private static long measureDijkstra(Graph<String> graph, Vertex<String> start, Vertex<String> end)
    {
        long total = 0;
        for (int i = 0; i < testRepeater; i++)
        {
            Dijkstra<String> dijkstra = new Dijkstra<>();
            long startTime = System.nanoTime();
            dijkstra.getLowWeightPathGraph(graph, start, end);
            long endTime = System.nanoTime();
            total += (endTime - startTime);
        }
        return total / testRepeater;
    }


    private static long measureQuadtreeBuild(Graph<String> graph)
    {
        long total = 0;
        for (int i = 0; i < testRepeater; i++)
        {
            Quadtree<String> qt = new Quadtree<>(MapCoordinateConfig.getDefaultBoundary());
            long startTime = System.nanoTime();
            for (Vertex<String> v : graph.getAllVertices())
            {
                qt.insert(v);
            }
            long endTime = System.nanoTime();
            total += (endTime - startTime);
        }
        return total / testRepeater;
    }


    private static long measureMST(Graph<String> graph, Vertex<String> root)
    {
        long total = 0;
        for (int i = 0; i < testRepeater; i++)
        {
            MST<String> mst = new MST<>();
            long startTime = System.nanoTime();
            mst.createMST(graph, root);
            long endTime = System.nanoTime();
            total += (endTime - startTime);
        }
        return total / testRepeater;
    }

}
