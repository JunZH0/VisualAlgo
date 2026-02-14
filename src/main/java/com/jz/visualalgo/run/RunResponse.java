package com.jz.visualalgo.run;

import java.util.List;

public record RunResponse(
        String algorithm,
        int totalSteps,
        boolean found,
        List<RunStep> steps
) {
}
