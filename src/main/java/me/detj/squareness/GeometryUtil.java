package me.detj.squareness;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.awt.Rectangle;

public class GeometryUtil {

    public static final BoundingBox getBounds(Geometry geometry) {
        Geometry envelope = geometry.getEnvelope();

        //minx miny, minx maxy, maxx maxy, maxx miny, minx miny
        Coordinate[] coordinates = envelope.getCoordinates();

        double minX = coordinates[0].getX();
        double maxX = coordinates[2].getX();
        double minY = coordinates[0].getY();
        double maxY = coordinates[2].getY();

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    public static Geometry rotate(Geometry geometry, double theta) {
        Geometry copy = geometry.copy();
        AffineTransformation rotation = AffineTransformation.rotationInstance(theta);
        copy.apply(rotation);
        return copy;
    }
}
