package me.detj.squareness.algorithm;

import lombok.Builder;
import lombok.Value;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;

@Value
@Builder
public class Country {

    private final String name;
    private final String code;
    private final Geometry polygon;

    public String getNameWithCode() {
        return String.format("%s (%s)", name, code);
    }
}
