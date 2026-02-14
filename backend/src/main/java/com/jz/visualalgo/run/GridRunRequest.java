package com.jz.visualalgo.run;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GridRunRequest(
        @NotBlank String algorithm,
        @Min(1) int rows,
        @Min(1) int cols,
        @NotNull Position start,
        @NotNull Position target,
        @NotNull List<Position> walls
) {
}
