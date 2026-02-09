(function () {
  function setText(id, value) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = value;
  }

  function setVisible(id, visible) {
    const el = document.getElementById(id);
    if (!el) return;
    el.style.display = visible ? "" : "none";
  }

  function setHealth(ok, title, sub) {
    const dot = document.getElementById("healthDot");
    const t = document.getElementById("healthTitle");
    const s = document.getElementById("healthSub");

    if (dot) {
      dot.style.background = ok ? "rgba(31,122,58,.95)" : "rgba(220,60,60,.95)";
      dot.style.boxShadow = ok
        ? "0 0 0 3px rgba(31,122,58,.18)"
        : "0 0 0 3px rgba(220,60,60,.18)";
    }
    if (t) t.textContent = title;
    if (s) s.textContent = sub;
  }

  async function loadSummary() {
    const root = document.querySelector(".dashboard-page");
    const url = root?.dataset?.summaryUrl;

    // por las dudas: al inicio, oculto
    setVisible("dashOkBtn", false);

    // Si no existe endpoint, no rompemos la UI
    if (!url) {
      setText("dashStatus", "Sin endpoint");
      setText("dashStatusSub", "Falta data-summary-url en el HTML");
      setHealth(false, "Sin datos", "No hay endpoint configurado");
      setVisible("dashOkBtn", false);
      return;
    }

    setText("dashStatus", "Cargando…");
    setText("dashStatusSub", "Leyendo métricas");

    try {
      const resp = await fetch(url, { headers: { "Accept": "application/json" } });
      if (!resp.ok) throw new Error("HTTP " + resp.status);

      const data = await resp.json();

      setText("kpiBancos", data.bancos ?? 0);
      setText("kpiSucursales", data.sucursales ?? 0);
      setText("kpiContratos", data.contratos ?? 0);
      setText("kpiVigilantes", data.vigilantes ?? 0);
      setText("kpiUsuarios", data.usuarios ?? 0);

      setText("dashStatus", "OK");
      setText("dashStatusSub", "Métricas actualizadas");
      setHealth(true, "Operativo", "Datos cargados correctamente");

      // ✅ solo si OK
      setVisible("dashOkBtn", true);

    } catch (e) {
      setText("dashStatus", "Error");
      setText("dashStatusSub", "No se pudieron cargar métricas");
      setText("kpiBancos", "—");
      setText("kpiSucursales", "—");
      setText("kpiContratos", "—");
      setText("kpiVigilantes", "—");
      setText("kpiUsuarios", "—");
      setHealth(false, "Sin conexión", String(e.message || e));

      // ✅ oculto si falla
      setVisible("dashOkBtn", false);
    }
  }

  document.addEventListener("DOMContentLoaded", loadSummary);
})();