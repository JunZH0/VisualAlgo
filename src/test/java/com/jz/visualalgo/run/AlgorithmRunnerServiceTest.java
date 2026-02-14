package com.jz.visualalgo.run;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class AlgorithmRunnerServiceTest {

    private final AlgorithmRunnerService service = new AlgorithmRunnerService();

    @Test
    void bfsShouldFindPathWhenReachable() {
        GridRunRequest request = new GridRunRequest(
                "BFS",
                3,
                3,
                new Position(0, 0),
                new Position(2, 2),
                List.of(new Position(1, 1))
        );

        RunResponse response = service.run(request);

        assertThat(response.found()).isTrue();
        assertThat(response.totalSteps()).isGreaterThan(0);
        assertThat(response.steps().getLast().current()).isEqualTo(new Position(2, 2));
    }

    @Test
    void dfsShouldFindPathWhenReachable() {
        GridRunRequest request = new GridRunRequest(
                "DFS",
                3,
                3,
                new Position(0, 0),
                new Position(2, 2),
                List.of()
        );

        RunResponse response = service.run(request);

        assertThat(response.algorithm()).isEqualTo("DFS");
        assertThat(response.found()).isTrue();
        assertThat(response.totalSteps()).isGreaterThan(0);
    }

    @Test
    void dijkstraShouldFindPathWhenReachable() {
        GridRunRequest request = new GridRunRequest(
                "DIJKSTRA",
                4,
                4,
                new Position(0, 0),
                new Position(3, 3),
                List.of(new Position(1, 1), new Position(2, 1))
        );

        RunResponse response = service.run(request);

        assertThat(response.algorithm()).isEqualTo("DIJKSTRA");
        assertThat(response.found()).isTrue();
        assertThat(response.totalSteps()).isGreaterThan(0);
    }

    @Test
    void runShouldFailForUnsupportedAlgorithm() {
        GridRunRequest request = new GridRunRequest(
                "A_STAR",
                3,
                3,
                new Position(0, 0),
                new Position(2, 2),
                List.of()
        );

        assertThatThrownBy(() -> service.run(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported algorithm");
    }
}
