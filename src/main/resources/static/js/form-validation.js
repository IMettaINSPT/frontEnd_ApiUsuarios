// Validación genérica de formularios (HTML5) para evitar POST si hay campos inválidos.
// - Agrega clase "was-validated" al form para que puedas estilar errores (Bootstrap o tu CSS).
// - Enfoca el primer campo inválido.
(function () {
  function focusFirstInvalid(form) {
    const firstInvalid = form.querySelector(":invalid");
    if (firstInvalid) firstInvalid.focus();
  }

  function attach(form) {
    form.addEventListener("submit", function (e) {
      // Si el navegador soporta constraint validation, checkValidity existe
      if (typeof form.checkValidity === "function") {
        if (!form.checkValidity()) {
          e.preventDefault();
          e.stopPropagation();
          form.classList.add("was-validated");
          focusFirstInvalid(form);
        } else {
          // opcional: deshabilitar submit para evitar doble click
          const btn = form.querySelector("[data-submit]");
          if (btn) {
            btn.disabled = true;
            btn.dataset.originalText = btn.innerText;
            btn.innerText = "Enviando...";
          }
        }
      }
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-validate]").forEach(attach);
  });
})();