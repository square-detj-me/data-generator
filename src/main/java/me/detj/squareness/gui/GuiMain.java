package me.detj.squareness.gui;

import me.detj.squareness.algorithm.Country;
import me.detj.squareness.Reader;
import org.geotools.geometry.jts.JTS;
import org.locationtech.jts.geom.Envelope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiMain {

    public static void main(String[] args) throws IOException {
        List<Country> countries = new ArrayList<>(Reader.read());

        countries.add(Country.builder()
                .name("Squareistan")
                .code("SIT")
                .polygon(JTS.toGeometry(new Envelope(0, 1, 0, 1)))
                .build()
        );

        new RenderFrame(
                countries
        );
    }
}
