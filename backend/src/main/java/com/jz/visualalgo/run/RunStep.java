package com.jz.visualalgo.run;

import java.util.List;

public record RunStep(
        int index,
        Position current,
        List<Position> visited,
        List<Position> frontier,
        boolean finished,
        boolean found
) {
}
