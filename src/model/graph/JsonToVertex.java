package model.graph;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class JsonToVertex {
    private static final String jsonFilePath = "data/svenska-servrar.json";
    private static final String jsonFilePathTestData = "data/mockup-data-10000-unique.json";

    public static List<Vertex<String>> readJson(boolean test) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file;
        if (test) {
            file = new File(jsonFilePathTestData);
        } else {
            file = new File(jsonFilePath);
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        JsonNode root = mapper.readTree(file);

        List<Vertex<String>> result = new ArrayList<>();
        for (JsonNode node : root) {

            double rawX = node.get("x-sweref99tm").asDouble();
            double rawY = node.get("y-sweref99tm").asDouble();

            double bandwidth = node.get("bandwidth").asDouble();
            String info = node.get("locality").asText();

            // Raw unformatted SWEREF99TM coordinates.
            result.add(new Vertex<>(rawX, rawY, bandwidth, info));
        }
        return result;
    }
}
