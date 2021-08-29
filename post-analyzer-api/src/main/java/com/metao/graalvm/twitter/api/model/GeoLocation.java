package com.metao.graalvm.twitter.api.model;

import java.io.Serial;
import java.io.Serializable;

public record GeoLocation(double latitude, double longitude) implements Serializable {
    @Serial
    private static final long serialVersionUID = 123827837123912L;
}
