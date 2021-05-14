package me.detj.squareness.gui;

import org.locationtech.jts.geom.Geometry;

import javax.swing.*;
import java.awt.*;

public class RenderPanel extends JPanel {

    private Geometry geometry;

    public RenderPanel() {
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;

        //draw background
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, 1 + (int) graphics.getClipBounds().getWidth(), 1 + (int) graphics.getClipBounds().getHeight());

        //draw polygon
        GeometryPainter.drawPolygon(g, geometry);
    }


}
