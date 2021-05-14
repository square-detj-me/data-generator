package me.detj.squareness;

import me.detj.squareness.algorithm.Country;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reader {

    public static List<Country> read() throws IOException {
        String file = "C:\\Users\\micro\\Downloads\\geo-countries_zip\\archive\\countries.geojson";

        FeatureJSON fJson = new FeatureJSON();

        FeatureCollection featureCollection = fJson.readFeatureCollection(new FileInputStream(file));

        List<Feature> features = extractFeatures(featureCollection);

        return features.stream()
                .map(Reader::fromFeature)
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<Feature> extractFeatures(FeatureCollection featureCollection) {
        List<Feature> features = new ArrayList<>();
        FeatureIterator iterator = featureCollection.features();
        while (iterator.hasNext()) {
            features.add(iterator.next());
        }
        return features;
    }

    private static Country fromFeature(Feature feature) {
        return Country.builder()
                .name((String) feature.getProperty("ADMIN").getValue())
                .code((String) feature.getProperty("ISO_A3").getValue())
                .polygon((Geometry) feature.getProperty("geometry").getValue())
                .build();
    }
}
