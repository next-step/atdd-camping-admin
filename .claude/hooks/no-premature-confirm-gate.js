#!/usr/bin/env node
// Stop 훅이 부르는 게이트 스크립트.
// 스킬의 통과 조건이 "사용자에게 묻지 않고 곧바로 다음 단계를 수행한다"고 되어
// 있는데도, 마지막 응답이 다음 단계로 넘어가도 되는지 다시 묻고 끝나면 그 턴
// 종료를 종료 코드 2로 막는다. 실제로 판단이 필요해 막힌 질문(모르는 것,
// 사용자 결정이 필요한 것)은 이 패턴에 걸리지 않게 최소한으로만 잡는다.

const fs = require("fs");
const path = require("path");

const STATE_DIR = path.join(path.resolve(__dirname, "..", ".."), ".claude", "state");
const LOG_FILE = path.join(STATE_DIR, "no-premature-confirm-log.txt");
const DEBUG_FILE = path.join(STATE_DIR, "no-premature-confirm-debug.log");

// 스킬이 "묻지 않고 곧바로 진행"하라고 정해둔 자리에서 되묻는 전형적인 문구.
const CONTINUE_QUESTION_PATTERNS = [
  /(이어서|바로|곧바로)[^\n]{0,40}(할까요|수행할까요|진행할까요|처리할까요)\s*\?/,
  /진행할까요\s*\?/,
  /커밋할까요\s*\?/,
  /넘어갈까요\s*\?/,
  /계속(해서)?\s*진행할까요\s*\?/,
  /수행할까요\s*\?/,
];

function nowIso() {
  return new Date().toISOString();
}

function appendLog(line) {
  fs.mkdirSync(STATE_DIR, { recursive: true });
  fs.appendFileSync(LOG_FILE, `${nowIso()} ${line}\n`, "utf8");
}

function readStdin() {
  try {
    return fs.readFileSync(0, "utf8");
  } catch (_) {
    return "";
  }
}

// 세션 transcript(JSONL)에서 마지막 assistant 메시지의 텍스트를 뽑는다.
// 정확한 스키마를 몰라도 되게, "assistant" 역할을 가진 마지막 줄에서
// text류 필드를 최대한 훑는다.
function extractLastAssistantText(transcriptPath) {
  let raw;
  try {
    raw = fs.readFileSync(transcriptPath, "utf8");
  } catch (_) {
    return null;
  }

  const lines = raw.split("\n").filter((l) => l.trim().length > 0);
  for (let i = lines.length - 1; i >= 0; i--) {
    let entry;
    try {
      entry = JSON.parse(lines[i]);
    } catch (_) {
      continue;
    }

    const role = entry.role || (entry.message && entry.message.role) || entry.type;
    if (role !== "assistant") continue;

    const texts = [];
    const collect = (node) => {
      if (!node) return;
      if (typeof node === "string") {
        texts.push(node);
        return;
      }
      if (Array.isArray(node)) {
        node.forEach(collect);
        return;
      }
      if (typeof node === "object") {
        if (typeof node.text === "string") texts.push(node.text);
        if (Array.isArray(node.content)) node.content.forEach(collect);
      }
    };
    collect(entry.message ? entry.message.content : entry.content);

    if (texts.length > 0) return texts.join("\n");
  }
  return null;
}

function main() {
  const stdinRaw = readStdin();
  fs.mkdirSync(STATE_DIR, { recursive: true });
  try {
    fs.appendFileSync(DEBUG_FILE, `${nowIso()} ${stdinRaw}\n`, "utf8");
  } catch (_) {
    // 디버그 로그는 실패해도 게이트 동작에 영향 주지 않는다.
  }

  let payload = {};
  try {
    payload = JSON.parse(stdinRaw);
  } catch (_) {
    // 입력이 없거나 JSON이 아니면 검사할 게 없다.
  }

  // 실제 페이로드에 last_assistant_message가 그대로 들어온다. 없을 때만
  // transcript_path를 뒤져서 마지막 assistant 메시지를 찾는다.
  let lastText = payload.last_assistant_message;
  if (!lastText && payload.transcript_path) {
    lastText = extractLastAssistantText(payload.transcript_path);
  }
  if (!lastText) {
    appendLog("PASS (no assistant text)");
    process.exit(0);
  }

  // 문장 중간에서 예시로 인용한 문구까지 잡히는 것을 막기 위해, 응답의
  // 맨 끝부분(마지막 문단)에서만 되묻는 패턴을 찾는다.
  const paragraphs = lastText.trim().split(/\n\s*\n/);
  const lastParagraph = paragraphs[paragraphs.length - 1].trim();

  const matched = CONTINUE_QUESTION_PATTERNS.find((re) => re.test(lastParagraph));
  if (!matched) {
    appendLog("PASS");
    process.exit(0);
  }

  const snippet = lastParagraph.slice(-200);
  appendLog(`BLOCK snippet="${snippet.replace(/\n/g, " ")}"`);
  process.stderr.write(
    "스킬 통과 조건에 따라 사용자에게 다시 묻지 말고 곧바로 다음 단계를 수행해야 합니다.\n" +
      `방금 응답이 진행 여부를 되묻고 끝났습니다: "...${snippet}"\n` +
      "질문을 멈추고, 해당 단계를 실제로 수행한 뒤 응답하세요.\n"
  );
  process.exit(2);
}

main();
