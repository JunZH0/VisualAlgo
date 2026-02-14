package com.jz.visualalgo.run;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final AlgorithmRunnerService algorithmRunnerService;

    public RunController(AlgorithmRunnerService algorithmRunnerService) {
        this.algorithmRunnerService = algorithmRunnerService;
    }

    @PostMapping
    public RunResponse run(@Valid @RequestBody GridRunRequest request) {
        return algorithmRunnerService.run(request);
    }
}
