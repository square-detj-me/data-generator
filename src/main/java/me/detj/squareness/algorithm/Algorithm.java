package me.detj.squareness.algorithm;

import me.detj.squareness.algorithm.min.FindMinProblem;
import me.detj.squareness.algorithm.min.MinResult;
import me.detj.squareness.algorithm.min.MinViaSampling;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    public static SquarenessResult calculateSquareness(Geometry geometry) {
        geometry = geometry.copy();

        center(geometry);
        geometry.normalize();
        return measureSquareness(geometry);
    }

    public static double calculateAngleOfMostSquareness(Geometry input) {
        Geometry geometry = input.copy();

        center(geometry);
        geometry.normalize();

        MinResult<Double, Double> result = getMinOfIntegration(geometry);
        return result.getInput() - Math.PI / 4;//subtract extra 45 deg rotation because measurement works on diamonds
    }

    private static SquarenessResult measureSquareness(Geometry geometry) {

        double root2Over3 = Math.sqrt(2) / 3;

        double areaPart = Math.pow(geometry.getArea(), 3.0 / 2.0);

        MinResult<Double, Double> result = getMinOfIntegration(geometry);
        double min = result.getOutput();

        return SquarenessResult.builder()
                .squareness(root2Over3 * areaPart / min)
                .rotation(result.getInput() - Math.PI / 4)//subtract extra 45 deg rotation because min input is closest to diamond not square
                .build();
    }

    private static MinResult<Double, Double> getMinOfIntegration(Geometry geometry) {
        int iterations = 300;
        double range = Math.PI / 2;
        double incrementFirst = range / iterations;
        FindMinProblem problemFirst = FindMinProblem.<Double, Double>builder()
                .initialInput(0d)
                .incrementFunction(angle -> angle + incrementFirst)
                .increments(iterations)
                .function(angle -> calculateIntegralPart(getRotation(geometry, angle)))
                .build();
        MinResult<Double, Double> resultFirst = MinViaSampling.minForFunction(problemFirst);

        //return resultFirst;
        double incrementSecond = incrementFirst * 1.5 / iterations;
        FindMinProblem problemSecond = FindMinProblem.<Double, Double>builder()
                .initialInput(resultFirst.getInput() - incrementFirst * 0.75)
                .incrementFunction(angle -> angle + incrementSecond)
                .increments(iterations)
                .function(angle -> calculateIntegralPart(getRotation(geometry, angle)))
                .build();

        return MinViaSampling.minForFunction(problemSecond);
    }

    private static Geometry getRotation(Geometry geometry, double theta) {
        Geometry copy = geometry.copy();
        AffineTransformation rotation = AffineTransformation.rotationInstance(theta);
        copy.apply(rotation);
        return copy;
    }

    private static double calculateIntegralPart(Geometry geometry) {
        Geometry boundingBox = geometry.getEnvelope();

        //minx miny, minx maxy, maxx maxy, maxx miny, minx miny
        Coordinate[] boundingCoords = boundingBox.getCoordinates();

        double minX = boundingCoords[0].getX();
        double maxX = boundingCoords[2].getX();
        double minY = boundingCoords[0].getY();
        double maxY = boundingCoords[2].getY();

        Geometry posX = removeEnvelope(geometry, new Envelope(0, maxX, minY, maxY));
        Geometry negX = removeEnvelope(geometry, new Envelope(minX, 0, minY, maxY));
        Geometry posY = removeEnvelope(geometry, new Envelope(minX, maxX, 0, maxY));
        Geometry negY = removeEnvelope(geometry, new Envelope(minX, maxX, minY, 0));

        double posXAverageX = posX.getCentroid().getX();
        double posXArea = posX.getArea();

        double negXAverageX = negX.getCentroid().getX();
        double negXArea = negX.getArea();

        double posYAverageY = posY.getCentroid().getY();
        double posYArea = posY.getArea();

        double negYAverageY = negY.getCentroid().getY();
        double negYArea = negY.getArea();

        return posXAverageX * posXArea - negXAverageX * negXArea + posYAverageY * posYArea - negYAverageY * negYArea;
    }

    private static Geometry removeEnvelope(Geometry polygon, Envelope envelope) {
        return polygon.intersection(JTS.toGeometry(envelope));
    }

    private static void center(Geometry geometry) {
        Point centroid = geometry.getCentroid();
        AffineTransformation centerOperation = AffineTransformation
                .translationInstance(-centroid.getX(), -centroid.getY());

        geometry.apply(centerOperation);
    }

    private static List<Coordinate> generatePoints(Geometry geometry) {
        Geometry envelope = geometry.getEnvelope();

        //minx miny, minx maxy, maxx maxy, maxx miny, minx miny
        Coordinate[] coordinates = envelope.getCoordinates();


        double minX = coordinates[0].getX();
        double maxX = coordinates[2].getX();
        double minY = coordinates[0].getY();
        double maxY = coordinates[2].getY();

        int samplesPerAxis = 1000;

        double xIncrement = (maxX - minX) / samplesPerAxis;
        double yIncrement = (maxY - minY) / samplesPerAxis;

        IndexedPointInAreaLocator pointInAreaLocator = new IndexedPointInAreaLocator(geometry);
        List<Coordinate> coordinatesInShape = new ArrayList<>();
        for (int i = 0; i < samplesPerAxis; i++) {
            for (int j = 0; j < samplesPerAxis; j++) {
                Coordinate coordinate = new Coordinate(minX + xIncrement * i, minY + yIncrement * j);
                if (pointInAreaLocator.locate(coordinate) == Location.INTERIOR) {
                    coordinatesInShape.add(coordinate);
                }
            }
        }

        return coordinatesInShape;
    }

}
