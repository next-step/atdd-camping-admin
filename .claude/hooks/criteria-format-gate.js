#!/usr/bin/env node
// Stop 훅이 부르는 게이트 스크립트.
// docs/acceptance-criteria.md가 원하는 형식(도메인 언어)을 벗어나 실제 HTTP
// 요청/응답 예시(엔드포인트 경로, 쿼리 파라미터, JSON 응답, 코드 위치 참조)를
// 그대로 담고 있으면 위반 규칙 이름별로 상태 파일에 횟수를 세고 종료 코드
// 2로 턴 종료를 막는다. 같은 규칙이 개별 한도를 넘거나 전체 위반 횟수가
// 전체 한도를 넘으면 더 막지 않고 종료 코드 0으로 사람에게 넘긴다.
// 사람에게 넘길 때와 통과했을 때는 상태 파일을 지운다.
// 막힌 줄(BLOCK)·넘긴 줄(HANDOFF)·통과한 줄(PASS)은 기록 파일에 남긴다.

const fs = require("fs");
const path = require("path");

const PER_NAME_LIMIT = parseInt(process.env.CRITERIA_GATE_PER_NAME_LIMIT || "3", 10);
const TOTAL_LIMIT = parseInt(process.env.CRITERIA_GATE_TOTAL_LIMIT || "5", 10);

const REPO_ROOT = path.resolve(__dirname, "..", "..");
const TARGET_FILE = path.join(REPO_ROOT, "docs", "acceptance-criteria.md");
const STATE_DIR = path.join(REPO_ROOT, ".claude", "state");
const STATE_FILE = path.join(STATE_DIR, "criteria-format-gate-state.json");
const LOG_FILE = path.join(STATE_DIR, "criteria-format-gate-log.txt");

// 위반으로 볼 패턴. 이름은 상태 파일·로그·stderr에 그대로 쓰인다.
const RULES = [
  {
    name: "http-request-line",
    re: /\b(GET|POST|PUT|PATCH|DELETE)\s+\/\S+/g,
    label: "HTTP 메서드+경로가 그대로 적혀 있음",
  },
  {
    name: "query-string",
    re: /\?[A-Za-z_][A-Za-z0-9_]*=/g,
    label: "쿼리 파라미터가 그대로 적혀 있음",
  },
  {
    name: "json-response-literal",
    re: /\{\s*"[A-Za-z_][A-Za-z0-9_]*"\s*:/g,
    label: "JSON 응답이 그대로 적혀 있음",
  },
  {
    // 단순 식별자 참조(id=1 등)는 도메인 언어로 허용한다. camelCase 복합어
    // (totalReservationRevenue=50000 같은 실제 응답 필드명)만 위반으로 본다.
    name: "response-field-literal",
    re: /\b[a-z]+[A-Z][A-Za-z0-9]*=(?:\d+(?:\.\d+)?|"[^"]*"|'[^']*')/g,
    label: "응답 필드=값 형태가 그대로 적혀 있음",
  },
  {
    name: "code-location-ref",
    re: /(\b[A-Za-z0-9_]+\.java\b(?::\d+)?)|(\d+\s*행)/g,
    label: "코드 파일·줄 번호가 Given·When·Then에 그대로 적혀 있음",
  },
];

function nowIso() {
  return new Date().toISOString();
}

function appendLog(line) {
  fs.mkdirSync(STATE_DIR, { recursive: true });
  fs.appendFileSync(LOG_FILE, `${nowIso()} ${line}\n`, "utf8");
}

function loadState() {
  try {
    const raw = fs.readFileSync(STATE_FILE, "utf8");
    const parsed = JSON.parse(raw);
    if (parsed && typeof parsed === "object" && parsed.counts && typeof parsed.counts === "object") {
      return parsed;
    }
  } catch (_) {
    // 상태 파일이 없거나 깨졌으면 처음부터 다시 센다.
  }
  return { counts: {} };
}

function saveState(state) {
  fs.mkdirSync(STATE_DIR, { recursive: true });
  fs.writeFileSync(STATE_FILE, JSON.stringify(state, null, 2), "utf8");
}

function clearState() {
  try {
    fs.unlinkSync(STATE_FILE);
  } catch (_) {
    // 이미 없으면 그대로 둔다.
  }
}

// Given·When·Then이 담긴 코드블록(```...```)만 검사한다 — 그 밖의 설명 문단은
// 원인이 되는 코드 위치를 적어도 되는 자리다(atdd-criteria SKILL.md 참고).
function extractCriteriaBlocks(content) {
  const blocks = [];
  const fenceRe = /```([\s\S]*?)```/g;
  let m;
  while ((m = fenceRe.exec(content)) !== null) {
    const body = m[1];
    if (/\bGiven\b|\bWhen\b|\bThen\b/.test(body)) {
      blocks.push(body);
    }
  }
  return blocks;
}

function findViolations(content) {
  const blocks = extractCriteriaBlocks(content);
  const violations = []; // { name, example }
  for (const rule of RULES) {
    for (const block of blocks) {
      const matches = block.match(rule.re);
      if (matches && matches.length > 0) {
        violations.push({ name: rule.name, label: rule.label, example: matches[0].trim() });
        break; // 이 규칙은 이 실행에서 한 번만 센다.
      }
    }
  }
  return violations;
}

function main() {
  let content;
  try {
    content = fs.readFileSync(TARGET_FILE, "utf8");
  } catch (_) {
    // 파일이 없으면 검사할 게 없으니 통과시킨다.
    clearState();
    appendLog("PASS (file missing)");
    process.exit(0);
  }

  const violations = findViolations(content);

  if (violations.length === 0) {
    clearState();
    appendLog("PASS");
    process.exit(0);
  }

  const state = loadState();
  for (const v of violations) {
    state.counts[v.name] = (state.counts[v.name] || 0) + 1;
  }
  saveState(state);

  const total = Object.values(state.counts).reduce((a, b) => a + b, 0);
  const overNameLimit = violations.filter((v) => state.counts[v.name] > PER_NAME_LIMIT);
  const overTotalLimit = total > TOTAL_LIMIT;

  const summary = violations.map((v) => `${v.name}=${state.counts[v.name]}/${PER_NAME_LIMIT}`).join(" ");

  if (overNameLimit.length > 0 || overTotalLimit) {
    appendLog(`HANDOFF ${summary} total=${total}/${TOTAL_LIMIT}`);
    clearState();
    process.stderr.write(
      `한도를 넘어 더 막지 않고 사람에게 넘깁니다. ${summary} total=${total}/${TOTAL_LIMIT}\n`
    );
    process.exit(0);
  }

  appendLog(`BLOCK ${summary} total=${total}/${TOTAL_LIMIT}`);
  process.stderr.write(
    "docs/acceptance-criteria.md가 도메인 언어가 아니라 실제 요청/응답 예시를 담고 있어 턴 종료를 막습니다.\n"
  );
  for (const v of violations) {
    process.stderr.write(
      `  ${v.name} (${v.label}): ${state.counts[v.name]}번째 (개별 한도 ${PER_NAME_LIMIT}, 전체 한도 ${TOTAL_LIMIT}, 지금 전체 ${total})\n` +
        `    예: ${v.example}\n`
    );
  }
  process.exit(2);
}

main();
