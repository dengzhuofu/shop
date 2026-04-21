import fs from "node:fs";
import path from "node:path";
import os from "node:os";
import { spawn } from "node:child_process";

const EDGE_PATHS = [
  "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe",
  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
  "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
];

const PAGES = [
  { name: "about-us", url: "https://www.isinwheel.com/pages/about-us-1" },
  { name: "contact-us", url: "https://www.isinwheel.com/pages/contact-us" },
  { name: "faq", url: "https://www.isinwheel.com/pages/support-faq" },
  { name: "photos", url: "https://www.isinwheel.com/pages/photos" },
  { name: "blog", url: "https://www.isinwheel.com/blogs/news" },
  { name: "video-labs", url: "https://www.isinwheel.com/pages/isinwheel-videos" },
];

const OUTPUT_DIR = path.resolve(process.cwd(), "output", "playwright", "isinwheel");
const PORT = Number(process.env.CDP_PORT || 9333);

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function waitForJson(url, timeoutMs = 15000) {
  const start = Date.now();
  let lastError;

  while (Date.now() - start < timeoutMs) {
    try {
      const response = await fetch(url);
      if (response.ok) {
        return await response.json();
      }
    } catch (error) {
      lastError = error;
    }
    await sleep(250);
  }

  throw lastError || new Error(`Timed out waiting for ${url}`);
}

function findBrowserPath() {
  const browserPath = EDGE_PATHS.find((candidate) => fs.existsSync(candidate));
  if (!browserPath) {
    throw new Error("No Edge or Chrome executable found on this machine.");
  }
  return browserPath;
}

function launchBrowser() {
  const browserPath = findBrowserPath();
  const profileDir = fs.mkdtempSync(path.join(os.tmpdir(), "isinwheel-capture-"));
  const args = [
    "--headless=new",
    `--remote-debugging-port=${PORT}`,
    `--user-data-dir=${profileDir}`,
    "--no-first-run",
    "--no-default-browser-check",
    "--hide-scrollbars",
    "--disable-gpu",
    "--disable-extensions",
    "--window-size=1440,1200",
    "about:blank",
  ];

  const browser = spawn(browserPath, args, {
    stdio: "ignore",
    windowsHide: true,
  });

  return { browser, profileDir, browserPath };
}

function stopBrowserTree(pid) {
  return new Promise((resolve) => {
    const killer = spawn("taskkill", ["/PID", String(pid), "/T", "/F"], {
      stdio: "ignore",
      windowsHide: true,
    });

    killer.on("exit", () => resolve());
    killer.on("error", () => resolve());
  });
}

function createCdpClient(webSocketUrl) {
  const socket = new WebSocket(webSocketUrl);
  let nextId = 1;
  const pending = new Map();
  const listeners = new Set();

  socket.addEventListener("message", (event) => {
    const message = JSON.parse(event.data);

    if (message.id) {
      const deferred = pending.get(message.id);
      if (!deferred) {
        return;
      }
      pending.delete(message.id);
      if (message.error) {
        deferred.reject(new Error(message.error.message));
      } else {
        deferred.resolve(message.result);
      }
      return;
    }

    for (const listener of listeners) {
      listener(message);
    }
  });

  async function open() {
    if (socket.readyState === WebSocket.OPEN) {
      return;
    }

    await new Promise((resolve, reject) => {
      socket.addEventListener("open", resolve, { once: true });
      socket.addEventListener("error", reject, { once: true });
    });
  }

  function send(method, params = {}, sessionId) {
    return new Promise((resolve, reject) => {
      const id = nextId++;
      pending.set(id, { resolve, reject });
      socket.send(JSON.stringify({ id, method, params, sessionId }));
    });
  }

  function waitForEvent(method, sessionId, predicate = () => true, timeoutMs = 20000) {
    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        listeners.delete(onMessage);
        reject(new Error(`Timed out waiting for ${method}`));
      }, timeoutMs);

      function onMessage(message) {
        if (message.method !== method) {
          return;
        }
        if (sessionId && message.sessionId !== sessionId) {
          return;
        }
        if (!predicate(message.params || {})) {
          return;
        }
        clearTimeout(timeout);
        listeners.delete(onMessage);
        resolve(message.params || {});
      }

      listeners.add(onMessage);
    });
  }

  async function close() {
    for (const deferred of pending.values()) {
      deferred.reject(new Error("CDP socket closed"));
    }
    pending.clear();
    socket.close();
  }

  return { open, send, waitForEvent, close };
}

async function capturePage(client, target) {
  console.log(`Capturing ${target.name}...`);
  const { targetId } = await client.send("Target.createTarget", {
    url: "about:blank",
  });
  const { sessionId } = await client.send("Target.attachToTarget", {
    targetId,
    flatten: true,
  });

  await client.send("Page.enable", {}, sessionId);
  await client.send("Runtime.enable", {}, sessionId);
  await client.send("Emulation.setDeviceMetricsOverride", {
    width: 1440,
    height: 1200,
    deviceScaleFactor: 1,
    mobile: false,
  }, sessionId);

  const loadEvent = client.waitForEvent("Page.loadEventFired", sessionId, () => true, 60000);
  await client.send("Page.navigate", { url: target.url }, sessionId);
  await loadEvent;
  await sleep(2500);

  await client.send("Runtime.evaluate", {
    expression: `
      (async () => {
        const pause = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
        const matchers = ["accept", "agree", "got it", "close"];
        const buttons = Array.from(document.querySelectorAll("button, [role='button'], a"));
        for (const element of buttons) {
          const label = (element.innerText || element.textContent || "").trim().toLowerCase();
          if (label && matchers.some((term) => label === term || label.includes(term))) {
            try { element.click(); } catch (error) {}
          }
        }
        const total = Math.max(
          document.body.scrollHeight,
          document.documentElement.scrollHeight
        );
        const step = Math.max(700, Math.floor(window.innerHeight * 0.8));
        for (let y = 0; y < total; y += step) {
          window.scrollTo(0, y);
          await pause(300);
        }
        window.scrollTo(0, 0);
        await pause(1000);
        return {
          title: document.title,
          height: Math.max(document.body.scrollHeight, document.documentElement.scrollHeight),
        };
      })();
    `,
    awaitPromise: true,
    returnByValue: true,
  }, sessionId);

  const metrics = await client.send("Page.getLayoutMetrics", {}, sessionId);
  const width = Math.ceil(metrics.contentSize.width);
  const height = Math.ceil(metrics.contentSize.height);

  const screenshot = await client.send("Page.captureScreenshot", {
    format: "png",
    fromSurface: true,
    captureBeyondViewport: true,
    clip: {
      x: 0,
      y: 0,
      width,
      height,
      scale: 1,
    },
  }, sessionId);

  const filePath = path.join(OUTPUT_DIR, `${target.name}.png`);
  fs.writeFileSync(filePath, Buffer.from(screenshot.data, "base64"));
  console.log(`${target.name}: ${filePath}`);

  await client.send("Target.closeTarget", { targetId });
}

async function main() {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });

  const { browser, profileDir, browserPath } = launchBrowser();
  console.log(`Using browser: ${browserPath}`);

  try {
    const version = await waitForJson(`http://127.0.0.1:${PORT}/json/version`, 20000);
    const client = createCdpClient(version.webSocketDebuggerUrl);
    await client.open();

    try {
      for (const target of PAGES) {
        await capturePage(client, target);
      }
    } finally {
      await client.close();
    }
  } finally {
    await stopBrowserTree(browser.pid);
    await sleep(1200);
    try {
      fs.rmSync(profileDir, { recursive: true, force: true });
    } catch (error) {
      console.warn(`Cleanup warning: ${error.message}`);
    }
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
