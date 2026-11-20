<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>APK Factory AI</title>
  <style>
    * { box-sizing: border-box; font-family: system-ui, -apple-system, sans-serif; }
    body { margin: 0; background: #0f172a; color: #f8fafc; padding: 16px; }
    h1 { font-size: 1.4rem; margin: 0 0 12px; color: #38bdf8; }
    .card { background: #1e293b; padding: 14px; border-radius: 12px; margin-bottom: 14px; }
    label { display: block; font-size: 0.85rem; color: #94a3b8; margin-bottom: 4px; margin-top: 10px; }
    input, textarea, select {
      width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #334155;
      background: #0f172a; color: #fff; font-size: 0.95rem;
    }
    textarea { min-height: 90px; resize: vertical; }
    button {
      width: 100%; padding: 12px; margin-top: 12px; border: none; border-radius: 8px;
      font-weight: bold; font-size: 1rem; cursor: pointer;
    }
    .btn-ai { background: #38bdf8; color: #0f172a; }
    .btn-build { background: #22c55e; color: #052e16; }
    .btn-build:disabled { background: #475569; color: #94a3b8; cursor: not-allowed; }
    iframe { width: 100%; height: 340px; border: 1px solid #334155; border-radius: 8px; background: #000; }
    #status { font-size: 0.85rem; color: #facc15; margin-top: 10px; white-space: pre-wrap; }
    details summary { cursor: pointer; color: #94a3b8; font-size: 0.9rem; }
  </style>
</head>
<body>

  <h1>⚡ APK Factory AI</h1>

  <div class="card">
    <details>
      <summary>⚙️ Configurações de API (Gemini + GitHub)</summary>
      <label>Gemini API Key</label>
      <input type="password" id="geminiKey" placeholder="AIzaSy..." />
      <label>GitHub Token (com permissão repo + workflow)</label>
      <input type="password" id="ghToken" placeholder="ghp_..." />
      <label>Repositório GitHub (usuario/repositorio)</label>
      <input type="text" id="ghRepo" placeholder="seu-usuario/fabrica-apk" />
    </details>
  </div>

  <div class="card">
    <label>Nome do Aplicativo / Jogo</label>
    <input type="text" id="appName" placeholder="Ex: Super Corrida 3D" />

    <label>Tipo de Projeto</label>
    <select id="appType">
      <option value="app">Programa / Ferramenta (HTML/JS)</option>
      <option value="3d">Jogo 3D (Three.js + Controles Touch)</option>
    </select>

    <label>Descreva o que o APK deve fazer:</label>
    <textarea id="prompt" placeholder="Ex: Crie um jogo 3D onde controlo um cubo que pula obstáculos em uma pista infinita, com botões na tela para celular e placar."></textarea>

    <button class="btn-ai" onclick="gerarCodigo()">1. Gerar App / Jogo com IA</button>
    <button class="btn-build" id="btnBuild" onclick="compilarAPK()" disabled>2. Compilar e Gerar APK</button>
    <div id="status"></div>
  </div>

  <div class="card">
    <label>📱 Preview ao Vivo</label>
    <iframe id="preview" sandbox="allow-scripts allow-same-origin"></iframe>
  </div>

<script>
  let codigoGerado = "";

  // Salva e carrega credenciais localmente
  ["geminiKey", "ghToken", "ghRepo"].forEach(id => {
    const el = document.getElementById(id);
    el.value = localStorage.getItem(id) || "";
    el.addEventListener("input", () => localStorage.setItem(id, el.value.trim()));
  });

  function setStatus(msg) {
    document.getElementById("status").textContent = msg;
  }

  async function gerarCodigo() {
    const apiKey = document.getElementById("geminiKey").value.trim();
    const promptUser = document.getElementById("prompt").value.trim();
    const appType = document.getElementById("appType").value;

    if (!apiKey || !promptUser) {
      return setStatus("❌ Preencha a Gemini API Key e a descrição do app.");
    }

    setStatus("🧠 Gerando código completo com IA...");
    document.getElementById("btnBuild").disabled = true;

    const systemInstruction = appType === "3d"
      ? "Gere um único arquivo HTML completo e autocontido para um JOGO 3D rodando em celular usando Three.js via CDN (https://unpkg.com/three@0.160.0/build/three.min.js). Inclua controles touch na tela (joystick ou botões), tela cheia, sem barras de rolagem. Retorne APENAS o código HTML puro, sem blocos markdown (```)."
      : "Gere um único arquivo HTML completo e autocontido (HTML + CSS + JS) para um aplicativo mobile responsivo e moderno. Retorne APENAS o código HTML puro, sem blocos markdown (```).";

    try {
      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ parts: [{ text: `${systemInstruction}\n\nPedido do usuário: ${promptUser}` }] }]
        })
      });

      const data = await res.json();
      let html = data.candidates?.[0]?.content?.parts?.[0]?.text || "";
      html = html.replace(/^```html\s*/i, "").replace(/```$/i, "").trim();

      if (!html) throw new Error("A IA não retornou código válido.");

      codigoGerado = html;
      document.getElementById("preview").srcdoc = codigoGerado;
      document.getElementById("btnBuild").disabled = false;
      setStatus("✅ Código gerado! Teste no preview abaixo e clique em 'Compilar e Gerar APK'.");
    } catch (err) {
      setStatus("❌ Erro ao gerar: " + err.message);
    }
  }

  async function compilarAPK() {
    const token = document.getElementById("ghToken").value.trim();
    const repo = document.getElementById("ghRepo").value.trim();
    const appName = document.getElementById("appName").value.trim() || "MeuAppGerado";

    if (!token || !repo) {
      return setStatus("❌ Configure o GitHub Token e o Repositório nas configurações.");
    }

    setStatus("🚀 Enviando código para a nuvem e iniciando compilação do APK...");

    try {
      // Converte HTML para Base64 (suportando acentos/UTF-8)
      const base64Html = btoa(unescape(encodeURIComponent(codigoGerado)));

      // Dispara o workflow do GitHub Actions
      const res = await fetch(`https://api.github.com/repos/${repo}/actions/workflows/build-apk.yml/dispatches`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Accept": "application/vnd.github+json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          ref: "main",
          inputs: {
            app_name: appName,
            html_base64: base64Html
          }
        })
      });

      if (res.status === 204) {
        setStatus(`✅ Compilação iniciada!\nEm ~2 minutos seu APK estará pronto em:\nhttps://github.com/${repo}/actions`);
      } else {
        const errData = await res.json();
        throw new Error(errData.message || "Falha ao disparar workflow.");
      }
    } catch (err) {
      setStatus("❌ Erro no GitHub: " + err.message);
    }
  }
</script>
</body>
</html>
