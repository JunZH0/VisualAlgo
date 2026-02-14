package com.jz.visualalgo.scenario;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    public ScenarioService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public ScenarioResponse create(ScenarioRequest request) {
        Scenario scenario = new Scenario();
        scenario.setName(request.name());
        scenario.setType(request.type());
        scenario.setDataJson(request.dataJson());
        scenario.setCreatedAt(Instant.now());

        Scenario saved = scenarioRepository.save(scenario);
        return toResponse(saved);
    }

    public ScenarioResponse getById(Long id) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found: " + id));
        return toResponse(scenario);
    }

    public List<ScenarioResponse> list() {
        return scenarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ScenarioResponse toResponse(Scenario scenario) {
        return new ScenarioResponse(
                scenario.getId(),
                scenario.getName(),
                scenario.getType(),
                scenario.getDataJson(),
                scenario.getCreatedAt()
        );
    }
}
