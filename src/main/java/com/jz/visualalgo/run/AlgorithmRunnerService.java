package com.jz.visualalgo.run;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmRunnerService {

    public RunResponse run(GridRunRequest request) {
        String algorithm = normalizeAlgorithm(request.algorithm());
        validatePosition(request.start(), request.rows(), request.cols(), "start");
        validatePosition(request.target(), request.rows(), request.cols(), "target");

        Set<Position> walls = new HashSet<>(request.walls());
        if (walls.contains(request.start()) || walls.contains(request.target())) {
            throw new IllegalArgumentException("Start and target must not be walls.");
        }

        return switch (algorithm) {
            case "BFS" -> runBfs(request, walls);
            case "DFS" -> runDfs(request, walls);
            case "DIJKSTRA" -> runDijkstra(request, walls);
            default -> throw new IllegalArgumentException(
                    "Unsupported algorithm: " + request.algorithm() + ". Supported: BFS, DFS, DIJKSTRA");
        };
    }

    private RunResponse runBfs(GridRunRequest request, Set<Position> walls) {
        Queue<Position> frontier = new ArrayDeque<>();
        Set<Position> visited = new LinkedHashSet<>();
        List<RunStep> steps = new ArrayList<>();

        frontier.add(request.start());
        visited.add(request.start());

        boolean found = false;
        int index = 0;
        while (!frontier.isEmpty()) {
            Position current = frontier.poll();
            if (current.equals(request.target())) {
                found = true;
            }

            List<Position> nextFrontier = new ArrayList<>();
            for (Position neighbor : neighbors(current, request.rows(), request.cols())) {
                if (walls.contains(neighbor) || visited.contains(neighbor)) {
                    continue;
                }
                visited.add(neighbor);
                frontier.add(neighbor);
                nextFrontier.add(neighbor);
            }

            steps.add(new RunStep(
                    index++,
                    current,
                    new ArrayList<>(visited),
                    new ArrayList<>(frontier),
                    found || frontier.isEmpty(),
                    found
            ));

            if (found) {
                break;
            }
        }

        return new RunResponse("BFS", steps.size(), found, steps);
    }

    private RunResponse runDfs(GridRunRequest request, Set<Position> walls) {
        ArrayDeque<Position> frontier = new ArrayDeque<>();
        Set<Position> discovered = new HashSet<>();
        Set<Position> visited = new LinkedHashSet<>();
        Set<Position> frontierSet = new LinkedHashSet<>();
        List<RunStep> steps = new ArrayList<>();

        frontier.push(request.start());
        frontierSet.add(request.start());
        discovered.add(request.start());

        boolean found = false;
        int index = 0;
        while (!frontier.isEmpty()) {
            Position current = frontier.pop();
            frontierSet.remove(current);
            visited.add(current);
            if (current.equals(request.target())) {
                found = true;
            }

            List<Position> neighbors = neighbors(current, request.rows(), request.cols());
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                Position neighbor = neighbors.get(i);
                if (walls.contains(neighbor) || discovered.contains(neighbor)) {
                    continue;
                }
                discovered.add(neighbor);
                frontier.push(neighbor);
                frontierSet.add(neighbor);
            }

            steps.add(new RunStep(
                    index++,
                    current,
                    new ArrayList<>(visited),
                    new ArrayList<>(frontierSet),
                    found || frontier.isEmpty(),
                    found
            ));

            if (found) {
                break;
            }
        }

        return new RunResponse("DFS", steps.size(), found, steps);
    }

    private RunResponse runDijkstra(GridRunRequest request, Set<Position> walls) {
        PriorityQueue<NodeCost> frontier = new PriorityQueue<>((a, b) -> {
            int byCost = Integer.compare(a.cost(), b.cost());
            if (byCost != 0) {
                return byCost;
            }
            int byRow = Integer.compare(a.position().row(), b.position().row());
            if (byRow != 0) {
                return byRow;
            }
            return Integer.compare(a.position().col(), b.position().col());
        });

        Set<Position> visited = new LinkedHashSet<>();
        Set<Position> frontierSet = new LinkedHashSet<>();
        LinkedHashMap<Position, Integer> distance = new LinkedHashMap<>();
        List<RunStep> steps = new ArrayList<>();

        frontier.add(new NodeCost(request.start(), 0));
        frontierSet.add(request.start());
        distance.put(request.start(), 0);

        boolean found = false;
        int index = 0;
        while (!frontier.isEmpty()) {
            NodeCost currentNode = frontier.poll();
            Position current = currentNode.position();

            if (visited.contains(current)) {
                continue;
            }

            frontierSet.remove(current);
            visited.add(current);
            if (current.equals(request.target())) {
                found = true;
            }

            for (Position neighbor : neighbors(current, request.rows(), request.cols())) {
                if (walls.contains(neighbor) || visited.contains(neighbor)) {
                    continue;
                }

                int nextCost = currentNode.cost() + 1;
                int knownCost = distance.getOrDefault(neighbor, Integer.MAX_VALUE);
                if (nextCost >= knownCost) {
                    continue;
                }

                distance.put(neighbor, nextCost);
                frontier.add(new NodeCost(neighbor, nextCost));
                frontierSet.add(neighbor);
            }

            steps.add(new RunStep(
                    index++,
                    current,
                    new ArrayList<>(visited),
                    new ArrayList<>(frontierSet),
                    found || frontier.isEmpty(),
                    found
            ));

            if (found) {
                break;
            }
        }

        return new RunResponse("DIJKSTRA", steps.size(), found, steps);
    }

    private List<Position> neighbors(Position current, int rows, int cols) {
        List<Position> positions = List.of(
                new Position(current.row() - 1, current.col()),
                new Position(current.row() + 1, current.col()),
                new Position(current.row(), current.col() - 1),
                new Position(current.row(), current.col() + 1)
        );

        List<Position> inBounds = new ArrayList<>(4);
        for (Position position : positions) {
            if (position.row() >= 0 && position.row() < rows && position.col() >= 0 && position.col() < cols) {
                inBounds.add(position);
            }
        }
        return inBounds;
    }

    private void validatePosition(Position position, int rows, int cols, String label) {
        Objects.requireNonNull(position, label + " must be provided");
        if (position.row() < 0 || position.row() >= rows || position.col() < 0 || position.col() >= cols) {
            throw new IllegalArgumentException(label + " is out of bounds: " + position);
        }
    }

    private String normalizeAlgorithm(String algorithm) {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must be provided.");
        }
        return algorithm.trim().toUpperCase();
    }

    private record NodeCost(Position position, int cost) {
    }
}
