package br.com.nextplugins.nextautosell.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Multiplier {

    private final String id, name, displayName;

    private final double value;

}
