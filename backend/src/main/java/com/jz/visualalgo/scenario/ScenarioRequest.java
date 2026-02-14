package com.jz.visualalgo.scenario;

import jakarta.validation.constraints.NotBlank;

public record ScenarioRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String dataJson
) {
}
