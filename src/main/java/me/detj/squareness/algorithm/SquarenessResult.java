package me.detj.squareness.algorithm;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SquarenessResult {

    private final double squareness;
    private final double rotation;
}
