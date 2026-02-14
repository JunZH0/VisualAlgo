package com.jz.visualalgo.scenario;

import java.time.Instant;

public record ScenarioResponse(
        Long id,
        String name,
        String type,
        String dataJson,
        Instant createdAt
) {
}
