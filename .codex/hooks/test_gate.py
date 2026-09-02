#!/usr/bin/env python3
"""Gate Codex Stop using only Gradle test results and repeated-failure counts."""

from __future__ import annotations

import json
import os
import re
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Dict, List, Tuple


TEST_COMMAND = ("./gradlew", "test", "--rerun-tasks")
PER_TEST_LIMIT = 3
TOTAL_FAILURE_LIMIT = 5
EVENT_HEADER = (
    "timestamp_utc\tsession_id\tturn_id\tstop_hook_active\tevent\tconditions\n"
)
ANSI_ESCAPE = re.compile(r"\x1b\[[0-?]*[ -/]*[@-~]")
FAILED_LINE = re.compile(r"^(?P<name>.+?)\s+FAILED\s*$")
UNSAFE_FILENAME = re.compile(r"[^A-Za-z0-9._-]")


def git_root() -> Path:
    result = subprocess.run(
        ["git", "rev-parse", "--show-toplevel"],
        stdin=subprocess.DEVNULL,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        detail = result.stderr.strip() or result.stdout.strip() or "unknown error"
        raise RuntimeError(f"Git 루트를 찾지 못했습니다: {detail}")
    return Path(result.stdout.strip())


def read_hook_input() -> Tuple[str, str, bool]:
    payload = json.load(sys.stdin)
    if not isinstance(payload, dict):
        raise ValueError("Codex 훅 입력은 JSON 객체여야 합니다.")

    session_id = str(payload.get("session_id") or "UNKNOWN_SESSION")
    turn_id = str(payload.get("turn_id") or "UNKNOWN_TURN")
    stop_hook_active = bool(payload.get("stop_hook_active", False))
    return session_id, turn_id, stop_hook_active


def safe_session_filename(session_id: str) -> str:
    safe_name = UNSAFE_FILENAME.sub("_", session_id).strip(".")
    return safe_name or "UNKNOWN_SESSION"


def run_tests(root: Path) -> Tuple[int, bytes]:
    try:
        result = subprocess.run(
            TEST_COMMAND,
            cwd=root,
            stdin=subprocess.DEVNULL,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
        return result.returncode, result.stdout or b""
    except OSError as error:
        output = f"테스트 명령을 실행하지 못했습니다: {error}\n".encode(
            "utf-8", errors="replace"
        )
        return 127, output


def append_test_output(log_path: Path, output: bytes) -> None:
    log_path.parent.mkdir(parents=True, exist_ok=True)
    with log_path.open("ab") as log_file:
        log_file.write(output)


def failed_test_names(output: bytes) -> List[str]:
    text = output.decode("utf-8", errors="replace")
    names = set()
    for raw_line in text.splitlines():
        line = ANSI_ESCAPE.sub("", raw_line).strip()
        match = FAILED_LINE.match(line)
        if match is None:
            continue

        name = match.group("name").strip()
        if name.startswith("> Task") or " > " not in name:
            continue
        names.add(name)

    return sorted(names) or ["TEST_COMMAND"]


def load_state(state_path: Path) -> Dict[str, Any]:
    if not state_path.exists():
        return {"total_failures": 0, "failures_by_test": {}}

    try:
        raw_state = json.loads(state_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {"total_failures": 0, "failures_by_test": {}}

    raw_total = raw_state.get("total_failures", 0)
    total_failures = raw_total if isinstance(raw_total, int) and raw_total >= 0 else 0

    failures_by_test: Dict[str, int] = {}
    raw_failures = raw_state.get("failures_by_test", {})
    if isinstance(raw_failures, dict):
        for name, count in raw_failures.items():
            if isinstance(name, str) and isinstance(count, int) and count >= 0:
                failures_by_test[name] = count

    return {
        "total_failures": total_failures,
        "failures_by_test": failures_by_test,
    }


def write_state(
    state_path: Path,
    session_id: str,
    turn_id: str,
    total_failures: int,
    failures_by_test: Dict[str, int],
) -> None:
    state_path.parent.mkdir(parents=True, exist_ok=True)
    state = {
        "session_id": session_id,
        "last_turn_id": turn_id,
        "total_failures": total_failures,
        "failures_by_test": failures_by_test,
        "updated_at": datetime.now(timezone.utc).isoformat(),
    }
    temporary_path = state_path.with_name(f".{state_path.name}.{os.getpid()}.tmp")
    temporary_path.write_text(
        json.dumps(state, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    temporary_path.replace(state_path)


def tsv_value(value: str) -> str:
    return (
        value.replace("\\", "\\\\")
        .replace("\t", "\\t")
        .replace("\r", "\\r")
        .replace("\n", "\\n")
    )


def append_event(
    events_path: Path,
    session_id: str,
    turn_id: str,
    stop_hook_active: bool,
    event: str,
    conditions: Dict[str, Any],
) -> None:
    events_path.parent.mkdir(parents=True, exist_ok=True)
    needs_header = not events_path.exists() or events_path.stat().st_size == 0
    values = (
        datetime.now(timezone.utc).isoformat(),
        session_id,
        turn_id,
        "true" if stop_hook_active else "false",
        event,
        json.dumps(conditions, ensure_ascii=False, separators=(",", ":")),
    )
    with events_path.open("a", encoding="utf-8", newline="") as events_file:
        if needs_header:
            events_file.write(EVENT_HEADER)
        events_file.write("\t".join(tsv_value(value) for value in values) + "\n")


def retry_message(
    failed_names: List[str],
    failures_by_test: Dict[str, int],
    total_failures: int,
) -> str:
    test_conditions = "; ".join(
        f"{name}={failures_by_test[name]}/{PER_TEST_LIMIT}" for name in failed_names
    )
    return (
        f"테스트 게이트 조건: {test_conditions}; "
        f"TOTAL_FAILURES={total_failures}/{TOTAL_FAILURE_LIMIT}"
    )


def handoff_condition(
    failed_names: List[str],
    failures_by_test: Dict[str, int],
    total_failures: int,
) -> Tuple[str, int, int]:
    reached_test_limits = [
        name for name in failed_names if failures_by_test[name] >= PER_TEST_LIMIT
    ]
    if reached_test_limits:
        name = sorted(
            reached_test_limits,
            key=lambda item: (-failures_by_test[item], item),
        )[0]
        return name, failures_by_test[name], PER_TEST_LIMIT
    return "TOTAL_FAILURES", total_failures, TOTAL_FAILURE_LIMIT


def main() -> int:
    session_id, turn_id, stop_hook_active = read_hook_input()
    root = git_root()
    codex_dir = root / ".codex"
    session_filename = safe_session_filename(session_id)
    log_path = codex_dir / "logs" / f"{session_filename}.log"
    state_path = codex_dir / "state" / f"{session_filename}.json"
    events_path = codex_dir / "gate-events.tsv"

    return_code, output = run_tests(root)
    append_test_output(log_path, output)

    if return_code == 0:
        append_event(
            events_path,
            session_id,
            turn_id,
            stop_hook_active,
            "PASSED",
            {"test_command": " ".join(TEST_COMMAND), "return_code": return_code},
        )
        state_path.unlink(missing_ok=True)
        return 0

    failed_names = failed_test_names(output)
    state = load_state(state_path)
    total_failures = state["total_failures"] + 1
    failures_by_test = state["failures_by_test"]
    for name in failed_names:
        failures_by_test[name] = failures_by_test.get(name, 0) + 1

    write_state(
        state_path,
        session_id,
        turn_id,
        total_failures,
        failures_by_test,
    )

    conditions = {
        "failed_this_run": failed_names,
        "failures_by_test": dict(sorted(failures_by_test.items())),
        "per_test_limit": PER_TEST_LIMIT,
        "total_failures": total_failures,
        "total_limit": TOTAL_FAILURE_LIMIT,
        "return_code": return_code,
    }
    reached_test_limit = any(
        failures_by_test[name] >= PER_TEST_LIMIT for name in failed_names
    )
    reached_total_limit = total_failures >= TOTAL_FAILURE_LIMIT

    if reached_test_limit or reached_total_limit:
        condition_name, current_count, limit = handoff_condition(
            failed_names,
            failures_by_test,
            total_failures,
        )
        conditions["trigger"] = {
            "name": condition_name,
            "count": current_count,
            "limit": limit,
        }
        append_event(
            events_path,
            session_id,
            turn_id,
            stop_hook_active,
            "HANDED_OFF",
            conditions,
        )
        state_path.unlink(missing_ok=True)
        message = (
            f"테스트 게이트 HANDED_OFF: 조건 {condition_name}="
            f"{current_count}/{limit}"
        )
        if condition_name != "TOTAL_FAILURES":
            message += f"; TOTAL_FAILURES={total_failures}/{TOTAL_FAILURE_LIMIT}"
        print(json.dumps({"systemMessage": message}, ensure_ascii=False))
        return 0

    append_event(
        events_path,
        session_id,
        turn_id,
        stop_hook_active,
        "BLOCKED",
        conditions,
    )
    print(
        retry_message(failed_names, failures_by_test, total_failures),
        file=sys.stderr,
    )
    return 2


if __name__ == "__main__":
    raise SystemExit(main())
