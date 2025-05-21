package model.delaunay;

import model.graph.Graph;
import model.graph.Vertex;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Delaunay<T>
{

    public Graph<T> triangulate(Graph<T> graph)
    {
        Graph<T> triangulatedGraph = new Graph<>();

        GeometryFactory geomFactory = new GeometryFactory();
        DelaunayTriangulationBuilder triangulator = new DelaunayTriangulationBuilder();

        List<Coordinate> coords = new ArrayList<>();
        Map<String, Vertex<T>> coordToVertex = new HashMap<>();

        for (Vertex<T> v : graph.getAllVertices())
        {
            Coordinate coord = new Coordinate(v.getX(), v.getY());
            coords.add(coord);

            String key = coord.x + "," + coord.y;
            coordToVertex.put(key, v);
        }

        triangulator.setSites(coords);
        GeometryCollection edgeLines = (GeometryCollection) triangulator.getEdges(geomFactory);

        for (int i = 0; i < edgeLines.getNumGeometries(); i++) {
            LineString line = (LineString) edgeLines.getGeometryN(i);
            Coordinate[] points = line.getCoordinates();

            String key1 = points[0].x + "," + points[0].y;
            String key2 = points[1].x + "," + points[1].y;

            Vertex<T> from = coordToVertex.get(key1);
            Vertex<T> to = coordToVertex.get(key2);

            if (from != null && to != null) {
                triangulatedGraph.addVertex(from);
                triangulatedGraph.addVertex(to);
                triangulatedGraph.addEdge(from.getInfo(), to.getInfo());
            }
        }
        return triangulatedGraph;
    }
}
