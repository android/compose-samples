import os
import sys
import subprocess
import tempfile

def analyze_trace(trace_path, package_name=None):
    tp_bin = "/tmp/trace_processor"
    if not os.path.exists(tp_bin):
        tp_bin = "trace_processor"
    
    where_clauses = [
        "(slice.name LIKE 'Compose:%' "
        "OR slice.name LIKE 'Recompose:%' "
        "OR slice.name LIKE 'Layout:%' "
        "OR slice.name LIKE 'Measure:%' "
        "OR slice.name = 'Choreographer#doFrame')",
        "slice.dur > 15000000"
    ]
    
    if package_name:
        where_clauses.append(
            f"(process.name = '{package_name}' "
            f"OR process.name = '{package_name}-debug' "
            f"OR process.name = '{package_name}.benchmark')"
        )
    
    where_clause = " AND ".join(where_clauses)
    
    query = f"""
        SELECT 
            slice.name,
            process.name as process_name,
            COUNT(*) as occurrence_count,
            ROUND(AVG(slice.dur) / 1e6, 2) as avg_duration_ms,
            ROUND(MAX(slice.dur) / 1e6, 2) as max_duration_ms,
            ROUND(SUM(slice.dur) / 1e6, 2) as total_duration_ms
        FROM slice
        JOIN thread_track ON slice.track_id = thread_track.id
        JOIN thread USING (utid)
        JOIN process USING (upid)
        WHERE {where_clause}
        GROUP BY slice.name, process.name
        ORDER BY total_duration_ms DESC
        LIMIT 10;
    """
    
    with tempfile.NamedTemporaryFile("w", suffix=".sql", delete=False) as f:
        f.write(query)
        sql_path = f.name
        
    try:
        cmd = [tp_bin, "query", "-f", sql_path, trace_path]
        res = subprocess.run(cmd, capture_output=True, text=True, check=True)
        print("Top Performance Hotspots:")
        print(f"{'Slice Name':<50} | {'Process':<30} | {'Count':<6} | {'Avg (ms)':<10} | {'Max (ms)':<10} | {'Total (ms)':<10}")
        print("-" * 128)
        
        lines = res.stdout.strip().splitlines()
        for line in lines:
            if line.startswith('"name"') or line.startswith("column ") or line.startswith("Loading trace:"):
                continue
            parts = [p.strip('"') for p in line.split(",")]
            if len(parts) >= 6:
                name, proc, cnt, avg_d, max_d, tot_d = parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]
                print(f"{name:<50} | {proc:<30} | {cnt:<6} | {float(avg_d):<10.2f} | {float(max_d):<10.2f} | {float(tot_d):<10.2f}")
    finally:
        if os.path.exists(sql_path):
            os.remove(sql_path)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 analyze_trace.py <path_to_perfetto_trace> [package_name]")
        sys.exit(1)
    
    pkg = sys.argv[2] if len(sys.argv) > 2 else None
    analyze_trace(sys.argv[1], pkg)
