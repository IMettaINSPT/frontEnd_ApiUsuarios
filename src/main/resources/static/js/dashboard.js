(function () {
  function readSummary() {
    const el = document.getElementById("dashboardSummaryJson");
    if (!el) return null;

    const raw = el.value || "";
    if (!raw.trim()) return null;

    try {
      return JSON.parse(raw);
    } catch (e) {
      console.error("No se pudo parsear summaryJson:", e);
      return null;
    }
  }

  function safeArray(v) {
    return Array.isArray(v) ? v : [];
  }

  function safeObject(v) {
    return v && typeof v === "object" ? v : {};
  }

  const summary = readSummary();
  if (!summary || !window.Chart) return;

  // --- Asaltos por mes ---
  const asaltosPorMes = safeArray(summary.asaltosPorMes);
  const asaltosLabels = asaltosPorMes.map(x => x.mes);
  const asaltosData = asaltosPorMes.map(x => x.cantidad);

  const cAsaltos = document.getElementById("chartAsaltos");
  if (cAsaltos) {
    new Chart(cAsaltos, {
      type: "bar",
      data: {
        labels: asaltosLabels,
        datasets: [{ label: "Asaltos", data: asaltosData }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: true } }
      }
    });
  }

  // --- Usuarios por rol ---
  const rolesMap = safeObject(summary.usuariosPorRol);
  const rolesLabels = Object.keys(rolesMap);
  const rolesData = Object.values(rolesMap);

  const cRoles = document.getElementById("chartRoles");
  if (cRoles) {
    new Chart(cRoles, {
      type: "doughnut",
      data: {
        labels: rolesLabels,
        datasets: [{ data: rolesData }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }

  // --- Contratos con arma vs sin arma ---
  const armaMap = safeObject(summary.contratosPorArma);
  const armaLabels = Object.keys(armaMap);
  const armaData = Object.values(armaMap);

  const cArma = document.getElementById("chartArma");
  if (cArma) {
    new Chart(cArma, {
      type: "pie",
      data: {
        labels: armaLabels,
        datasets: [{ data: armaData }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }
})();