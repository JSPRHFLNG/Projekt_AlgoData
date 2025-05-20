package model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class ReadFromJSON {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VertexDTO {
        public String locality;
        public String municipality;
        @com.fasterxml.jackson.annotation.JsonProperty("lat")
        public double lat;
        @com.fasterxml.jackson.annotation.JsonProperty("lon")
        public double lon;
    }

    public static List<Vertex<String>> readData(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<VertexDTO> dtoList = mapper.readValue(
                    new File(filePath),
                    new TypeReference<>() {}
            );

            return dtoList.stream()
                    .filter(dto -> dto.municipality != null && dto.municipality.contains("Gävle"))
                    .map(dto -> new Vertex<>(dto.lon, dto.lat, dto.locality))
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void main(String[] args) {
        List<Vertex<String>> vertexList = readData("data/svenska-orter.json");
        for (Vertex<String> v : vertexList) {
            System.out.println("Place: " + v.getInfo() + " | X-Koordinater: " + v.getX() + "| Y-koordinater: " + v.getY());
        }
    }
}
