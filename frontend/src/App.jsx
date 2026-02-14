import { useEffect, useMemo, useState } from 'react';

const DEFAULT_ROWS = 14;
const DEFAULT_COLS = 28;

const keyOf = (row, col) => `${row},${col}`;

const parseKey = (value) => {
  const [row, col] = value.split(',').map(Number);
  return { row, col };
};

export default function App() {
  const [algorithm, setAlgorithm] = useState('BFS');
  const [rows, setRows] = useState(DEFAULT_ROWS);
  const [cols, setCols] = useState(DEFAULT_COLS);
  const [start, setStart] = useState({ row: 2, col: 2 });
  const [target, setTarget] = useState({ row: 11, col: 24 });
  const [walls, setWalls] = useState(() => new Set());

  const [steps, setSteps] = useState([]);
  const [stepIndex, setStepIndex] = useState(0);
  const [playing, setPlaying] = useState(false);
  const [speedMs, setSpeedMs] = useState(140);
  const [tool, setTool] = useState('wall');
  const [status, setStatus] = useState('Draw walls, choose an algorithm, then run.');
  const [loading, setLoading] = useState(false);

  const currentStep = steps[stepIndex] ?? null;
  const visited = useMemo(
    () => new Set((currentStep?.visited ?? []).map((p) => keyOf(p.row, p.col))),
    [currentStep]
  );
  const frontier = useMemo(
    () => new Set((currentStep?.frontier ?? []).map((p) => keyOf(p.row, p.col))),
    [currentStep]
  );

  useEffect(() => {
    if (!playing || steps.length === 0) {
      return undefined;
    }
    const id = setInterval(() => {
      setStepIndex((prev) => {
        if (prev >= steps.length - 1) {
          setPlaying(false);
          return prev;
        }
        return prev + 1;
      });
    }, speedMs);

    return () => clearInterval(id);
  }, [playing, speedMs, steps]);

  useEffect(() => {
    if (!currentStep) {
      return;
    }

    if (currentStep.found && currentStep.finished) {
      setStatus(`Target found in ${currentStep.index + 1} steps.`);
      return;
    }

    if (!currentStep.found && currentStep.finished) {
      setStatus('No path to target with current walls.');
      return;
    }

    setStatus(`Animating step ${currentStep.index + 1}/${steps.length}`);
  }, [currentStep, steps.length]);

  const onCellClick = (row, col) => {
    const key = keyOf(row, col);

    if (tool === 'start') {
      if (key !== keyOf(target.row, target.col) && !walls.has(key)) {
        setStart({ row, col });
      }
      return;
    }

    if (tool === 'target') {
      if (key !== keyOf(start.row, start.col) && !walls.has(key)) {
        setTarget({ row, col });
      }
      return;
    }

    setWalls((prev) => {
      const next = new Set(prev);
      const startKey = keyOf(start.row, start.col);
      const targetKey = keyOf(target.row, target.col);

      if (key === startKey || key === targetKey) {
        return next;
      }

      if (tool === 'erase') {
        next.delete(key);
      } else {
        if (next.has(key)) {
          next.delete(key);
        } else {
          next.add(key);
        }
      }
      return next;
    });
  };

  const clearTrace = () => {
    setPlaying(false);
    setStepIndex(0);
    setSteps([]);
    setStatus('Trace cleared. Ready to run.');
  };

  const clearWalls = () => {
    setWalls(new Set());
    clearTrace();
  };

  const randomWalls = () => {
    const nextWalls = new Set();
    for (let r = 0; r < rows; r += 1) {
      for (let c = 0; c < cols; c += 1) {
        if (Math.random() < 0.22) {
          nextWalls.add(keyOf(r, c));
        }
      }
    }
    nextWalls.delete(keyOf(start.row, start.col));
    nextWalls.delete(keyOf(target.row, target.col));
    setWalls(nextWalls);
    clearTrace();
    setStatus('Random walls generated.');
  };

  const runAlgorithm = async () => {
    setLoading(true);
    setPlaying(false);
    setStatus(`Running ${algorithm} on backend...`);

    try {
      const payload = {
        algorithm,
        rows,
        cols,
        start,
        target,
        walls: Array.from(walls).map(parseKey)
      };

      const response = await fetch('/api/runs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Run failed.');
      }

      const data = await response.json();
      setSteps(data.steps ?? []);
      setStepIndex(0);
      setPlaying(true);
      setStatus(`${data.algorithm} run loaded with ${data.totalSteps} steps.`);
    } catch (error) {
      setStatus(`Error: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const gridStyle = {
    gridTemplateColumns: `repeat(${cols}, minmax(0, 1fr))`
  };

  return (
    <div className="page">
      <header className="topbar">
        <div>
          <h1>VisualAlgo Lab</h1>
          <p>React + Spring Boot pathfinding visualizer starter</p>
        </div>
        <div className="status">{status}</div>
      </header>

      <section className="controls">
        <div className="group">
          <button onClick={runAlgorithm} disabled={loading}>
            {loading ? 'Running...' : `Run ${algorithm}`}
          </button>
          <button onClick={() => setPlaying((p) => !p)} disabled={steps.length === 0}>
            {playing ? 'Pause' : 'Play'}
          </button>
          <button
            onClick={() => setStepIndex((v) => Math.min(v + 1, Math.max(steps.length - 1, 0)))}
            disabled={steps.length === 0}
          >
            Step +
          </button>
          <button onClick={clearTrace}>Clear Trace</button>
          <button onClick={clearWalls}>Clear Walls</button>
          <button onClick={randomWalls}>Random Walls</button>
        </div>

        <div className="group">
          <label>
            Algorithm
            <select value={algorithm} onChange={(e) => setAlgorithm(e.target.value)}>
              <option value="BFS">BFS</option>
              <option value="DFS">DFS</option>
              <option value="DIJKSTRA">DIJKSTRA</option>
            </select>
          </label>

          <label>
            Tool
            <select value={tool} onChange={(e) => setTool(e.target.value)}>
              <option value="wall">Wall</option>
              <option value="erase">Erase</option>
              <option value="start">Move Start</option>
              <option value="target">Move Target</option>
            </select>
          </label>

          <label>
            Speed {speedMs}ms
            <input
              type="range"
              min="40"
              max="500"
              step="20"
              value={speedMs}
              onChange={(e) => setSpeedMs(Number(e.target.value))}
            />
          </label>

          <label>
            Rows
            <input
              type="number"
              min="5"
              max="30"
              value={rows}
              onChange={(e) => setRows(Math.max(5, Math.min(30, Number(e.target.value) || 5)))}
            />
          </label>

          <label>
            Cols
            <input
              type="number"
              min="5"
              max="50"
              value={cols}
              onChange={(e) => setCols(Math.max(5, Math.min(50, Number(e.target.value) || 5)))}
            />
          </label>
        </div>
      </section>

      <section className="legend">
        <span><i className="cell empty" /> Empty</span>
        <span><i className="cell wall" /> Wall</span>
        <span><i className="cell start" /> Start</span>
        <span><i className="cell target" /> Target</span>
        <span><i className="cell visited" /> Visited</span>
        <span><i className="cell frontier" /> Frontier</span>
        <span><i className="cell current" /> Current</span>
      </section>

      <section className="grid" style={gridStyle}>
        {Array.from({ length: rows * cols }, (_, i) => {
          const row = Math.floor(i / cols);
          const col = i % cols;
          const key = keyOf(row, col);

          const startKey = keyOf(start.row, start.col);
          const targetKey = keyOf(target.row, target.col);

          let cls = 'empty';
          if (walls.has(key)) cls = 'wall';
          if (visited.has(key)) cls = 'visited';
          if (frontier.has(key)) cls = 'frontier';
          if (currentStep && key === keyOf(currentStep.current.row, currentStep.current.col)) cls = 'current';
          if (key === targetKey) cls = 'target';
          if (key === startKey) cls = 'start';

          return (
            <button
              type="button"
              key={key}
              className={`cell ${cls}`}
              onClick={() => onCellClick(row, col)}
              title={`(${row}, ${col})`}
            />
          );
        })}
      </section>
    </div>
  );
}
