// Evita mostrar páginas cacheadas (BFCache) luego de logout
(function () {

  window.addEventListener("pageshow", function (event) {
    if (event.persisted) {
      window.location.reload();
    }
  });

  try {
    const navEntries = performance.getEntriesByType("navigation");
    if (navEntries && navEntries.length && navEntries[0].type === "back_forward") {
      window.location.reload();
    }
  } catch (e) {}

})();