package me.detj.squareness.gui;

import lombok.SneakyThrows;
import me.detj.squareness.BoundingBox;
import me.detj.squareness.GeometryUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GeometryPainter {

    private final static int PADDING = 5;

    @SneakyThrows
    public static void renderImageToFile(Geometry geometry, int maxWidth, int maxHeight, String file) {
        BufferedImage image = createImage(geometry, maxWidth, maxHeight);
        ImageIO.write(image, "png", new File(file));
    }

    private static BufferedImage createImage(Geometry geometry, int maxWidth, int maxHeight) {
        Geometry backup = geometry;
        geometry = geometry.copy();

        //rescale to max size including padding
        rescale(geometry, maxWidth - PADDING * 2, maxHeight - PADDING * 2);

        //move to middle, accounting for padding
        moveTo(geometry, PADDING, PADDING);

        //create image
        BoundingBox boundingBox = GeometryUtil.getBounds(geometry);
        BufferedImage image = new BufferedImage((int) boundingBox.getWidth() + PADDING * 2, (int) boundingBox.getHeight() + PADDING * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setClip(0, 0, image.getWidth(), image.getHeight());

        //draw
        GeometryPainter.drawPolygon(g, geometry);

        return image;
    }

    public static void drawPolygon(Graphics g, Geometry geometry) {
        if (geometry == null) {
            return;
        }
        Graphics2D graphics = (Graphics2D) g;

        //paint shape
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(3));
        drawGeometry(graphics, geometry, graphics.getClipBounds());
    }

    private static void rescale(Geometry geometry, int maxWidth, int maxHeight) {
        //minx miny, minx maxy, maxx maxy, maxx miny, minx miny
        BoundingBox geometryBounds = GeometryUtil.getBounds(geometry);

        double width = geometryBounds.getWidth();
        double widthScale = maxWidth / width;

        double height = geometryBounds.getHeight();
        double heightScale = maxHeight / height;

        double scale = Math.min(widthScale, heightScale);
        geometry.apply(AffineTransformation.scaleInstance(scale, scale));
    }

    private static void moveTo(Geometry geometry, double x, double y) {
        //minx miny, minx maxy, maxx maxy, maxx miny, minx miny
        BoundingBox boundsOfGeometry = GeometryUtil.getBounds(geometry);

        geometry.apply(AffineTransformation.translationInstance(
               x - boundsOfGeometry.getX(), y - boundsOfGeometry.getY()));
    }


    private static void drawGeometry(Graphics2D graphics, Geometry geometry, Rectangle bounds) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry geometryN = geometry.getGeometryN(i);
            if (geometryN instanceof org.locationtech.jts.geom.Polygon) {
                org.locationtech.jts.geom.Polygon polygon = (org.locationtech.jts.geom.Polygon) geometryN;
                drawPolygon(graphics, polygon, bounds);
            }
        }
    }

    private static void drawPolygon(Graphics2D graphics, Polygon polygon, Rectangle bounds) {
        Coordinate[] coordinates = polygon.getCoordinates();
        for (int i = 0; i < coordinates.length - 1; i++) {
            Coordinate c1 = coordinates[i];
            Coordinate c2 = coordinates[i + 1];
            drawLine(graphics, c1, c2, bounds);
        }
    }

    private static void drawLine(Graphics2D graphics, Coordinate c1, Coordinate c2, Rectangle bounds) {
        c1 = convertCoordToSwing(c1, bounds);
        c2 = convertCoordToSwing(c2, bounds);
        graphics.drawLine(
                (int) c1.getX(),
                (int) c1.getY(),
                (int) c2.getX(),
                (int) c2.getY());
    }

    private static Coordinate convertCoordToSwing(Coordinate coordinate, Rectangle bounds) {
        //flip the x axis
        double newY = bounds.getHeight()  - coordinate.getY();
        return new CoordinateXY(coordinate.getX(), newY);
    }
}
