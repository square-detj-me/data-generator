package me.detj.squareness;

import me.detj.squareness.algorithm.Algorithm;
import me.detj.squareness.algorithm.Country;
import me.detj.squareness.algorithm.CountrySquareness;
import org.geotools.geometry.jts.JTS;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Ignore("Not really unit tests! Just used for manual testing")
public class ReaderTest {

    @Test
    public void testRead() throws IOException {
        List<Country> read = Reader.read();

        List<CountrySquareness> collect = read.stream()
                .parallel()
                .map(country -> {
                    CountrySquareness sq = new CountrySquareness(country, Algorithm.calculateSquareness(country.getPolygon()).getSquareness());
                    System.out.printf("%s (%s): %s%n", sq.getCountry().getName(), sq.getCountry().getCode(), sq.getSquareness());
                    return sq;
                })
                .sorted(Comparator.comparing(country -> country.getSquareness()))
                .collect(Collectors.toList());

        System.out.println("#### RESULTS ####");
        for (CountrySquareness sq : collect) {
            System.out.printf("%s (%s): %s%n", sq.getCountry().getName(), sq.getCountry().getCode(), sq.getSquareness());
        }

    }

    @Test
    public void testSquare() throws IOException {
        Polygon polygon = JTS.toGeometry(new Envelope(-1, 1, -1, 1));
        double squareness = Algorithm.calculateSquareness(polygon).getSquareness();

        System.out.println("#### RESULTS ####");
        System.out.printf("%s%n", squareness);

    }

    @Test
    public void reef() throws IOException {
        Country country = Reader.read()
                .stream()
                .filter(c -> c.getName().equals("Scarborough Reef"))
                .findAny()
                .get();

        int i = 0;
    }
}