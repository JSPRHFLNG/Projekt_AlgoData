package model.graph;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class JsonToVertex
{
    private static final String jsonFilePath = "data/svenska-servrar.json";

    public static List<Vertex<String>> readJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(jsonFilePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }

        JsonNode root = mapper.readTree(file);

        List<Vertex<String>> result = new ArrayList<>();
        for (JsonNode node : root) {
            double rawX = node.get("x-sweref99tm").asDouble();
            double rawY = node.get("y-sweref99tm").asDouble();

            double population = node.get("population").asDouble();
            String info = node.get("locality").asText();

            // Store raw SWEREF99TM coordinates directly (no normalization)
            result.add(new Vertex<>(rawX, rawY, population, info));
        }

        return result;
    }
}
