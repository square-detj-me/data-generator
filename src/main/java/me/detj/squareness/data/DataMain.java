package me.detj.squareness.data;

import me.detj.squareness.GeometryUtil;
import me.detj.squareness.Reader;
import me.detj.squareness.algorithm.Algorithm;
import me.detj.squareness.algorithm.Country;
import me.detj.squareness.algorithm.SquarenessResult;
import me.detj.squareness.gui.GeometryPainter;
import org.geotools.geometry.jts.JTS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataMain {

    private static final String ACTUAL_OUTPUT_DIRECTORY = "target/output/";
    private static final String ACTUAL_IMAGE_DIRECTORY = ACTUAL_OUTPUT_DIRECTORY + "images/";
    private static final String JSON_IMAGE_DIRECTORY = "images/";

    private static final int MAX_IMAGE_WIDTH = 1000;
    private static final int MAX_IMAGE_HEIGHT = 1000;

    //Countries where the polygons are too low quality for the website
    private static final Set<String> BAD_DATA_COUNTRIES = Set.of("Scarborough Reef", "Nauru", "Baykonur Cosmodrome",
            "Bajo Nuevo Bank (Petrel Is.)", "Clipperton Island", "Saint Barthelemy", "Serranilla Bank",
            "Coral Sea Island", "Sint Maarten");

    public static void main(String[] args) throws IOException {
        List<Country> countries = new ArrayList<>(Reader.read());

        //add fake testing countries
        Geometry square = JTS.toGeometry(new Envelope(0, 1, 0, 1));
        countries.add(Country.builder()
                .name("Squaristan")
                .code("SQR")
                .polygon(square)
                .build());

        Geometry diamond = square.copy();
        diamond.apply(AffineTransformation.rotationInstance(Math.PI / 4));//rotate 45 degrees
        countries.add(Country.builder()
                .name("Diamondistan")
                .code("DIA")
                .polygon(diamond)
                .build()
        );

        JSONObject outputJson = new JSONObject();
        JSONArray countriesJsonArray = new JSONArray();
        outputJson.put("countries", countriesJsonArray);

        countries.stream()
                .parallel()
                .filter(country -> !BAD_DATA_COUNTRIES.contains(country.getName()))
                .forEach(country -> {
                    System.out.println("Processing " + country.getName());

                    String actualImageName = sanitize(country.getName()) + ".png";
                    String actualImageLocation = ACTUAL_IMAGE_DIRECTORY + actualImageName;
                    GeometryPainter.renderImageToFile(country.getPolygon(), MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, actualImageLocation);

                    SquarenessResult squarenessResult = Algorithm.calculateSquareness(country.getPolygon());

                    String rotatedImageName = sanitize(country.getName()) + "_rotated.png";
                    String rotatedImageLocation = ACTUAL_IMAGE_DIRECTORY + rotatedImageName;
                    GeometryPainter.renderImageToFile(GeometryUtil.rotate(country.getPolygon(), squarenessResult.getRotation()), MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, rotatedImageLocation);

                    JSONObject countryJson = new JSONObject();
                    countryJson.put("name", country.getName());
                    countryJson.put("code", country.getCode());
                    countryJson.put("squareness", squarenessResult.getSquareness());
                    countryJson.put("imageLocation", JSON_IMAGE_DIRECTORY + actualImageName);
                    countryJson.put("rotatedImageLocation", JSON_IMAGE_DIRECTORY + rotatedImageName);
                    countriesJsonArray.add(countryJson);
                });

        String asJson = outputJson.toJSONString();
        byte[] bytes = asJson.getBytes(Charset.forName("UTF-8"));
        Files.createDirectories(Path.of(ACTUAL_OUTPUT_DIRECTORY));
        Files.write(Path.of(ACTUAL_OUTPUT_DIRECTORY, "countries.json"), bytes);

    }

    private static String sanitize(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}
