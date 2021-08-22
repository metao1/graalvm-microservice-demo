package com.metao.graalvm.twitter.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public record GeoLocation(double latitude, double longitude) implements Serializable {
    @Serial
    private static final long serialVersionUID = 123827837123912L;
}
